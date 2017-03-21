package com.beidouapp.et;

/**
 * Created by allen on 2017/1/3.
 */

public interface StatusListener {
    /**
     * 返回用户状态成功
     *
     * @param uid  用户uid
     * @param code 用户状态code   1为在线，0为离线
     */
    public void onSuccess(String uid, int code);

    /**
     * 查询用户状态失败
     *
     * @param info 失败原因
     */
    public void onFailure(ErrorInfo info);
}
