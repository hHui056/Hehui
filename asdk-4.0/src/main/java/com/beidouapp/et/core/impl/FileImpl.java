package com.beidouapp.et.core.impl;

import com.alibaba.fastjson.JSON;
import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.api.IFile;
import com.beidouapp.et.client.api.IM;
import com.beidouapp.et.client.callback.FileCallBack;
import com.beidouapp.et.client.callback.ICallback;
import com.beidouapp.et.client.domain.DocumentInfo;
import com.beidouapp.et.client.domain.EtMsg;
import com.beidouapp.et.common.constant.EtConstants;
import com.beidouapp.et.common.enums.FileTransferTypeEnum;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;
import com.beidouapp.et.util.FileTransportUtil;
import com.beidouapp.et.util.codec.EncryptUtil;
import com.beidouapp.et.util.param.CheckingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileImpl extends BaseFileImpl implements IFile {
    public static final Logger logger = LoggerFactory.getLogger(FileImpl.class);
    public static final String ERROR_MSG = "param is null!";
    public static final String USER_ID = "userid";
    public static final String USER_NAME = "username";
    public static final String USER = "user";
    private IM im;

    public FileImpl(IM im) {
        this.im = im;
        logger.debug("init File Module is successful.");
    }

    @Override
    public void fileTo(final String receiverId, String fileFullName,
                       final FileCallBack callBack) {

        logger.debug("call fileTo(receiverId={}, fileFullName={})", receiverId,
                fileFullName);
        CheckingUtil.checkNull(receiverId, "receiverId is null");
        CheckingUtil.checkNull(fileFullName, "fileFullName is null");
        CheckingUtil.checkNull(callBack, "callback is null");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            callBack.onFailure(null, e);
            return;
        }
        File f = new File(fileFullName);
        if (!f.exists()) {
            RuntimeException e = new IllegalArgumentException("file【"
                    + fileFullName + "】does not exist.");
            logger.error(e.getLocalizedMessage(), e);
            callBack.onFailure(fileFullName, e);
            return;
        }
        if (!f.isFile()) {
            RuntimeException e = new IllegalArgumentException(
                    "please input file, not the directory.");
            callBack.onFailure(fileFullName, e);
            logger.error(e.getLocalizedMessage(), e);
            return;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            callBack.onFailure(fileFullName, e);
            return;
        }
        // 1、通过HTTP协议上传文件.
        FileTransportUtil.asynUploadFile(fileFullName, new FileCallBack() {
            @Override
            public void onProcess(DocumentInfo documentInfo, String filePath,
                                  long currentIndex, long total) {
                callBack.onProcess(documentInfo, filePath, currentIndex, total);
            }

            @Override
            public void onSuccess(final DocumentInfo documentInfo,
                                  final String filePath) {
                documentInfo.setType(FileTransferTypeEnum.PUSH.getCode());
                String jsonText = JSON.toJSONString(documentInfo);
                // 3、发送消息给接收客户端.
                im.publish(TopicTypeEnum.SFILE.getCode() + receiverId,
                        jsonText, 1, "", new ICallback<EtMsg>() {
                            @Override
                            public void onSuccess(EtMsg value) {
                                callBack.onSuccess(documentInfo, filePath);
                            }

                            @Override
                            public void onFailure(EtMsg t, Throwable e) {
                                callBack.onFailure(filePath, e);
                            }
                        });
            }

            @Override
            public void onFailure(String fileFullPath, Throwable throwable) {
                callBack.onFailure(fileFullPath, throwable);
            }
        }, getParamsMap(this.im.getETContext()));
    }


    @Override
    public void fileTo(final String receiverId, final String fileFullName,
                       final String desc, final FileCallBack callBack) {
        logger.debug("call fileTo(receiverId={}, fileFullName={}, desc={})",
                receiverId, fileFullName, desc);
        CheckingUtil.checkNull(receiverId, "receiverId is null");
        CheckingUtil.checkNull(fileFullName, "fileFullName is null");
        CheckingUtil.checkNull(callBack, "callback is null");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            callBack.onFailure(null, e);
            return;
        }
        File f = new File(fileFullName);
        if (!f.exists()) {
            RuntimeException e = new IllegalArgumentException("file【"
                    + fileFullName + "】does not exist.");
            logger.error(e.getLocalizedMessage(), e);
            callBack.onFailure(fileFullName, e);
            return;
        }
        if (!f.isFile()) {
            RuntimeException e = new IllegalArgumentException("please input file, not the directory.");
            callBack.onFailure(fileFullName, e);
            logger.error(e.getLocalizedMessage(), e);
            return;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            callBack.onFailure(fileFullName, e);
            return;
        }
        // 1、通过HTTP协议上传文件.
        FileTransportUtil.asynUploadFile(fileFullName, new FileCallBack() {
            @Override
            public void onProcess(DocumentInfo documentInfo, String filePath,
                                  long currentIndex, long total) {
                callBack.onProcess(documentInfo, filePath, currentIndex, total);
            }

            @Override
            public void onSuccess(final DocumentInfo documentInfo,
                                  final String filePath) {
                documentInfo.setType(FileTransferTypeEnum.PUSH.getCode());
                documentInfo.setDescn(desc);
                String jsonText = JSON.toJSONString(documentInfo);
                // 3、发送消息给接收客户端.
                im.publish(TopicTypeEnum.SFILE.getCode() + receiverId,
                        jsonText, 1, "", new ICallback<EtMsg>() {
                            @Override
                            public void onSuccess(EtMsg value) {
                                callBack.onSuccess(documentInfo, filePath);
                            }

                            @Override
                            public void onFailure(EtMsg t, Throwable e) {
                                callBack.onFailure(filePath, e);
                            }
                        });
            }

            @Override
            public void onFailure(String fileFullPath, Throwable throwable) {
                callBack.onFailure(fileFullPath, throwable);
            }
        }, getParamsMap(this.im.getETContext()));
    }


    @Override
    public int downloadFile(DocumentInfo documentInfo, String saveFilePath) {
        logger.debug("call downloadFile(documentInfo={}, saveFilePath={})",
                documentInfo, saveFilePath);
        CheckingUtil.checkNull(saveFilePath, "please set the save path.");
        CheckingUtil.checkNull(documentInfo,
                "file information can not be empty.");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            throw e;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new EtRuntimeException(
                    EtExceptionCode.ET_ERR_LB_GET_SERVER_FAILED,
                    e.getLocalizedMessage(), e);
        }
        return FileTransportUtil.downloadFile(documentInfo, saveFilePath,
                getParamsMap(this.im.getETContext()));
    }


    @Override
    public int downloadFile(DocumentInfo documentInfo, String saveFilePath,
                            FileCallBack fileCallBack) {
        logger.debug(
                "call downloadFile(documentInfo={}, saveFilePath={}, fileCallBack={})",
                documentInfo, saveFilePath, fileCallBack);
        CheckingUtil.checkNull(saveFilePath, "please set the save path.");
        CheckingUtil.checkNull(documentInfo,
                "file information can not be empty.");
        CheckingUtil.checkNull(fileCallBack, "callback is null");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            fileCallBack.onFailure(saveFilePath, e);
            return -1;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            fileCallBack.onFailure(saveFilePath, e);
            return -1;
        }
        return FileTransportUtil.downloadFile(documentInfo, saveFilePath,
                fileCallBack, getParamsMap(this.im.getETContext()));
    }


    @Override
    public void asynDownloadFile(DocumentInfo documentInfo,
                                 String saveFilePath, FileCallBack fileCallBack) {
        logger.debug(
                "call asynDownloadFile(documentInfo={}, saveFilePath={}, fileCallBack={})",
                documentInfo, saveFilePath, fileCallBack);
        CheckingUtil.checkNull(documentInfo,
                "file information can not be empty.");
        CheckingUtil.checkNull(saveFilePath, "please set the save path.");
        CheckingUtil.checkNull(fileCallBack, "callback is null");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            fileCallBack.onFailure(null, e);
            return;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            fileCallBack.onFailure(saveFilePath, e);
            return;
        }
        FileTransportUtil.asynDownloadFile(documentInfo, saveFilePath,
                fileCallBack, getParamsMap(this.im.getETContext()));

    }


    @Override
    public void asynUploadFile(String fileFullPath, FileCallBack fileCallBack) {
        logger.debug("call asynUploadFile(fileFullPath={})", fileFullPath, fileCallBack);
        CheckingUtil
                .checkNull(fileFullPath, "please set the upload file path.");
        CheckingUtil.checkNull(fileCallBack, "callback is null");
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            fileCallBack.onFailure(fileFullPath, e);
            return;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            fileCallBack.onFailure(fileFullPath, e);
            return;
        }
        FileTransportUtil.asynUploadFile(fileFullPath, fileCallBack,
                getParamsMap(this.im.getETContext()));
    }

    @Override
    public DocumentInfo uploadFile(String fileFullPath, FileCallBack fileCallBack) {
        CheckingUtil
                .checkNull(fileFullPath, "please set the upload file path.");
        CheckingUtil.checkNull(fileCallBack, "callback is null");
        logger.debug("call uploadFile(fileFullPath={}, fileCallBack={})",
                fileFullPath, fileCallBack);
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            fileCallBack.onFailure(fileFullPath, e);
            return null;
        }
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            fileCallBack.onFailure(fileFullPath, e);
            return null;
        }
        return FileTransportUtil.realUploadFile(fileFullPath, fileCallBack,
                getParamsMap(this.im.getETContext()));
    }

    @Override
    public void sendMsg(String receiverId, DocumentInfo documentInfo,
                        final ICallback<EtMsg> callback) {
        logger.debug(
                "call sendMsg(receiverId={}, documentInfo={}, callback={})",
                receiverId, documentInfo, callback);
        CheckingUtil.checkNull(receiverId, "receiverId is null");
        CheckingUtil
                .checkNull(documentInfo.getType(),
                        "Please set the file format of the message type. pull/push/exception");
        CheckingUtil.checkNull(callback, "callback is null");
        String p2pTopic = TopicTypeEnum.SFILE.getCode() + receiverId;
        String jsonText = JSON.toJSONString(documentInfo);
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            callback.onFailure(null, e);
            return;
        }
        im.publish(p2pTopic, jsonText, "", new ICallback<EtMsg>() {
            @Override
            public void onSuccess(EtMsg value) {
                if (callback == null) {
                    return;
                }
                callback.onSuccess(value);
            }

            @Override
            public void onFailure(EtMsg t, Throwable value) {
                if (callback == null) {
                    return;
                }
                callback.onFailure(t, value);
            }
        });
    }


    @Override
    public int deleteFile(DocumentInfo documentInfo) {
        logger.debug("call deleteFile(documentInfo={})", documentInfo);
        try {
            obtainFileServerByLB(this.im.getETContext()); // 刷新文件服务器地址.
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new EtRuntimeException(EtExceptionCode.FILE_REMOVE,
                    e.getLocalizedMessage(), e);
        }
        if (!this.im.isConnected()) {
            EtRuntimeException e = new EtRuntimeException(
                    EtExceptionCode.IM_OFFLINE, "im is not connected.");
            logger.error("im is not connected.", e);
            return -1;
        }
        return FileTransportUtil.removeFile(documentInfo,
                getParamsMap(this.im.getETContext()));
    }

    /**
     * 获得加密部分.
     *
     * @param ctx 用户上下文参数配置接口
     * @return
     */
    private Map<String, Object> getParamsMap(IContext ctx) {
        Map<String, Object> params = new HashMap<String, Object>();
        String random = EncryptUtil.getRandomString(10);
        String uid = ctx.getUserName();
        params.put(EtConstants.ENCRYPT_KEY,
                this.encryptFileString(ctx.getAppKey(), uid, random));
        params.put(EtConstants.UID, uid);
        params.put(EtConstants.RANDOM_KEY, random);
        return params;
    }

    private String encryptFileString(String appKey, String userId, String random) {
        StringBuilder sb = new StringBuilder(appKey.length() + 34
                + random.length());
        sb.append(appKey).append(random).append(userId);
        return EncryptUtil.MD5(sb.toString());
    }
}