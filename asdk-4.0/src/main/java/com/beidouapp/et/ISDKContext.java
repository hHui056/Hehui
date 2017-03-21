package com.beidouapp.et;

import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.client.domain.GroupInfo;
import com.beidouapp.et.client.domain.UserInfo;

import java.util.List;

/**
 * A-SDK的所有操作，如发送消息，发送文件等。
 */
public interface ISDKContext {
    /**
     * 设置A-SDK的配置参数。
     *
     * @param parameters 配置参数。
     */
    public void setContextParameters(SDKContextParameters parameters);

    /**
     * 获取A-SDK的配置参数。
     *
     * @return 配置参数。
     */
    public SDKContextParameters getContextParameters();

    /**
     * 扫描<b>服务器</b>，扫描结果通过{@link ISDKContextCallback#onServer(Server)}返回。
     * <p>
     * 在调用该方法之前，需要先设置SDK回调方法，{@link #setCallback(ISDKContextCallback)}。
     * </p>
     *
     * @param timeout  扫描的最大持续时间，如果超时时间内扫描到服务器，那么通过
     *                 {@link ISDKContextCallback#onServer(Server)}通知，否则通过
     *                 {@link IActionListener#onFailure(ErrorInfo)} 通知。超时时间只能是5 ~ 30秒之间。
     * @param listener 操作结果回调。 {@link IActionListener#onSuccess()}
     *                 只表示发现操作成功，不代表一定会有服务器返回。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void discoverServers(int timeout, IActionListener listener);

    /**
     * 扫描<b>服务器</b>，扫描结果通过{@link ISDKContextCallback#onServer(Server)}返回。
     * <p>
     * 在调用该方法之前，需要先设置SDK回调方法，{@link #setCallback(ISDKContextCallback)}。
     * </p>
     *
     * @param timeoutSencond 扫描的最大持续时间，如果超时时间内扫描到服务器，那么通过
     *                       {@link ISDKContextCallback#onServer(Server)}通知，否则通过
     *                       {@link IActionListener#onFailure(ErrorInfo)} 通知。超时时间只能是5 ~ 30秒之间。
     * @param opt            自定义扫描参数 ,可以为null。
     * @param listener       操作结果回调。 {@link IActionListener#onSuccess()}
     *                       只表示发现操作成功，不代表一定会有服务器返回。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void discoverServers(int timeoutSencond, DiscoverOptions opt, IActionListener listener);

    /**
     * 连接到服务器，异步连接，操作结果通过<code>listener</code>返回。
     *
     * @param svr      服务器。
     * @param opt      连接参数。
     * @param listener 连接结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void connect(Server svr, ConnectOptions opt, IActionListener listener);

    /**
     * 断开已经连接的服务器。
     *
     * @param svr      已经连接的服务器。
     * @param listener 操作结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void disconnect(Server svr, IActionListener listener);

    /**
     * 查询用户的在线状态。
     *
     * @param uid      用户的uid。
     * @param listener 操作结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void getUserState(String uid, StatusListener listener);

    /**
     * 发送消息到指定用户。
     *
     * @param receiverUid 消息接收者的uid。
     * @param msg         要发送的消息。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void chatTo(String receiverUid, Message msg, IActionListener listener);

    /**
     * 发布消息给订阅了主题的所有用户。
     *
     * @param topic    主题
     * @param qos      消息质量级别
     * @param msg      消息
     * @param listener 操作结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void publish(String topic, Qos qos, Message msg,
                        IActionListener listener);

    /**
     * 发布消息到群
     *
     * @param groupId  群ID
     * @param msg      消息
     * @param listener 操作结果回调
     */
    public void publishToGroup(String groupId, Message msg, IActionListener listener);

    /**
     * 订阅主题。
     *
     * @param topic    主题
     * @param qos      订阅qos等级
     * @param listener 操作结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void subscribe(String topic, Qos qos, IActionListener listener);

    /**
     * 取消已经订阅的主题。
     *
     * @param topic    主题
     * @param listener 操作结果回调。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void unsubscribe(String topic, IActionListener listener);

    /**
     * 订阅用户在线状态。<br>
     * 用户状态变化通过{@link ISDKContextCallback#onPeerState(String, String)} 返回。
     *
     * @param uid      关注的用户uid。
     * @param listener 操作是否成功。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void subUserState(String uid, IActionListener listener);

    /**
     * 取消订阅用户在线状态。<br>
     * 用户状态变化通过{@link ISDKContextCallback#onPeerState(String, String)} 返回。
     *
     * @param uid      关注的用户uid。
     * @param listener 操作是否成功。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void unSubUserState(String uid, IActionListener listener);

    /**
     * 获取所有离线消息。
     */
    public void requestOfflineMessage();

