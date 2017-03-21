package com.beidouapp.et.client.domain;

/**
 * 群组信息.
 * 
 * @author mhuang.
 */
public class GroupInfo
{
    /** 群编码. */
    private String topic;

    /** 群名称. */
    private String groupname;

    public GroupInfo ()
    {

    }

    /**
     * 群编码.
     * 
     * @return
     */
    public String getTopic ()
    {
        return topic;
    }

    /**
     * 群编码.
     * 
     * @param topic
     */
    public void setTopic (String topic)
    {
        this.topic = topic;
    }

    /**
     * 群编码.
     * 
     * @return
     */
    public String getGroupname ()
    {
        return groupname;
    }

    /**
     * 群编码.
     * 
     * @param groupname
     */
    public void setGroupname (String groupname)
    {
        this.groupname = groupname;
    }

    @Override
    public String toString ()
    {
        return "GroupInfo [topic=" + topic + ", groupname=" + groupname + "]";
    }
}