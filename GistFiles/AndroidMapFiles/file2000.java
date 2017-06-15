package tewilove.unlock.pb25;

/**
 * Created by tewilove on 15/10/18.
 */

import android.content.res.XModuleResources;

import java.lang.Override;
import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XHook_TeleService implements IXposedHookZygoteInit, IXposedHookInitPackageResources,
        IXposedHookLoadPackage {

    private static String MODULE_PATH = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.android.phone"))
            return;
        try {
            XposedBridge.log("hook bgn");
            // resparam.res.setReplacement("com.android.phone", "bool", "world_phone", false);
            // resparam.res.setReplacement("com.android.phone", "bool", "config_enabled_lte", true);
            XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
            resparam.res.setReplacement("com.android.phone", "array", "preferred_network_mode_choices",
                    modRes.fwd(R.array.preferred_network_mode_choices));
            resparam.res.setReplacement("com.android.phone", "array", "preferred_network_mode_values",
                    modRes.fwd(R.array.preferred_network_mode_values));
            XposedBridge.log("hook end");
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpParam) throws Throwable {
        if (!lpParam.packageName.equals("com.android.phone"))
            return;
        try {
            XposedHelpers.findAndHookMethod("com.android.internal.telephony.CallManager", lpParam.classLoader,
                    "unregisterPhone",
                    Class.forName("com.android.internal.telephony.Phone"),
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("prevent com.android.phone from force closing");
                            try {
                                Object thz = param.thisObject;
                                Class<?> clz = thz.getClass();
                                Field fid = clz.getDeclaredField("mWFCStateRegistered");
                                fid.setAccessible(true);
                                fid.setBoolean(thz, false);
                            } catch (Throwable t) {
                                XposedBridge.log(t);
                            }
                        }
                    }
            );
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}
