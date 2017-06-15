public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {

    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "Warning: Device Admin is going to be disabled.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

    @Override
    public void onLockTaskModeEntering(Context context, Intent intent,
                                       String pkg) {
    }

    @Override
    public void onLockTaskModeExiting(Context context, Intent intent) {
    }

    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), AdminReceiver.class);
    }
}

/*
----

AndroidManifest.xml:

----

<receiver
    android:name=".AdminReceiver"
    android:label="@string/device_admin"
    android:permission="android.permission.BIND_DEVICE_ADMIN">
    <meta-data
        android:name="android.app.device_admin"
        android:resource="@xml/device_owner_receiver" />

    <intent-filter>
        <action android:name="android.app.action.DEVICE_ADMIN_ENABLED" />
    </intent-filter>
</receiver>

----

res/xml/device_owner_receiver.xml

----

<?xml version="1.0" encoding="utf-8"?>
<device-admin>
    <uses-policies>
        <limit-password/>
        <watch-login/>
        <reset-password/>
        <force-lock/>
        <wipe-data/>
        <expire-password/>
        <encrypted-storage/>
        <disable-camera/>
    </uses-policies>
</device-admin>

----
*/