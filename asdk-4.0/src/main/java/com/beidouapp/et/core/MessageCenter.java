package com.beidouapp.et.core;

import com.beidouapp.et.*;
import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 消息中心，负责分发消息给目标TCP连接，通知各个连接的消息状态
 *
 * @author allen
 */
public class MessageCenter {

    private static final String TAG = MessageCenter.class.getSimpleName();
    private Log LOG = LogFactory.getLog("java");
    /**
     * context,回调所有消息变化情况
     */
    private SDKContext mContext;
    /**
     * 正在连接的网络连接回调
     */
    private Hashtable<String, IActionListener> mPendingConnectListenerMap = new Hashtable<String, IActionListener>();
    /**
     * 正在断开连接的网络连接回调
     */
    private Hashtable<String, IActionListener> mPendingDisconnectListenerMap = new Hashtable<String, IActionListener>();
    /**
     * 当前已经建立的所有连接
     */
    private Hashtable<String, INetworkConnect> mNetworkConnectMap = new Hashtable<String, INetworkConnect>();

    /**
     * FIFO队列，保存所有要发送的消息
     */
    private LinkedBlockingQueue<Object> mSendMessageQueue = new LinkedBlockingQueue<Object>();
    /**
     * FIFO队列，保存所有已经接收到的消息
     */
    private LinkedBlockingQueue<ReceiveMessageEntry> mReceivedMessageQueue = new LinkedBlockingQueue<ReceiveMessageEntry>();
    /**
     * 消息轮询线程，依次发送消息到指定的设备
     */
    private Thread mSendThread = null;
    /**
     * 消息轮询线程，依次接收消息，通知给app
     */
    private Thread mRecieveThread = null;
    /**
     * 消息中心运行标志
     */
    private volatile boolean mRunning = false;
    /**
     * 当前连接sever对象
     */
    private Server mServer = null;
    /**
     * 连接参数
     */
    private ConnectOptions options = null;
    /**
     * 外网连接connect对象
     */
    private MqttNetworkConnect mqttNetworkConnect = null;

    // private Context androidContext;

    public MessageCenter(SDKContext context/*, Context androidContext*/) {
        mContext = context;
       /* this.androidContext = androidContext;*/
    }

    /**
     * 启动消息中心
     */
    public void start() {
        if (!mRunning) {
            mRunning = true;
            mSendThread = new Thread(new SendDispatcherRunnable());
            mSendThread.start();
            LOG.d(TAG, "**** Send thread started!");

            mRecieveThread = new Thread(new ReceiveNotifyRunnable());
            mRecieveThread.start();
            LOG.d(TAG, "++++ Recieve thread started!");
        }
    }

    /**
     * 停止消息中心
     */
    public void stop() {
        if (mRunning) {
            mRunning = false;
        }
    }

    /**
     * 释放资源
     */
    public void destory() {
        mRunning = false;
        Set<Entry<String, INetworkConnect>> connectEntrySet = mNetworkConnectMap
                .entrySet();
        Iterator<Entry<String, INetworkConnect>> iterator = connectEntrySet
                .iterator();
        while (iterator.hasNext()) {
            Entry<String, INetworkConnect> entry = iterator.next();
            INetworkConnect networkConnect = entry.getValue();
            networkConnect.destory();
        }
        mNetworkConnectMap.clear();

        if (mServer != null) {
            mServer = null;
        }
        if (options != null) {
            options = null;
        }
        if (mqttNetworkConnect != null) {
            mqttNetworkConnect = null;
        }
    }

    /**
     * 注册一个已经建立的连接，委托给消息中心管理。
     *
     * @param networkConnect
     */
    public void registerNetworkConnect(INetworkConnect networkConnect) {
        mNetworkConnectMap.put(networkConnect.getIdentifer(), networkConnect);
    }

    /**
     * 注销网络连接，消息中心不在管理该连接
     *
     * @param networkConnect
     */
    public void unregisterNetworkConnect(INetworkConnect networkConnect) {
        mNetworkConnectMap.remove(networkConnect.getIdentifer());
    }

