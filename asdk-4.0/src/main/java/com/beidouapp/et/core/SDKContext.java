package com.beidouapp.et.core;

import android.content.Context;
import com.beidouapp.et.*;
import com.beidouapp.et.client.EtFactory;
import com.beidouapp.et.client.api.IBaseWeb;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.client.domain.UserInfo;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.core.DiscoverMqttSvrService.OnDiscoverMqttSvrListener;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;
import com.beidouapp.et.util.ParamUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * 定义了sdk所有的操作，包括发现设备，连接设备，发送消息等
 *
 * @author allen
 */
public class SDKContext implements ISDKContext, IMessageCenterCallback, OnDiscoverListener {
    static final int PLATFORM_FOR_A_SDK = 1;
    private static final String TAG = SDKContext.class.getSimpleName();
    private static final Log LOG = LogFactory.getLog("java");
    /**
     * Context的参数
     */
    private SDKContextParameters mContextParameters;
    /**
     * 已经扫描到的服务器
     */
    private Hashtable<String, Server> mServerMap = new Hashtable<String, Server>();
    /**
     * 内网udp扫描设备
     */
    private DiscoverPeersService mDiscoverPeersService = null;
    /**
     * 外网扫描mqtt服务器
     */
    private DiscoverMqttSvrService mDiscoverMqttSvrService = null;
    private IActionListener mDiscoverActionListener = null;

    private MessageCenter mMsgCenter;
    private ISDKContextCallback mContextCallback = null;


    public SDKContext(SDKContextParameters parameters, Context androidcontext) {
        mContextParameters = parameters;
        mDiscoverPeersService = new DiscoverPeersService(this, androidcontext);
        mDiscoverPeersService.setOnDiscoverListener(this);
        mDiscoverMqttSvrService = new DiscoverMqttSvrService(this);
        mMsgCenter = new MessageCenter(this/*, androidcontext*/);
    }

    /**
     * @return the mContextParameters
     */
    public SDKContextParameters getContextParameters() {
        return mContextParameters;
    }

    /**
     * @param contextParameters the mContextParameters to set
     */
    public void setContextParameters(SDKContextParameters contextParameters) {
        this.mContextParameters = contextParameters;
    }

    @Override
    public void discoverServers(final int timeout, IActionListener listener) {
        discoverServers(timeout, null, listener);
    }