    /**
     * 主动发送文件。
     *
     * @param receiverId   接收文件的用户ID。
     * @param fileFullName 文件全路径名。
     * @param desc         文件描述信息
     * @param callBack     文件操作回调接口。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void fileTo(String receiverId, String fileFullName, String desc,
                       final FileCallBack callBack);

    /**
     * 下载文件。
     *
     * @param documentInfo 文件信息。
     * @param saveFilePath 本地文件保存路径。
     * @param callBack     文件操作回调接口。
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void downloadFile(DocumentInfo documentInfo, String saveFilePath, final FileCallBack callBack);

    /**
     * 文件上传
     *
     * @param fullPath 文件完整路径
     * @param callBack 上传成功失败回调
     */
    public void uploadFile(String fullPath, final FileCallBack callBack);

    /**
     * 添加好友。
     *
     * @param buddyUid 好友的uid.
     * @param notify   是否通知好友  ture 通知，false 不通知
     * @param listener 增加好友成功，回调{@link IFriendsActionListener#onSuccess()}； 否则，回调
     *                 {@link IFriendsActionListener#onFailure(ErrorInfo))}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void addBuddy(String buddyUid, boolean notify, IFriendsActionListener listener);


    /**
     * 删除好友。
     *
     * @param buddyUid 好友的uid.
     * @param notify   是否通知好友  ture 通知，false 不通知
     * @param listener 删除好友成功，回调{@link IFriendsActionListener#onSuccess()}； 否则，回调
     *                 {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void removeBuddy(String buddyUid, boolean notify, IFriendsActionListener listener);


    /**
     * 获取好友列表.
     *
     * @param listener 获取好友列表成功，回调{@link IFriendsActionListener#onResultData(Object)}
     *                 ，参数Object类型是<code>List<{@link UserInfo}></code>，表示好友信息列表；
     *                 否则，回调 {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void getBuddies(IFriendsActionListener listener);

    /**
     * 创建群.
     *
     * @param groupname  组群名称.
     * @param userIdList 拉进群的用户id.
     * @param listener   创建群成功，回调{@link IFriendsActionListener#onResultData(Object)}
     *                   ，参数Object类型是<code>{@link GroupInfo}</code>，表示群信息； 否则，回调
     *                   {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void createGroup(String groupname, List<String> userIdList, IFriendsActionListener listener);

    /**
     * 用户主动退出群
     *
     * @param groupId  群ID
     * @param listener 退出群成功失败的回调
     */
    public void exitGroup(String groupId, IActionListener listener);

    /**
     * 获取群列表.
     *
     * @param listener 获取群列表成功，回调{@link IFriendsActionListener#onResultData(Object)}
     *                 ，参数Object类型是<code>List<{@link GroupInfo}></code>
     *                 ，表示该用户的所有群信息列表； 否则，回调
     *                 {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void getGroups(IFriendsActionListener listener);

    /**
     * 注销群.
     *
     * @param grougId  群Id.
     * @param listener 删除群成功，回调{@link IFriendsActionListener#onSuccess()}； 否则，回调
     *                 {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void destroyGroup(String grougId, IFriendsActionListener listener);

    /**
     * 添加群成员.
     *
     * @param grougId   群Id，在系统里群Id唯一.
     * @param userlists 进群的用户id列表.
     * @param listener  添加群成员成功，回调{@link IFriendsActionListener#onSuccess()}； 否则，回调
     *                  {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void addGroupMembers(String grougId, List<String> userlists, IFriendsActionListener listener);

    /**
     * 删除群成员.
     *
     * @param grougId   群Id，在系统里群名称唯一.
     * @param userlists 该群中要删除的用户id列表.
     * @param listener  删除群成员成功，回调{@link IFriendsActionListener#onSuccess()}； 否则，回调
     *                  {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void removeGroupMembers(String grougId, List<String> userlists, IFriendsActionListener listener);

    /**
     * 获取群成员列表.
     *
     * @param grougId  群Id.
     * @param listener 获取群成员成功，回调 {@link IFriendsActionListener#onResultData(Object)}
     *                 ，参数类型是<code>List<{@link UserInfo}></code>，表示所有群成员信息列表； 否则，回调
     *                 {@link IFriendsActionListener#onFailure(ErrorInfo)}
     * @throws IllegalArgumentException 如果参数为null。
     */
    public void getGroupMembers(String grougId, IFriendsActionListener listener);

    /**
     * 设置A-SDK回调函数。
     *
     * @param callback 回调函数。
     * @throws IllegalArgumentException 如果参数为null
     */
    public void setCallback(ISDKContextCallback callback);

    /**
     * 获取A-SDK的版本。
     *
     * @return iLink SDK for android version xx.yy.zz.tt <br>
     */
    public String getSdkVersion();

    /**
     * 获取iLink服务器时间
     *
     * @param listener 系统服务器时间回调
     */
    public void getIlinkTime(TimeListener listener);

    /**
     * 释放资源，销毁A-SDK。
     */
    public void destroyContext();

    /**
     * 重连服务器
     *
     * @param actionlistener
     */
    public void reConnect(IActionListener actionlistener);
}
