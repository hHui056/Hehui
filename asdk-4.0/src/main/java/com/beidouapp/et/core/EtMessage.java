/**
 *
 */
package com.beidouapp.et.core;

/**
 * sdk内部消息
 *
 * @author ztao
 */
public class EtMessage {
    public static final int CHAT_TO = 0;
    public static final int PUBLISH = 1;
    public static final int SUBSCRIBE = 2;
    public static final int UNSUBSCRIBE = 3;
    public static final int CHAT_TO_EX = 4;
    public static final int GROUP = 5;
    public static final int SYSTEM = 6;

    /**
     * 消息类别
     */
    private int category = CHAT_TO;

    /**
     * 该消息要发送到的服务器的id.作为与服务器的连接标识
     */
    private String svrId = "";

    /**
     * {@link #CHAT_TO}消息表示接收者的userId. 消息表示发送者的userId.
     * <p>
     * 在内网模式下userId和svrId相同
     * </p>
     */
    private String userId = "";

    /**
     * 只有publish,subscribe,received消息,该字段才有意义
     */
    private String topic = "";

    /**
     * 消息内容
     */
    private byte[] payload;

    /**
     * chatTo,publish,subscribe有意义
     */
    private int qos = 1;

    /**
     * qos > 0时，chat,publish,subscribe需要指定消息id
     */
    private int msgId = -1;

    public EtMessage() {

    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getSvrId() {
        return svrId;
    }

    public void setSvrId(String svrId) {
        this.svrId = svrId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

}
