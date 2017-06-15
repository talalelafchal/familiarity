package com.stonete.qrtoken.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.stonete.qrtoken.activity.Homepage;
import com.stonete.qrtoken.dialog.My_Dialog;

/**
 * Created by kangwei on 2015/2/28.
 */
public class DialogUtils {

    // 自定义单一按钮对话框
    public static My_Dialog showDialog(final Context ctx, final String message) {
        MyLog.i(message);
        My_Dialog.Builder builder = new My_Dialog.Builder(ctx);
        builder.setMessage(message);
        builder.setPositiveButton("确   定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (ctx instanceof Homepage) {
                            ((Homepage) ctx).restartScan();
                        }
                    }
                });
        builder.setCancelable(false);
        builder.create().show();
        return builder.create();
    }

    public static My_Dialog showDialog(final Context ctx, final String message, final DialogInterface.OnClickListener listener) {
        MyLog.i(message);
        My_Dialog.Builder builder = new My_Dialog.Builder(ctx);
        builder.setMessage(message);

        builder.setPositiveButton("确   定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(ctx instanceof Homepage){
                    ((Homepage) ctx).restartScan();
                }
                if(listener != null){
                    listener.onClick(dialog, which);
                }
            }
        });
        builder.setCancelable(false);
        builder.create().show();
        return builder.create();
    }

    public static AlertDialog showDialog(final Context ctx, final String message, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", listener)
                .setNegativeButton("No", cancelListener);
        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }



    public static void showToast(final Context ctx, final String message){
        Toast.makeText(ctx,message,Toast.LENGTH_SHORT).show();
    }
}
