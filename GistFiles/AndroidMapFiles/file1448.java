package com.capitalone.mobile.wallet.testing;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.manifest.ActivityData;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.manifest.IntentFilterData;
import org.robolectric.res.builder.DefaultPackageManager;

public class RobolectricTestUtils {

    /**
     * Get the "AndroidManifest" model for an Initialized Robolectric Application.
     */
    public static AndroidManifest getAndroidManifest() {
        DefaultPackageManager pm = (DefaultPackageManager) RuntimeEnvironment.getRobolectricPackageManager();
        try {
            Field androidManifestsField = DefaultPackageManager.class.getDeclaredField("androidManifests");
            androidManifestsField.setAccessible(true);
            Map manifestsMap = (Map) androidManifestsField.get(pm);
            Object manifestObject = new ArrayList(manifestsMap.values()).get(0);
            if (manifestObject != null && manifestObject instanceof AndroidManifest) {
                return (AndroidManifest) manifestObject;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Determine if an intent filter is registered for an Activity.
     */
    public static boolean getIntentFiltersForActivity(String activityClassName, String intentFilterAction) {
        AndroidManifest manifest = getAndroidManifest();
        ActivityData activityData = manifest.getActivityData(activityClassName);
        List<IntentFilterData> intentFilters = activityData.getIntentFilters();
        for (IntentFilterData intentFilter : intentFilters) {
            List<String> actions = intentFilter.getActions();
            if (actions.contains(intentFilterAction)) {
                return true;
            }
        }
        return false;
    }
}