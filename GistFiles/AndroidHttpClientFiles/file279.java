package com.example.untitled3;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: zemin
 * Date: 19.03.2013
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class Feed {
    public String username;
    public String text;
    public String image_url;
    public Bitmap avatar;
    public String tip;

    public Feed(String username, String text, String image_url) {
        this.username = username;
        this.text = text;
        this.image_url = image_url;
    }

}