--------- beginning of crash
E/AndroidRuntime( 3769): FATAL EXCEPTION: main
E/AndroidRuntime( 3769): Process: com.redmadrobot.plazius, PID: 3769
E/AndroidRuntime( 3769): java.lang.NullPointerException: Attempt to invoke interface method 'boolean java.util.Map.containsKey(java.lang.Object)' on a null object reference
E/AndroidRuntime( 3769): 	at com.arellomobile.mvp.PresenterStore.add(PresenterStore.java:36)
E/AndroidRuntime( 3769): 	at com.arellomobile.mvp.MvpProcessor.getMvpPresenter(MvpProcessor.java:112)
E/AndroidRuntime( 3769): 	at com.arellomobile.mvp.MvpProcessor.getMvpPresenters(MvpProcessor.java:155)
E/AndroidRuntime( 3769): 	at com.arellomobile.mvp.MvpDelegate.onCreate(MvpDelegate.java:100)
E/AndroidRuntime( 3769): 	at com.arellomobile.mvp.MvpAppCompatFragment.onCreate(MvpAppCompatFragment.java:20)
E/AndroidRuntime( 3769): 	at com.redmadrobot.plazius.ui.fragment.places.LocationPermissionsFragment.onCreate(LocationPermissionsFragment.kt:40)
E/AndroidRuntime( 3769): 	at android.support.v4.app.Fragment.performCreate(Fragment.java:2075)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:1060)
E/AndroidRuntime( 3769): 	at android.support.v4.app.BackStackRecord.setLastIn(BackStackRecord.java:838)
E/AndroidRuntime( 3769): 	at android.support.v4.app.BackStackRecord.calculateFragments(BackStackRecord.java:861)
E/AndroidRuntime( 3769): 	at android.support.v4.app.BackStackRecord.run(BackStackRecord.java:719)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl.execPendingActions(FragmentManager.java:1682)
E/AndroidRuntime( 3769): 	at android.support.v4.app.Fragment.performStart(Fragment.java:2109)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:1151)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:1295)
E/AndroidRuntime( 3769): 	at android.support.v4.app.BackStackRecord.run(BackStackRecord.java:801)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl.execPendingActions(FragmentManager.java:1682)
E/AndroidRuntime( 3769): 	at android.support.v4.app.FragmentManagerImpl$1.run(FragmentManager.java:541)
E/AndroidRuntime( 3769): 	at android.os.Handler.handleCallback(Handler.java:739)
E/AndroidRuntime( 3769): 	at android.os.Handler.dispatchMessage(Handler.java:95)
E/AndroidRuntime( 3769): 	at android.os.Looper.loop(Looper.java:135)
E/AndroidRuntime( 3769): 	at android.app.ActivityThread.main(ActivityThread.java:5254)
E/AndroidRuntime( 3769): 	at java.lang.reflect.Method.invoke(Native Method)
E/AndroidRuntime( 3769): 	at java.lang.reflect.Method.invoke(Method.java:372)
E/AndroidRuntime( 3769): 	at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:903)
E/AndroidRuntime( 3769): 	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:698)
W/ActivityManager( 1487):   Force finishing activity 1 com.redmadrobot.plazius/.ui.activity.main.MainActivity