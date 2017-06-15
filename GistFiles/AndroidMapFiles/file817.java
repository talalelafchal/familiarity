  /**
   * Enables and disables {@linkplain android.app.Activity activities} based on their
   * {@link #TARGET_FORM_FACTOR_ACTIVITY_METADATA}" meta-data and the current device.
   * Values should be either "handset", "tablet", or not present (meaning universal).
   * <p>
   * <a href="http://stackoverflow.com/questions/13202805">Original code</a> by Dandre Allison.
   * @param context the current context of the device
   * @see #isHoneycombTablet(android.content.Context)
   */
public static void enableDisableActivitiesByFormFactor(Context context) {
    final PackageManager pm = context.getPackageManager();
    boolean isTablet = isTablet(context);

    try {
        PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        if (pi == null) {
            LOGE(TAG, "No package info found for our own package.");
            return;
        }

        final ActivityInfo[] activityInfos = pi.activities;
        for (ActivityInfo info : activityInfos) {
            String targetDevice = null;
            if (info.metaData != null) {
                targetDevice = info.metaData.getString(TARGET_FORM_FACTOR_ACTIVITY_METADATA);
            }
            boolean tabletActivity = TARGET_FORM_FACTOR_TABLET.equals(targetDevice);
            boolean handsetActivity = TARGET_FORM_FACTOR_HANDSET.equals(targetDevice);

            boolean enable = !(handsetActivity && isTablet)
                    && !(tabletActivity && !isTablet);

            String className = info.name;
            pm.setComponentEnabledSetting(
                    new ComponentName(context, Class.forName(className)),
                    enable
                            ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    } catch (PackageManager.NameNotFoundException e) {
        LOGE(TAG, "No package info found for our own package.", e);
    } catch (ClassNotFoundException e) {
        LOGE(TAG, "Activity not found within package.", e);
    }
}

public static boolean isTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
            & Configuration.SCREENLAYOUT_SIZE_MASK)
            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
}