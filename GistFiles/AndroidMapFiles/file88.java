package com.stonete.qrtoken.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kangwei on 2015/3/19.
 */
public class TimeUtils {

    public static void main(String[] args){
        System.out.println(1);
        new Date();
    }

    public static String currentFormateData(){
        return dateToString(new Date());
    }

    public static String dateToString(Date time){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);
        return ctime;
    }
}
