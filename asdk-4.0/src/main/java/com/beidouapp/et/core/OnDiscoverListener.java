package com.beidouapp.et.core;

import com.beidouapp.et.ErrorInfo;
import com.beidouapp.et.Server;

/**
 * 发现服务器的回调，只用于内网。
 */
public interface OnDiscoverListener {

    /**
     * 扫描到终端服务器
     *
     * @param peer 已经发现的终端服务器
     */
    public void onResult(Server peer);

    /**
     * 发现终端服务器失败
     */
    public void onDiscoverFail(ErrorInfo errorInfo);

    /**
     * 仅表示搜索动作成功，不表示成功搜到到server
     */
    public void onSuccess();
}
