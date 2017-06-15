// replace package name with your bundle identifier
package com.arm.mgdactivity;

import android.os.Bundle;
import android.util.Log;

public class MGDNativeActivity extends UnityPlayerNativeActivity
{
    @Override protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
            System.loadLibrary("MGD");
        }
        catch( UnsatisfiedLinkError e ) {
            // Feel free to remove this log message.
            Log.i("MGD", "libMGD.so not loaded.");
        }
    }
}