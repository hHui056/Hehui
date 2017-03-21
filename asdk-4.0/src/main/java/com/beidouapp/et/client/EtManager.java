package com.beidouapp.et.client;

import com.beidouapp.et.client.api.IFile;
import com.beidouapp.et.client.api.ILifeCycle;
import com.beidouapp.et.client.api.IM;
import com.beidouapp.et.client.api.IWeb;
import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.callback.IFileReceiveListener;
import com.beidouapp.et.client.callback.IReceiveListener;
import com.beidouapp.et.client.callback.IUserStatusListener;

/**
 * 服务管理器.
 *
 * @author mhuang.
 */
public interface EtManager extends ILifeCycle {
    /**
     * 设置连接回调方法.
     *
     * @param cb
     */
    void setConnectCallback(ICallback<Void> cb);

    /**
     * 设置消息监听器，接收服务器推送的消息内容.
     *
     * @param listener
     */
    void setListener(IReceiveListener listener);

    /**
     * 设置文件监听器，接收服务器推送的文件信息.
     *
     * @param fileListener
     */
    void setFileListener(IFileReceiveListener fileListener);

    /**
     * 设置用户状态监听器.
     *
     * @param userStatusListener
     */
    void setUserStatusListener(IUserStatusListener userStatusListener);

    /**
     * 获得IM 消息模块.
     *
     * @return
     */
    IM getIm();

    /**
     * 获得 Web 服务模块.<br/>
     * 如果为空，则抛Web功能未配置异常(10702).
     *
     * @return
     */
    IWeb getWeb();

    /**
     * 获得File 服务模块.<br/>
     * 如果为空，则抛File功能未配置异常(10802).
     *
     * @return
     */
    IFile getFile();

    /**
     * 获取SDK版本.
     *
     * @return
     */
    String getSdkVersion();
}