    @Override
    public void discoverServers(final int timeoutSecond, final DiscoverOptions opt, final IActionListener listener) {
        /**
         * 参数校验
         */
        if (ParamUtil.isNull(listener)) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (timeoutSecond < 0) {
            listener.onFailure(new ErrorInfo(ErrorCode.TIME_NUMBER_ERROR));
            return;
        }
        if (ParamUtil.isAppKeyHaveSpeStr(mContextParameters.getAppKey())) {//校验appkey
            listener.onFailure(new ErrorInfo(ErrorCode.APPKEY_ERROR, "appkey invalid"));
            return;
        }
        if (ParamUtil.isUidContansSpeStr(mContextParameters.getUid())) {//校验uid
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        if (ParamUtil.isSecretKeyHaveSpeStr(mContextParameters.getSecretKey())) {//校验secretkey
            listener.onFailure(new ErrorInfo(ErrorCode.SECRETKEY_ERROR, "secretkey invalid"));
            return;
        }
        //====================================================================================================

        // timeout 限制在5 - 30秒之间
        int timeout = Math.max(Math.min(timeoutSecond, 5), 30);
        mDiscoverActionListener = listener;
        // 同时进行内外网扫描，在超时时间内有任何一个server返回即成功，否则超时。
        // 扫描MQTT服务器
        mDiscoverMqttSvrService.doDiscover(timeout,
                new OnDiscoverMqttSvrListener() {
                    @Override
                    public void onResult(Server mqttSvr) {
                        SDKContext.this.onResult(mqttSvr);
                    }

                    @Override
                    public void onFailure(ErrorInfo info) {
                        listener.onFailure(info);
                    }
                });
        // UDP广播发现内网服务器
        mDiscoverPeersService.doDiscovery(timeout, opt);
    }

    @Override
    public void connect(Server svr, ConnectOptions opt, IActionListener listener) {
        if (ParamUtil.isNull(svr, opt, listener)) {
            throw new IllegalArgumentException("svr, opt, listener can not be null");
        }
        mMsgCenter.connect2Svr(svr, opt, listener);
    }

    @Override
    public void disconnect(Server svr, IActionListener listener) {
        if (ParamUtil.isNull(svr, listener)) {
            throw new IllegalArgumentException("svr, listener can not be null");
        }
        mMsgCenter.disconnectFromSvr(svr, listener);
    }

    @Override
    public void getUserState(String uid, StatusListener listener) {
        if (ParamUtil.isNull(uid, listener)) {
            throw new IllegalArgumentException("uid, listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(uid)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        mMsgCenter.peerState(uid, listener);
    }

    @Override
    public void chatTo(String userId, Message msg, IActionListener listener) {
        /**
         * chatto只支持qos等级1
         */
        if (ParamUtil.isNull(userId, msg, listener)) {
            throw new IllegalArgumentException("userId, msg, listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(userId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        EtMessage etMessage = new EtMessage();
        etMessage.setSvrId(userId);
        etMessage.setUserId(userId);
        etMessage.setPayload(msg.getPayload());
        etMessage.setQos(Qos.QoS_1.getCode());
        etMessage.setCategory(EtMessage.CHAT_TO);
        mMsgCenter.chatTo(etMessage, listener);
    }

    @Override
    public void publish(String topic, Qos qos, Message msg, IActionListener listener) {

        if (ParamUtil.isNull(topic, msg, listener)) {
            throw new IllegalArgumentException("topic, msg, listener can not be null");
        } else if (ParamUtil.isTopicContansSpecialStr(topic)) {
            listener.onFailure(new ErrorInfo(ErrorCode.TOPIC_CONTAINS_SPSTR));
        } else {
            topic = EtConstants.PUBLISH_PREFIX + topic;
            EtMessage etMessage = new EtMessage();
            etMessage.setSvrId(Server.PROXY_SERVER_ID);
            etMessage.setCategory(EtMessage.PUBLISH);
            etMessage.setTopic(topic);
            etMessage.setQos(qos.getCode());
            etMessage.setPayload(msg.getPayload());
            mMsgCenter.publish(etMessage, listener);
        }
    }

    @Override
    public void subscribe(String topic, Qos qos, IActionListener listener) {

        if (ParamUtil.isNull(topic, listener)) {
            throw new IllegalArgumentException("topic, listener can not be null");
        } else if (ParamUtil.isTopicContansSpecialStr(topic)) {
            listener.onFailure(new ErrorInfo(ErrorCode.TOPIC_CONTAINS_SPSTR));
        } else {
            topic = EtConstants.PUBLISH_PREFIX + topic;
            EtMessage etMessage = new EtMessage();
            etMessage.setSvrId(Server.PROXY_SERVER_ID);
            etMessage.setCategory(EtMessage.SUBSCRIBE);
            etMessage.setTopic(topic);
            etMessage.setQos(qos.getCode());
            mMsgCenter.subscribe(etMessage, listener);
        }
    }

    @Override
    public void unsubscribe(String topic, IActionListener listener) {
        if (ParamUtil.isNull(topic, listener)) {
            throw new IllegalArgumentException("topic, listener can not be null");
        } else if (ParamUtil.isTopicContansSpecialStr(topic)) {
            listener.onFailure(new ErrorInfo(ErrorCode.TOPIC_CONTAINS_SPSTR));
        } else {
            topic = EtConstants.PUBLISH_PREFIX + topic;
            EtMessage etMessage = new EtMessage();
            etMessage.setSvrId(Server.PROXY_SERVER_ID);
            etMessage.setCategory(EtMessage.UNSUBSCRIBE);
            etMessage.setTopic(topic);
            etMessage.setQos(Qos.QoS_1.getCode());
            mMsgCenter.unsubscribe(etMessage, listener);
        }
    }

    @Override
    public void subUserState(String uid, IActionListener listener) {
        if (ParamUtil.isNull(uid, listener)) {
            throw new IllegalArgumentException("uid, listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(uid)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        mMsgCenter.stateSubscribe(uid, listener);
    }

    @Override
    public void unSubUserState(String uid, IActionListener listener) {
        if (ParamUtil.isNull(uid, listener)) {
            throw new IllegalArgumentException("uid, listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(uid)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.stateUnsubscribe(uid, listener);
        }
    }

    @Override
    public void requestOfflineMessage() {
        if (mMsgCenter != null) {
            mMsgCenter.requestOfflineMessage();
        }
    }

    @Override
    public void fileTo(final String receiverId, final String fileFullName, final String desc, final FileCallBack callBack) {
        if (ParamUtil.isNull(receiverId, fileFullName, callBack)) {
            throw new IllegalArgumentException("topic, msg, listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(receiverId)) {
            callBack.onFailure(fileFullName, new Throwable("receive uid invalid"));
            return;
        }
        if (mMsgCenter != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mMsgCenter.fileTo(receiverId, fileFullName, desc, callBack);
                }
            }).start();
        } else {
            callBack.onFailure(fileFullName, new Throwable("sdk not init"));
        }
    }

    @Override
    public void downloadFile(final DocumentInfo documentInfo, final String saveFilePath, final FileCallBack callBack) {
        if (ParamUtil.isNull(documentInfo, saveFilePath, callBack)) {
            throw new IllegalArgumentException("topic, msg, listener can not be null");
        }
        if (mMsgCenter != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mMsgCenter.downloadFile(documentInfo, saveFilePath, callBack);
                }
            }).start();

        } else {
            callBack.onFailure(saveFilePath, new Throwable("sdk not init"));
        }
    }

    /**
     * 注册新用户用户
     */
    public static void registerUser(final String username, final String nickname, final String serverHost, final int serverPort, final String appKey, final String secretKey, final IFriendsActionListener listener) {
        if (ParamUtil.isNull(username, serverHost, appKey, secretKey, listener)) {
            throw new IllegalArgumentException("username, serverHost, appKey, secretKey, listener can not be null");
        }
        if (ParamUtil.isSecretKeyHaveSpeStr(secretKey)) {//校验secretkey
            listener.onFailure(new ErrorInfo(ErrorCode.SECRETKEY_ERROR, "secretKey invalid"));
            return;
        }
        if (ParamUtil.isAppKeyHaveSpeStr(appKey)) {//校验appkey
            listener.onFailure(new ErrorInfo(ErrorCode.APPKEY_ERROR, "appKey invalid"));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EtFactory factory = new EtFactory();
                    IContext etContext = factory.createContext().setAppKey(appKey).setSecretKey(secretKey).set(EtKeyConstant.PLATFORM_FOR_S_SDK, PLATFORM_FOR_A_SDK).setDefaultQos(1).setSSLEnable(false).setServerDomain(serverHost).setServerPort(serverPort);
                    IBaseWeb web = factory.createWebWithLB(etContext);
                    List<UserInfo> newUserList = new ArrayList<UserInfo>();
                    UserInfo info = new UserInfo(username);
                    info.setNickname(nickname);
                    newUserList.add(info);
                    List<UserInfo> registeredUserList = web.addUser(newUserList);
                    if (registeredUserList != null && registeredUserList.size() == 1) {
                        listener.onResultData(registeredUserList.get(0));
                    } else {
                        listener.onFailure(new ErrorInfo(ErrorCode.REGISTER_USER_FAIL, "返回注册用户信息错误"));
                    }
                } catch (Throwable t) {
                    listener.onFailure(new ErrorInfo(ErrorCode.REGISTER_USER_FAIL, t.getMessage()));
                }
            }
        }).start();
    }

