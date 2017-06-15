package org.koperwlabs.toadygram;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import org.apache.cordova.*;
import org.apache.cordova.api.IPlugin;

public class ToadyGramActivity extends DroidGap {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	if (savedInstanceState == null || savedInstanceState.isEmpty())
    	{
    		super.onCreate(savedInstanceState); //app would not load without this either in or outside of if() statement
    		super.loadUrl("file:///android_asset/www/index.html");
    	}
    	else
    	{
    		String sCallback = savedInstanceState.getString("callback");
    		if (sCallback != null)
    		{
    			try
    			{
    				if (sCallback.equals(PersistentCamera.class.toString()))
    				{
                                        super.init();
    					PersistentCamera c = new PersistentCamera();
    					c.callbackId = savedInstanceState.getString("callbackId");
    					c.setContext(this);
    					c.setView(this.appView);
    					String uri = savedInstanceState.getString("imageUri");
    					if (uri != null)
    					{
    						c.imageUri = Uri.fromFile(new File(uri));
    					}
    					c.mediaType = savedInstanceState.getInt("mediaType");
    					c.targetHeight = savedInstanceState.getInt("targetHeight");
    					c.targetWidth = savedInstanceState.getInt("targetWidth");
    					setActivityResultCallback(c);
    				}
    			}
    			catch (Exception e)
    			{
    				//e.printStackTrace();
    			}
    		}
    		// add a hash to the url to indicate we need to add the custom callback
    		super.loadUrl("file:///android_asset/www/index.html#new");
    	}
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
    	IPlugin callback = (IPlugin) this.activityResultCallback;
    	if (callback != null)
    	{
    		String className = callback.getClass().toString();
    		savedInstanceState.putString("callback", className);

    		if (className.equals(PersistentCamera.class.toString()))
    		{
    			PersistentCamera c = (PersistentCamera) callback;

    			savedInstanceState.putString("callbackId", "Camera");
    			if (c.imageUri != null)
    				savedInstanceState.putString("imageUri", c.imageUri.getPath());
    			savedInstanceState.putInt("mediaType", c.mediaType);
    			savedInstanceState.putInt("targetHeight", c.targetHeight);
    			savedInstanceState.putInt("targetWidth", c.targetWidth);
    		}
    	}
    	super.onSaveInstanceState(savedInstanceState);
    }
}