/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/

package com.mobint.locker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;

final class av extends AlertDialog {

    final LockScreenActivity a;

    public av(LockScreenActivity lockscreenactivity, Context context) {
        a = lockscreenactivity;
        super(context, 2131361792); // theme/dialog
        android.view.WindowManager.LayoutParams layoutparams = getWindow().getAttributes();
        layoutparams.type = 2003; // TYPE_SYSTEM_ALERT
        layoutparams.dimAmount = 0.0F; // transparent
        layoutparams.width = 0;
        layoutparams.height = 0;
        layoutparams.gravity = 80;  // BOTTOM
        getWindow().setAttributes(layoutparams);
        getWindow().setFlags(524320, 16777215); // FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL, 0xffffff
        setOwnerActivity((Activity) context);
        setCancelable(false);
    }

    public final boolean dispatchTouchEvent(MotionEvent motionevent) {
        return true;
    }

    protected final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FrameLayout framelayout = new FrameLayout(getContext());
        framelayout.setBackgroundColor(0);
        setContentView(framelayout);
    }

    public final boolean onKeyDown(int i, KeyEvent keyevent) {
        label0: {
            label1: {
                if (i == 4) { // KEYCODE_BACK
                    LockScreenActivity.c(a, true);
                    LockScreenActivity.a(a, System.currentTimeMillis());
                    return true;
                }
                if (i == 26) {  // KEYCODE_POWER
                    LockScreenActivity.c(a, false);
                    return super.onKeyDown(i, keyevent);
                }
                if (i != 25)  // KEYCODE_VOLUME_DOWN
                    break label0;
                if (!LockScreenActivity.r(a)
                        || 2000L + LockScreenActivity.s(a) <= System.currentTimeMillis())
                    break label1;
                if (!LockScreenActivity.i(a).c()) {
                    boolean flag;
                    if (LockScreenActivity.h(a) && LockScreenActivity.i(a).s() != 1
                            && LockScreenActivity.i(a).u() != 1 && LockScreenActivity.i(a).w() != 1
                            && LockScreenActivity.i(a).y() != 1)
                        flag = true;
                    else
                        flag = false;
                    if (!flag)
                        break label1;
                }
                LockScreenActivity.l(a);
                return true;
            }
            LockScreenActivity.c(a, false);
            return super.onKeyDown(i, keyevent);
        }
        if (i == 24) {  // KEYCODE_VOLUME_UP
            LockScreenActivity.c(a, false);
            return super.onKeyDown(i, keyevent);
        } else {
            LockScreenActivity.c(a, false);
            return true;
        }
    }

    public final void onWindowFocusChanged(boolean flag) {
        super.onWindowFocusChanged(flag);
        if (flag)
            LockScreenActivity.p(a);
        else if (LockScreenActivity.i(a).g()) {
            LockScreenActivity.q(a);
            return;
        }
    }
}
