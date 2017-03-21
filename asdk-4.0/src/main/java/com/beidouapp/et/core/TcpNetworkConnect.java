/**
 *
 */
package com.beidouapp.et.core;

import com.beidouapp.et.*;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.core.impl.TopicTypeEnum;
import com.beidouapp.et.mqtt.*;
import com.beidouapp.et.util.HexUtil;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;
import com.beidouapp.et.util.codec.EncryptUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 内网tcp连接
 *
 * @author ztao
 */
public class TcpNetworkConnect implements INetworkConnect {

    private static final String TAG = TcpNetworkConnect.class.getSimpleName();
    private Log LOG = LogFactory.getLog("java");

    private static final int MIN_MSG_ID = 1; // MQTT Msg id的最小值
    private static final int MAX_MSG_ID = 65535; // MQTT Msg id的最大值

    // 与服务器的当前连接状态
    private static enum State {
        idle, connecting, connected, connect_timeout, disconnecting
    }

    private int nextMsgId = MIN_MSG_ID - 1;
    // Used to store a set of in-use message IDs
    private Hashtable<Integer, Integer> mInUseMsgIds = new Hashtable<Integer, Integer>();

    private SDKContext mContext;
    private Server mServer;
    private ConnectOptions mConnectOptions = new ConnectOptions();

    private MessageCenter mMsgCenter = null;
    private PingManager mPingManager = null;

    private volatile State mState = State.idle;

    // 是否socket已经连接，能否操作InputStream，OutputStream
    private volatile boolean mSocketConnected = false;

    /**
     * 连接到设备回调
     */
    private IActionListener mConnectActionListener = null;
    /**
     * 与设备断开连接回调
     */
    private IActionListener mDisconnectActionListener = null;

    /**
     * 缓存chatTo的回调函数
     */
    private Hashtable<Integer, IActionListener> mSendListenerMap = new Hashtable<Integer, IActionListener>();

    /**
     * 正在发送的消息,缓存所有qos > 0 的消息
     */
    private Hashtable<Integer, EtMessage> mPendingMessage = new Hashtable<Integer, EtMessage>();

    private Thread mReceiveThread = null;

    private volatile Socket mSocket;
    private volatile MqttInputStream mInputStream;
    private volatile MqttOutputStream mOutputStream;

    private Timer mConnectTimer;

    public TcpNetworkConnect(SDKContext context, MessageCenter msgCenter, Server svr, ConnectOptions opt) {
        mContext = context;
        mMsgCenter = msgCenter;
        mServer = svr;
        mConnectOptions = opt;

        mSocket = new Socket();

        mSocketConnected = false;
    }

