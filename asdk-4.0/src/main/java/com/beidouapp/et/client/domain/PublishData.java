package com.beidouapp.et.client.domain;

/**
 * 发布消息数据对象.
 * 
 * @author mhuang.
 */
public class PublishData
{
    /** 发布的消息主题. */
    private String subject;

    /** 发布的消息内容. */
    private String message;

    /** 本次接收消息的用户，不传则是推给订阅主题下的所有用户,用逗号分隔 可以是多个用户登录名。登录名为userid. */
    private String role;

    /** 订阅的主题(为空，则使用AppKey作为默认主题). */
    private String topic;

    public PublishData ()
    {

    }

    /**
     * 发布的消息主题.
     * 
     * @return
     */
    public String getSubject ()
    {
        return subject;
    }

    /**
     * 发布的消息主题.
     * 
     * @param subject
     * @return
     */
    public PublishData setSubject (String subject)
    {
        this.subject = subject;
        return this;
    }

    /**
     * 发布的消息内容.
     * 
     * @return
     */
    public String getMessage ()
    {
        return message;
    }

    /**
     * 发布的消息内容.
     * 
     * @param message
     * @return
     */
    public PublishData setMessage (String message)
    {
        this.message = message;
        return this;
    }

    /**
     * 本次接收消息的用户，不传则是推给订阅主题下的所有用户,用逗号分隔 可以是多个用户登录名。登录名为userid.
     * 
     * @return
     */
    public String getRole ()
    {
        return role;
    }

    /**
     * 本次接收消息的用户，不传则是推给订阅主题下的所有用户,用逗号分隔 可以是多个用户登录名。登录名为userid.
     * 
     * @param role
     * @return
     */
    public PublishData setRole (String role)
    {
        this.role = role;
        return this;
    }

    /**
     * 订阅的主题(为空，则使用AppKey作为默认主题).
     * 
     * @return
     */
    public String getTopic ()
    {
        return topic;
    }

    /**
     * 订阅的主题(为空，则使用AppKey作为默认主题).
     * 
     * @param topic
     * @return
     */
    public PublishData setTopic (String topic)
    {
        this.topic = topic;
        return this;
    }

    @Override
    public String toString ()
    {
        return "PublishData [subject=" + subject + ", message=" + message + ", role=" + role + ", topic=" + topic + "]";
    }
}