    /**
     * 连接到设备/服务器
     *
     * @param opt
     * @param actionListener
     */
    public void connect2Svr(final Server svr, final ConnectOptions opt,
                            final IActionListener actionListener) {
        if (svr == null || opt == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.NULL_PARAMETER));
            return;
        }
        // 判断要连接的设备是否已经连接好，或者正在连接中
        if (mNetworkConnectMap.containsKey(svr.getId())) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.SERVER_HAS_CONNECTED));
            return;
        }
        if (mPendingConnectListenerMap.containsKey(svr.getId())) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.CONNECTING));
            return;
        }
        mPendingConnectListenerMap.put(svr.getId(), actionListener);

        // 新建连接
        int type = svr.getType();
        if (type == Server.TYPE_LAN) { // 内网
            INetworkConnect tcpNetworkConnect = new TcpNetworkConnect(mContext,
                    this, svr, opt);
            tcpNetworkConnect.connect(new IActionListener() {
                @Override
                public void onSuccess() {
                    actionListener.onSuccess();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    mPendingConnectListenerMap.remove(svr.getId());
                    actionListener.onFailure(errorInfo);
                }
            });
        } else if (type == Server.TYPE_WAN) { // 外网
            mqttNetworkConnect = new MqttNetworkConnect(
                    mContext, /*androidContext,*/ this, svr, opt);
            mqttNetworkConnect.connect(new IActionListener() {

                @Override
                public void onSuccess() {
                    actionListener.onSuccess();
                    mServer = svr;
                    options = opt;
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    mPendingConnectListenerMap.remove(svr.getId());
                    actionListener.onFailure(errorInfo);
                }
            });
        }
    }

    /**
     * 与服务器断开连接
     *
     * @param svr
     * @param actionListener
     */
    public void disconnectFromSvr(final Server svr, final IActionListener actionListener) {
        if (svr == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.NULL_PARAMETER));
            return;
        }

        // 判断要连接的设备是否已经连接好，或者正在连接中
        if (!mNetworkConnectMap.containsKey(svr.getId())) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
            return;
        }

        if (mPendingDisconnectListenerMap.containsKey(svr.getId())) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.DISCONNECTING));
            return;
        }

        mPendingDisconnectListenerMap.put(svr.getId(), actionListener);

        final INetworkConnect networkConnect = mNetworkConnectMap.get(svr
                .getId());
        if (networkConnect != null) {
            networkConnect.disconnect(new IActionListener() {

                @Override
                public void onSuccess() {
                    mNetworkConnectMap.remove(svr.getId());
                    mPendingDisconnectListenerMap.remove(svr.getId());
                    // XXX:mqtt不销毁是否存在问题
                    if (networkConnect instanceof TcpNetworkConnect) {
                        networkConnect.destory();
                    }
                    actionListener.onSuccess();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    LOG.d(TAG, svr.getId() + " 断开连接失败:" + errorInfo.getReason());
                    mNetworkConnectMap.remove(svr.getId());
                    mPendingDisconnectListenerMap.remove(svr.getId());
                    // XXX:mqtt不销毁是否存在问题
                    if (networkConnect instanceof TcpNetworkConnect) {
                        networkConnect.destory();
                    }
                    actionListener.onFailure(errorInfo);
                }
            });
        }
    }

    public void peerState(String uid, StatusListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            mqttConnect.peerState(uid, listener);
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     * @param listener
     */
    public void chatTo(final EtMessage msg, final IActionListener listener) {
        if (msg == null) {
            listener.onFailure(new ErrorInfo(ErrorCode.NULL_PARAMETER));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1.优先查找内网连接是否存在，如果存在，则发送消息；(内网优先发送)
                // 2.如果内网不存在，那么判断外网连接是否存在，如果存在，则发送；不存在，那么发送失败。
                INetworkConnect networkConnect = mNetworkConnectMap.get(msg
                        .getSvrId());
                if (networkConnect != null
                        && networkConnect instanceof TcpNetworkConnect) {// 内网
                    SendMessageEntry entry = new SendMessageEntry(msg, listener);
                    mSendMessageQueue.add(entry);
                } else {// 外网
                    INetworkConnect mqttConnect = mNetworkConnectMap
                            .get(Server.PROXY_SERVER_ID);
                    if (mqttConnect != null) {
                        msg.setSvrId(Server.PROXY_SERVER_ID);// 更改为外网服务器
                        SendMessageEntry entry = new SendMessageEntry(msg,
                                listener);
                        mSendMessageQueue.add(entry);
                    } else {
                        listener.onFailure(new ErrorInfo(
                                ErrorCode.NONE_SERVER_CONNECTED));
                    }
                }
            }
        }).start();
    }

    // 目前只支持外网，如果是内网的publish消息，也转到外网
    public void publish(EtMessage msg, IActionListener listener) {
        // XXX:增加pulish的消息队列
        INetworkConnect mqttConnect = mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            msg.setSvrId(Server.PROXY_SERVER_ID);// 更改为外网服务器
            mqttConnect.publish(msg, listener);
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    // 目前只支持外网，如果是内网的subscribe消息，也转到外网
    public void subscribe(EtMessage msg, IActionListener listener) {
        INetworkConnect mqttConnect = mNetworkConnectMap.get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            msg.setSvrId(Server.PROXY_SERVER_ID);// 更改为外网服务器
            mqttConnect.subscribe(msg, listener);
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    // 目前只支持外网，如果是内网的unsubscribe消息，也转到外网
    public void unsubscribe(EtMessage msg, IActionListener listener) {
        // XXX:增加unsubscribe的消息队列
        INetworkConnect mqttConnect = mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            msg.setSvrId(Server.PROXY_SERVER_ID);// 更改为外网服务器
            mqttConnect.unsubscribe(msg, listener);
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void stateSubscribe(String uid, IActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        mqttConnect.stateSubscribe(uid, listener);
    }

    public void stateUnsubscribe(String uid, IActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap.get(Server.PROXY_SERVER_ID);
        mqttConnect.stateUnsubscribe(uid, listener);
    }

    public void requestOfflineMessage() {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        mqttConnect.requestOfflineMessage();
    }


    // 目前只支持外网，如果是内网也转到外网
    public void fileTo(String receiverId, String fileFullName, String desc,
                       final FileCallBack callBack) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            mqttConnect.fileTo(receiverId, fileFullName, desc, callBack);
        } else {
            callBack.onFailure(fileFullName, new Throwable(
                    "none server connected"));
        }
    }

    // 目前只支持外网，如果是内网也转到外网
    public void downloadFile(DocumentInfo documentInfo, String saveFilePath,
                             final FileCallBack callBack) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            mqttConnect.downloadFile(documentInfo, saveFilePath, callBack);
        } else {
            callBack.onFailure(saveFilePath, new Throwable(
                    "none server connected"));
        }
    }

    public void addBuddy(String friendId, IFriendsActionListener listener, boolean notify) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.addBuddy(friendId, listener, notify);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }

        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void removeBuddy(String friendId, IFriendsActionListener listener, boolean notify) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.removeBuddy(friendId, listener, notify);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void getAllBuddies(IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.getAllBuddies(listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void createGroup(String groupname, List<String> userIdList, IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.createGroup(groupname, userIdList, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void getAllGroups(IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.getAllGroups(listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }

        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void exitGroup(String groupId, IActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.exitGroup(groupId, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.EXIT_GROUP_FAIL,
                    "server is not connect,please check"));
        }
    }

    public void dismissGroup(String groupId, IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.dismissGroup(groupId, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }

        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void addGroupMember(String groupId, List<String> userlists,
                               IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.addGroupMember(groupId, userlists, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }

        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void removeGroupMember(String groupId, List<String> userlists,
                                  IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.removeGroupMember(groupId, userlists, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void getAllGroupMembers(String groupId, IFriendsActionListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            try {
                mqttConnect.getAllGroupMembers(groupId, listener);
            } catch (Exception e) {
                listener.onFailure(new ErrorInfo(ErrorCode.CONNECTION_LOST));
            }

        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.NONE_SERVER_CONNECTED));
        }
    }

    public void getIlinkTime(TimeListener listener) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            mqttConnect.getIlinkTime(listener);
        }
    }

    /**
     * 通知收到消息
     *
     * @param arrivedMsg
     */
    public synchronized void notifyMessageArrived(Server svr, EtMessage arrivedMsg) {
        if (arrivedMsg == null) {
            return;
        }
        mReceivedMessageQueue.add(new ReceiveMessageEntry(svr, arrivedMsg));
    }

    /**
     * 通知<code>context</code>消息发送成功
     *
     * @param msg
     */
    public void notifyMessageSendSuccess(EtMessage msg) {
        mContext.onMsgSendSuccess(msg);
    }

    public void notifyPeerState(String uid, String statusCode) {
        mContext.onPeerState(uid, statusCode);
    }

    public void notifyFileArrived(String senderId, DocumentInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        mContext.onFileArrived(senderId, fileInfo);
    }

    /**
     * 通知<code>context</code>，与svr异常断开连接
     */
    public void notifyConnectLost(final Server svr, final int errorCode) {
        LOG.d(TAG,
                svr.getId() + " 连接丢失 >> " + ErrorCode.getErrorReason(errorCode));
        INetworkConnect networkConenct = mNetworkConnectMap.get(svr.getId());
        if (networkConenct != null) {
            // networkConenct.destory();
            mNetworkConnectMap.remove(svr.getId());
        }

        mContext.onConnectLost(svr, errorCode);
    }

    /**
     * 通知<code>context</code>，与svr建立连接成功
     */
    public void notifyConnected(Server svr) {
        LOG.d(TAG, "============= " + svr.getId() + " 已经连接！");
        mPendingConnectListenerMap.remove(svr.getId());

        // 在第一个svr连接成功后，启动消息中心
        // XXX：是否应该在实例化消息中心时就启动？
        start();
    }

    /**
     * 把发送消息分发给对应的svr连接
     */
    private class SendDispatcherRunnable implements Runnable {

        @Override
        public void run() {
            while (mRunning) {
                try {
                    SendMessageEntry entry = (SendMessageEntry) mSendMessageQueue
                            .take();
                    if (entry != null) {
                        EtMessage msg = entry.message;
                        INetworkConnect networkConnect = mNetworkConnectMap
                                .get(msg.getSvrId());
                        if (networkConnect != null) {
                            LOG.d(TAG, String.format(
                                    "发送消息 to [%s] through [%s]：%s",
                                    msg.getUserId(), msg.getSvrId(),
                                    new String(msg.getPayload())));
                            networkConnect.chatTo(msg, entry.listener);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOG.d(TAG, "---- send thread is over!");
        }
    }

    /**
     * 把已经收到的消息通知到<code>context</code>
     */
    private class ReceiveNotifyRunnable implements Runnable {
        @Override
        public void run() {
            while (mRunning) {
                ReceiveMessageEntry msg = null;
                try {
                    msg = (ReceiveMessageEntry) mReceivedMessageQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    EtMessage etMsg = msg.message;
                    mContext.onMessageArrived(msg.peer, etMsg);
                }
            }
            LOG.d(TAG, "---- revieve thread is over!");
        }
    }

    /**
     * 发送消息entry
     */
    private class SendMessageEntry {
        public EtMessage message;
        public IActionListener listener;

        SendMessageEntry(EtMessage message, IActionListener listener) {
            this.message = message;
            this.listener = listener;
        }
    }

    /**
     * 接收到的消息entry
     */
    private class ReceiveMessageEntry {
        public Server peer;
        public EtMessage message;

        ReceiveMessageEntry(Server p, EtMessage m) {
            peer = p;
            message = m;
        }
    }

    /**
     * 上传文件
     *
     * @param fullPath
     * @param callBack
     */
    public void uploadFile(String fullPath, FileCallBack callBack) {
        MqttNetworkConnect mqttConnect = (MqttNetworkConnect) mNetworkConnectMap
                .get(Server.PROXY_SERVER_ID);
        if (mqttConnect != null) {
            mqttConnect.uploadFile(fullPath, callBack);
        } else {
            callBack.onFailure(fullPath, new Throwable(
                    "none server connected"));
        }
    }


    public void reConnectSvr(final IActionListener actionListener) {
        if (mServer == null || options == null || mqttNetworkConnect == null) {
            actionListener.onFailure(new ErrorInfo(10010, "has not connect the server before"));
            return;
        }
        mqttNetworkConnect.reConnect(new IActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                actionListener.onSuccess();
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                // TODO Auto-generated method stub
                actionListener.onFailure(errorInfo);
            }
        });
    }

}