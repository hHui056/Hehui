package com.beidouapp.et;

/**
 * Created by ray on 2016/12/28.
 * <p>
 * 查询iLink时间的回调
 */
public interface TimeListener {
    /**
     * @param time 系统回复的时间
     */
    public void onResult(long time);
}
