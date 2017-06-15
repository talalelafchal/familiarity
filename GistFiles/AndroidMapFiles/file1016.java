package com.stonete.qrtoken.utils;

import android.content.Context;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;


/**
 * 友盟更新+是否需要强制更新
 *
 * @author luwenzheng
 */
public class UmengUpdateUtil {

    public static void prepare4UmengUpdate(final Context context) {
        MobclickAgent.updateOnlineConfig(context);
        //获取友盟在线参数
        String update_mode = MobclickAgent.getConfigParams(context, "upgrade_mode");
        if (update_mode == null || "".equals(update_mode)) {
            return;
        }
        String mUpdateModeArray[];
        //转换为数组
        mUpdateModeArray = update_mode.split(",");
        UmengUpdateAgent.setUpdateOnlyWifi(false); //在任意网络环境下都进行更新自动提醒

        final UpdateResponse[] mUpdateResponse = new UpdateResponse[1];
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                mUpdateResponse[0] = updateResponse;
            }
        });
        UmengUpdateAgent.forceUpdate(context);  //调用umeng更新接口
        String curr_version_name = AppUtils.getVersionNameByPkgName(context, context.getPackageName());
        for (int i = 0; i < mUpdateModeArray.length; i += 2) {
            if (curr_version_name.equals(mUpdateModeArray[i])) {
                if ("F".equals(mUpdateModeArray[i + 1])) {

                    //对话框按键的监听，对于强制更新的版本，如果用户未选择更新的行为，关闭app
                    UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {

                        @Override
                        public void onClick(int status) {
                            switch (status) {
                                case UpdateStatus.Update:
                                    break;
                                default:
                                    //"以后再说" 进行重复显示此显示升级Dialog
                                    UmengUpdateAgent.showUpdateDialog(context, mUpdateResponse[0]);
                            }
                        }
                    });
                    break;  //只要找到对应的版本号，即结束循环
                }
            }
        }

    }
}
