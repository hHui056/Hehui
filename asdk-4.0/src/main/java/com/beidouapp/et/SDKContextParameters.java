/**
 *
 */
package com.beidouapp.et;


/**
 * A-SDK的配置参数。
 */
public class SDKContextParameters {

    /**
     * 本地ip地址，用于discover
     */
    private String localIp = "";
    /**
     * app的userId
     */
    private String uid;
    private String appKey = "";
    private String secretKey = "";
    private String blanceServerAddress = null;
    private int blanceServerPort = 0;

    /**
     * 获取UID。
     *
     * @return
     */
    public String getUid() {
        return uid;
    }

    /**
     * 设置UID。
     *
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * 获取AppKey
     *
     * @return
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * 设置AppKey
     *
     * @param appKey
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * 获取SecretKey
     *
     * @return
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置SecretKey
     *
     * @param secretKey
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * 获取服务器的地址，可以是IP或者域名。
     *
     * @return
     */
    public String getBlanceServerAddress() {
        return blanceServerAddress;
    }

    /**
     * 设置服务器的地址，可以是IP或者域名。
     *
     * @param blanceServerAddress
     */
    public void setBlanceServerAddress(String blanceServerAddress) {
        this.blanceServerAddress = blanceServerAddress;
    }

    /**
     * 获取服务器的端口。
     *
     * @return
     */
    public int getBlanceServerPort() {
        return blanceServerPort;
    }

    /**
     * 设置服务器的端口。
     *
     * @param blanceServerPort
     */
    public void setBlanceServerPort(int blanceServerPort) {
        this.blanceServerPort = blanceServerPort;
    }

    @Override
    public String toString() {
        return "ContextParameters [uid=" + uid + ", appKey=" + appKey + ", secretKey=" + secretKey + ", blanceServerIp="
                + blanceServerAddress + ", blanceServerPort=" + blanceServerPort + "]";
    }

}
