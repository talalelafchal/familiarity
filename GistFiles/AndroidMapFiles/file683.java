/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.copy;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class AirHockeyActivity extends Activity {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView glSurfaceView;//声明GLSufaceView，用于GLSufaceView实例
    private boolean rendererSet = false;//通过renderSet记录GLSurfaceView是否处于有效状态

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);//新建GLSufaceView实例，用于初始化OpenGL

        // Request an OpenGL ES 2.0 compatible context./配置surface视图
        glSurfaceView.setEGLContextClientVersion(2);
        // Assign our renderer./给surfaceView绑定渲染器Render
        glSurfaceView.setRenderer(new AirHockeyRenderer(this));
        //记录渲染状态为true
        rendererSet = true;

        //检查模拟器是否支持OpenGL ES 2.0

     /*   // 检查设备是否支持OpenGL ES 2.0
        ActivityManager activityManager = 
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
            .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
            configurationInfo.reqGlEsVersion >= 0x20000
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 
                 && (Build.FINGERPRINT.startsWith("generic")
                  || Build.FINGERPRINT.startsWith("unknown")
                  || Build.MODEL.contains("google_sdk") 
                  || Build.MODEL.contains("Emulator")
                  || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);            
            
            // Assign our renderer.
            glSurfaceView.setRenderer(new AirHockeyRenderer(this));
            rendererSet = true;
        } else {
            *//*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since 
             * we're not doing anything, the app will crash if the device 
             * doesn't support OpenGL ES 2.0. If we publish on the market, we 
             * should also add the following to AndroidManifest.xml:
             * 
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             * 
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             *//*
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG).show();
            return;
        }*/

        //将GLSurfaceView添加到Activity中，显示到屏幕上
        setContentView(glSurfaceView);
    }

    //处理GLSurfaceView声明周期
    @Override
    protected void onPause() {
        super.onPause();

        //判断是否处于渲染有效状态，进而执行onPause操作
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //判断是否处于渲染有效状态，进而执行onResume操作
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}