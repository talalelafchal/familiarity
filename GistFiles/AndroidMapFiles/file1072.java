package com.stonete.qrtoken.utils;

import android.content.Context;
import android.util.ArrayMap;

import com.stonete.qrtoken.Bean.QrtUserInfo;
import com.stonete.qrtoken.sharedperfences.GetEmail;
import com.stonete.qrtoken.sharedperfences.GetTimeStamp;
import com.stonete.qrtoken.sharedperfences.GetUsername;
import com.stonete.qrtoken.sharedperfences.SharePrefUtil;
import com.stonete.qrtoken.statics.StaticUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 二维令的工具类
 * Created by kangwei on 2015/1/30.
 */
public class QrtUtils {


    public static boolean isNeed2TimeCheck(Context context) {

        if (GetTimeStamp.getKey(context) == 0) {
            GetTimeStamp.setKey(context, System.currentTimeMillis() / 1000);
            return false;
        }
        //检查是否需要时间校验，如果5天还没有校验过，则校验   === 432000
        long result = Math.abs(System.currentTimeMillis() / 1000 - GetTimeStamp.getKey(context));
        MyLog.i("QRToken", "比较一下-" + System.currentTimeMillis() / 1000 + "-" + GetTimeStamp.getKey(context) + "=" + result + "=" + result / (60 * 60 * 24) + "天" + result % (60 * 60 * 24));
        return result > 432000;
    }

    public static Map<String,String> getErrorMsg(String errorCode){
        Map<String,String> map = new HashMap<String,String>();
        String[] errorMap = StaticUtil.errorMap;
        for (int i = 0; i < errorMap.length; i = i + 3) {
            if (errorMap[i].equals(errorCode.trim())) {
                map.put("message", errorMap[i + 1]);
                map.put("toastordialog", errorMap[i + 2]);
                return map;
            }
        }
        return map;
    };
    public static void getDiaOrToast(Context context,String errorCode){
        Map<String, String> errorMsg1 = getErrorMsg(errorCode);
        String message = errorMsg1.get("message");
        String toastordialog = errorMsg1.get("toastordialog");
        MyLog.i("message====>>"+message+"===toastordialog===>>"+toastordialog);
        if (toastordialog.equals(StaticUtil.SHOW_TYPE_DIALOG))
        {
            DialogUtils.showDialog(context,message);
        } else {
            DialogUtils.showToast(context,message);
        }
    }

    public static String getSuccessMsg(String url) {

        String[] successMap = StaticUtil.successMap;
        for (int i = 0; i < successMap.length; i = i + 2) {
            if (successMap[i].equals(url.trim())) {
                return successMap[i + 1];
            }
        }
        return "成功!";
    }


    public static QrtUserInfo getCurrentUser(Context ctx) {
        QrtUserInfo userInfo = new QrtUserInfo();
        userInfo.qrtUserName = SharePrefUtil.getUserName(ctx);
        userInfo.email = GetEmail.getKey(ctx);
        userInfo.hasBound = SharePrefUtil.ifHasBoundQrt(ctx);
        return  userInfo;
    }
}
