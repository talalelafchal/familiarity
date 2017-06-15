package com.stonete.qrtoken.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.stonete.qrtoken.activity.IActivity;
import com.stonete.qrtoken.dialog.My_Dialog;

/**
 * Created by kangwei on 2015/2/15.
 */
public class IError {

    public String errorMsg = null;
    public int errorCode = -1;
    public static final int ERROR_HTTP_500 = 500;

    public void showErrorDialog(final Context context) {

        String showMsg = errorMsg;
        if(ifNeed2sendQQ()){
            showMsg = "当前为测试模式。Http请求发生错误，请您将错误日志发送给开发人员";
        }
        MyLog.e(errorMsg);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            if(ifNeed2sendQQ() && context instanceof Activity){
                AppUtils.sendLog2QQ((Activity) context, errorMsg);
            }
            }
        };
        DialogUtils.showDialog(context, showMsg, listener);

    }

    public void printTestLog() {
        MyLog.e(errorMsg);
    }

    public boolean ifNeed2sendQQ(){
        return MyLog.DEBUG && errorCode == 500;
    }
}
