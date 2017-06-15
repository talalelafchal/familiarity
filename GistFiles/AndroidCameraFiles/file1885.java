package com.up366.mobile.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.up366.common.log.Logger;
import com.up366.mobile.R;
import com.up366.mobile.common.ui.baseui.BaseActivity;
import com.up366.mobile.common.ui.baseui.BaseFragmentActivity;
import com.up366.mobile.common.ui.baseui.IPermissionsCallback;

/**
 * @author 王静
 * @description 权限申请公用类
 * @date 2017年3月9日 下午1:55:37
 */
public class PermissionUtils {

    public static int permisionIndexCount = 1;
    public static final int PERMISSION_ALL = 0;
    public static final int PERMISSION_CAMERA = 1;
    public static final int PERMISSION_READ_EXTERNAL_STORAGE = 2;
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSION_READ_PHONE_STATE = 4;
    public static final int PERMISSION_RECORD_AUDIO = 5;

    /**
     * 检查权限
     *
     * @param context
     * @param permissionsCallback：回调做后续操作
     */
    public static void requestToUserPermission(Context context, IPermissionsCallback permissionsCallback) {
        //BaseActivity将PermissionsCallback传入，应用同一个callBack做同样的后续操作
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setPermissionsCallback(permissionsCallback);
        } else if (context instanceof BaseFragmentActivity) {
            ((BaseFragmentActivity) context).setPermissionsCallback(permissionsCallback);
        } else {
            throw new IllegalStateException("not Activity context");
        }
        //判断是哪种类型权限申请
        switch (permisionIndexCount) {
            case PERMISSION_ALL://所有的，针对小米6.0手机权限有问题，启动不了项目，故去掉刚刚进入app的MainVkActivity就调用申请，可写到内部的Fragment
                int i = 0;
                String[] permissionStr = new String[6];
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    permissionStr[i++] = android.Manifest.permission.CAMERA;
                }
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionStr[i++] = android.Manifest.permission.READ_EXTERNAL_STORAGE;
                }
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionStr[i++] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                }
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    permissionStr[i++] = android.Manifest.permission.READ_PHONE_STATE;
                    permissionStr[i++] = android.Manifest.permission.PROCESS_OUTGOING_CALLS;
                }
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    permissionStr[i++] = android.Manifest.permission.RECORD_AUDIO;
                }
                if (permissionStr.length != 0)
                    ActivityCompat.requestPermissions((Activity) context, permissionStr, 100);//去请求所有权限
                break;
            case PERMISSION_CAMERA://相机权限也要有存储权限
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有此权限，去请求权限
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    //有权限，直接回去做后续操作
                    handleResult(context, permisionIndexCount);
                }
                break;
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    handleResult(context, permisionIndexCount);
                }
                break;
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    handleResult(context, permisionIndexCount);
                }
                break;
            case PERMISSION_READ_PHONE_STATE:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS}, 100);
                } else {
                    handleResult(context, permisionIndexCount);
                }
                break;
            case PERMISSION_RECORD_AUDIO:
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                } else {
                    handleResult(context, permisionIndexCount);
                }
                break;
        }
    }

    /**
     * 有权限，直接去做后续操作
     *
     * @param context
     * @param permisionIndexCount：权限类型index
     */
    private static void handleResult(Context context, int permisionIndexCount) {
        IPermissionsCallback permissionsCallback = null;
        if (context instanceof BaseActivity) {
            permissionsCallback = ((BaseActivity) context).getPermissionsCallback();
        } else if (context instanceof BaseFragmentActivity) {
            permissionsCallback = ((BaseFragmentActivity) context).getPermissionsCallback();
        } else {
            throw new IllegalStateException("not Activity context");
        }
        if (permissionsCallback == null) {
            Logger.warn("没有注册IPermissionsCallback");
        } else {
            permissionsCallback.onResult(permisionIndexCount);
        }
    }

    /**
     * 申请权限，回调
     *
     * @param requestCode：code码=100成功
     * @param permissions:所请求的权限名称
     * @param grantResults：请求结果：PackageManager.PERMISSION_DENIED申请被用户点禁用；PERMISSION_GRANTED申请成功
     * @param context：上下文
     * @param result：回调，回去做后续操作
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, final Context context, doSomethingResult result) {
        if (requestCode == 100) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {//拒绝啦
                        String msg = "";
                        String permissionName = "";
                        switch (permisionIndexCount) {//根据请求的类型，弹提示“您已禁用应用所需权限，有些功能将无法使用，请返回重新尝试，并选择允许权限。”
                            case PERMISSION_ALL:
                                msg = context.getString(R.string.permission_all);
                                permissionName = "相关权限";
                                break;
                            case PERMISSION_CAMERA:
                                msg = context.getString(R.string.permission_camera);
                                permissionName = "相机权限";
                                break;
                            case PERMISSION_READ_EXTERNAL_STORAGE:
                                msg = context.getString(R.string.permission_read_exteranal_storage);
                                permissionName = "存储权限";
                                break;
                            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                                msg = context.getString(R.string.permission_write_exteranal_storage);
                                permissionName = "存储权限";
                                break;
                            case PERMISSION_READ_PHONE_STATE:
                                msg = context.getString(R.string.permission_read_phone_state);
                                permissionName = "电话权限";
                                break;
                            case PERMISSION_RECORD_AUDIO:
                                msg = context.getString(R.string.permission_read_exteranal_storage);
                                permissionName = "录音权限";
                                break;
                        }
                        //这个用户拒绝了权限请求，并且选择了不再提示！跳转至设置页面
                        //shouldShowRequestPermissionRationale方法只能在23及以上版本调用
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !((Activity) context).shouldShowRequestPermissionRationale(permissions[i])) {

                            new CuDialog(context).create(R.layout.permission_denyed_forerer_to_setting)
                                    .setText(R.id.dialog_msg, "无法获取" + permissionName + "，需要手动授权，否则该功能无法使用。")
                                    .setOnClickListener(new int[]{R.id.dialog_leftbtn, R.id.dialog_rightbtn}, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (v.getId() == R.id.dialog_rightbtn) {//去设置
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                                intent.setData(uri);
                                                ((Activity) context).startActivityForResult(intent, 1024);
                                                return;
                                            } else if (v.getId() == R.id.dialog_leftbtn) {//取消

                                            }
                                        }
                                    }).show();
                        } else {
                            CuDialog.showOkDialog((Activity) context, msg);
                        }
                        return;
                    }
                }
                //权限被同意，做后续操作
                result.onResult(permisionIndexCount);

            }
        }
    }

    /**
     * 做后续操作接口
     */
    public interface doSomethingResult {
        void onResult(int permisionIndexCount);
    }
}
