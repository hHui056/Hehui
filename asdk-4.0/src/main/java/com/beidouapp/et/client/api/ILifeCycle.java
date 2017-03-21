package com.beidouapp.et.client.api;

/**
 * 生命周期接口.
 *
 * @author mhuang .
 */
public interface ILifeCycle {
    /**
     * 连接服务器.
     */
    public void connect();

    /**
     * 释放资源.
     */
    public void destroy();
}