    @Override
    public void addBuddy(String friendId, boolean notify, IFriendsActionListener listener) {
        if (ParamUtil.isNull(friendId, listener)) {
            throw new IllegalArgumentException("friendId,listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(friendId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        if (friendId.equals(mContextParameters.getUid())) {
            listener.onFailure(new ErrorInfo(ErrorCode.ADD_BUDDY_FAIL, "can't add yourself"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.addBuddy(friendId, listener, notify);
        }
    }

    @Override
    public void removeBuddy(String friendId, boolean notify, IFriendsActionListener listener) {
        if (ParamUtil.isNull(friendId, listener)) {
            throw new IllegalArgumentException("friendId,listener can not be null");
        }
        if (ParamUtil.isUidContansSpeStr(friendId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.UIDERROR, "uid invalid"));
            return;
        }
        if (friendId.equals(mContextParameters.getUid())) {
            listener.onFailure(new ErrorInfo(ErrorCode.REMOVE_BUDDY_FAIL, "can't remove yourself"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.removeBuddy(friendId, listener, notify);
        }
    }

    @Override
    public void getBuddies(IFriendsActionListener listener) {
        if (ParamUtil.isNull(listener)) {
            throw new IllegalArgumentException("listener can not be null");
        }
        if (mMsgCenter != null) {
            mMsgCenter.getAllBuddies(listener);
        }
    }

    @Override
    public void createGroup(String groupname, List<String> userIdList, IFriendsActionListener listener) {
        if (ParamUtil.isNull(groupname, userIdList, listener)) {
            throw new IllegalArgumentException("groupname, userIdList, listener can not be null");
        }
        if (mMsgCenter != null) {
            mMsgCenter.createGroup(groupname, userIdList, listener);
        }
    }

    @Override
    public void getGroups(IFriendsActionListener listener) {
        if (ParamUtil.isNull(listener)) {
            throw new IllegalArgumentException("/listener can not be null");
        }
        if (mMsgCenter != null) {
            mMsgCenter.getAllGroups(listener);
        }
    }

    @Override
    public void destroyGroup(String groupId, IFriendsActionListener listener) {
        if (ParamUtil.isNull(groupId, listener)) {
            throw new IllegalArgumentException("groupId, listener can not be null");
        }
        if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.dismissGroup(groupId, listener);
        }
    }

    @Override
    public void addGroupMembers(String groupId, List<String> userIdList, IFriendsActionListener listener) {
        if (ParamUtil.isNull(groupId, userIdList, listener)) {
            throw new IllegalArgumentException("groupId, userIdList, userIdList, listener can not be null");
        }
        if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.addGroupMember(groupId, userIdList, listener);
        }
    }

    @Override
    public void removeGroupMembers(String groupId, List<String> userIdList,
                                   IFriendsActionListener listener) {
        if (ParamUtil.isNull(groupId, userIdList, listener)) {
            throw new IllegalArgumentException("groupId, userIdList, listener can not be null");
        }
        if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.removeGroupMember(groupId, userIdList, listener);
        }
    }

    @Override
    public void getGroupMembers(String groupId, IFriendsActionListener listener) {
        if (ParamUtil.isNull(groupId, listener)) {
            throw new IllegalArgumentException("groupId, listener can not be null");
        }
        if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.getAllGroupMembers(groupId, listener);
        }
    }

