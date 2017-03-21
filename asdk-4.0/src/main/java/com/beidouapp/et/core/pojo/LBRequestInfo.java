package com.beidouapp.et.core.pojo;

/**
 * 负载均衡请求信息.
 *
 * @author mhuang.
 */
public class LBRequestInfo extends BaseLBInfo {
    /**
     * 随机数.
     */
    private String randrom;

    /**
     * MD5加密值.
     */
    private String md5;

    /**
     * 用户UID.
     */
    private String uid;

    public String getRandrom() {
        return randrom;
    }

    public LBRequestInfo setRandrom(String randrom) {
        this.randrom = randrom;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public LBRequestInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public LBRequestInfo setUid(String uid) {
        this.uid = uid;
        return this;
    }

    @Override
    public String toString() {
        return "LBRequestInfo{" +
                "randrom=" + randrom +
                ", md5='" + md5 + '\'' +
                ", uid='" + uid + '\'' +
                "} " + super.toString();
    }
}