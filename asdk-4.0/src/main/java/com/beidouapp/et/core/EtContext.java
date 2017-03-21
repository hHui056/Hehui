package com.beidouapp.et.core;

import com.beidouapp.et.client.api.IContext;
import com.beidouapp.et.client.callback.IReceiveListener;
import com.beidouapp.et.common.constant.EtKeyConstant;
import com.beidouapp.et.exception.EtExceptionCode;
import com.beidouapp.et.exception.EtRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ET全局上下文.
 *
 * @author mhuang.
 */
public class EtContext implements IContext {
    /**
     * 配置缓存.
     */
    private Map<String, Object> ctxCacheMap = new ConcurrentHashMap<String, Object>();

    public EtContext() {
        setDefaultQos(1).setKeepAlive((short) 60).setTracerEnable(Boolean.FALSE);
        setDefaultInstanceTimeout(3000L);
        set(EtKeyConstant.DEFAULT_RETAIN, Boolean.FALSE).setCleanSession(Boolean.TRUE).setSSLEnable(Boolean.FALSE);
        set(EtKeyConstant.IS_EXISTS_HTTP, false).set(EtKeyConstant.IS_EXISTS_FILE, false);
    }

    /**
     * 设置系统参数.
     *
     * @param key
     * @param value
     * @return
     */
    public <T> IContext set(String key, T value) {
        if (key == null) {
            throw new EtRuntimeException(EtExceptionCode.PARAM_ILLEGAL);
        }
        ctxCacheMap.put(key, value);
        return this;
    }

    /**
     * 获取系统参数设置.
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (key == null) {
            throw new EtRuntimeException(EtExceptionCode.PARAM_NULL);
        }
        if (ctxCacheMap.containsKey(key)) {
            return (T) ctxCacheMap.get(key);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T remove(String key) {
        if (key == null) {
            throw new EtRuntimeException(EtExceptionCode.PARAM_NULL);
        }
        return (T) ctxCacheMap.remove(key);
    }

    /**
     * 设置AppKey.
     *
     * @param appKey
     * @return
     */
    public IContext setAppKey(String appKey) {
        set(EtKeyConstant.APP_KEY, appKey);
        return this;
    }

    /**
     * 获取AppKey.
     *
     * @return
     */
    public String getAppKey() {
        return get(EtKeyConstant.APP_KEY);
    }

    /**
     * 设置安全密钥Key.
     *
     * @param secretKey
     * @return
     */
    public IContext setSecretKey(String secretKey) {
        set(EtKeyConstant.SECRET_KEY, secretKey);
        return this;
    }

    /**
     * 获取安全密钥Key.
     *
     * @return
     */
    public String getSecretKey() {
        return get(EtKeyConstant.SECRET_KEY);
    }

    /**
     * 设置客户端Id.
     *
     * @param clientId
     * @return
     */
    //public IContext setClientId(String clientId) {
    //    set(EtKeyConstant.USER_NAME, clientId);
    //    return this;
    //}

    @Override
    public IContext setUserName (String userName){
        set(EtKeyConstant.USER_NAME, userName);
        return this;
    }

    /**
     * 获取客户端Id.<br/>
     * 如果存在，则返回空字符串"",而不是null.
     *
     * @return
     */

    //public String getClientId() {
    //    if (ctxCacheMap.containsKey(EtKeyConstant.USER_NAME)) {
    //        return (String) ctxCacheMap.get(EtKeyConstant.USER_NAME);
    //    } else {
    //        return "";
    //    }
    //}

    @Override
    public String getUserName() {
        if (ctxCacheMap.containsKey(EtKeyConstant.USER_NAME)) {
            return (String) ctxCacheMap.get(EtKeyConstant.USER_NAME);
        } else {
            return "";
        }
    }

    /**
     * 设置 心跳时间.<br/>
     * 默认60秒.
     *
     * @param keepAlive 心跳时间(毫秒).
     * @return
     */
    @Override
    public IContext setKeepAlive(Short keepAlive) {
        set(EtKeyConstant.KEEP_ALIVE, keepAlive);
        return this;
    }

    /**
     * 获取心跳时间.
     *
     * @return
     */
    @Override
    public Short getKeepAlive() {
        return get(EtKeyConstant.KEEP_ALIVE);
    }

    /**
     * 设置默认QoS级别.<br/>
     * 默认为1.
     *
     * @param qos 消息级别.
     * @return
     */
    @Override
    public IContext setDefaultQos(Integer qos) {
        set(EtKeyConstant.DEFAULT_QOS, qos);
        return this;
    }

    /**
     * 获取默认消息级别.
     *
     * @return
     */
    @Override
    public Integer getDefaultQos() {
        return get(EtKeyConstant.DEFAULT_QOS);
    }

