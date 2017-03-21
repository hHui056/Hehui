/**
 *
 */
package com.beidouapp.et.core;

import com.beidouapp.et.IActionListener;
import com.beidouapp.et.Server;

/**
 * 网络连接，绑定一个指定的服务器，发送，订阅，接收消息，每个连接需要维护一个心跳。
 *
 * @author allen
 */
public interface INetworkConnect {

    /**
     * 发送消息到指定的设备
     *
     * @param msg 消息
     */
    public void chatTo(EtMessage msg, IActionListener actionListener);

    public void publish(EtMessage msg, IActionListener actionListener);

    public void subscribe(EtMessage msg, IActionListener actionListener);

    public void unsubscribe(EtMessage msg, IActionListener actionListener);

    /**
     * 异步连接到已经绑定的服务器
     */
    public void connect(IActionListener actionListener);

    /**
     * 与已经绑定的服务器断开连接
     */
    public void disconnect(IActionListener actionListener);

    /**
     * 唯一标示
     */
    public String getIdentifer();

    /**
     * 释放资源
     */
    public void destory();

    /**
     * @return 已经绑定的服务器
     */
    public Server getSvr();

    /**
     * 重连服务器
     * @param actionListener
     */
    public void reConnect(IActionListener actionListener);

}
