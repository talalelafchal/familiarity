package mobi.espier.locker.receiver;

import android.content.*;
import mobi.espier.locker.a.d;

public class LockScreenUserPresentReciver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.USER_PRESENT"))
            d.h(context);
    }
}