    /**
     * 获取 消息保留标识.
     *
     * @return
     */
    @Override
    public Boolean getDefaultRetain() {
        return get(EtKeyConstant.DEFAULT_RETAIN);
    }

    /**
     * 客户端首次连接的最大重试次数.超出该次数客户端将抛异常.<br/>
     * -1:无限次(默认). 0:不重连 .>0:重连次数.
     *
     * @param count
     * @return
     */
    //@Override
    //public IContext setFirstReconnectCount(long count) {
    //    set(EtKeyConstant.FIRST_MAX_CONNECT_COUNT, count);
    //    return this;
    //}

    /**
     * 首次连接重连次数.
     *
     * @return
     */
    //@Override
    //public Long getFirstReconnectCount() {
    //    return get(EtKeyConstant.FIRST_MAX_CONNECT_COUNT);
    //}

    /**
     * 重连次数.
     *
     * @param reconnectCount
     * @return
     */
    //@Override
    //public IContext setReconnectCount(long reconnectCount) {
    //    set(EtKeyConstant.RECONNECT_COUNT, reconnectCount);
    //    return this;
    //}

    /**
     * 重连次数.
     *
     * @return
     */
    //@Override
    //public Long getReconnectCount() {
    //    return get(EtKeyConstant.RECONNECT_COUNT);
    //}

    /**
     * 设置 首次重连间隔时间.
     *
     * @param reconnectDelay
     * @return
     */
    //@Override
    //public IContext setFirstReconnectDelay(Long reconnectDelay) {
    //    set(EtKeyConstant.FIRST_RECONNECT_DELAY, reconnectDelay);
    //    return this;
    //}

    /**
     * 获取 首次重连间隔时间.
     *
     * @return
     */
    //@Override
    //public Long getFirstReconnectDelay() {
    //    return get(EtKeyConstant.FIRST_RECONNECT_DELAY);
    //}

    /**
     * 设置 重连间隔时间上限.
     *
     * @param reconnectDelayMax
     * @return
     */
    //@Override
    //public IContext setReconnectDelayMax(Long reconnectDelayMax) {
    //    set(EtKeyConstant.RECONNECT_DELAY_MAX, reconnectDelayMax);
    //    return this;
    //}

    /**
     * 获取 重连间隔时间上限.
     *
     * @return
     */
    //@Override
    //public Long getReconnectDelayMax() {
    //    return get(EtKeyConstant.RECONNECT_DELAY_MAX);
    //}

    /**
     * 设置 默认实例化对象超时时间.<br/>
     * 默认2000ms.
     *
     * @param defaultInstanceTimeout
     * @return
     */
    @Override
    public IContext setDefaultInstanceTimeout(Long defaultInstanceTimeout) {
        set(EtKeyConstant.DEFAULT_INSTANCE_TIMEOUT, defaultInstanceTimeout);
        return this;
    }

    /**
     * 获取 默认实例化对象超时时间.
     *
     * @return
     */
    @Override
    public Long getDefaultInstanceTimeout() {
        return get(EtKeyConstant.DEFAULT_INSTANCE_TIMEOUT);
    }

    public IContext setReceiveListener(IReceiveListener receiveListener) {
        set(EtKeyConstant.RECEIVE_LISTENER, receiveListener);
        return this;
    }

    public IReceiveListener getReceiveListener() {
        return get(EtKeyConstant.RECEIVE_LISTENER);
    }

    @Override
    public IContext setCleanSession(Boolean cleanSession) {
        set(EtKeyConstant.CLEAN_SESSION_STATUS, cleanSession);
        return this;
    }

    @Override
    public Boolean getCleanSession() {
        return get(EtKeyConstant.CLEAN_SESSION_STATUS);
    }

    @Override
    public Boolean getSSLEnable() {
        return get(EtKeyConstant.SSL_ENABLE_STATUS);
    }

    @Override
    public IContext setSSLEnable(Boolean sslFlag) {
        set(EtKeyConstant.SSL_ENABLE_STATUS, sslFlag);
        return this;
    }

    @Override
    public IContext setServerDomain(String domain) {
        set(EtKeyConstant.LB_IP, domain);
        return this;
    }

    @Override
    public IContext setServerPort(String port) {
        set(EtKeyConstant.LB_PORT, Integer.valueOf(port));
        return this;
    }

    @Override
    public IContext setServerPort(int port) {
        set(EtKeyConstant.LB_PORT, port);
        return this;
    }

    @Override
    public IContext setTracerEnable(Boolean sslFlag) {
        set(EtKeyConstant.TRACER_ENABLE, sslFlag);
        return this;
    }

    @Override
    public Boolean getTracerEnable() {
        return get(EtKeyConstant.TRACER_ENABLE);
    }
}