package com.beidouapp.et;

/**
 * Created by allen on 2017/2/8.
 */

/**
 * qos等级枚举
 */
public enum Qos {
    /**
     * 最多一次.
     */
    QoS_0(0, "AT_MOST_ONCE"),

    /**
     * 至少一次.
     */
    QoS_1(1, "AT_LEAST_ONCE"),

    /**
     * 仅此一次.
     */
    QoS_2(2, "EXACTLY_ONCE");

    /**
     * 字典类型代码.
     */
    private int code;

    /**
     * 字典类型名称.
     */
    private String m_name;

    /**
     * 构造一个类型.
     *
     * @param code 代码
     * @param name 名称
     */
    private Qos(int code, String name) {
        this.code = code;
        this.m_name = name;
    }

    /**
     * 获得类型代码.
     *
     * @return 类型代码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置类型代码.
     *
     * @param code 类型代码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获得类型名称.
     *
     * @return 类型名称
     */
    public String getName() {
        return m_name;
    }

    /**
     * 设置类型名称.
     *
     * @param name 类型名称
     */
    public void setName(String name) {
        this.m_name = name;
    }
}