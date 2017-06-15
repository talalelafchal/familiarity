if (mMapView == null) {
    mMapView = new MapView(this, "*censored*"); //THIS LINE
    mMapView.setBuiltInZoomControls(true);
    mMapView.setStreetView(true);
}
((RelativeLayout)findViewById(R.id.mapLayout)).addView(mMapView);


Stack trace:

09-16 22:37:34.825: ERROR/AndroidRuntime(637): Uncaught handler: thread main exiting due to uncaught exception
09-16 22:37:34.844: ERROR/AndroidRuntime(637): java.lang.RuntimeException: Error receiving broadcast Intent { act=hero-message (has extras) } in nu.quickly.orvar.heroclient.MainActivity$4@436c1760
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.app.ActivityThread$PackageInfo$ReceiverDispatcher$Args.run(ActivityThread.java:723)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.os.Handler.handleCallback(Handler.java:587)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.os.Handler.dispatchMessage(Handler.java:92)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.os.Looper.loop(Looper.java:123)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.app.ActivityThread.main(ActivityThread.java:4321)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at java.lang.reflect.Method.invokeNative(Native Method)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at java.lang.reflect.Method.invoke(Method.java:521)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:791)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:549)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at dalvik.system.NativeStart.main(Native Method)
09-16 22:37:34.844: ERROR/AndroidRuntime(637): Caused by: java.lang.IllegalStateException: You are only allowed to have a single MapView in a MapActivity
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at com.google.android.maps.MapActivity.setupMapView(MapActivity.java:180)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at com.google.android.maps.MapView.<init>(MapView.java:279)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at com.google.android.maps.MapView.<init>(MapView.java:225)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at nu.quickly.orvar.heroclient.MainActivity.setContentView(MainActivity.java:103)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at nu.quickly.orvar.heroclient.MainActivity$4.onReceive(MainActivity.java:278)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     at android.app.ActivityThread$PackageInfo$ReceiverDispatcher$Args.run(ActivityThread.java:712)
09-16 22:37:34.844: ERROR/AndroidRuntime(637):     ... 9 more
09-16 22:37:35.094: ERROR/dalvikvm(637): Unable to open stack trace file '/data/anr/traces.txt': Permission denied
