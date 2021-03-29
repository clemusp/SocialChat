package com.optic.socialchat.models;

public class SliderItem {

    String imageurl;
    long timestamp;

    public SliderItem(){

    }

    public SliderItem(String imageurl, long timestamp) {
        this.imageurl = imageurl;
        this.timestamp = timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
