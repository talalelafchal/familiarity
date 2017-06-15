package tewilove.unlock.pb25;

import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ming on 15-10-22.
 */
public class XHook_SystemProperties implements IXposedHookLoadPackage {
    private static Map<String,String> sStringProperties = new HashMap<String,String>();
    private static Map<String,Integer> sIntegerProperties = new HashMap<String,Integer>();
    private static XC_MethodHook sHook_android_os_SystemProperties_get = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            if (param.args == null)
                return;
            if (param.args.length < 1)
                return;
            if (!(param.args[0] instanceof String))
                return;
            String key = (String) param.args[0];
            if (sStringProperties.containsKey(key))
                param.setResult(sStringProperties.get(key));
            param.hasThrowable();
        }
    };
    private  static XC_MethodHook sHook_android_os_SystemProperties_getInt = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            if (param.args == null)
                return;
            if (param.args.length < 1)
                return;
            if (!(param.args[0] instanceof String))
                return;
            String key = (String) param.args[0];
            if (sIntegerProperties.containsKey(key))
                param.setResult(sIntegerProperties.get(key));
        }
    };

    static {
        sStringProperties.put("ro.debuggable", "1");
        sStringProperties.put("telephony.lteOnCdmaDevice", "0");
        sStringProperties.put("ro.telephony.default_network", "9");
        sIntegerProperties.put("ro.debuggable", Integer.valueOf(1));
        sIntegerProperties.put("telephony.lteOnCdmaDevice", Integer.valueOf(0));
        sIntegerProperties.put("ro.telephony.default_network", Integer.valueOf(9));
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Those are properties applies to ALL
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader,
                "get", String.class,
                sHook_android_os_SystemProperties_get);
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader,
                "get", String.class, String.class,
                sHook_android_os_SystemProperties_get);
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader,
                "getInt",
                String.class, int.class,
                sHook_android_os_SystemProperties_getInt);
    }

}
