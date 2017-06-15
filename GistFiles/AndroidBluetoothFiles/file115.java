package mobi.espier.locker.a;
public final class d {
    public static void c(Context context) {
        if (a == null && b == null) {
            KeyguardManager keyguardmanager = (KeyguardManager) context
                    .getSystemService("keyguard");
            a = keyguardmanager;
            b = keyguardmanager.newKeyguardLock("jiao");
        }
        Log.i("ScreenLockerUtil",
                (new StringBuilder(" isOpen ")).append(c).append(" disableKeyguard inkey unlock  ")
                        .append(a.inKeyguardRestrictedInputMode()).toString());
        if (!c) {
            b.disableKeyguard();
            c = true;
        }
    }
    
    public static void h(Context context) {
        if (!mobi.espier.locker.a.b.a(context))
            return;
        if (a == null)
            a = (KeyguardManager) context.getSystemService("keyguard");
        synchronized (k) {
            if (b != null) {
                b.reenableKeyguard();
                b = null;
            }
            android.app.KeyguardManager.KeyguardLock keyguardlock = a.newKeyguardLock("jiao");
            b = keyguardlock;
            keyguardlock.disableKeyguard();
        }
    }
}