package com.shareobj.search;

public class RowItem {
    private String url;
    private String phone;
    private String title;
    private String distance;
    private String type;
    private String desc;

    public RowItem(String url, String phone, String title, String distance, String type, String desc) {
        this.url = url;
        this.title = title;
        this.desc = desc;
        this.distance = distance;
        this.type = type;
        this.phone = phone;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title + "\n" + desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
