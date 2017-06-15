package com.imrannazar.anpr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.net.*;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;

public class Anpr extends Activity implements SurfaceHolder.Callback
{
    private static final String LOGTAG = "Anpr";
    private Camera mCam;
    private SurfaceHolder mSH;
    private SurfaceView mSV;
    private boolean mPreview = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v(LOGTAG, "Created");
    	try
	{
	    super.onCreate(savedInstanceState);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.main);

	    mSV = (SurfaceView)findViewById(R.id.surface_camera);
	    mSH = mSV.getHolder();
	    mSH.addCallback(this);
	    mSH.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	catch(Exception e)
	{
	    Log.e(LOGTAG, "View creation failed");
	    e.printStackTrace();
	}
    }

    public void surfaceCreated(SurfaceHolder sh)
    {
        Log.v(LOGTAG, "Created surface");
	mCam = Camera.open();
	mPreview = false;
    }

    public void surfaceChanged(SurfaceHolder sh, int format, int w, int h)
    {
    	Log.v(LOGTAG, "Surface parameters changed: "+w+"x"+h);
        if(mPreview) mCam.stopPreview();
	Camera.Parameters p = mCam.getParameters();
	p.setPreviewSize(w, h);
	mCam.setParameters(p);
	Log.v(LOGTAG, "Preview size set");
	try
	{
	    mCam.setPreviewDisplay(sh);
	}
	catch(Exception e)
	{
	    Log.e(LOGTAG, "Preview set failed");
	    e.printStackTrace();
	}

	mCam.startPreview();
	mPreview = true;
    }

    public void surfaceDestroyed(SurfaceHolder sh)
    {
	Log.v(LOGTAG, "Destroyed surface");
	mCam.stopPreview();
	mPreview = false;
	mCam.release();
    }
}