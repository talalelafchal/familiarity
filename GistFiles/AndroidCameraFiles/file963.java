package org.example.video;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;
import android.util.Log;

public class Video extends Activity {

	public static final String TAG = &quot;Video&quot;;
	private VideoView videoObject;
	private String videoLocation;
	private int videoPosition;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // create view
        setContentView(R.layout.main);

    	// initialize video object
        videoObject = (VideoView) findViewById(R.id.video);

        // attempt to get data from before device configuration change
        Bundle returnData = (Bundle) getLastNonConfigurationInstance();
        
        if (returnData == null) {
        	
        	// first startup
        	Log.d(TAG, &quot;Player started for 1st time&quot;);
        		        
	        // set video path
	        videoLocation = &quot;/sdcard/DCIM/Camera/video-2010-01-10-15-21-17.3gp&quot;;
	        videoObject.setVideoPath(videoLocation);
	        
	        // play video
	        videoObject.start();
	        
        } else {
        	// restart after device config change
        	Log.d(TAG, &quot;Player re-started after device configuration change&quot;);
        	
        	// fetch data from bundle
        	videoLocation = returnData.getString(&quot;LOCATION&quot;);
        	videoPosition = returnData.getInt(&quot;POSITION&quot;);
    		Log.d(TAG, &quot;Video location: &quot; + videoLocation);
    		Log.d(TAG, &quot;Video position: &quot; + videoPosition);
    		
    		// apply properties to new object
	        videoObject.setVideoPath(videoLocation);
	        videoObject.seekTo(videoPosition);
	        
	        // play video
        	videoObject.start();
        }
    }
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		// Device configuration changed
		// Save current video playback state
		Log.d(TAG, &quot;Saving video playback state&quot;);
		videoPosition = videoObject.getCurrentPosition();
		Log.d(TAG, &quot;Video location: &quot; + videoLocation);
		Log.d(TAG, &quot;Video position: &quot; + videoPosition);

		// Build bundle to save data for return
		Bundle data = new Bundle();
		data.putString(&quot;LOCATION&quot;, videoLocation);
		data.putInt(&quot;POSITION&quot;, videoPosition);
		return data;
	}
	

	
}
