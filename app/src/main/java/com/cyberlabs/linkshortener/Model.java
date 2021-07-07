package com.cyberlabs.linkshortener;

public class Model {
    String originalurl,shorturl,time;
    Model(){

    }

    public Model(String originalurl, String shorturl, String time) {
        this.originalurl = originalurl;
        this.shorturl = shorturl;
        this.time = time;
    }

    public String getOriginalurl() {
        return originalurl;
    }

    public void setOriginalurl(String originalurl) {
        this.originalurl = originalurl;
    }

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
