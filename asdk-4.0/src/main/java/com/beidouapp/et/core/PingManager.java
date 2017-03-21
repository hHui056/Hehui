/**
 *
 */
package com.beidouapp.et.core;

import com.beidouapp.et.ErrorCode;
import com.beidouapp.et.ErrorInfo;
import com.beidouapp.et.IActionListener;
import com.beidouapp.et.util.Log;
import com.beidouapp.et.util.LogFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 内网心跳，每个tcp连接对应一个心跳
 *
 * @author allen
 */
public class PingManager {
    private static final String TAG = PingManager.class.getSimpleName();
    private Log LOG = LogFactory.getLog("java");
    private SDKContext mContext;
    private TcpNetworkConnect mNetworkConnect;
    private MessageCenter mMsgCenter;

    /**
     * 检查保活时间间隔
     */
    private final int CHECK_ALIVE_INTERVAL = 3000;
    /**
     * 发送保活PING REQ时间间隔
     */
    private short mKeepAliveInterval = 60;
    /**
     * 发送ping req 定时器
     */
    private Timer mSendTimer;
    /**
     * 检查ping res 定时器
     */
    private Timer mCheckTimer;
    /**
     * 上一个ping response的时间
     */
    volatile private long mLastInboundPingTime = 0;

    volatile boolean mRunning = false;

    public PingManager(SDKContext context, MessageCenter msgCenter,
                       TcpNetworkConnect networkConnect) {
        mContext = context;
        mMsgCenter = msgCenter;
        mNetworkConnect = networkConnect;
    }

    /**
     * 设置心跳间隔时间
     *
     * @param timeSecond
     */
    public void setKeepAliveInterval(short timeSecond) {
        mKeepAliveInterval = timeSecond;
    }

    /**
     * 开始ping
     */
    public void start() {
        mRunning = true;
        mLastInboundPingTime = System.currentTimeMillis();
        if (mSendTimer != null) {
            mSendTimer.cancel();
        }
        mSendTimer = new Timer();
        mSendTimer.schedule(new SendTask(), 1000, mKeepAliveInterval * 1000);

        if (mCheckTimer != null) {
            mCheckTimer.cancel();
        }
        mCheckTimer = new Timer();
        mCheckTimer.schedule(new CheckTask(), 0, CHECK_ALIVE_INTERVAL);
    }

    /**
     * 刷新ping的时间
     */
    public void refreshPing() {
        LOG.d(TAG, "心跳刷新！");
        mLastInboundPingTime = System.currentTimeMillis();
    }

    /**
     * 停止ping
     */
    public void stop() {
        mRunning = false;
        if (mSendTimer != null) {
            mSendTimer.cancel();
        }
        if (mCheckTimer != null) {
            mCheckTimer.cancel();
        }
    }

    /**
     * 释放资源
     */
    public void destory() {
        mSendTimer = null;
    }

    /**
     * 定时发送Ping req包
     */
    private class SendTask extends TimerTask {
        @Override
        public void run() {
            mNetworkConnect.sendPingReq(new IActionListener() {
                @Override
                public void onSuccess() {
                    LOG.d(TAG, "心跳发送成功");
                    // mLastInboundPingTime = System.currentTimeMillis();
                }

                @Override
                public void onFailure(ErrorInfo errorInfo) {
                    LOG.d(TAG, "心跳发送失败:" + errorInfo.getReason());
                    // XXX:发送心跳异常，通知context，可能tcp断开连接了
                }
            });
        }
    }

    /**
     * 定时检查是否还能和server正常收发数据。
     */
    private class CheckTask extends TimerTask {
        @Override
        public void run() {
            if (!mRunning) {
                return;
            }
            long curTime = System.currentTimeMillis();
            if (curTime - mLastInboundPingTime > mKeepAliveInterval * 1000) {
                mNetworkConnect.destory();
                mMsgCenter.notifyConnectLost(mNetworkConnect.getSvr(), ErrorCode.CONNECTION_LOST);
            } else {
                // LOG.d(TAG, "与server连接正常。");
            }
        }
    }

}
