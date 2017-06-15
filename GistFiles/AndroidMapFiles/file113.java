package com.package.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by umesh0492 on 04/01/16.
 */
public class AccessibilityUtils {

    private static final String TAG = "AccessibilityUtils";


    public static boolean isAccessibilityEnabled(Context context) {
        return isAccessibilityEnabled(context, context.getPackageName() + "/com.package.accessibility.NewAccessibilityService");
    }

    private static boolean isAccessibilityEnabled(Context context, String str) {
        for (AccessibilityServiceInfo id : ((AccessibilityManager)
                context.getSystemService(Context.ACCESSIBILITY_SERVICE))
                .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)) {
            Log.d(TAG, id.getId());
            if (str.equals(id.getId())) {
                return true;
            }
        }
        return false;
    }
}
