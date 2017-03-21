package com.beidouapp.et.core.pojo;

/**
 * 负载均衡响应信息.<br/>
 * 当requestType为AV(3)时，需要另一种解析.
 *
 * @author mhuang.
 */
public class LBResponseInfo extends BaseLBInfo {
    /**
     * 超时时间(秒.).
     */
    private long timeout;

    /**
     * 数据的字符串表示.
     */
    private String data;

    /**
     * IP or 域名.
     */
    private String domain;

    /**
     * 端口.
     */
    private int port;

    /**
     * 过期时间(ms).
     */
    private long timeExpiration;


    public long getTimeout() {
        return timeout;
    }

    public LBResponseInfo setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getData() {
        return data;
    }

    public LBResponseInfo setData(String data) {
        this.data = data;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public LBResponseInfo setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public int getPort() {
        return port;
    }

    public LBResponseInfo setPort(int port) {
        this.port = port;
        return this;
    }

    public long getTimeExpiration() {
        return timeExpiration;
    }

    public LBResponseInfo setTimeExpiration(long timeExpiration) {
        this.timeExpiration = timeExpiration;
        return this;
    }

    @Override
    public String toString() {
        return "LBResponseInfo{" +
                "timeout=" + timeout +
                ", data='" + data + '\'' +
                ", domain='" + domain + '\'' +
                ", port=" + port +
                ", timeExpiration=" + timeExpiration +
                "} " + super.toString();
    }
}