/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/

package com.mobint.locker;

import android.app.*;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import com.a.a.a.a;
import com.a.a.a.e;
import com.mobint.locker.jbmultiwaveview.GlowPadView;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

public class LockScreenActivity extends Activity {

    public MyApplication a;

    y b;

    al c;

    private View d;

    private View e;

    private View f;

    private boolean g;

    private long h;

    private ay i;

    private af j;

    private ab k;

    private boolean l;

    private cj m;

    private final Handler n = new Handler();

    private BroadcastReceiver o;

    private boolean p;

    private LinearLayout q;

    private final HashMap r = new HashMap();

    private a s;

    private am t;

    private boolean u;

    private final Runnable v = new ar(this);

    private av w;

    private ServiceConnection x;

    private e y;

    public LockScreenActivity() {
        g = false;
        h = 0L;
        s = null;
        b = new ap(this);
        c = new aq(this);
        x = new as(this);
        y = new at(this);
    }

    private void a() {
        if (m.f()) {
            getWindow().addFlags(1024);
            return;
        } else {
            getWindow().clearFlags(1024);
            return;
        }
    }

    private static void a(StatusBarManager statusbarmanager, String s1) {
        try {
            statusbarmanager.getClass().getMethod(s1, new Class[0])
                    .invoke(statusbarmanager, new Object[0]);
            return;
        } catch (Exception exception) {
            return;
        }
    }

    private void a(Intent intent) {
        int ai[] = new int[2];
        f.getLocationOnScreen(ai);
        intent.setFlags(270532608);
        intent.setSourceBounds(new Rect(ai[0], ai[1], ai[0] + f.getWidth(), ai[1] + f.getHeight()));
        try {
            startActivity(intent);
            return;
        } catch (Exception exception) {
            Log.w("LockScreenActivity", (new StringBuilder("Activity not found for intent + "))
                    .append(intent.getAction()).toString());
        }
    }

    static void a(LockScreenActivity lockscreenactivity, int i1, String s1, int j1)
    {
        if(!lockscreenactivity.r.containsKey(s1)) goto _L2; else goto _L1
_L1:
        ck ck1 = (ck)lockscreenactivity.r.get(s1);
_L14:
        if(ck1 == null) goto _L4; else goto _L3
_L3:
        if(ck1.e == null)
        {
            BubbleTextView bubbletextview = new BubbleTextView(lockscreenactivity);
            bubbletextview.a(ck1, lockscreenactivity.m);
            bubbletextview.setOnClickListener(new au(lockscreenactivity, ck1));
        }
        if(j1 <= 0) goto _L6; else goto _L5
_L5:
        ck1.e.a(j1);
        ck1.e.invalidate();
        if(!a(((ViewGroup) (lockscreenactivity.q)), ((View) (ck1.e))))
            lockscreenactivity.q.addView(ck1.e);
_L4:
        return;
_L2:
        String s2;
        String s3;
        boolean flag;
        if(s1 == null)
            break MISSING_BLOCK_LABEL_412;
        int k1 = s1.indexOf("/");
        if(k1 <= 0 || k1 >= -1 + s1.length())
            break MISSING_BLOCK_LABEL_412;
        s2 = s1.substring(0, k1);
        s3 = s1.substring(k1 + 1, s1.length());
        flag = lockscreenactivity.a(s2, s3);
        if(flag) goto _L8; else goto _L7
_L7:
        Exception exception;
        String s4;
        String s5;
        ComponentName componentname;
        String as1[];
        String as2[];
        int l1;
        String s6;
        if(i1 == 1)
        {
            as2 = com.mobint.locker.i.a;
            as1 = i.b;
        } else
        if(i1 == 2)
        {
            as2 = i.c;
            as1 = i.d;
        } else
        {
            as1 = null;
            as2 = null;
        }
        l1 = 0;
        if(as2 == null) goto _L8; else goto _L9
_L9:
        if(l1 < as2.length) goto _L10; else goto _L8
_L8:
        s4 = s2;
        s5 = s3;
_L11:
        if(!flag)
            break MISSING_BLOCK_LABEL_412;
        ck1 = new ck();
        ck1.d = new ComponentName(s4, s5);
        componentname = ck1.d;
        ck1.b = new Intent("android.intent.action.MAIN");
        ck1.b.addCategory("android.intent.category.LAUNCHER");
        ck1.b.setComponent(componentname);
        ck1.b.setFlags(270532608);
        lockscreenactivity.a.f.a(ck1, lockscreenactivity.a.g);
_L12:
        lockscreenactivity.r.put(s1, ck1);
        continue; /* Loop/switch isn't completed */
_L10:
label0:
        {
            flag = lockscreenactivity.a(as2[l1], as1[l1]);
            if(!flag)
                break label0;
            s6 = as2[l1];
            s5 = as1[l1];
            s4 = s6;
        }
          goto _L11
        l1++;
          goto _L9
        ck1 = null;
          goto _L12
_L6:
        try
        {
            lockscreenactivity.q.removeView(ck1.e);
            return;
        }
        // Misplaced declaration of an exception variable
        catch(Exception exception)
        {
            return;
        }
        if(true) goto _L14; else goto _L13
_L13:
    }