    /*
     *
     * @see com.beidouapp.et.core.INetworkConnect#connect()
     */
    @Override
    public void connect(IActionListener actionListener) {
        if (actionListener == null) {
            throw new NullPointerException("actionListener must not be null");
        }
        mState = State.connecting;
        mConnectActionListener = actionListener;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (mSocket == null) {
                        mSocket = new Socket();
                    }
                    InetSocketAddress socketAddress = new InetSocketAddress(
                            mServer.getIp(), mServer.getPort());
                    LOG.d(TAG, "connecting to " + socketAddress.toString());
                    mSocket.connect(socketAddress,
                            mConnectOptions.getConnectionTimeout() * 1000);
                    if (mSocket.isConnected()) {
                        socketConnected();
                    }
                } catch (IOException e) {
                    LOG.d(TAG, e.getMessage());
                    if (e instanceof ConnectException
                            || e instanceof SocketTimeoutException) {
                        handleConnectResult(false, new ErrorInfo(
                                ErrorCode.CONNECT_SERVER_TIMEOUT));
                    } else {
                        handleConnectResult(false, new ErrorInfo(
                                ErrorCode.IO_EXCEPTION));
                    }
                    mSocketConnected = false;
                }
            }
        }, "TcpNetworkConnect-connect-thread");
        t.start();
    }

    /**
     * 已经与设备Tcp server建立连接，可以收发数据了。
     */
    private void socketConnected() {
        try {
            // 打开I/O流，准备收发数据
            mInputStream = new MqttInputStream(mSocket.getInputStream());
            mOutputStream = new MqttOutputStream(mSocket.getOutputStream());
            mReceiveThread = new Thread(new ReceiverRunnable());
            mReceiveThread.start();
            mSocketConnected = true;
            if (mConnectTimer != null) {
                mConnectTimer.cancel();
            }
            mConnectTimer = new Timer();
            mConnectTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mState == State.connecting) {
                        handleConnectResult(false, new ErrorInfo(
                                ErrorCode.TIMEOUT));
                    }
                }
            }, mConnectOptions.getConnectionTimeout() * 1000);

            // 发送mqtt connect packet
            sendConnectMessage();
        } catch (IOException e) {
            handleConnectResult(false, new ErrorInfo(ErrorCode.IO_EXCEPTION));
        }
    }

    private void sendConnectMessage() throws IOException {
        final String appKey = mContext.getContextParameters().getAppKey();
        final String uid = mContext.getContextParameters().getUid();
        final String clientId = uid + EtConstants.SEPARATOR_AIT
                + SDKContext.PLATFORM_FOR_A_SDK + "|2.0.0.0";
        final int mqttVersion = 4;
        final String username = uid;
        final String password = EncryptUtil.generatedPasswords(appKey, uid);
        final boolean cleanSession = mConnectOptions.getCleanSession();
        final int keepAliveInterval = mConnectOptions.getKeepAliveInterval();

        MqttConnect connectMsg = new MqttConnect(clientId, mqttVersion,
                cleanSession, keepAliveInterval, username,
                password.toCharArray(), null, null);
        mOutputStream.write(connectMsg);
    }

    // 服务器连接成功
    private void serverConnected() {
        startKeepalive();
        // 通知消息中心连接成功
        mMsgCenter.registerNetworkConnect(this);
        mMsgCenter.notifyConnected(mServer);
        // 通知用户连接成功
        handleConnectResult(true, null);
    }

    // 开启心跳
    private void startKeepalive() {
        mPingManager = new PingManager(mContext, mMsgCenter, this);
        mPingManager.setKeepAliveInterval(mConnectOptions
                .getKeepAliveInterval());
        mPingManager.start();
    }

    /**
     * 处理connect的结果
     *
     * @param connected true，连接成功；false，连接失败。
     * @param errorInfo failure infornmation if <code>connected</code> is false, or
     *                  null;
     */
    private synchronized void handleConnectResult(boolean connected,
                                                  ErrorInfo errorInfo) {
        if (mState != State.connecting) {
            return;
        }
        releaseConnectTimer();
        if (connected) {
            mState = State.connected;
            mConnectActionListener.onSuccess();
        } else {
            mState = State.idle;
            mConnectActionListener.onFailure(errorInfo);
        }
    }

    private void releaseConnectTimer() {
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
            mConnectTimer = null;
        }
    }

    /**
     * 发送ping req
     */
    void sendPingReq(IActionListener actionListener) {
        try {
            mOutputStream.write(new MqttPingReq());
            actionListener.onSuccess();
        } catch (IOException e) {
            e.printStackTrace();
            actionListener.onFailure(new ErrorInfo(ErrorCode.IO_EXCEPTION));
        }
    }

    @Override
    public void chatTo(EtMessage msg, IActionListener listener) {
        msg.setQos(0);//内网只支持qos 0
        if (mState != State.connected) {
            LOG.d(TAG, "连接已经断开，不能发送消息");
            listener.onFailure(new ErrorInfo(ErrorCode.SERVER_HAS_DISCONNECTED));
            return;
        }

        try {
            LOG.d(TAG, "to " + msg.getUserId() + ",消息内容：" + HexUtil.printHexString(msg.getPayload()));
            msg.setTopic(TopicTypeEnum.CHAT.getCode() + msg.getUserId());
//            if (msg.getQos() > 0) {
//                // 生成msgId
//                try {
//                    int msgId = getNextMessageId();
//                    msg.setMsgId(msgId);
//                } catch (RuntimeException e) {
//                    e.printStackTrace();
//                    listener.onFailure(new ErrorInfo(ErrorCode.CHAT_TO_FAIL, e.getMessage()));
//                    return;
//                }
//                // 缓存消息回调函数
//                mSendListenerMap.put(Integer.valueOf(msg.getMsgId()), listener);
//                // 缓存消息
//                mPendingMessage.put(Integer.valueOf(msg.getMsgId()), msg);
//            }
            mOutputStream.writeMessage(msg);
            if (msg.getQos() == 0) {
                listener.onSuccess();
                // mMsgCenter.notifyMessageSendSuccess(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.onFailure(new ErrorInfo(ErrorCode.CHAT_TO_FAIL, e
                    .getMessage()));
        }
    }

    @Override
    public void publish(EtMessage msg, IActionListener actionListener) {
        // 内网z暂不支持publish
    }

    @Override
    public void subscribe(EtMessage msg, IActionListener actionListener) {
        // 内网暂不支持subscribe
    }

    @Override
    public void unsubscribe(EtMessage msg, IActionListener actionListener) {
        // 内网暂不支持unsubsribe
    }

    @Override
    public void disconnect(IActionListener actionListener) {
        mDisconnectActionListener = actionListener;

        if (mState != State.connected) {
            mDisconnectActionListener.onFailure(new ErrorInfo(
                    ErrorCode.NONE_SERVER_CONNECTED));
            return;
        }
        mSocketConnected = false;
        mState = State.disconnecting;

        // 停止心跳
        mPingManager.stop();
        // 关闭socket
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null) {
                    try {
                        try {
                            mReceiveThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        mState = State.idle;
                        mSocket = null;
                        mInputStream = null;
                        mOutputStream = null;
                        mDisconnectActionListener.onSuccess();
                    }
                }
            }
        }, "TcpNetworkConnect-disconnect-thread");
        t.start();
    }

    @Override
    public String getIdentifer() {
        String identifer = mServer.getId() + "@" + mServer.getIp() + ":"
                + mServer.getPort();
        String identifer2 = mServer.getId();
        return identifer2;
    }

    @Override
    public Server getSvr() {
        return mServer;
    }

    @Override
    public void reConnect(IActionListener actionListener) {
        //内网不实现重连
    }

    @Override
    public void destory() {
        if (mSocketConnected) {
            disconnect(new IActionListener() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {

                }
            });
        }
        mPendingMessage.clear();
        mInUseMsgIds.clear();
        mSendListenerMap.clear();
    }

    private synchronized int getNextMessageId() throws RuntimeException {
        int startingMessageId = nextMsgId;
        int loopCount = 0;
        do {
            nextMsgId++;
            if (nextMsgId > MAX_MSG_ID) {
                nextMsgId = MIN_MSG_ID;
            }
            if (nextMsgId == startingMessageId) {
                loopCount++;
                if (loopCount == 2) {
                    throw new RuntimeException("NO MESSAGE ID AVAILABLE");
                }
            }
        } while (mInUseMsgIds.containsKey(new Integer(nextMsgId)));
        Integer id = new Integer(nextMsgId);
        mInUseMsgIds.put(id, id);
        return nextMsgId;
    }

    private synchronized void releaseMessageId(int msgId) {
        mInUseMsgIds.remove(new Integer(msgId));
    }

    /**
     * 接收线程
     */
    private class ReceiverRunnable implements Runnable {
        @Override
        public void run() {
            LOG.d(TAG, "接收线程is running...");
            while (mSocketConnected) {
                if (mSocket.isClosed() || mSocket.isInputShutdown()) {
                    mMsgCenter.notifyConnectLost(mServer,
                            ErrorCode.IO_EXCEPTION);
                    return;
                }
                try {
                    int length = mInputStream.available();
                    if (length <= 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            LOG.d(TAG, "sleep interrupted");
                        }
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    LOG.d(TAG, "评估数据长度异常");
                    continue;
                }
                MqttWireMessage message = null;
                try {
                    message = mInputStream.readMqttWireMessage();
                } catch (IOException e) {
                    LOG.d(TAG, "接收消息异常：" + e.getMessage());
                    continue;
                }
                if (message == null) {
                    LOG.d(TAG, "接收到未知消息..................");
                    continue;
                }
                if (message instanceof MqttConnack) {
                    LOG.d(TAG, "接收到conncet ack");
                    serverConnected();
                } else if (message instanceof MqttPingResp) {
                    LOG.d(TAG, "接收到心跳回复");
                    mPingManager.refreshPing();
                } else if (message instanceof MqttPubAck) {
                    LOG.d(TAG, "接收到publish ack");
                    int msgId = message.getMessageId();
                    // LOG.d(TAG, "publish ack msgid:" + msgId);
                    releaseMessageId(msgId);
                    EtMessage pendingMsg = mPendingMessage.get(Integer.valueOf(msgId));
                    mPendingMessage.remove(Integer.valueOf(msgId));
                    if (pendingMsg != null) {
                        mMsgCenter.notifyMessageSendSuccess(pendingMsg);
                    } else {
                        LOG.d(TAG, "不能识别publish ack 返回的消息");
                    }
                    IActionListener listener = mSendListenerMap.get(Integer.valueOf(msgId));
                    if (listener != null) {
                        listener.onSuccess();
                        mSendListenerMap.remove(Integer.valueOf(msgId));
                    }
                } else if (message instanceof MqttPublish) {
                    try {
                        LOG.d(TAG, "new publish message arrived!");
                        mPingManager.refreshPing();
                        int msgId = message.getMessageId();
                        // 内网只支持chatTo消息
                        EtMessage newMsg = new EtMessage();
                        newMsg.setCategory(EtMessage.CHAT_TO);
                        newMsg.setMsgId(msgId);
                        newMsg.setSvrId(getIdentifer());
                        newMsg.setUserId(getIdentifer());
                        newMsg.setPayload(message.getPayload());
                        mMsgCenter.notifyMessageArrived(mServer, newMsg);
                        // TODO 回复publish ack
                    } catch (IOException e) {
                        // 获取payload异常，忽略此条消息。
                    }
                }
            }
            LOG.d(TAG, "接收线程 is over");
        }
    }

}