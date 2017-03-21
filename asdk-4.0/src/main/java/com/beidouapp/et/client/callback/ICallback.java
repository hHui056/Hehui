package com.beidouapp.et.client.callback;

/**
 * 回调基类.
 *
 * @param <T>
 * @author mhuang.
 */
public interface ICallback <T>
{
    /**
     * 成功.
     * 
     * @param value
     */
    public void onSuccess(T value);

    /**
     * 失败.
     * 
     * @param value
     */
    public void onFailure(T t, Throwable value);
}