    static void a(LockScreenActivity lockscreenactivity, long l1) {
        lockscreenactivity.h = l1;
    }

    static void a(LockScreenActivity lockscreenactivity, Intent intent) {
        lockscreenactivity.a(intent);
    }

    static void a(LockScreenActivity lockscreenactivity, a a1) {
        lockscreenactivity.s = a1;
    }

    static void a(LockScreenActivity lockscreenactivity, boolean flag) {
        lockscreenactivity.l = flag;
    }

    private void a(String s1, String s2, int ai[]) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(new ComponentName(s1, s2));
        intent.addFlags(270532608);
        intent.setSourceBounds(new Rect(ai[0], ai[1], ai[0] + f.getWidth(), ai[1] + f.getHeight()));
        startActivity(intent);
    }

    private void a(boolean flag) {
        if (flag != u)
            u = flag;
    }

    private void a(String as1[], String as2[]) {
        int ai[] = new int[2];
        f.getLocationOnScreen(ai);
        int i1 = 0;
        do {
            if (i1 >= as2.length)
                return;
            try {
                a(as1[i1], as2[i1], ai);
                return;
            } catch (Exception exception) {
                i1++;
            }
        } while (true);
    }

    private static boolean a(ViewGroup viewgroup, View view) {
        int i1 = 0;
        do {
            if (i1 >= viewgroup.getChildCount())
                return false;
            if (viewgroup.getChildAt(i1) == view)
                return true;
            i1++;
        } while (true);
    }

    static boolean a(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.l;
    }

    static boolean a(LockScreenActivity lockscreenactivity, String s1) {
        return lockscreenactivity.a(s1);
    }

    private boolean a(String s1) {
        int ai[] = new int[2];
        f.getLocationOnScreen(ai);
        int i1 = s1.indexOf("/");
        boolean flag = false;
        if (i1 > 0) {
            int j1 = -1 + s1.length();
            flag = false;
            if (i1 < j1) {
                String s2 = s1.substring(0, i1);
                String s3 = s1.substring(i1 + 1, s1.length());
                try {
                    a(s2, s3, ai);
                } catch (Exception exception) {
                    return false;
                }
                flag = true;
            }
        }
        return flag;
    }

    private boolean a(String s1, String s2) {
        PackageManager packagemanager = getPackageManager();
        android.content.pm.ResolveInfo resolveinfo;
        try {
            int _tmp = packagemanager.getPackageInfo(s1, 0).versionCode;
            Intent intent = new Intent();
            intent.setClassName(s1, s2);
            resolveinfo = getPackageManager().resolveActivity(intent, 0);
        } catch (android.content.pm.PackageManager.NameNotFoundException namenotfoundexception) {
            return false;
        }
        return resolveinfo != null;
    }

    static ay b(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.i;
    }

    static void b(LockScreenActivity lockscreenactivity, boolean flag) {
        lockscreenactivity.a(flag);
    }

    static void c(LockScreenActivity lockscreenactivity) {
        ay _tmp = lockscreenactivity.i;
    }

    static void c(LockScreenActivity lockscreenactivity, boolean flag) {
        lockscreenactivity.g = flag;
    }

    static a d(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.s;
    }

    static e e(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.y;
    }

    static am f(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.t;
    }

    static ServiceConnection g(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.x;
    }

    static boolean h(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.u;
    }

    static cj i(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.m;
    }

    static void j(LockScreenActivity lockscreenactivity) {
        if (!lockscreenactivity.a(lockscreenactivity.m.B())) {
            SearchManager searchmanager = (SearchManager) lockscreenactivity
                    .getSystemService("search");
            Bundle bundle = new Bundle();
            bundle.putString("source", "locker-search");
            searchmanager.startSearch(null, false, lockscreenactivity.getComponentName(), bundle,
                    true);
        }
    }

    static void k(LockScreenActivity lockscreenactivity) {
        if (!lockscreenactivity.a(lockscreenactivity.m.A()))
            lockscreenactivity.a(new Intent("android.media.action.STILL_IMAGE_CAMERA"));
    }

    static void l(LockScreenActivity lockscreenactivity) {
        lockscreenactivity.a.e = System.currentTimeMillis();
        lockscreenactivity.a.c = false;
        lockscreenactivity.finish();
        lockscreenactivity.overridePendingTransition(0, 0);
    }

    static void m(LockScreenActivity lockscreenactivity) {
        if (!lockscreenactivity.a(lockscreenactivity.m.C()))
            lockscreenactivity.a(com.mobint.locker.i.a, i.b);
    }

    static void n(LockScreenActivity lockscreenactivity) {
        if (!lockscreenactivity.a(lockscreenactivity.m.D()))
            lockscreenactivity.a(i.c, i.d);
    }

    static void o(LockScreenActivity lockscreenactivity) {
        if (!lockscreenactivity.a(lockscreenactivity.m.D()))
            lockscreenactivity.a(com.mobint.locker.i.e, i.f);
    }

    static void p(LockScreenActivity lockscreenactivity) {
        lockscreenactivity.a();
    }

    static void q(LockScreenActivity lockscreenactivity) {
        StatusBarManager statusbarmanager;
        label0: {
            statusbarmanager = (StatusBarManager) lockscreenactivity.getSystemService("statusbar");
            if (statusbarmanager != null) {
                if (android.os.Build.VERSION.SDK_INT < 17)
                    break label0;
                a(statusbarmanager, "collapsePanels");
            }
            return;
        }
        a(statusbarmanager, "collapse");
    }

    static boolean r(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.g;
    }

    static long s(LockScreenActivity lockscreenactivity) {
        return lockscreenactivity.h;
    }

    public void onCreate(Bundle bundle) {
        getWindow().addFlags(524288);
        getWindow().addFlags(4194304);
        overridePendingTransition(0, 0);
        super.onCreate(bundle);
        requestWindowFeature(1);
        boolean flag;
        long l1;
        long l2;
        int i1;
        GlowPadView glowpadview;
        ax ax1;
        if (getResources().getConfiguration().orientation == 2)
            flag = true;
        else
            flag = false;
        p = flag;
        m = new cj(this);
        t = new am(this);
        u = t.a();
        l1 = t.a.a("lc");
        l2 = System.currentTimeMillis();
        if (!u || l1 > l2 || l2 - l1 > 604800000L)
            if (t.b)
                (new aw(this, (byte) 0)).start();
            else
                a(false);
        a();
        i1 = m.i();
        if (i1 == 0)
            setRequestedOrientation(2);
        else if (i1 == 1)
            setRequestedOrientation(1);
        else if (i1 == 2)
            setRequestedOrientation(0);
        else
            setRequestedOrientation(5);
        setVolumeControlStream(3);
        setContentView(2130903046);
        d = findViewById(2131427348);
        e = findViewById(2131427349);
        glowpadview = (GlowPadView) findViewById(2131427351);
        glowpadview.a(m.h());
        ax1 = new ax(this, glowpadview);
        glowpadview.a(ax1);
        f = glowpadview;
        i = ax1;
        j = new af(this);
        k = new ab(e, j, new ao(this), b, m);
        q = (LinearLayout) findViewById(2131427352);
        if (!p && u && m.y() != 0) {
            android.view.ViewGroup.LayoutParams layoutparams = glowpadview.getLayoutParams();
            if (layoutparams instanceof android.widget.RelativeLayout.LayoutParams)
                ((android.widget.RelativeLayout.LayoutParams) layoutparams).bottomMargin = getResources()
                        .getDimensionPixelSize(2131034140);
        }
        if (u) {
            if (m.m()) {
                File file = new File(getFilesDir(), "images/alternative_wallpaper.png");
                IntentFilter intentfilter;
                String s1;
                if (!file.exists() || !file.isFile() || !file.canRead())
                    s1 = null;
                else
                    s1 = file.getAbsolutePath();
                if (s1 != null)
                    try {
                        BitmapDrawable bitmapdrawable = (BitmapDrawable) BitmapDrawable
                                .createFromPath(s1);
                        d.setBackgroundDrawable(bitmapdrawable);
                    } catch (Exception exception) {
                    }
            }
            e.setBackgroundColor(Color.argb((255 * m.n()) / 100, 0, 0, 0));
        }
        a = (MyApplication) getApplication();
        a.c = true;
        startService(new Intent(this, com / mobint / locker / KeyguardService));
        if (u && m.o()) {
            o = new az(this, (byte) 0);
            intentfilter = new IntentFilter("com.mobint.notifier.SEND");
            registerReceiver(o, intentfilter);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        j.d();
        if (o != null)
            unregisterReceiver(o);
    }

    public boolean onKeyLongPress(int i1, KeyEvent keyevent) {
        return true;
    }

    protected void onPause() {
        super.onPause();
        if (w != null) {
            try {
                w.dismiss();
            } catch (Throwable throwable) {
            }
            w = null;
        }
        j.a(c);
        k.a();
    }

    protected void onResume()
    {
        overridePendingTransition(0, 0);
        super.onResume();
        // show AlertDialog to disable home key
        if(w == null)
            w = new av(this, this);
        w.show();
        a.c = true;
        j.a(c);
        k.b();
        n.postDelayed(v, 500L);
        if(a.a)
            com.mobint.locker.a.a(this).b((new StringBuilder("/Locker-")).append(a.b).toString());
        Intent intent;
        Exception exception;
        if(m.F() && 86400000L + m.a("last_check_version") < System.currentTimeMillis())
            try
            {
                PackageInfo packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                (new com.mobint.locker.e(this, packageinfo.versionCode, packageinfo.versionName, false)).execute(new Void[0]);
                m.a("last_check_version", System.currentTimeMillis());
            }
            catch(Exception exception1) { }
        q.removeAllViews();
        if(!u || !m.o())
            break MISSING_BLOCK_LABEL_248;
        intent = new Intent();
        intent.setAction("com.mobint.notifier.QUERY");
        sendBroadcast(intent);
        return;
        exception;
    }
}
