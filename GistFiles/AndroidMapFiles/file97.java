package ti.sygic;

import java.util.HashMap;

import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiCompositeLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.SurfaceView;

import com.sygic.ApplicationApi.ApiCallback;
import com.sygic.ApplicationApi.ApplicationAPI;
import com.sygic.ApplicationApi.ApplicationEvents;
import com.sygic.ApplicationApi.ApplicationHandler;
import com.sygic.ApplicationApi.LONGPOSITION;
import com.sygic.ApplicationApi.NavigationParams;
import com.sygic.ApplicationApi.SError;
import com.sygic.ApplicationApi.SWayPoint;
import com.sygic.drive.SygicDriveActivity;

public class NavigationActivity extends SygicDriveActivity {

	private Handler mHandler = new Handler();
	private static final String LCAT = "NavigationActivity";

	private static NavigationActivity _instance;
	public static NavigationActivity getInstance() {
		return _instance;
	}
	private static SygicModule _module;
	public static void setParentModule(SygicModule module) {
		_module = module;
	}
	private static TiViewProxy _view;
	public static void setView(TiViewProxy view) {
		_view = view;
	}
	private static SurfaceView _surface;
	public static void setSurfaceView(SurfaceView surface) {
		_surface = surface;
	}
	private static Float _latitude;
	private static Float _longitude;
	public static void setDestination(Float latitude, Float longitude) {
		_latitude = latitude;
		_longitude = longitude;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_instance = this;

		if(_surface == null)
		{
			Log("Creating surface view");
			_surface = new SurfaceView(this);
			Log("Getting layout");
			TiCompositeLayout layout = (TiCompositeLayout) _view.peekView().getNativeView();
			Log("Adding surface view to layout");
			layout.addView(_surface);
		}
		else
			Log("Surface view already created");

		Log("Calling startDrive");
		StartDrive();
	}
	
	public void StartDrive()
	{
		try
		{
			final ApiCallback callback  = new ApiCallback() {
                public void onInitApi() {
                	// code to make sure that GPS is enabled before initializing
    				String provider = "com.android.settings.widget.SettingsAppWidgetProvider";
    				Criteria criteria = new Criteria();
    				criteria.setAccuracy(Criteria.NO_REQUIREMENT);
    				criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
    				criteria.setCostAllowed(false);
    				ContentResolver contentResolver = getContentResolver();

    				if (Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER) == false) {
    					Log("Gps disabled, launching gps settings intent");
    					final Intent poke = new Intent();
    					poke.setClassName("com.android.settings", provider);
    					poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
    					poke.setData(Uri.parse("3"));
    					sendBroadcast(poke);
    				}
                	
                    //init api.
        			Log("Initializing API");
        			//System.loadLibrary("ApplicationAPI");
        			int status = ApplicationAPI.InitApi(getPackageName(), true, msgHandler); // api initialization
        			Log("status = " + Integer.toString(status));
        	
        			if (status != 1)
        				Log("InitApi failed.");
        			else 
        			{
        				//set route.
        				SetRoute();
        			}
                }
                public void onRunDrive() {
                    // must be post to UI thread
                	mHandler.post(new Runnable() {
                        public void run() {
                        	Log("Calling runDrive");
		                	runDrive(_surface, getPackageName());
                        }
                	});
                }
			};
			Log("Calling startDrive");
			ApplicationAPI.startDrive(callback);
		}
		catch(Exception exc)
		{
			Log(exc.toString());
		}
	}

	final ApplicationHandler msgHandler = new ApplicationHandler() {

		@Override
		public void onApplicationEvent(int nEvent, String strData) {
			Log("Event No. " + Integer.toString(nEvent) + " detected."); // event handling

			// TODO: fire events?
			// _module.fireEvent("rar", new HashMap<String, String>());

			switch (nEvent) {
				case ApplicationEvents.EVENT_WAIPOINT_VISITED:
					Log("Waypoint visited.");
					break;
				case ApplicationEvents.EVENT_ROUTE_COMPUTED:
					Log("Route computed.");
					break;
				case ApplicationEvents.EVENT_ROUTE_FINISH:
					Log("Route finished.");
					break;
				case ApplicationEvents.EVENT_POI_WARNING:
					Log("Poi warning!.");
					break;
				case ApplicationEvents.EVENT_CHANGE_LANGUAGE:
					Log("Language changed.");
					break;
				case ApplicationEvents.EVENT_EXIT_MENU:
					Log("Menu exited.");
					break;
				case ApplicationEvents.EVENT_MAIN_MENU:
					Log("Entering main menu.");
					break;
				case ApplicationEvents.EVENT_BORDER_CROSSING:
					Log("Crossing border.");
			}
		}
	};
	
	private void SetRoute()
	{
		Log("Starting navigation to route " + _latitude + " " + _longitude);
		SError error = new SError(); 
		SWayPoint Location = new SWayPoint();
		Integer latitude = (int)(_latitude * 100000);
		Integer longitude = (int)(_longitude * 100000);
		Location.Location = new LONGPOSITION(latitude, longitude);
		ApplicationAPI.StartNavigation(error, Location, NavigationParams.NpMessageAvoidTollRoadsUnable, true, true, 10000);
	}
	
	private static void Log(String message)
	{
		org.appcelerator.kroll.common.Log.i(LCAT, message);
		android.util.Log.i(LCAT, message);
	}

}