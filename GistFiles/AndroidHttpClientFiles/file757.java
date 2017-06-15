package org.hackerdojo.shutterpost;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity { 
    private Preview preview;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
        		WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	*/
        preview = new Preview(this);
        setContentView(preview);
    }
    
    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
    	if(keycode == KeyEvent.KEYCODE_CAMERA || keycode == KeyEvent.KEYCODE_DPAD_CENTER) {
			preview.takePicture();
			return true;
		} else {
			return super.onKeyDown(keycode, event);
		}
    }
    
}


class Preview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback {
    SurfaceHolder holder;
    Camera camera;
    UploadQueue uploadQueue;
    boolean readyToSnap;
    
    private static final String TAG = "ShutterPost";
    
    Preview(Context context) {
        super(context);
        
        uploadQueue = new UploadQueue();
        uploadQueue.start();
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open();
        try {
           camera.setPreviewDisplay(holder);
           readyToSnap = true;
        } catch (IOException exception) {
            camera.release();
            camera = null;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera.release();
        camera = null;
        readyToSnap = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(w, h);
        camera.setParameters(parameters);
        camera.startPreview();
    }

	@Override
	public void onPictureTaken(final byte[] data, final Camera camera) {
		Log.d(TAG, "onPictureTaken");
		uploadQueue.upload(data);
		camera.startPreview();
		readyToSnap = true;
	}
	
	public void takePicture() {
		if(readyToSnap) {
			readyToSnap = false;
			camera.takePicture(null, null, this);
		}
	}
}

class UploadQueue extends HandlerThread implements Handler.Callback {
	private static final String TAG = "UploadQueue";
	private static final int UPLOAD_DATA = 1;
	
	Handler handler;
	
	public UploadQueue() {
		super(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
	}
	
	public void onLooperPrepared() {
		handler = new Handler(getLooper(), UploadQueue.this);
	}
	
	public void upload(byte[] jpegData) {
		Log.d(TAG, "queuing");
		handler.sendMessage(handler.obtainMessage(UPLOAD_DATA, jpegData));
	}
	
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
		case UPLOAD_DATA:
			doUpload((byte[])msg.obj);
			return true;
		}
		return false;
	}
	
	private void doUpload(byte[] jpegData) {
		Log.d(TAG, "doUpload: " + jpegData.length + " bytes");
		
		final MultipartEntity entity = new MultipartEntity();
		
		entity.addPart(
				"photo",
				new InputStreamBody(
						new ByteArrayInputStream(jpegData),
						"shutterpost.jpeg"));
		
		/*
		try {
			entity.addPart("photo", new StringBody(new String(jpegData, "UTF-8"), Charset.forName("UTF-8")));
		} catch (IllegalCharsetNameException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedCharsetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		final String destination = "http://www.postbin.org/ttxp20";
		//final String destination = "http://shutterpost.nfshost.com/";
		//final String destination = "http://chapaai.adamsmith.as:8787/";
		final HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter("http.useragent", "ShutterPost/0.0");
		final HttpPost request = new HttpPost(destination);
		request.setEntity(entity);
				
		Log.d(TAG, "request: " + request);
		
		HttpResponse response = null;
		
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.d(TAG, "response: " + response.getStatusLine());
	}
}