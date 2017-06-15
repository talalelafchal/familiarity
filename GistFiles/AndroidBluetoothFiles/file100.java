package com.dnn.zapbuild.commons.util;

import android.os.StrictMode;

import com.dnn.zapbuild.commons.BuildConfig;

/**
 * Created by zapbuild on 28/11/14.
 * This class contains methods that are useful for performance analysis.
 * Other ways-
 * 1.Strict mode enable in developer options
 * 2.Don't keep activities enabled
 * 3.Gpu Overdraw setting
 * 4.Hierarchy viewer in Device monitor
 *
 * Tips
 * 1.Use apply instead of commit in Shared preferences if you are not using the result.
 */
public class PerformanceUtil {

    private static final boolean DEVELOPER_MODE=true;

    /**
     * Alternatively enable Strict mode enable on device Developer settings
     */
    public static  void enableStrictMode()
    {
        if(BuildConfig.DEBUG && DEVELOPER_MODE) {
            // Activate StrictMode
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                            // alternatively .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }



}
