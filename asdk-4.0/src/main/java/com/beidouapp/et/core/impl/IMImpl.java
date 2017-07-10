package com.beidouapp.et.core.impl;

import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.api.IM;
import com.beidouapp.et.client.callback.*;
import com.beidouapp.et.client.domain.EtMsg;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.common.enums.ConnectStateEnum;
import com.beidouapp.et.core.EtCallbackConnection;
import com.beidouapp.et.core.ReceiveHandler;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtExceptionWrapUtil;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.ConnectionUtil;
import com.beidouapp.et.util.LogFileUtil;
import com.beidouapp.et.util.codec.ReflectUtil;
import com.beidouapp.et.util.param.CheckingUtil;
import com.beidouapp.et.util.param.QosUtil;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.internal.DispatcherConfig;
import org.fusesource.hawtdispatch.internal.HawtDispatcher;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * IM 默认实现类.
 *
 * @author mhuang.
 */
public class IMImpl implements IM {
    public static final Logger logger = LoggerFactory.getLogger(IMImpl.class);
    public static final String EXCEPTION_INFO = " is null";

    private volatile EtCallbackConnection connection;
    private volatile byte connectStatus = ConnectStateEnum.DISCONNECTED.getCode();
    private IReceiveListener receiveListener;

    private Listener listener;
    private ICallback<Void> connectCallback; // 用户的真实连接回调.
    private Callback<Void> _connectCallback; // SDK内部连接回调.
    private IContext etContext;
    private Map<String, IListener> listenerMap = new HashMap<String, IListener>();

    public IMImpl(IContext etContext) {
        logger.debug("creating IM...");
        setConnectionStatus(DISCONNECTED);
        this.etContext = etContext;
        connection = ConnectionUtil.getConnection(this.etContext);
    }

    @Override
    public void setReceiveListener(IReceiveListener receiveListener) {
        this.receiveListener = receiveListener;
        listenerMap.put(EtKeyConstant.LISTENER_MASTER, this.receiveListener); // 注册监听器
        this.listener = initMessageListener();
        connection.listener(this.listener);
    }

