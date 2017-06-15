package com.example.wenfahu.simplecam;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenfahu on 16-9-21.
 */
public class JniTools {
    static {
        System.loadLibrary("native");
        if (test() != 123) {
            Log.e("[load lib]", "fail to load native lib");
        }
    }

    public static native int test();

    public static native int[] lanePlus(long framePtr);

    public static native float[] detCars(long framePtr);

    public static native float[] detPeople(long framePtr);

}
