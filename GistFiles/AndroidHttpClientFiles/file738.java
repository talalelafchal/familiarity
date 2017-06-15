package com.baidu.demo.model;

import com.baidu.demo.data.DemoStreetscapeStreetData;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-10-29
 * Time: 下午5:28
 * To change this template use File | Settings | File Templates.
 */
public class DemoSSModel {
    private static DemoSSModel self;

    // 通用数据
    public DemoStreetscapeStreetData mStreetInfo;
    public String mIndoorId;

    public String mBookType;
    public int mMovieNum = -1;
    public String mBookTel;
    public String mBookWeb;

    // 室内景数据
    public int mDefaultSelected = -1;
    public String mIndoorSwitchId;
    public int mIndoorRotation;
    public int mIndoorOverlooking;
    public String mIndoorFromSource;

    // 街景数据
    // 图区和usercenter进入的参数
    public double mMatchptx;
    public double mMatchpty;
    public double mMatchptz;
    public int mNorthDir;

    public String mDayNightMode;

    //POI进入的参数
    public String mPoiUid;
    public int mPoix;
    public int mPoiy;
    public String mPoiName;
    public boolean mHaveIndoor;

    public String mPanoid;
    public int mStreetRotation;
    public int mStreetOverlooking;
    public String mStreetFromSource;

    public static DemoSSModel getInstance() {
        if (self == null) {
            self = new DemoSSModel();
        }
        return self;
    }

    public DemoSSModel() {
        mStreetInfo = new DemoStreetscapeStreetData();
        mIndoorId = null;

        mBookType = null;
        mMovieNum = -1;
        mBookTel = null;
        mBookWeb = null;

        resetIndoorData();
        resetStreetData();
    }

    public void resetStreetData() {
        mMatchptx = 0;
        mMatchpty = 0;
        mMatchptz = 0;
        mNorthDir = 0;
        mStreetRotation = 0;

        mDayNightMode = "day";

        mPoiUid = null;
        mPoix = 0;
        mPoiy = 0;
        mPoiName = null;
        mHaveIndoor = false;
        mPanoid = null;
        mStreetRotation = 0;
        mStreetOverlooking = 0;
        mStreetFromSource = null;
    }

    public void resetIndoorData() {
        mDefaultSelected = -1;
        mIndoorSwitchId = null;
        mIndoorRotation = 0;
        mIndoorOverlooking = 0;
        mIndoorFromSource = null;
    }

}
