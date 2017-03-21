package com.beidouapp.et.client.callback;

import com.beidouapp.et.client.domain.DocumentInfo;

/**
 * 文件操作回调接口.
 * 
 * @author mhuang.
 */
public interface FileCallBack
{
    /**
     * 文件正在处理回调.
     * 
     * @param documentInfo 此时 documentInfo对象中信息尚不完全,但文件名称、大小已经可用.
     * @param fileFullPath 文件全路径.
     * @param currentIndex 当前处理索引.
     * @param total 总数.
     */
    public void onProcess(DocumentInfo documentInfo, String fileFullPath, long currentIndex, long total);

    /**
     * 文件操作成功回调.
     * 
     * @param documentInfo 文件信息.
     * @param fileFullPath 文件全路径.
     */
    public void onSuccess(DocumentInfo documentInfo, String fileFullPath);

    /**
     * 文件操作失败的回调
     * 
     * @param fileFullPath 文件全路径.
     * @param throwable 异常信息.
     */
    public void onFailure(String fileFullPath, Throwable throwable);
}