package com.beidouapp.et.client.api;

import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.callback.IFileReceiveListener;
import com.beidouapp.et.client.callback.IReceiveListener;
import com.beidouapp.et.client.callback.IUserStatusListener;
import com.beidouapp.et.client.domain.EtMsg;

/**
 * 可处理消息的接口.
 *
 * @author mhuang.
 */
public interface IM {
    /**
     * 已连接.
     */
    public static final byte CONNECTED = 0;// 已连接.

    /**
     * 正在连接.
     */
    public static final byte CONNECTING = 1;// 正在连接.

    /**
     * 正在断开.
     */
    public static final byte DISCONNECTING = 2;// 正在断开.

    /**
     * 已断开.
     */
    public static final byte DISCONNECTED = 3;// 已断开.

    /**
     * 设置连接回调.
     *
     * @param callback 回调对象.
     */
    public void setConnectCallBack(ICallback<Void> callback);

    /**
     * 设置消息监听器.<br/>
     * 用户设置后，能够接收到服务器推送过来的消息.
     *
     * @param receiveListener
     */
    public void setReceiveListener(IReceiveListener receiveListener);

    /**
     * 设置 文件监听器.<br/>
     * 用户设置后，能够接收到服务器推送过来的文件信息.
     *
     * @param fileListener
     */
    public void setFileReceiveListener(IFileReceiveListener fileListener);

    /**
     * 设置关注用户状态监听器.
     *
     * @param userStatusListener
     */
    public void setUserStatusListener(IUserStatusListener userStatusListener);

    /**
     * 连接服务器.
     */
    public void connect();

    /**
     * 断开与服务器的连接.<br/>
     *
     * @param callback
     */
    public void disconnect(ICallback<Void> callback);

    /**
     * 获取指定用户状态.<br/>
     *
     * @param userId   用户ID.
     * @param callback 异步状态回调.
     */
    public void peerState(String userId, String msgId, ICallback<EtMsg> callback);

    /**
     * 发布消息.<br/>
     * 其中 callback 为空时，抛EtRuntimeException.
     *
     * @param topic    消息主题.
     * @param content  消息内容.
     * @param callback 异步结果回调(必填).
     */
    public void publish(String topic, String content, String msgId,
                        ICallback<EtMsg> callback);

    /**
     * 发布消息.<br/>
     * 其中 callback 为空时，抛EtRuntimeException.
     *
     * @param topic    消息主题.
     * @param content  消息内容.
     * @param qos      消息质量等级.
     * @param callback 异步结果回调(必填).
     */
    public void publish(String topic, String content, int qos, String msgId,
                        ICallback<EtMsg> callback);

    /**
     * 发布消息.<br/>
     * 消息内容通过byte[]数组传入.
     *
     * @param topic    消息主题.
     * @param content  消息内容的字节数组.
     * @param qos      消息质量等级 值域[0,1,2].
     * @param msgId    消息唯一标识.(唯一性SDK不做校验).
     * @param callback 发布消息是否成功的结果回调(必填).
     */
    public void publish(String topic, byte[] content, int qos, String msgId,
                        ICallback<EtMsg> callback);

    /**
     * 订阅消息.
     *
     * @param topic    消息主题.
     * @param callback 异步结果回调.
     */
    public void subscribe(String topic, ICallback<Void> callback);

    /**
     * 订阅消息.
     *
     * @param topic    消息主题.
     * @param callback 异步结果回调.
     */
    public void subscribe(String topic, int qos, ICallback<Void> callback);

    /**
     * 取消订阅.
     *
     * @param topic    消息主题.
     * @param callback 异步结果回调.
     */
    public void unsubscribe(String topic, ICallback<Void> callback);

    /**
     * 点对点发送消息.<br/>
     * 可能会抛出EtRuntimeException异常.
     *
     * @param userId   用户Id.
     * @param content  消息内容.
     * @param callback 异步状态回调.
     */
    public void chatTo(String userId, String content, String msgId,
                       ICallback<EtMsg> callback);

    /**
     * 以字节数组方式 点对点发送消息.<br/>
     *
     * @param userId   接收端用户Id.
     * @param content  字节数组类型的消息.
     * @param qos      消息质量控制级别.默认为1.值域[0,1,2].
     * @param msgId    消息Id,用户设置，不做唯一性校验.用于标识消息是否处理成功的状态.
     * @param callback 异步消息回调.
     */
    public void chatTo(String userId, byte[] content, int qos, String msgId,
                       ICallback<EtMsg> callback);

