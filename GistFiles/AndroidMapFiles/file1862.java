package com.geexFinance.GeexSaler.getui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.geexFinance.GeexSaler.R;
import com.geexFinance.GeexSaler.ui.HomeActivity;
import com.geexFinance.GeexSaler.util.GeexLocationManager;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushDemoReceiver extends BroadcastReceiver {

    private static final String TAG = PushDemoReceiver.class.getSimpleName();
    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private NotificationManagerCompat notificationManager;
    private String cid = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.i(TAG, "BroadcastReceiver.onReceive() action=" + bundle.getInt("action"));
            switch (bundle.getInt(PushConsts.CMD_ACTION)) {

                case PushConsts.GET_MSG_DATA:
                    // 获取透传数据
                    // String appid = bundle.getString("appid");
                    byte[] payload = bundle.getByteArray("payload");
                    final String taskid = bundle.getString("taskid");
                    final String messageid = bundle.getString("messageid");

                    // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                            Log.i(TAG, "第三方回执接口调用" + (result ? "成功" : "失败"));
                        }
                    }).start();

                    if (payload != null) {
                        String data = new String(payload);

                        Log.d("GetuiSdkDemo", "Got Payload:" + data);
                        if (GetuiSdkDemoActivity.tLogView != null)
                            GetuiSdkDemoActivity.tLogView.append(data + "\n");
                        String title = context.getString(R.string.app_name);
                        Notification notification = initNotifiManager(context, title, data);
                        notification.when = System.currentTimeMillis();
                        if (notificationManager == null)
                            notificationManager = NotificationManagerCompat.from(context);
                        int notificationId = 001;
                        notificationManager.notify(notificationId, notification);
                    }
                    break;
                case PushConsts.GET_CLIENTID:
                    // 获取ClientID(CID)
                    // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                    String cid = bundle.getString("clientid");
                    Log.i(TAG, "getui clientId got: " + cid);
                    this.setCid(cid);
                    if (GetuiSdkDemoActivity.tView != null)
                        GetuiSdkDemoActivity.tView.setText(cid);
                    break;
                case PushConsts.THIRDPART_FEEDBACK:
                    String appid = bundle.getString("appid");
                    String feedBackTaskid = bundle.getString("taskid");
                    String actionid = bundle.getString("actionid");
                    String result = bundle.getString("result");
                    long timestamp = bundle.getLong("timestamp");

                    Log.d("GetuiSdkDemo", "appid = " + appid);
                    Log.d("GetuiSdkDemo", "taskid = " + feedBackTaskid);
                    Log.d("GetuiSdkDemo", "actionid = " + actionid);
                    Log.d("GetuiSdkDemo", "result = " + result);
                    Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void setCid(String cid) {
        this.cid = cid;
    }

    //初始化通知栏配置
    private Notification initNotifiManager(Context ctx, String notification_title, String notification_text) {
        ///Build intent for notification content
        Intent viewIntent = new Intent(ctx, HomeActivity.class);
        //fixme resolve EXTRA_EVENT_ID in the activity
        //viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(ctx, 0, viewIntent, 0);
        // Build an intent for an action to view a map
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
//        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        double latitude = GeexLocationManager.getInstance().getLatitude();
        double longtitude = GeexLocationManager.getInstance().getLongitude();
        Uri geoUri = Uri.parse(String.format("geo:0,0?q=%f,%f(label)", latitude, longtitude));
        Log.d(TAG, geoUri.toString());
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(ctx, 0, mapIntent, 0);

        //remote input
        String replyLabel = "Tell me your advice";
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(new String[]{"Talk to you later, I'm driving"})
                .build();

        // Create the action
        NotificationCompat.Action findSelfAction =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_edit,
                        "find my location", mapPendingIntent)
                        .build();
        NotificationCompat.Action inputAction =
                new NotificationCompat.Action.Builder(R.drawable.emo_im_laughing,
                        replyLabel, mapPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        // Specify the 'big view' content to display the long
        // event description that may not fit the normal content text.
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        String eventDescription = "//Specify the 'big view' content to display the long\n" +
                "//event description";
        bigStyle.bigText(eventDescription);

        // Create a WearableExtender to add functionality for wearables
        Bitmap mBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.icongeex);
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(mBitmap);
        wearableExtender.addAction(findSelfAction)
                .addAction(inputAction);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                //  你所关注的信息有变更
                .setContentTitle(notification_title)
                // 点击查看
                .setContentText(notification_text)
                .setSmallIcon(R.drawable.icongeex)
                .setContentIntent(viewPendingIntent)
                //wearable feature
                .extend(wearableExtender)
                .setStyle(bigStyle);

        return mBuilder.build();
    }

}
