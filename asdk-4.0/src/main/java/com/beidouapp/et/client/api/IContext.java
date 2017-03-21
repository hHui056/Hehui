package com.beidouapp.et.client.api;

/**
 * 用户参数配置.
 *
 * @author mhuang.
 */
public interface IContext
{
    /**
     * 设置系统参数.
     * 
     * @param key 参数键.
     * @param value 存储值.
     * @return
     */
    public <T> IContext set(String key, T value);

    /**
     * 获取系统参数设置.
     *
     * @param key 参数键.
     * @return 存储值.
     */
    public <T> T get(String key);

    public <T> T remove(String key);

    /**
     * 设置AppKey.
     *
     * @param appKey
     * @return
     */
    public IContext setAppKey(String appKey);

    /**
     * 获取AppKey.
     * 
     * @return
     */
    public String getAppKey();

    /**
     * 设置安全密钥Key.
     * 
     * @param secretKey
     * @return
     */
    public IContext setSecretKey(String secretKey);

    /**
     * 获取安全密钥Key.
     * 
     * @return
     */
    public String getSecretKey();

    /**
     * 设置 心跳时间.<br/>
     * 默认60秒.
     * 
     * @param keepAlive 心跳时间(秒).
     * @return
     */
    public IContext setKeepAlive(Short keepAlive);

    /**
     * 是否保存离线消息.<br/>
     * true：保存离线消息(默认)，false：删除离线消息.
     * 
     * @param cleanSession 值域[true,false].
     * @return
     */
    public IContext setCleanSession(Boolean cleanSession);

    /**
     * 是否保存离线消息.<br/>
     * true：保存离线消息(默认)，false：删除离线消息
     * 
     * @return
     */
    public Boolean getCleanSession();

    /**
     * 获取心跳时间.
     * 
     * @return
     */
    public Short getKeepAlive();

    /**
     * 设置默认QoS级别.<br/>
     * 默认为1.
     * 
     * @param qos 消息级别.
     * @return
     */
    public IContext setDefaultQos(Integer qos);

    /**
     * 获取默认消息级别.
     * 
     * @return
     */
    public Integer getDefaultQos();

    /**
     * 获取 消息保留标识.
     * 
     * @return
     */
    public Boolean getDefaultRetain();

    /**
     * 客户端首次连接的最大重试次数.超出该次数客户端将抛异常.<br/>
     * -1:无限次(默认). 0:不重连 .>0:重连次数.
     * 
     * @param count
     * @return
     */
    //public IContext setFirstReconnectCount (long count);

    /**
     * 首次连接重连次数.
     * 
     * @return
     */
    //public Long getFirstReconnectCount ();

    /**
     * 重连次数.
     * 
     * @param reconnectCount
     * @return
     */
    //public IContext setReconnectCount (long reconnectCount);

    /**
     * 重连次数.
     * 
     * @return
     */
    //public Long getReconnectCount ();

    /**
     * 设置 首次重连间隔时间.
     * 
     * @param reconnectDelay
     * @return
     */
    //public IContext setFirstReconnectDelay (Long reconnectDelay);

    /**
     * 获取 首次重连间隔时间.
     * 
     * @return
     */
    //public Long getFirstReconnectDelay ();

    /**
     * 设置 重连间隔时间上限.
     * 
     * @param reconnectDelayMax
     * @return
     */
    //public IContext setReconnectDelayMax (Long reconnectDelayMax);

    /**
     * 获取 重连间隔时间上限.
     * 
     * @return
     */
    //public Long getReconnectDelayMax ();

    /**
     * 设置 默认实例化对象超时时间.<br/>
     * 默认2000ms.
     * 
     * @param defaultInstanceTimeout
     * @return
     */
    public IContext setDefaultInstanceTimeout(Long defaultInstanceTimeout);

    /**
     * 获取 默认实例化对象超时时间.
     * 
     * @return
     */
    public Long getDefaultInstanceTimeout();

    /**
     * 设置 是否启用SSL.<br/>
     * 默认false.
     * 
     * @param sslFlag
     */
    public IContext setSSLEnable(Boolean sslFlag);

    /**
     * 获取 是否启用SSL.
     * 
     * @return
     */
    public Boolean getSSLEnable();

    /**
     * 设置 服务器域名或IP.
     * 
     * @param domain 域名或IP.
     * @return
     */
    public IContext setServerDomain(String domain);

    /**
     * 设置 开启SDK内部跟踪信息.<br/>
     * true:开启,false:关闭(默认).
     * 
     * @return
     */
    public IContext setTracerEnable(Boolean isTracer);

    /**
     * 设置 开启SDK内部跟踪信息.<br/>
     * true:开启,false:关闭(默认).
     * 
     * @return
     */
    public Boolean getTracerEnable();

    //---------------------------------------------
    ///**
    // * 设置客户端Id.
    // *
    // * @param clientId
    // * @return
    // */

    //public IContext setClientId (String clientId);


    /**
     * 获取客户端Id.
     *
     * @return
     */
    //public String getClientId ();

    public IContext setUserName(String userName);
    public String getUserName();


    /**
     * 设置 服务器端口.
     *
     * @param port 端口.
     * @return
     */
    @Deprecated
    public IContext setServerPort(String port);

    /**
     * 设置 服务器端口.
     *
     * @param port 端口.
     * @return
     */
    public IContext setServerPort(int port);
}