    @Override
    public void setCallback(ISDKContextCallback callback) {
        if (ParamUtil.isNull(callback)) {
            throw new IllegalArgumentException("callback can not be null");
        }
        mContextCallback = callback;
    }

    private Server getSvrById(String svrId) {
        return mServerMap.get(svrId);
    }

    @Override
    public String getSdkVersion() {
        // 这里记录最后一次修改时间，2017/7/6  16:35 ET
        return "4.0.1.0";
    }

    @Override
    public void destroyContext() {
        mServerMap.clear();
        mMsgCenter.destory();
        mDiscoverPeersService.stop();
    }

    @Override
    public void reConnect(IActionListener actionlistener) {
        if (ParamUtil.isNull(actionlistener)) {
            throw new IllegalArgumentException("listener can not be null");
        }
        mMsgCenter.reConnectSvr(actionlistener);
    }

    // ****** OnDiscoverLisetner start
    @Override
    public void onResult(Server svr) {
        String svrId = svr.getId();
        if (!mServerMap.containsKey(svrId)) {
            LOG.d(TAG, "put:" + mServerMap.put(svrId, svr));
        }
        if (mContextCallback != null) {
            mContextCallback.onServer(svr);
        }
    }

    @Override
    public void onDiscoverFail(ErrorInfo errorInfo) {
        // 扫描server,不提示失败。

    }

