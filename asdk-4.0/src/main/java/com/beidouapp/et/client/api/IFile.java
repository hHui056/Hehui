package com.beidouapp.et.client.api;

import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.client.domain.EtMsg;

/**
 * 文件操作接口.
 *
 * @author mhuang.
 */
public interface IFile {


    /**
     * 主动发送文件.
     *
     * @param receiverId   接收文件的用户ID.
     * @param fileFullName 文件全路径名.
     * @param callBack     文件操作回调接口.
     */
    public void fileTo(final String receiverId, String fileFullName, final FileCallBack callBack);


    /**
     * 主动发送文件.<br/>
     * 用户可以设置文件描述信息.
     *
     * @param receiverId   接收文件的用户ID.
     * @param fileFullName 文件全路径名.
     * @param desc         文件描述信息.
     * @param callBack     文件操作回调接口.
     */
    public void fileTo(final String receiverId, String fileFullName, final String desc, final FileCallBack callBack);


    /**
     * 下载文件.
     *
     * @param documentInfo 文件信息.
     * @param saveFilePath 文件保存路径(需用户指定).
     * @return 0:成功； 非0:失败
     */
    public int downloadFile(DocumentInfo documentInfo, String saveFilePath);


    /**
     * 下载文件.
     *
     * @param documentInfo 文件信息.
     * @param saveFilePath 文件保存路径(需用户指定).
     * @param fileCallBack 文件操作回调接口.
     * @return 0:成功； 非0:失败.
     */
    public int downloadFile(DocumentInfo documentInfo, String saveFilePath, FileCallBack fileCallBack);


    /**
     * 异步下载文件.
     *
     * @param documentInfo 文件信息.
     * @param saveFilePath 文件保存路径(需用户指定).
     * @param fileCallBack 文件操作回调接口.
     */
    public void asynDownloadFile(DocumentInfo documentInfo, String saveFilePath, FileCallBack fileCallBack);


    /**
     * 上传文件.
     *
     * @param fileFullPath 上传文件的全路径.
     * @param fileCallBack 文件操作回调接口.
     * @return 文件信息对象.
     */
    public DocumentInfo uploadFile(String fileFullPath, FileCallBack fileCallBack);


    /**
     * 异步上传文件.
     *
     * @param fileFullPath 上传文件的全路径.
     * @param fileCallBack 文件操作回调接口.
     */
    public void asynUploadFile(String fileFullPath, FileCallBack fileCallBack);


    /**
     * 发送点对点消息给对方.
     *
     * @param receiverId   接收消息的用户ID.
     * @param documentInfo 消息.
     * @param callback     消息发送后的回调监听器.
     */
    public void sendMsg(String receiverId, DocumentInfo documentInfo, final ICallback<EtMsg> callback);

    /**
     * 删除文件(非校验).
     *
     * @param documentInfo 文件信息.
     * @return 0 成功, 失败则抛File 删除异常(10804).
     */
    public int deleteFile(DocumentInfo documentInfo);

}