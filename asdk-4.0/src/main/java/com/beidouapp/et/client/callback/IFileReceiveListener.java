package com.beidouapp.et.client.callback;

import com.beidouapp.et.client.domain.DocumentInfo;

/**
 * 用户文件传输处理接口.
 * 
 * @author mhuang.
 */
public interface IFileReceiveListener extends IListener
{
    /**
     * 接收文件处理.
     * 
     * @param senderId 发送者ID.
     * @param documentInfo 文件信息对象.
     */
    public void onReceived (String senderId, DocumentInfo documentInfo);

    /**
     * 检查文件是否存在
     * 
     * @param senderId 发送者ID.
     * @param documentInfo 文件信息对象.
     */
    public void onCheckingFile (String senderId, DocumentInfo documentInfo);
}
