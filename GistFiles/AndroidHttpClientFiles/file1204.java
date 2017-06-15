package com.example.smartthermostat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;









import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import com.example.smartthermostat.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.RadioGroup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.loopj.android.http.*;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Home extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	private int actual_temp;
	private int target_temp;
	
	public void setActualTemp(int value) {
		final TextView actual_temp_value = (TextView) findViewById(R.id.actual_temp_value);
		actual_temp = value;
		actual_temp_value.setText(String.valueOf(value) + "° F");
	}
	
		
	public void setTargetTemp(int value) {
		final TextView target_temp_value = (TextView) findViewById(R.id.target_temp_value);
		target_temp = value;
		target_temp_value.setText(String.valueOf(value) + "° F");
	}
		
	public void incTargetTemp() {
		setTargetTemp(target_temp+1);
	}
	
	public void decTargetTemp() {
		setTargetTemp(target_temp-1);
	}
	
	public void putTargetTemp(int value) throws Exception{
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("Target Temperature", "value");
		client.post("http://api.mtgapi.com/v1/card/name/raging%20goblin", params, new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	
		    	System.out.println("it worked");	    	
		    	
		    }});
		
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.show();
		
	
		// Or do it manually
		setTargetTemp(40);
		
		// Setup the buttons
		final Button goto_builder = (Button) findViewById(R.id.goto_builder_button);
		final Button goto_saved_schedules = (Button) findViewById(R.id.goto_saved_schedules_button);
		final Button goto_help = (Button) findViewById(R.id.goto_help_button);
		final Button goto_setup = (Button) findViewById(R.id.goto_setup_button);
		final RadioGroup fan_choice = (RadioGroup) findViewById(R.id.fan_status_value);
		final RadioGroup mode_choice = (RadioGroup) findViewById(R.id.mode_status);
		fan_choice.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch(checkedId)
				{
				case R.id.fan_status_auto:
					// Something
					
					System.out.println("fan_auto");
					break;
				case R.id.fan_status_off:
					// Something
					System.out.println("fan_off");
					break;
				case R.id.fan_status_on:
					// Something
					System.out.println("fan_on");
					break;
				}
			}
		});
		
		mode_choice.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				String hello = getSelected(mode_choice);
				System.out.println(hello);
				switch(checkedId)
				{
				case R.id.mode_cool:
					// Something
					
					break;
				case R.id.mode_heat:
					
					// Something
					break;
				case R.id.mode_off:
					// Something
					break;
				}
			}
		});
		


		goto_builder.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startBuilder = new Intent("com.example.smartthermostat.BUILDER");
				startActivity(startBuilder);
				
			}
		});
		goto_saved_schedules.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startSaved = new Intent("com.example.smartthermostat.SAVEDSHEDULES");
				startActivity(startSaved);
				
			}
		});
		goto_help.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startHelp = new Intent("com.example.smartthermostat.HELP");
				startActivity(startHelp);
				
			}
		});
		goto_setup.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent startSetup = new Intent("com.example.smartthermostat.SETUP");
				startActivity(startSetup);
				
//				AsyncHttpClient client = new AsyncHttpClient();
//				client.get("http://www.google.com", new AsyncHttpResponseHandler() {
//				    @Override
//				    public void onSuccess(String response) {
//				        System.out.println(response);
//				        goto_setup.setText("FUUUU!");
//				    }
//				});  
			}
				});                                                                  
	
		
		findViewById(R.id.target_temp_inc).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				incTargetTemp();
				System.out.println("Increment the temperature");
			}
		});
		
		findViewById(R.id.target_temp_dec).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				decTargetTemp();
				System.out.println("Decrement the temperature");
			}
		});
		
		new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                    	System.out.println("run the request");
                        Thread.sleep(10000);
                        String[] status = new String[3];
                        status = get_status();
                        
                        
                        
                        //runOnUiThread(new Runnable() // start actions in UI thread
                        {
//
//                            @Override
//                            public void run()
//                            {
//                            	String[] status = new String[3];
//                                status = get_status();
//                            }
                        //});
                    }
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        }).start(); // the while thread will start in BG thread
	}



	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	public String[] get_status()
	
	{
		System.out.println("do we even get in here");
		final String[] status_return = new String[4];
		AsyncHttpClient client = new AsyncHttpClient();
		//currently sets the target temp from the id of a magic card
		try{
		client.get("http://smartstat.no-ip.biz:9000/temperature/", new AsyncHttpResponseHandler() {
		    @Override
		    public void onSuccess(String response) {
		    	
		    	Object json = JSONValue.parse(response);
		    	String value = (String)json.get("current");
		    	
//		    	JSONObject status = (JSONObject)status_array.get(0);
//		    	status_return[0] = (String) status.get("current temperature");
//		    	status_return[1] = (String) status.get("target temperature");
//		    	status_return[2] = (String) status.get("mode");
		    	System.out.println(value);
		    //	System.out.println(status.get("scale"));
		    	
		    }});
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("fuck my life");
		}
		
		
		
		System.out.println("are we exiting");
		return status_return;
		
	}
	public String getSelected(RadioGroup group){
		  int id= group.getCheckedRadioButtonId();
		    View radioButton = group.findViewById(id);
		    int radioId = group.indexOfChild(radioButton);
		    RadioButton btn = (RadioButton) group.getChildAt(radioId);
		    String selection = (String) btn.getText();
		    return selection;
	}
	public int get_mode(String cool_heat, String fan){
		int mode = 0;
		String on = "on";
		String off = "off";
		String auto = "auto";
		String cool = "cool";
		String heat = "heat";
		System.out.println(cool_heat.equals(cool));
		if((cool_heat.equals(off)) && (fan.equals(on)))
		{
			mode = 1;
		}
		
		if((cool_heat.equals(heat)) && (fan.equals(auto)))
		{
			mode = 2;
		}
		
		if((cool_heat.equals(heat)) && (fan.equals(on)))
		{
			mode = 3;
		}
		
		if((cool_heat.equals(cool)) && (fan.equals(auto)))
		{
			mode = 4;
		}
		
		if((cool_heat.equals(cool)) && (fan.equals(on)))
		{
			mode = 5;
		}
		
		return mode;
	}
	

public String get_mode_fan(int mode){
	String mode_fan0 = "off"; // cool_heat
	String mode_fan1 = "off";// fan
	String final_mode = mode_fan0 + " " + mode_fan1;
	
	if(mode == 1);
	{
		mode_fan0 = "off";
		mode_fan1 = "on";
		final_mode = mode_fan0 + " " + mode_fan1;
	}
	
	if(mode == 2)
	{
		mode_fan0 = "heat";
		mode_fan1 = "auto";
		final_mode = mode_fan0 + " " + mode_fan1;
	}
	if(mode == 3)
	{
		mode_fan0 = "heat";
		mode_fan1 = "on";
		final_mode = mode_fan0 + " " + mode_fan1;
	}
	
	if(mode == 4)
	{
		mode_fan0 = "cool";
		mode_fan1 = "auto";
		final_mode = mode_fan0 + " " + mode_fan1;
	}
	
	if(mode == 5)
	{
		mode_fan0 = "cool";
		mode_fan1 = "on";
		final_mode = mode_fan0 + " " + mode_fan1;
	}
	return final_mode;
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */






	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};
	
    ////  Make sure the buttons are on the screen
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
