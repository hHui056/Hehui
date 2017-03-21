package com.beidouapp.et.core.pojo;

import java.io.Serializable;

/**
 * 基本的负载均衡协议信息.
 *
 * @author mhuang.
 */
public class BaseLBInfo implements Serializable {
    private static final long serialVersionUID = 115676784125698704L;

    /**
     * 协议头标识.
     */
    private String header;

    /**
     * 协议版本.
     */
    private int version;

    /**
     * 协议剩余长度(不包含total).
     */
    private int total;

    /**
     * 加密类型.
     */
    private int encryType;

    /**
     * 请求类型.
     */
    private int requestType;


    public String getHeader() {
        return header;
    }

    public BaseLBInfo setHeader(String header) {
        this.header = header;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public BaseLBInfo setVersion(int version) {
        this.version = version;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public BaseLBInfo setTotal(int total) {
        this.total = total;
        return this;
    }

    public int getEncryType() {
        return encryType;
    }

    public BaseLBInfo setEncryType(int encryType) {
        this.encryType = encryType;
        return this;
    }

    public int getRequestType() {
        return requestType;
    }

    public BaseLBInfo setRequestType(int requestType) {
        this.requestType = requestType;
        return this;
    }

    @Override
    public String toString() {
        return "BaseLBInfo{" +
                "header='" + header + '\'' +
                ", version=" + version +
                ", total=" + total +
                ", encryType=" + encryType +
                ", requestType=" + requestType +
                '}';
    }
}