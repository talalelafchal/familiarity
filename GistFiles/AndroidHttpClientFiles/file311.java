package com.kalyan.fsdashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kalyan on 2/20/15.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            /* Only perform this code if the BroadcastReceiver received the ACTION_BOOT_COMPLETED action.*/
            Intent intentone = new Intent(context.getApplicationContext(), FSBrowser.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);

        }

    }

}