    /**
     * 内容以JSON方式得点对点发送消息.<br/>
     * 可能会抛出EtRuntimeException异常.
     *
     * @param userId   用户Id.
     * @param content  消息内容, json格式.其中，必须包含(但SDK不做校验):<br/>
     *                 content : 消息内容<br/>
     *                 nickname: 发送者名称<br/>
     *                 datetime: 发送消息时间（时间戳，精确到秒）<br/>
     *                 其余内容用户可自行扩展。<br/>
     * @param qos      消息质量控制级别.默认为1.值域[0,1,2].
     * @param msgId    消息Id,用户设置，不做唯一性校验.用于标识消息是否处理成功的状态.
     * @param callback 异步状态回调.
     *                 <p>
     *                 <pre>
     *                  eg:
     *                  {"content": "hello", "nickname": "zhangsan", "datetime": 1442296773, "age":29}
     *                  其中， age属性则为用户扩展内容.
     *                 </pre>
     */
    public void chatToJson(String userId, String content, int qos,
                           String msgId, ICallback<EtMsg> callback);

    /**
     * 内容以JSON方式得点对点发送消息.内容以字节数组为参数.<br/>
     * 参考 chatToEx(String userId, String content, int qos, String msgId,
     * ICallback <EtMsg> callback)中的注释.
     *
     * @param userId
     * @param content
     * @param qos
     * @param msgId
     * @param callback
     */
    public void chatToJson(String userId, byte[] content, int qos,
                           String msgId, ICallback<EtMsg> callback);

    /**
     * 内容以JSON方式得点对点发送消息.内容以字节数组为参数.<br/>
     * 参考 chatToEx(String userId, String content, int qos, String msgId,
     * ICallback <EtMsg> callback)中的注释.
     *
     * @param userId
     * @param content
     * @param msgId
     * @param callback
     */
    public void chatToJson(String userId, byte[] content, String msgId,
                           ICallback<EtMsg> callback);

    /**
     * 向服务器主动获取离线消息.<br/>
     * 此接口调用后，服务器会将离线消息推送到客户端。如果用户离线后，需要再次调用此接口获取。用户在线时，多次调用此方法时，服务器忽略.
     */
    public void requestOfflineMessage();

    /**
     * 向服务器获取时间消息.<br/>
     * 异步消息返回字符串类型时间戳.eg："1440842946".
     */
    public void getIlinkTime();

    /**
     * 手动重连.
     */
    public void reconnect();

    /**
     * 获取当前连接状态<br/>
     * 0:已连接; 1:正在连接; 2:正在断开; 3:已断开.
     *
     * @return
     */
    public byte getConnectionStatus();

    /**
     * 获取SDK上下文.
     *
     * @return
     */
    public IContext getETContext();

    /**
     * 已连接状态.<br/>
     * true 已连接; false 未连接;
     *
     * @return
     */
    public boolean isConnected();

    /**
     * 正在连接状态. <br/>
     * true 正在连接状态; false 非正在连接;
     *
     * @return
     */
    public boolean isConnecting();

    /**
     * 已断开状态.<br/>
     * true 已断开状态; false 非已断开;
     *
     * @return
     */
    public boolean isDisconnected();

    /**
     * 正在断开状态.<br/>
     * true 正在断开状态; false 非正在断开;
     *
     * @return
     */
    public boolean isDisconnecting();

    /**
     * 销毁连接.
     */
    public void destroy();

    /**
     * 用户状态订阅.
     *
     * @param userId   需要关注状态的发布者ID.
     * @param qos      消息质量控制级别.默认为1.值域[0,1,2].
     * @param msgId    消息Id,用户设置，不做唯一性校验.用于标识消息是否处理成功的状态.
     * @param callback 异步状态回调.可为null.
     */
    public void stateSubscribe(String userId, int qos, String msgId,
                               ICallback<EtMsg> callback);

    /**
     * 取消用户状态订阅.
     *
     * @param userId   需要取消关注状态的发布者ID.
     * @param qos      消息质量控制级别.默认为1.值域[0,1,2].
     * @param msgId    消息Id,用户设置，不做唯一性校验.用于标识消息是否处理成功的状态.
     * @param callback 异步状态回调.可为null.
     */
    public void stateUnsubscribe(String userId, int qos, String msgId,
                                 ICallback<EtMsg> callback);
}
