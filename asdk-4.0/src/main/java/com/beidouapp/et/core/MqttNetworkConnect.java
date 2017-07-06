package com.beidouapp.et.core;

import com.beidouapp.et.*;
import com.beidouapp.et.client.EtManager;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.api.IFile;
import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.callback.IFileReceiveListener;
import com.beidouapp.et.client.callback.IUserStatusListener;
import com.beidouapp.et.client.domain.*;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.impl.TopicTypeEnum;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 外网mqtt连接
 */
public class MqttNetworkConnect implements INetworkConnect {
    private static final String TAG = MqttNetworkConnect.class.getSimpleName();
    private Log LOG = LogFactory.getLog("java");
    // private Context androidContext;
    private SDKContext mContext;
    private MessageCenter mMsgCenter;
    private Server mServer;
    private ConnectOptions mConnectOptions;

    private IActionListener mConnectActionLisenter;
    // ========== mqtt ========
    // private IM mEtImClient;
    private volatile EtManager mEtMqttManager;
    /**
     * 用于存储查询系统时间的回调
     **/
    private LinkedBlockingQueue<TimeListener> mTimeListenerQueue = new LinkedBlockingQueue<TimeListener>();
    /**
     * 用于存主动查询用户状态的回调
     */
    private LinkedBlockingQueue<StatusListener> mUserStatusListenerQueue = new LinkedBlockingQueue<StatusListener>();

    public MqttNetworkConnect(SDKContext context,/* Context androidContext,*/ MessageCenter msgCenter, Server svr, ConnectOptions opt) {
       /* this.androidContext = androidContext;*/
        mContext = context;
        mMsgCenter = msgCenter;
        mServer = svr;
        mConnectOptions = opt;

    }

