package com.beidouapp.et.myapplication.bean;

/**
 * Created by allen on 2017/3/6.
 */

public class CarouselImage {
    private String url;
    private String info;

    public CarouselImage(String info, String url) {
        this.info = info;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
