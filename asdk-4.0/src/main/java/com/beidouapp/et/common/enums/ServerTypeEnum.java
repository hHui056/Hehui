package com.beidouapp.et.common.enums;

/**
 * 服务器类型枚举.
 * 
 * @author mhuang.
 */
public enum ServerTypeEnum
{
    /**
     * 消息服务器.
     */
    IM ("IM", "IM服务器"),

    /**
     * Web服务器.
     */
    WS ("WS", "Web服务器"),

    /**
     * File服务器.
     */
    FS ("FS", "File服务器");

    /** 字典类型代码. */
    private String m_code;

    /** 字典类型名称. */
    private String m_name;

    /**
     * 构造一个类型.
     * 
     * @param code 代码
     * @param name 名称
     */
    private ServerTypeEnum (String code, String name)
    {
        this.m_code = code;
        this.m_name = name;
    }

    /**
     * 获得类型代码.
     * 
     * @return 类型代码
     */
    public String getCode ()
    {
        return m_code;
    }

    /**
     * 设置类型代码.
     * 
     * @param code 类型代码
     */
    public void setCode (String code)
    {
        this.m_code = code;
    }

    /**
     * 获得类型名称.
     * 
     * @return 类型名称
     */
    public String getName ()
    {
        return m_name;
    }

    /**
     * 设置类型名称.
     * 
     * @param name 类型名称
     */
    public void setName (String name)
    {
        this.m_name = name;
    }
}