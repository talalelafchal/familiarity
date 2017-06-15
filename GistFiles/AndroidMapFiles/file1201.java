package tewilove.unlock.pb25;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ming on 15-10-22.
 */
public class XHook_TelephonyCommon implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {
        if (!lpParam.packageName.equals("com.android.phone"))
            return;
        XposedHelpers.findAndHookMethod("com.android.internal.telephony.gsm.GSMPhone", lpParam.classLoader,
                "setProperties", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("create missing ApnListTracker bgn");
                try {
                    Class<?> clz = Class.forName("jp.co.sharp.android.internal.telephony.dataconnection.ApnListTracker");
                    Constructor<?> ctor = clz.getConstructor(Class.forName("com.android.internal.telephony.PhoneBase"));
                    ctor.newInstance(param.thisObject);
                } catch (Throwable t) {
                    XposedBridge.log(t);
                }
                XposedBridge.log("create missing ApnListTracker end");
            }
        });
        XposedHelpers.findAndHookMethod("jp.co.sharp.android.internal.telephony.dataconnection.ApnListTracker", lpParam.classLoader,
                "createAllApnList", new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("fix mAllApns bgn");
                        try {
                            Object thz = param.thisObject;
                            Class<?> clz = thz.getClass();
                            Field fid = clz.getDeclaredField("mAllApns");
                            fid.setAccessible(true);
                            Object obj = fid.get(thz);
                            ArrayList<Object> mAllApns = (ArrayList<Object>) obj;
                            for (Object apn : mAllApns) {
                                Class<?> clzApn = apn.getClass();
                                Field fidApn = clzApn.getDeclaredField("apnClass");
                                fidApn.setAccessible(true);
                                // internet 1
                                // ota 2
                                // pam 3
                                fidApn.setInt(apn, 1);
                            }
                        } catch (Throwable t) {
                            XposedBridge.log(t);
                        }
                        XposedBridge.log("fix mAllApns end");
                    }
                });
        XposedHelpers.findAndHookMethod("jp.co.sharp.android.internal.telephony.dataconnection.ApnSyncedToBP", lpParam.classLoader,
                "log", String.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args != null && param.args[0] instanceof String) {
                            String str = (String) param.args[0];
                            XposedBridge.log("[ApnSyncedToBP] " + str);
                        }
                        return null;
                    }
                });
    }
}
