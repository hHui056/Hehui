package com.beidouapp.et.core;

import com.beidouapp.et.ErrorCode;
import com.beidouapp.et.ErrorInfo;
import com.beidouapp.et.Server;
import com.beidouapp.et.core.pojo.LBResponseInfo;
import com.beidouapp.et.handler.impl.ObtainIPHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ray
 *         <p>
 *         通过LB发现负载均衡的服务器host和port
 */
public class DiscoverMqttSvrService {
    private static int TIMEOUT = 5000;
    private int mTimeoutMS = TIMEOUT;
    private SDKContext mContext;
    private OnDiscoverMqttSvrListener mOnDiscoverMqttSvrListener;
    private Timer mTimer;
    private volatile State mState = State.idle;
    /**
     * 缓存发现的服务器
     */
    private LBResponseInfo LBServerInfo = null;

    public DiscoverMqttSvrService(SDKContext ctx) {
        mContext = ctx;
    }


    /**
     * 检测mqtt服务器
     *
     * @param timeoutSecond 发现服务器超时时间
     * @param listener
     */
    public void doDiscover(int timeoutSecond, OnDiscoverMqttSvrListener listener) {
        mTimeoutMS = timeoutSecond * 1000;
        mOnDiscoverMqttSvrListener = listener;
        mState = State.discovering;
        startTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (LBServerInfo == null || LBServerInfo.getTimeExpiration() <= System.currentTimeMillis()) {
                        ObtainIPHandler ipHandler = new ObtainIPHandler();
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("serverType", 1);
                        params.put("domain", mContext.getContextParameters().getBlanceServerAddress());
                        params.put("port", mContext.getContextParameters().getBlanceServerPort());
                        params.put("userId", mContext.getContextParameters().getUid());
                        params.put("soTimeout", 0);
                        LBServerInfo = ipHandler.execute(params);
                    }
                    if (mState == State.discovering) {
                        mState = State.idle;
                        cancelTimer();
                        String mqttIp = LBServerInfo.getDomain();
                        int mqttPort = LBServerInfo.getPort();
                        Server mqttServer = new Server(Server.TYPE_WAN, Server.PROXY_SERVER_ID, mqttIp, mqttPort);
                        if (mOnDiscoverMqttSvrListener != null) {
                            mOnDiscoverMqttSvrListener.onResult(mqttServer);
                        }
                    }

                } catch (Exception e) {
                    if (mState == State.discovering) {
                        mState = State.idle;
                        cancelTimer();

                        if (mOnDiscoverMqttSvrListener != null) {
                            mOnDiscoverMqttSvrListener.onFailure(new ErrorInfo(ErrorCode.DISCOVER_SERVER_FAIL, e.getMessage()));
                        }
                    }
                }
            }
        }).start();
    }

    private void startTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mState == State.discovering) {
                    mState = State.timeout;
                    if (mOnDiscoverMqttSvrListener != null) {
                        mOnDiscoverMqttSvrListener.onFailure(new ErrorInfo(ErrorCode.DISCOVER_SERVER_FAIL, "discover timeout"));
                    }
                }
            }
        }, mTimeoutMS);
    }

    private void cancelTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private enum State {
        idle, discovering, timeout
    }

    /**
     * 检测mqtt服务器的回调
     */
    public static interface OnDiscoverMqttSvrListener {
        public void onResult(Server mqttSvr);

        public void onFailure(ErrorInfo info);

    }
}