    @Override
    public void onSuccess() {
        if (mDiscoverActionListener != null) {
            mDiscoverActionListener.onSuccess();
        }
    }

    // ******* OnDiscoverLisetner end
    @Override
    public void onMessageArrived(Server svr, EtMessage etMsg) {
        if (mContextCallback != null) {
            Message msg = new Message();
            msg.setPayload(etMsg.getPayload());
            MessageType type;
            String topic;
            switch (etMsg.getCategory()) {
                case EtMessage.CHAT_TO:// 点对点消息
                    topic = etMsg.getUserId();
                    type = MessageType.CHAT_TO;
                    mContextCallback.onMessage(type, topic, msg);
                    break;
                case EtMessage.PUBLISH:// publish
                    topic = etMsg.getTopic();
                    type = MessageType.PUBLISH;
                    mContextCallback.onMessage(type, topic, msg);
                    break;
                case EtMessage.GROUP:// 群消息
                    topic = etMsg.getTopic();
                    type = MessageType.GROUP;
                    mContextCallback.onMessage(type, topic, msg);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onMsgSendSuccess(EtMessage etMsg) {
        if (mContextCallback != null) {
            Message msg = new Message();
            msg.setPayload(etMsg.getPayload());
        }
    }

    void onPeerState(String uid, String statusCode) {
        if (mContextCallback != null) {
            mContextCallback.onPeerState(uid, statusCode);
        }
    }

    void onFileArrived(String senderId, DocumentInfo fileInfo) {
        if (mContextCallback != null) {
            mContextCallback.onFileReceived(senderId, fileInfo);
        }
    }

    @Override
    public void onConnectLost(Server svr, int errorCode) {
        if (mContextCallback != null) {
            // TODO，修改错误码和错误信息
            mContextCallback.onBroken(svr, errorCode, ErrorCode.getErrorReason(errorCode));
        }
    }

    @Override
    public void publishToGroup(String groupId, Message msg, IActionListener listener) {
        /**
         * publishtogroup支持 qos等级1
         */
        // TODO Auto-generated method stub
        if (ParamUtil.isNull(groupId, msg, listener)) {
            throw new IllegalArgumentException("topic, msg, listener can not be null");
        } else if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
        } else {
            EtMessage etMessage = new EtMessage();
            etMessage.setSvrId(Server.PROXY_SERVER_ID);
            etMessage.setCategory(EtMessage.GROUP);
            etMessage.setTopic(groupId);
            etMessage.setQos(Qos.QoS_1.getCode());
            etMessage.setPayload(msg.getPayload());
            mMsgCenter.publish(etMessage, listener);
        }
    }

    @Override
    public void getIlinkTime(TimeListener listener) {
        // TODO Auto-generated method stub
        mMsgCenter.getIlinkTime(listener);
    }

    @Override
    public void exitGroup(String groupId, IActionListener listener) {
        // TODO Auto-generated method stub
        if (ParamUtil.isNull(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.EXIT_GROUP_FAIL, "groupId can not be null"));
            return;
        }
        if (ParamUtil.isGroupIdHaveSpeStr(groupId)) {
            listener.onFailure(new ErrorInfo(ErrorCode.GROUPID_ERROR, "groupId invalid"));
            return;
        }
        if (mMsgCenter != null) {
            mMsgCenter.exitGroup(groupId, listener);
        } else {
            listener.onFailure(new ErrorInfo(ErrorCode.EXIT_GROUP_FAIL, "not create ISDKContext"));
        }
    }

    @Override
    public void uploadFile(final String fullPath, final FileCallBack callBack) {
        if (ParamUtil.isNull(fullPath, callBack)) {
            throw new IllegalArgumentException("fullpath, listener can not be null");
        }
        if (mMsgCenter != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mMsgCenter.uploadFile(fullPath, callBack);
                }
            }).start();
        } else {
            callBack.onFailure(fullPath, new Throwable("sdk not init"));
        }
    }
}
