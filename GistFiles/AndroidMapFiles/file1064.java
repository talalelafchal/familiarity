package com.gmail.fedorenko.kostia.app1lesson4;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * Created by kfedoren on 17.09.2015.
 */
public class Item {
    //Items table name
    public static final String TABLE_NAME = "items";
    //Items Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_PLACE = "place";
    public static final String KEY_TIME = "time";
    public static final String KEY_DATE = "date";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_REGION = "region";

    public static final String[] COLUMNS = {KEY_ID, KEY_PLACE, KEY_TIME, KEY_DATE, KEY_IMAGE, KEY_REGION};

    private int id;
    private String place;
    private String time;
    private String date;
    private String region;
    private Bitmap image;

    public Item() {
    }

    public Item(String place, String time, String date, Bitmap image, String region) {
        this.place = place;
        this.time = time;
        this.date = date;
        this.image = image;
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Item: Description - " + this.place + ";Date - " + this.date + ";Time - " + this.time + ";ID - " + this.getId() + ";Reigon - " + this.region;
    }

    @Override
    public boolean equals(Object o) {
        Item item = (Item) o;
        if ((item.getPlace().equals(this.getPlace()))&&(item.getDate().equals(this.getDate()))&&(item.getTime().equals(this.getTime()))&&(item.getRegion().equals(this.getRegion()))) return true;
        else return false;
    }
}