    @Override
    public void setConnectCallBack(ICallback<Void> callback) {
        this.connectCallback = callback;
        this._connectCallback = new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                setConnectionStatus(CONNECTED);
                logger.debug(" User {} connect im server success.",
                        etContext.getUserName());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        connectCallback.onSuccess(null);
                    }
                }).start();
            }

            @Override
            public void onFailure(final Throwable e) {
                setConnectionStatus(DISCONNECTED);
                String us = etContext.getUserName();
                logger.error("User {} connect im server failed. cause {}", us,
                        e);
                final EtRuntimeException ex = EtExceptionWrapUtil.getWrapExcetpion(e);
                if (ex.getLocalizedMessage().equalsIgnoreCase("Peer disconnected")) {//解决服务器拒绝会再次回调,此时不再往上抛出
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectCallback != null) {
                            connectCallback.onFailure(null, ex);
                        }
                    }
                }).start();
            }
        };
    }

    // ==================================================================================================================

    @Override
    public void setFileReceiveListener(IFileReceiveListener fileListener) {
        listenerMap.put(EtKeyConstant.LISTENER_FILE, fileListener);
    }

    @Override
    public void setUserStatusListener(IUserStatusListener userStatusListener) {
        listenerMap.put(EtKeyConstant.LISTENER_USER_STATUS, userStatusListener);
    }

    @Override
    public void connect() {
        if (isConnected() || isConnecting() || isDisconnecting()) {
            logger.info("current status is {}({}), that is do nothing",
                    ConnectStateEnum.getEnumByCode(getConnectionStatus())
                            .getName(), getConnectionStatus());
            return;
        }
        if (this.receiveListener == null) {
            connectCallback.onFailure(null, new EtRuntimeException(
                    EtExceptionCode.SYSTEM_INIT_EXCEPTION,
                    "please set msg receive Listener!"));
            return;
        }
        if (connection == null) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.SYSTEM_INIT_EXCEPTION,
                    "Please initialization.");
            logger.error("client connection uninitialized!", e);
            throw e;
        }
        setConnectionStatus(CONNECTING);
        HawtDispatcher hd = DispatcherConfig.getDefaultDispatcher();
        Object object = ReflectUtil.getFieldValue(hd, "shutdownState");
        if (Integer.valueOf(object.toString()) != 0) {
            hd.restart();
        }
        connection.connect(this._connectCallback);
    }

    @Override
    public synchronized void reconnect() {
        if (connection == null) {
            connectCallback.onFailure(null, new EtRuntimeException(EtExceptionCode.SYSTEM_INIT_EXCEPTION, "please initinal and connect server!"));
            return;
        } else {
            if (isConnecting() || isConnected() || isDisconnecting()) {
                logger.info("current connection status is " + ConnectStateEnum.getEnumByCode(connectStatus).getName());
                return;
            } else {
                connection = ConnectionUtil.getConnection(this.etContext);
                connection.listener(this.listener);
                connection.connect(this._connectCallback);
            }
        }
    }

    /**
     * 初始化消息监听
     *
     * @return
     */
    private Listener initMessageListener() {
        return new Listener() {
            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
                ack.run();
                String sTopic = topic.utf8().toString();
                byte[] payload = body.utf8().toByteArray();
                if (TopicTypeEnum.SYS_KICK.getCode().equalsIgnoreCase(sTopic)) {
                    String content = "";
                    try {
                        content = new String(payload, EtConstants.CHARSETS_UTF8);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    logger.warn("user is kicked. because " + content);
                    setConnectionStatus(DISCONNECTED); // 设置为未连接状态,并断开连接(不再自动重连).
                    // 主动断开连接.
                    if (connection == null) {
                        logger.warn("kick user, connect object is not instanse.");
                        return;
                    }
                    final EtRuntimeException e = new EtRuntimeException(EtExceptionCode.SYS_KICK, "userId " + etContext.getUserName() + " is kick." + content);
                    connection.kill(null);
                    setConnectionStatus(DISCONNECTED);
                    receiveListener.connectionLost(e);
                    return;
                }
                ReceiveHandler.processInnerTopic(etContext, sTopic, payload, listenerMap);
            }

            @Override
            public void onFailure(Throwable value) {
                EtRuntimeException e = EtExceptionWrapUtil.getWrapExcetpion(value);
                logger.error("IM Listener onFailure", value);
                connection.kill(null);
                setConnectionStatus(DISCONNECTED);
                if (e.getReasonCode() == EtExceptionCode.SYSTEM_UNKNOWN_EXCEPTION) {
                    receiveListener.connectionLost(e);
                    return;
                }
                if (value instanceof SocketException) {
                    receiveListener.connectionLost(e);
                    return;
                }
                if (value instanceof IOException) {
                    if (value.getLocalizedMessage().contains("Peer disconnected")) {
                        e = new EtRuntimeException(EtExceptionCode.SERVER_PEER_DISCONNECTED, "peer disconnected");
                        receiveListener.connectionLost(e);
                    } else {
                        e = new EtRuntimeException(EtExceptionCode.IO_NET_EXCEPTION, "Net or IO exception");
                        receiveListener.connectionLost(e);
                    }
                    return;
                }
                connectCallback.onFailure(null, e);
            }

            @Override
            public void onDisconnected() {//主动断开连接，或IM挂掉
                logger.debug("IM Listener onDisconnected");
                setConnectionStatus(DISCONNECTED);
            }

            @Override
            public void onConnected() {
                logger.debug("im onConnected.");
                setConnectionStatus(CONNECTED);
            }
        };
    }

    @Override
    public void publish(final String topic, final String content, String msgId,
                        final ICallback<EtMsg> callback) {
        this.publish(topic, content, etContext.getDefaultQos(), msgId, callback);
    }

    @Override
    public void publish(final String topic, final byte[] content, final int qos, final String msgId, final ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(callback, "callback" + EXCEPTION_INFO);
        final EtMsg msg = new EtMsg();
        msg.setTopic(topic).setMsgId(msgId).setQos(qos).setSendUserId(etContext.getUserName());
        try {
            CheckingUtil.checkNullOrEmpty(topic, "topic" + EXCEPTION_INFO);
            CheckingUtil.checkNull(content, "content" + EXCEPTION_INFO);
            if (content.length > EtConstants.DEFAULT_MSG_MAX_INFLIGHT_LENGTH) {// 长度大于64K不发到服务器
                throw new EtRuntimeException(EtExceptionCode.CONTENT_IS_TOO_LONG, "content length is too long");
            }
            msg.setPayload(content);
            // checkConnectStatus(msg, callback);
            if (connection == null) {
                EtRuntimeException e = new EtRuntimeException(
                        EtExceptionCode.SYSTEM_INIT_EXCEPTION,
                        "please first init system.");
                callback.onFailure(msg, e);
                return;
            }
            connection.getDispatchQueue().execute(new Runnable() {
                public void run() {
                    connection.publish(topic, content, QosUtil.getQos(qos),
                            etContext.getDefaultRetain(), new Callback<Void>() {
                                @Override
                                public void onSuccess(final Void value) {
                                    logger.debug("send msg topic {}, content {}, qos {}, msgId {} is succeed!", topic, content, qos, msgId);
                                    callback.onSuccess(msg);
                                }

                                @Override
                                public void onFailure(final Throwable value) {
                                    logger.error(
                                            "publish topic {}, content {}, qos {}, msgId {} is failed!. because {}",
                                            topic, content, qos, msgId, value);
                                    callback.onFailure(
                                            msg,
                                            new EtRuntimeException(
                                                    EtExceptionCode.PUBLISH_FAIL,
                                                    "publish " + topic
                                                            + " is failed!",
                                                    value));

                                }
                            });
                }
            });
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("publish " + topic + "is failed :" + e.getMessage());
            logger.error("publish topic {}, qos {}, msgId {} is failed! errCode= {}. because {}", topic, qos, msgId, EtExceptionCode.PUBLISH_FAIL, e);
            callback.onFailure(msg, new EtRuntimeException(EtExceptionCode.PUBLISH_FAIL, "publish " + topic + " is failed!", e));
        }
    }

    @Override
    public void publish(final String topic, final String content, final int qos, final String msgId, final ICallback<EtMsg> callback) {
        if (content == null) {
            final EtMsg msg = new EtMsg().setTopic(topic).setMsgId(msgId)
                    .setQos(qos);
            callback.onFailure(msg, new EtRuntimeException(
                    EtExceptionCode.PARAM_NULL_OR_EMPTY, "publish " + topic
                    + " is failed!"));
            return;
        }
        byte[] tempArr = new byte[0];
        try {
            tempArr = content.getBytes(EtConstants.CHARSETS_UTF8);
        } catch (Exception e) {
            final EtMsg msg = new EtMsg().setTopic(topic).setMsgId(msgId)
                    .setQos(qos);
            callback.onFailure(
                    msg,
                    new EtRuntimeException(
                            EtExceptionCode.CHARACTER_SET_INCORRECT, e
                            .getLocalizedMessage()));
        }
        this.publish(topic, tempArr, qos, msgId, callback);
    }

    @Override
    public void subscribe(String topic, ICallback<Void> callback) {
        this.subscribe(topic, etContext.getDefaultQos(), callback);
    }

    @Override
    public void subscribe(final String topic, final int qos, final ICallback<Void> callback) {
        CheckingUtil.checkNullOrEmpty(topic, "callback" + EXCEPTION_INFO);
        try {
            final Topic[] topics = {new Topic(topic, QosUtil.getQos(qos))};
            checkConnectStatus(callback);
            connection.getDispatchQueue().execute(new Runnable() {
                public void run() {
                    connection.subscribe(topics, new Callback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] value) {
                            if ((value[0] & 0x80) == 0x80) {
                                callback.onFailure(null, new Throwable("subscribe faild"));
                            } else {
                                callback.onSuccess(null);
                                logger.info("subscribe topic {}, qos {} is succeed!", topic, qos);
                            }
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            logger.error("subscribe topic {} , qos {}  is failed! errCode= {} . because {}",
                                    topic, qos, EtExceptionCode.SUBSCRIBE_FAIL,
                                    "subscribe topic " + topic + " is failed!",
                                    value);
                            callback.onFailure(null, value);
                        }
                    });
                }
            });
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("subscribe " + topic + "is failed :" + e.getMessage());
            logger.error("subscribe topic {}, qos {} is failed! errCode={}. {}", topic, qos, EtExceptionCode.SUBSCRIBE_FAIL, e);
            callback.onFailure(null, new EtRuntimeException(EtExceptionCode.SUBSCRIBE_FAIL, "subscribe fail!", e));
        }
    }

    @Override
    public void peerState(String userId, String msgId, final ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(callback, "callback" + EXCEPTION_INFO);
        try {
            CheckingUtil.checkNullOrEmpty(userId, "userId is null!");
            final String topic = TopicTypeEnum.STATUS.getCode() + userId;
            // publish内部使用了默认队列,此处不需要再增加.
            this.publish(topic, userId, etContext.getDefaultQos(), msgId, callback);
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("peerState " + userId + "is failed :" + e.getMessage());
            logger.error(
                    "peerState userid {}, qos {} ,msgId {} is fail! errCode={}. {}",
                    userId, etContext.getDefaultQos(), msgId,
                    EtExceptionCode.PEERSTATE_FAIL, e);
            throw new EtRuntimeException(EtExceptionCode.PEERSTATE_FAIL,
                    "peerState " + userId + " status is failed!", e);
        }

    }

    @Override
    public void unsubscribe(final String topic, final ICallback<Void> callback) {
        CheckingUtil.checkNullOrEmpty(topic, "topic is null!");
        checkConnectStatus(callback);
        try {
            connection.getDispatchQueue().execute(new Runnable() {
                public void run() {
                    connection.unsubscribe(new UTF8Buffer[]{new UTF8Buffer(
                            topic)}, new Callback<Void>() {
                        @Override
                        public void onSuccess(Void value) {
                            if (callback != null) {
                                callback.onSuccess(value);
                            }
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            if (callback != null) {
                                callback.onFailure(null, value);
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            LogFileUtil.writeErrorLog("unsubscribe " + topic + "is failed :" + e.getMessage());
            logger.error("unsubscribe topic {} is fail! errCode= {} . {}", topic, EtExceptionCode.UNSUBSCRIBE_FAIL, e);
            throw new EtRuntimeException(EtExceptionCode.UNSUBSCRIBE_FAIL, "unsubscribe fail!", e);
        }
    }

    public void chatTo(final String userId, final byte[] content, final int qos, final String msgId, ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(callback, "callback" + EXCEPTION_INFO);
        CheckingUtil.checkNullOrEmpty(userId, "userId" + EXCEPTION_INFO);
        CheckingUtil.checkArgument(!(etContext.getUserName().equals(userId)), EtConstants.SENT_TO_YOURSELF_EXCEPTION);
        try {
            // publish内部已经使用了队列.
            this.publish(TopicTypeEnum.CHAT.getCode() + userId, content, etContext.getDefaultQos(), msgId, callback);
        } catch (Exception e) {
            logger.error(
                    "chatTo userId {} , content {} , qos {} , retain {}  is fail! errCode= {} . {}",
                    userId, content, etContext.getDefaultQos(),
                    etContext.getDefaultRetain(), EtExceptionCode.CHAT_TO_FAIL,
                    e);
            throw new EtRuntimeException(EtExceptionCode.CHAT_TO_FAIL,
                    "chatTo " + userId + " msg is fail!", e);
        }
    }

    @Override
    public void chatTo(final String userId, final String content, final String msgId, final ICallback<EtMsg> callback) {
        int qos = etContext.getDefaultQos();
        if (content == null) {
            final EtMsg msg = new EtMsg().setTopic(userId).setMsgId(msgId).setQos(qos);
            callback.onFailure(msg, new EtRuntimeException(EtExceptionCode.PARAM_NULL_OR_EMPTY, "chatTo " + userId + " is failed!"));
            return;
        }
        byte[] tempArr = new byte[0];
        try {
            tempArr = content.getBytes(EtConstants.CHARSETS_UTF8);
        } catch (Exception e) {
            final EtMsg msg = new EtMsg().setTopic(userId).setMsgId(msgId).setQos(qos);
            callback.onFailure(msg, new EtRuntimeException(EtExceptionCode.CHARACTER_SET_INCORRECT, e.getLocalizedMessage()));
        }
        this.chatTo(userId, tempArr, qos, msgId, callback);
    }

    @Override
    public void chatToJson(String userId, String content, int qos, String msgId, ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(content, "content" + EXCEPTION_INFO);
        byte[] tempArr = new byte[0];
        try {
            tempArr = content.getBytes(EtConstants.CHARSETS_UTF8);
        } catch (Exception e) {
            final EtMsg msg = new EtMsg().setTopic(TopicTypeEnum.CHAT_EX.getCode() + userId).setMsgId(msgId).setQos(qos);
            callback.onFailure(msg, new EtRuntimeException(EtExceptionCode.CHARACTER_SET_INCORRECT, e.getLocalizedMessage()));
        }
        this.chatToJson(userId, tempArr, qos, msgId, callback);
    }

    @Override
    public void chatToJson(String userId, byte[] content, int qos,
                           String msgId, ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(callback, "callback" + EXCEPTION_INFO);
        CheckingUtil.checkNullOrEmpty(userId, "userId" + EXCEPTION_INFO);
        CheckingUtil.checkArgument(!(etContext.getUserName().equals(userId)),
                EtConstants.SENT_TO_YOURSELF_EXCEPTION);
        try {
            // publish内部已经使用了队列.
            this.publish(TopicTypeEnum.CHAT_EX.getCode() + userId, content,
                    qos, msgId, callback);
        } catch (Exception e) {
            logger.error(
                    "chatTo userId {} , content {} , qos {} , retain {}  is fail! errCode= {} . {}",
                    userId, content, etContext.getDefaultQos(),
                    etContext.getDefaultRetain(), EtExceptionCode.CHAT_TO_FAIL,
                    e);
            throw new EtRuntimeException(EtExceptionCode.CHAT_TO_FAIL,
                    "chatTo " + userId + " msg is fail!", e);
        }
    }

    @Override
    public void requestOfflineMessage() {
        this.publish(TopicTypeEnum.OFFLINEMSG.getCode(), "", "",
                new ICallback<EtMsg>() {
                    @Override
                    public void onSuccess(EtMsg value) {
                        logger.debug("send offline msg publish success!");
                    }

                    @Override
                    public void onFailure(EtMsg t, Throwable value) {
                        logger.error("send offline msg publish failure!", value);
                    }
                });
    }

    @Override
    public void getIlinkTime() {
        this.publish(TopicTypeEnum.SERVER_DATETIME.getCode(), "", "",
                new ICallback<EtMsg>() {
                    @Override
                    public void onSuccess(EtMsg value) {
                        logger.debug("send server datetime msg publish success!");
                    }

                    @Override
                    public void onFailure(EtMsg t, Throwable value) {
                        logger.error("send server datetime msg publish failure!", value);
                    }
                });
    }

    @Override
    public void stateSubscribe(String userId, int qos, String msgId,
                               final ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(userId, "userId" + EXCEPTION_INFO);
        CheckingUtil.checkArgument(!(etContext.getUserName().equals(userId)),
                EtConstants.SENT_TO_YOURSELF_EXCEPTION);
        this.publish(TopicTypeEnum.STATE_SUB.getCode() + userId, "", qos,
                msgId, new ICallback<EtMsg>() {
                    @Override
                    public void onSuccess(EtMsg value) {
                        if (callback == null) {
                            return;
                        }
                        callback.onSuccess(value);
                    }

                    @Override
                    public void onFailure(EtMsg t, Throwable value) {
                        if (callback == null) {
                            return;
                        }
                        callback.onFailure(t, value);
                    }
                });
    }

    @Override
    public void stateUnsubscribe(String userId, int qos, String msgId,
                                 final ICallback<EtMsg> callback) {
        CheckingUtil.checkNull(userId, "userId" + EXCEPTION_INFO);
        CheckingUtil.checkArgument(!(etContext.getUserName().equals(userId)),
                EtConstants.SENT_TO_YOURSELF_EXCEPTION);
        this.publish(TopicTypeEnum.STATE_UNSUB.getCode() + userId, "", qos,
                msgId, new ICallback<EtMsg>() {
                    @Override
                    public void onSuccess(EtMsg value) {
                        if (callback == null) {
                            return;
                        }
                        callback.onSuccess(value);
                    }

                    @Override
                    public void onFailure(EtMsg t, Throwable value) {
                        if (callback == null) {
                            return;
                        }
                        callback.onFailure(t, value);
                    }
                });
    }

    @Override
    public void chatToJson(String userId, byte[] content, String msgId, ICallback<EtMsg> callback) {
        this.chatToJson(userId, content, etContext.getDefaultQos(), msgId, callback);
    }

    @Override
    public void disconnect(final ICallback<Void> callback) {
        CheckingUtil.checkNull(callback, "callback" + EXCEPTION_INFO);
        try {
            checkConnectStatus(callback);
            setConnectionStatus(DISCONNECTING);
            connection.disconnect(new Callback<Void>() {
                @Override
                public void onSuccess(Void value) {
                    logger.debug("disconnect succeed!");
                    setConnectionStatus(DISCONNECTED);
                    connection.disconnect(new Callback<Void>() {
                        @Override
                        public void onSuccess(Void value) {
                            callback.onSuccess(value);
                        }

                        @Override
                        public void onFailure(Throwable value) {
                            // 忽略.永远不可能发生.
                        }
                    });
                }

                @Override
                public void onFailure(Throwable value) {
                    logger.error("disconnect failed!", value);
                    callback.onFailure(null, value);
                }
            });
        } catch (Exception e) {
            String clientId = etContext.getUserName();
            LogFileUtil.writeErrorLog(clientId + " disconnect failed " + e);
            logger.error(
                    "current user Id {} disconnect is failed! errCode={}. because {}",
                    clientId, EtExceptionCode.DISCONNECT, e);
            callback.onFailure(null, new EtRuntimeException(
                    EtExceptionCode.DISCONNECT, "current user " + clientId
                    + " disconnect is failed!", e));
        }
    }

    @Override
    public void destroy() {
        logger.debug("destroy IM");
        if (connection == null) {
            logger.warn("connection is null, destroy is do nothing.");
            return;
        }
        if (connection.transport() != null) {
            final CountDownLatch destroyCount = new CountDownLatch(1);
            connection.kill(new Callback<Void>() {
                @Override
                public void onSuccess(Void value) {
                    logger.debug("kill IM successed.");
                    connection = null;
                    // etContext.remove(ModuleType.WEB.getCode());
                    etContext.remove(EtKeyConstant.CONTEXT);
                    destroyCount.countDown();
                }

                @Override
                public void onFailure(Throwable value) {
                    logger.debug("destroy IM failed because={}.", value);
                }
            });
            try {
                destroyCount.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e1) {
            }
            DispatcherConfig.getDefaultDispatcher().shutdown();
            logger.info("destroy DispatcherConfig done.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        } else {

            logger.warn("im is not connected , destroy is do nothing.");
            DispatcherConfig.getDefaultDispatcher().shutdown();
            logger.info("destroy DispatcherConfig done.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

    }

    public synchronized byte getConnectionStatus() {
        return connectStatus;
    }

    private synchronized void setConnectionStatus(byte status) {
        // if (this.connectStatus == status) {
        // logger.debug("The same connection state, it does not need to be changed.");
        // return;
        // }
        StringBuilder sb = new StringBuilder(25);
        sb.append(ConnectStateEnum.getEnumByCode(this.connectStatus).getName())
                .append("(").append(this.connectStatus).append(")")
                .append(" changed to ");
        this.connectStatus = status;
        sb.append(ConnectStateEnum.getEnumByCode(this.connectStatus).getName())
                .append("(").append(this.connectStatus).append(")");
        logger.debug("connect status is {}", sb.toString());
    }

    @Override
    public IContext getETContext() {
        return this.etContext;
    }

    private void checkConnectStatus(ICallback<Void> callback) {
        if (connection == null) {
            callback.onFailure(null, new EtRuntimeException(
                    EtExceptionCode.OBJECT_NOT_INSTANTIATED,
                    "Connect Object is not instantiated"));
        }
        if (isDisconnected() || isDisconnecting()) {
            callback.onFailure(
                    null,
                    new EtRuntimeException(
                            EtExceptionCode.CONNACK_OFFLINE,
                            "User "
                                    + etContext.getUserName()
                                    + " status is offline! please reconnect online."));
        }
    }

    @Override
    public synchronized boolean isConnected() {
        return getConnectionStatus() == CONNECTED;
    }

    @Override
    public synchronized boolean isConnecting() {
        return getConnectionStatus() == CONNECTING;
    }

    @Override
    public synchronized boolean isDisconnected() {
        return getConnectionStatus() == DISCONNECTED;
    }

    @Override
    public synchronized boolean isDisconnecting() {
        return getConnectionStatus() == DISCONNECTING;
    }
}