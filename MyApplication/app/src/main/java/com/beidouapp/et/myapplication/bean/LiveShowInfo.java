package com.beidouapp.et.myapplication.bean;

/**
 * Created by allen on 2017/2/27.
 */

public class LiveShowInfo {
    /**
     * 开始时间
     */
    private String startime;
    /**
     * 举办场地
     */
    private String address;
    /**
     * 赛事名字
     */
    private String name;
    /**
     * 封面url
     */

    private String url;

    public LiveShowInfo(String name, String startime, String address, String url) {
        this.name = name;
        this.startime = startime;
        this.address = address;
        this.url = url;
    }

    public LiveShowInfo() {

    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
