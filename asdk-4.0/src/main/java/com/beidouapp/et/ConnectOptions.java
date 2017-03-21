
package com.beidouapp.et;

/**
 * 连接服务器的参数。
 */
public class ConnectOptions {
    /**
     * 与服务器的默认保持连接时间。
     */
    public static final short KEEP_ALIVE_INTERVAL_DEFAULT = 60;
    /**
     * 默认连接服务器的请求时间。
     */
    public static final int CONNECTION_TIMEOUT_DEFAULT = 30;
    /**
     * 服务器默认不保留离线消息。
     */
    public static final boolean CLEAN_SESSION_DEFAULT = true;
    private short keepAliveInterval = KEEP_ALIVE_INTERVAL_DEFAULT;
    private boolean cleanSession = CLEAN_SESSION_DEFAULT;
    private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;

    public ConnectOptions() {

    }

    /**
     * 获取与服务器的连接保活时间。
     *
     * @return 单位秒。
     */
    public short getKeepAliveInterval() {
        return keepAliveInterval;
    }

    /**
     * 设置与服务器的连接保活时间。
     *
     * @param keepAliveInterval 保活时间，秒。
     */
    public void setKeepAliveInterval(short keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    /**
     * 获取服务器保留离线消息标志。
     *
     * @return true, 不保留；false，保留。
     */
    public boolean getCleanSession() {
        return cleanSession;
    }

    /**
     * 设置服务器是否保留离线消息。
     *
     * @param cleanSession true,不保留；false，保留。
     */
    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    /**
     * 获取连接的请求超时时间。
     *
     * @return 超时时间，单位秒。
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置连接的请求超时时间。
     *
     * @param connectionTimeout 超时时间，单位秒。
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

}