    // 初始化mqtt core，
    private void initMsgClient() {
        IContext etContext = new EtContext();
        etContext.setAppKey(mContext.getContextParameters().getAppKey()).setSecretKey(mContext.getContextParameters().getSecretKey())
                .setUserName(mContext.getContextParameters().getUid()).set(EtKeyConstant.PLATFORM_FOR_S_SDK, SDKContext.PLATFORM_FOR_A_SDK);
        // clean session 取非，是因为服务器1：保存离线消息，0：不保存离线消息。
        // connecttimeout 限制在5 - 60秒之间
        int connecttimeout = Math.max(Math.min(mConnectOptions.getConnectionTimeout(), 60), 5);
        //mqtt心跳时间 限制在15 - 300 s 之间
        short mqttKeepAliveInterval = (short) Math.max(Math.min(mConnectOptions.getKeepAliveInterval(), 300), 15);
        etContext.setDefaultInstanceTimeout(connecttimeout * 1000L).setDefaultQos(1).setKeepAlive(mqttKeepAliveInterval).setCleanSession(!mConnectOptions.getCleanSession())
                .setServerPort(mContext.getContextParameters().getBlanceServerPort());

        etContext.set(EtKeyConstant.LB_IP, mContext.getContextParameters().getBlanceServerAddress());
        etContext.set(EtKeyConstant.IM_IP, mServer.getIp());
        etContext.set(EtKeyConstant.IM_PORT, mServer.getPort());

        mEtMqttManager = new EtManagerImpl(etContext);
        if (mEtMqttManager == null) {
            mConnectActionLisenter.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "UID ERROR"));
            return;
        }
        mEtMqttManager.setConnectCallback(new ICallback<Void>() {
            @Override
            public void onSuccess(Void value) {
                LOG.d(TAG, "-------------connect mqtt success-----------------");
                mMsgCenter.registerNetworkConnect(MqttNetworkConnect.this);
                mMsgCenter.notifyConnected(mServer);
                if (mConnectActionLisenter != null) {
                    mConnectActionLisenter.onSuccess();
                }
            }

            @Override
            public void onFailure(Void t, Throwable value) {
                LOG.d(TAG, "-------------connect mqtt fail-----------------");
                value.printStackTrace();
                if (mConnectActionLisenter != null) {
                    mConnectActionLisenter.onFailure(new ErrorInfo(ErrorCode.CONNECT_FAIL, value.getLocalizedMessage()));
                }
            }
        });
        mEtMqttManager.setListener(new com.beidouapp.et.client.callback.IReceiveListener() {//设置消息接收监听
            @Override
            public void onMessage(EtMsg msg) {
                onMessage(msg.getTopic(), msg.getPayload());
            }

            public void onMessage(String topic, byte[] payload) {
                EtMessage msg = new EtMessage();
                if (topic.startsWith(TopicTypeEnum.CHAT.getCode())) {//chatTo
                    String uid = topic.split("@")[1];
                    msg.setUserId(uid);
                } else if (topic.startsWith(TopicTypeEnum.PUBLISH.getCode())) {// publish
                    msg.setCategory(EtMessage.PUBLISH);
                    msg.setTopic(topic.substring(2, topic.length()));
                } else if (topic.startsWith(TopicTypeEnum.SERVER_DATETIME.getCode())) {// 系统时间
                    try {
                        TimeListener listener = (TimeListener) mTimeListenerQueue.take();
                        listener.onResult(Long.parseLong(new String(payload)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if (topic.startsWith(TopicTypeEnum.STATUS.getCode())) {//主动查询用户状态
                    try {
                        String info = new String(payload);
                        String[] infos = info.split(EtConstants.SEPARATOR_VERSUS);
                        StatusListener listener = (StatusListener) mUserStatusListenerQueue.take();
                        listener.onSuccess(infos[1], Integer.parseInt(infos[0]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return;
                } else {// group
                    msg.setCategory(EtMessage.GROUP);
                    msg.setTopic(topic);
                }
                msg.setPayload(payload);
                msg.setSvrId(mServer.getId());
                mMsgCenter.notifyMessageArrived(mServer, msg);
            }

            @Override
            public void connectionLost(Throwable cause) {//断开连接
                cause.printStackTrace();
                if (cause instanceof EtRuntimeException) {
                    int reasonCode = ((EtRuntimeException) cause).getReasonCode();
                    if (reasonCode == EtExceptionCode.SYS_KICK) {//异地登录
                        mMsgCenter.notifyConnectLost(mServer, ErrorCode.LOGINED_IN_OTHER_PLACE);
                    } else if (reasonCode == EtExceptionCode.SERVER_PEER_DISCONNECTED) {//服务器主动断开连接
                        mMsgCenter.notifyConnectLost(mServer, ErrorCode.PEER_DISCONNECTED);
                    } else {//其他IO or Net 异常
                        mMsgCenter.notifyConnectLost(mServer, ErrorCode.IO_EXCEPTION);
                    }
                } else {//socket异常
                    mMsgCenter.notifyConnectLost(mServer, ErrorCode.CONNECTION_LOST);
                }
            }
        });

        mEtMqttManager.setUserStatusListener(new IUserStatusListener() {
            @Override
            public void concernOnlineStatus(String userId, String statusCode) {
                if (mMsgCenter != null) {
                    mMsgCenter.notifyPeerState(userId, statusCode);
                }
            }
        });

        mEtMqttManager.setFileListener(new IFileReceiveListener() {
            @Override
            public void onCheckingFile(String senderId, DocumentInfo documentInfo) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onReceived(String senderId, DocumentInfo documentInfo) {
                mMsgCenter.notifyFileArrived(senderId, documentInfo);
            }
        });
    }

    @Override
    public void connect(IActionListener actionListener) {
        mConnectActionLisenter = actionListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LOG.d(TAG, "+++++++ initing ilink... start");
                initMsgClient();
                LOG.d(TAG, "+++++++ connecting ilink... start");
                if (mEtMqttManager != null) {
                    mEtMqttManager.connect();
                }
                LOG.d(TAG, "+++++++ connecting ilink... complete");
            }
        }).start();
    }

    @Override
    public void chatTo(EtMessage msg, final IActionListener actionListener) {
        if (mEtMqttManager == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().chatTo(msg.getUserId(), msg.getPayload(),
                    msg.getQos(), "0", new ICallback<EtMsg>() {
                        public void onSuccess(EtMsg value) {
                            actionListener.onSuccess();
                        }

                        @Override
                        public void onFailure(EtMsg t, Throwable value) {
                            actionListener.onFailure(new ErrorInfo(ErrorCode.CHAT_TO_FAIL, value.getMessage()));
                        }
                    });
        } catch (Throwable t) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.CHAT_TO_FAIL, t
                    .getMessage()));
        }
    }

    @Override
    public void publish(EtMessage msg, final IActionListener actionListener) {
        if (mEtMqttManager == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().publish(msg.getTopic(), msg.getPayload(),
                    msg.getQos(), "0", new ICallback<EtMsg>() {
                        public void onSuccess(EtMsg value) {
                            actionListener.onSuccess();
                        }

                        @Override
                        public void onFailure(EtMsg t, Throwable value) {
                            actionListener.onFailure(new ErrorInfo(
                                    ErrorCode.PUBLISH_FAIL, value.getMessage()));
                        }
                    });
        } catch (Throwable t) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.PUBLISH_FAIL, t.getMessage()));
        }
    }

    @Override
    public void subscribe(EtMessage msg, final IActionListener actionListener) {
        if (mEtMqttManager == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().subscribe(msg.getTopic(), msg.getQos(),
                    new ICallback<Void>() {
                        public void onSuccess(Void value) {
                            actionListener.onSuccess();
                        }

                        @Override
                        public void onFailure(Void t, Throwable value) {
                            actionListener.onFailure(new ErrorInfo(ErrorCode.SUBSCRIBE_FAIL, value
                                    .getMessage()));
                        }
                    });
        } catch (Throwable t) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.SUBSCRIBE_FAIL, t.getMessage()));
        }
    }

    @Override
    public void unsubscribe(EtMessage msg, final IActionListener actionListener) {
        if (mEtMqttManager == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().unsubscribe(msg.getTopic(),
                    new ICallback<Void>() {
                        public void onSuccess(Void value) {
                            actionListener.onSuccess();
                        }

                        @Override
                        public void onFailure(Void t, Throwable value) {
                            actionListener.onFailure(new ErrorInfo(
                                    ErrorCode.UNSUBSCRIBE_FAIL, value
                                    .getMessage()));
                        }
                    });
        } catch (Throwable t) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.UNSUBSCRIBE_FAIL,
                    t.getMessage()));
        }
    }

    public void stateSubscribe(String uid, final IActionListener listener) {
        if (mEtMqttManager == null) {
            listener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().stateSubscribe(uid, 1, "",
                    new ICallback<EtMsg>() {
                        @Override
                        public void onSuccess(EtMsg value) {
                            listener.onSuccess();
                        }

                        @Override
                        public void onFailure(EtMsg t, Throwable value) {
                            listener.onFailure(new ErrorInfo(
                                    ErrorCode.STATE_SUBSCRIBLE_FAIL, value
                                    .getMessage()));
                        }
                    });
        } catch (Throwable t) {
            listener.onFailure(new ErrorInfo(ErrorCode.STATE_SUBSCRIBLE_FAIL, t
                    .getMessage()));
        }
    }

    public void stateUnsubscribe(String uid, final IActionListener listener) {
        if (mEtMqttManager == null) {
            listener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().stateUnsubscribe(uid, 1, "",
                    new ICallback<EtMsg>() {
                        @Override
                        public void onSuccess(EtMsg value) {
                            listener.onSuccess();
                        }

                        @Override
                        public void onFailure(EtMsg t, Throwable value) {
                            listener.onFailure(new ErrorInfo(
                                    ErrorCode.STATE_UNSUBSCRIBE_FAIL, value
                                    .getMessage()));
                        }
                    });
        } catch (Throwable t) {
            listener.onFailure(new ErrorInfo(ErrorCode.STATE_UNSUBSCRIBE_FAIL,
                    t.getMessage()));
        }
    }

    public void requestOfflineMessage() {
        if (mEtMqttManager == null) {
            LOG.d(TAG, "sdk has not init");
            return;
        }
        mEtMqttManager.getIm().requestOfflineMessage();
    }

    public void fileTo(String receiverId, String fileFullName, String desc,
                       final FileCallBack callBack) {
        if (mEtMqttManager == null) {
            LOG.d(TAG, "sdk has not init [fileTo]");
            return;
        }
        IFile fileManage = mEtMqttManager.getFile();
        if (fileManage != null) {
            fileManage.fileTo(receiverId, fileFullName, desc, callBack);
        } else {
            callBack.onFailure(fileFullName, new Throwable("unsupport file transport"));
        }

    }

    public void downloadFile(DocumentInfo documentInfo, String saveFilePath, final FileCallBack callBack) {
        if (mEtMqttManager == null) {
            LOG.d(TAG, "sdk has not init [downloadFile]");
            return;
        }
        IFile fileManage = mEtMqttManager.getFile();
        if (fileManage != null) {
            /**
             * 下载文件
             */
            fileManage.asynDownloadFile(documentInfo, saveFilePath, callBack);
        } else {
            callBack.onFailure(saveFilePath, new Throwable("unsupport file transport"));
        }
    }

    @Override
    public void disconnect(final IActionListener actionListener) {
        if (mEtMqttManager == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        try {
            mEtMqttManager.getIm().disconnect(new ICallback<Void>() {
                @Override
                public void onSuccess(Void value) {
                    actionListener.onSuccess();
                }

                @Override
                public void onFailure(Void t, Throwable value) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.INTERNAL_ERROR, value.getMessage()));
                }
            });
        } catch (Throwable t) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, t
                    .getMessage()));
        }
    }

    public void peerState(String uid, final StatusListener listener) {
        if (mEtMqttManager == null) {
            listener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        try {
            LOG.d(TAG, String.format("查询%s的在线状态...", uid));
            mEtMqttManager.getIm().peerState(uid, "", new ICallback<EtMsg>() {
                @Override
                public void onSuccess(EtMsg value) {
                    //  listener.onSuccess();
                    mUserStatusListenerQueue.add(listener);
                }

                @Override
                public void onFailure(EtMsg t, Throwable value) {
                    listener.onFailure(new ErrorInfo(ErrorCode.PEER_STATE_FAIL,
                            value.getMessage()));
                }
            });
        } catch (Throwable t) {
            listener.onFailure(new ErrorInfo(ErrorCode.PEER_STATE_FAIL, t
                    .getMessage()));
        }
    }

    // **** web relative start
    public void addBuddy(final String friendId, final IFriendsActionListener actionListener, final boolean notify) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ResultEntity result = mEtMqttManager.getWeb().addBuddy(
                            mContext.getContextParameters().getUid(), friendId,
                            notify ? 1 : 0);
                    if (result != null) {
                        if ("0".equals(result.getCode())) {
                            actionListener.onSuccess();
                        } else {
                            actionListener.onFailure(new ErrorInfo(
                                    ErrorCode.ADD_BUDDY_FAIL, result
                                    .getMessageInfo()));
                        }
                    } else {
                        actionListener.onFailure(new ErrorInfo(
                                ErrorCode.ADD_BUDDY_FAIL, "返回好友信息是null"));
                    }
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.ADD_BUDDY_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void removeBuddy(final String friendid,
                            final IFriendsActionListener actionListener, final boolean notify) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ResultEntity result = mEtMqttManager.getWeb().removeBuddy(
                            mContext.getContextParameters().getUid(), friendid,
                            notify ? 1 : 0);
                    if (result != null) {
                        if ("0".equals(result.getCode())) {
                            actionListener.onSuccess();
                        } else {
                            actionListener.onFailure(new ErrorInfo(
                                    ErrorCode.REMOVE_BUDDY_FAIL, result
                                    .getMessageInfo()));
                        }
                    } else {
                        actionListener.onFailure(new ErrorInfo(
                                ErrorCode.REMOVE_BUDDY_FAIL, "返回好友信息是null"));
                    }
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.REMOVE_BUDDY_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void getAllBuddies(
            final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<UserInfo> result = mEtMqttManager.getWeb().getBuddies(mContext.getContextParameters().getUid());
                    actionListener.onResultData(result);
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(ErrorCode.QUERY_ALL_BUDDIES_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void createGroup(final String groupname,
                            final List<String> userIdList,
                            final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GroupInfo result = mEtMqttManager.getWeb().createGroup(
                            mContext.getContextParameters().getUid(),
                            groupname, userIdList);
                    actionListener.onResultData(result);
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(ErrorCode.CREATE_GROUP_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void getAllGroups(
            final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    List<GroupInfo> result = mEtMqttManager.getWeb().getGroups(
                            mContext.getContextParameters().getUid());
                    actionListener.onResultData(result);
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.QUERY_ALL_GROUPS_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void exitGroup(final String groupId, final IActionListener listener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mEtMqttManager.getWeb().exitGroup(groupId,
                            mContext.getContextParameters().getUid());
                    listener.onSuccess();
                } catch (EtRuntimeException e) {
                    // throw new
                    // EtRuntimeException(EtExceptionCode.WEB_GRP_QUIT,
                    // e.getMessage());
                    listener.onFailure(new ErrorInfo(ErrorCode.EXIT_GROUP_FAIL,
                            "server is not connect,please check"));
                }
            }
        }).start();
    }

    public void dismissGroup(final String groupId, final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GroupInfo result = mEtMqttManager.getWeb().destroyGroup(
                            groupId, mContext.getContextParameters().getUid());
                    actionListener.onSuccess();
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.REMOVE_GROUP_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void addGroupMember(final String grougId,
                               final List<String> userlists,
                               final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR,
                    "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    List<UserInfo> result = mEtMqttManager.getWeb()
                            .addGroupMembers(grougId, userlists);
                    actionListener.onSuccess();
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.ADD_MEMBERS_TO_GROUP_FAIL, e.getMessage()));
                }
            }
        }).start();
    }

    public void removeGroupMember(final String groupId, final List<String> userList, final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<UserInfo> result = mEtMqttManager.getWeb()
                            .removeGroupMembers(
                                    mContext.getContextParameters().getUid(),
                                    groupId, userList);
                    actionListener.onSuccess();
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.REMOVE_MEMBER_FROM_GROUP_FAIL, e
                            .getMessage()));
                }
            }
        }).start();
    }

    public void getIlinkTime(TimeListener listener) {
        if (mEtMqttManager == null || mEtMqttManager.getIm() == null) {
            return;
        }
        mTimeListenerQueue.add(listener);
        mEtMqttManager.getIm().getIlinkTime();
    }

    public void getAllGroupMembers(final String groupId, final IFriendsActionListener actionListener) {
        if (mEtMqttManager == null || mEtMqttManager.getWeb() == null) {
            actionListener.onFailure(new ErrorInfo(ErrorCode.INTERNAL_ERROR, "sdk has not init"));
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    List<UserInfo> result = mEtMqttManager.getWeb().getGroupMembers(groupId, mContext.getContextParameters().getUid());
                    if (result != null) {
                        actionListener.onResultData(result);
                    } else {
                        actionListener.onFailure(new ErrorInfo(
                                ErrorCode.QUERY_ALL_MEMBERS_OF_GROUP_FAIL,
                                "返回信息是null"));
                    }
                } catch (EtRuntimeException e) {
                    actionListener.onFailure(new ErrorInfo(
                            ErrorCode.QUERY_ALL_MEMBERS_OF_GROUP_FAIL, e
                            .getMessage()));
                }
            }
        }).start();
    }

    // **** web relative end

    @Override
    public String getIdentifer() {
        return mServer.getId();
    }

    @Override
    public Server getSvr() {
        return mServer;
    }

    @Override
    public void reConnect(IActionListener actionListener) {

        mConnectActionLisenter = actionListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mEtMqttManager != null) {
                    mEtMqttManager.getIm().reconnect();
                }
            }
        }).start();
    }

    @Override
    public void destory() {
        if (mEtMqttManager != null) {
            mEtMqttManager.destroy();
        } else {
            LOG.e(TAG, "mqtt manager is null, no need to destroy it.");
        }
        mContext = null;
        mMsgCenter = null;
        mServer = null;
        mConnectOptions = null;
        mEtMqttManager = null;
    }

    /**
     * 上传文件
     *
     * @param fullPath
     * @param callBack
     */
    public void uploadFile(String fullPath, FileCallBack callBack) {
        if (mEtMqttManager == null) {
            LOG.d(TAG, "sdk has not init [fileTo]");
            return;
        }
        IFile fileManage = mEtMqttManager.getFile();
        if (fileManage != null) {
            fileManage.asynUploadFile(fullPath, callBack);
        } else {
            callBack.onFailure(fullPath, new Throwable("unsupport file transport"));
        }

    }

   /* private class MyBroadcaseReceiver extends BroadcastReceiver {//网络状态监听回调

        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = null;
                NetworkInfo info = null;
                connectivityManager = (ConnectivityManager) androidContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {//网络连接可用 --->不做自动重连处理
//                    reConnect(new IActionListener() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailure(ErrorInfo errorInfo) {
//
//                        }
//                    });
                } else {//没有可用网络
                    mMsgCenter.notifyConnectLost(mServer, ErrorCode.CONNECTION_LOST);
                }
            }
        }
    }*/
}
