package com.beidouapp.et.common.enums;

/**
 * 连接状态枚举.
 *
 * @author mhuang.
 */
public enum ConnectStateEnum
{
    /**
     * 已连接.
     */
    CONNECTED((byte) 0, "connected"),

    /**
     * 正在连接.
     */
    CONNECTING((byte) 1, "connecting"),


    /**
     * 正在断开.
     */
    DISCONNECTING((byte) 2, "disconnecting"),

    /**
     * 已断开.
     */
    DISCONNECTED((byte) 3, "disconnected");

    /**
     * 字典类型代码.
     */
    private byte code;

    /**
     * 字典类型名称.
     */
    private String name;


    /**
     * 构造一个类型.
     *
     * @param code 代码
     * @param name 名称
     */
    ConnectStateEnum(byte code, String name)
    {
        this.code = code;
        this.name = name;
    }

    /**
     * 获得类型代码.
     *
     * @return 类型代码
     */
    public byte getCode()
    {
        return code;
    }

    /**
     * 设置类型代码.
     *
     * @param code 类型代码
     */
    public void setCode(byte code)
    {
        this.code = code;
    }

    /**
     * 获得类型名称.
     *
     * @return 类型名称
     */
    public String getName()
    {
        return name;
    }

    /**
     * 设置类型名称.
     *
     * @param name 类型名称
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * 通过编码查询对应枚举.
     *
     * @param code 枚举值.
     * @return 连接状态枚举.
     */
    public static ConnectStateEnum getEnumByCode(byte code)
    {
        for (ConnectStateEnum e : ConnectStateEnum.values())
        {
            if (e.getCode() == code)
            {
                return e;
            }
        }
        throw new RuntimeException(String.format("Can not find the enumeration by code {%d}.", code));
    }
}