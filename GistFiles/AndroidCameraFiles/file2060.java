package mobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import mobile.core.ActivationDetailActivity;
import mobile.exception.ApplicationException;
import mobile.model.core.Internacionalizable;
import mobile.model.core.Language;
import mobile.model.remote.ActivationClosureCauseClass;
import mobile.model.remote.ActivationClosureType;
import mobile.model.remote.DispatchProject;
import mobile.model.remote.InterruptionType;
import mobile.model.remote.ItemPendingType;
import mobile.model.remote.PendingType;
import mobile.model.remote.ServerInformationDto;
import mobile.model.remote.StandbyType;
import mobile.model.remote.VehicleLicensePlate;
import mobile.model.remote.WorkOrderRefusalType;
import mobile.service.SessionHandler;
import mobile.utils.EngineerTracker;

/**
 * Basic class to be used by all activities on the application.
 * 
 */
@SuppressLint("UseSparseArrays")
public abstract class BasicActivity extends FragmentActivity {

	private SessionHandler sessionHandler;
	
	public SessionHandler getSessionHandler() {
		sessionHandler = new SessionHandler();
		return sessionHandler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * Check if the application is on debug mode.
	 * 
	 * @return
	 */
	public boolean isDebugMode() {
		boolean debuggable = false;

		PackageManager pm = getApplicationContext().getPackageManager();
		try {
			ApplicationInfo appinfo = pm.getApplicationInfo(
					getApplicationContext().getPackageName(), 0);
			debuggable = (0 != (appinfo.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
		} catch (NameNotFoundException e) {
			Log.d("BASIC", "Debug flag is not available.", e);
		}

		return debuggable;
	}

	/**
	 * From a list of internacionalizable enum get the list of strings using the
	 * current location.
	 * 
	 * @param internacionalizables
	 * @return
	 */
	public List<String> getInternaciolizedValues(Context context,
			List<? extends Internacionalizable> internacionalizables) {
		List<String> result = new LinkedList<String>();

		for (Internacionalizable obj : internacionalizables) {
			int resource = context.getResources()
					.getIdentifier("int_" + obj.getI18ln(), "string",
							context.getPackageName());

			result.add(context.getResources().getText(resource).toString());
		}

		// sort in alphabet order
		Collections.sort(result);
		return result;
	}

	/**
	 * From a exception gets the i18ln value.
	 * 
	 * @param exception
	 * @return
	 */
	public String getI18lnException(ApplicationException exception) {
		String result = null;
		if (exception != null) {
			int resource = getApplicationContext().getResources()
					.getIdentifier("int_" + exception.getI18ln(), "string",
							getApplicationContext().getPackageName());

			result = getApplicationContext().getResources().getText(resource)
					.toString();
		}
		return result;
	}

	/**
	 * Get the object of internacionalizable based on the label.
	 * 
	 * @param context
	 * @param internacionalizables
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Internacionalizable> T getObjectFromInternacionalized(
			List<? extends Internacionalizable> internacionalizables,
			String value) {
		T result = null;
		
		for (Internacionalizable obj : internacionalizables) {
			int resource = getApplicationContext().getResources()
					.getIdentifier("int_" + obj.getI18ln(), "string",
							getApplicationContext().getPackageName());
			
			// test string from the resource
			if (getApplicationContext().getResources().getText(resource)
					.toString().equals(value)) {
				result = (T) obj;
			}
		}

		return result;
	}
	
	/**
	 * From a list of vehicle plates get the list of strings - vehicle plate
	 * @param <T>
	 * 
	 * @param list
	 * @param defaultValue
	 * @return
	 */
	public <T> List<String> getSpinnerStringList(List<T> list,
			boolean defaultValue, Class<T> typeClass) {
		
		List<String> result = new LinkedList<String>();

		if (list != null) {
			for (T obj : list) {
				String name = "";
				if (typeClass.isAssignableFrom(VehicleLicensePlate.class)) {
					name = ((VehicleLicensePlate) obj).getPlate();
				} else if (typeClass.isAssignableFrom(ItemPendingType.class)) {
					name = ((ItemPendingType) obj).getCode();
				} else if (typeClass.isAssignableFrom(PendingType.class)) {
					name = ((PendingType) obj).getCode();
				} else if (typeClass.isAssignableFrom(InterruptionType.class)) {
					name = ((InterruptionType) obj).getCode();
				} else if (typeClass.isAssignableFrom(ActivationClosureCauseClass.class)) {
					name = ((ActivationClosureCauseClass) obj).getName();
				} else if (typeClass.isAssignableFrom(ActivationClosureType.class)) {
					name = ((ActivationClosureType) obj).getName();
				} else if (typeClass.isAssignableFrom(WorkOrderRefusalType.class)) {
					name = ((WorkOrderRefusalType) obj).getAbbreviation();
				} else if (typeClass.isAssignableFrom(DispatchProject.class)) {
					name = ((DispatchProject) obj).getName();
				} else if (typeClass.isAssignableFrom(StandbyType.class)) {
					name = ((StandbyType) obj).getCode();
				}

				result.add(name);
			}
		}

		if (defaultValue) {
			result.add("");
		}

		// sort in alphabet order
		Collections.sort(result);
		return result;
	}


	/**
	 * Set a list of internacionalizable enum into a spinner.
	 * 
	 * @param spinner
	 * @param internacionalizables
	 */
	public void addSpinnerValues(Spinner spinner,
			List<? extends Internacionalizable> internacionalizables) {

		List<String> strings = new ArrayList<String>(getInternaciolizedValues(
				getApplicationContext(), internacionalizables));

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, strings);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);
	}

	/**
	 * Set a list of string into a spinner.
	 * 
	 * @param spinner
	 * @param stringList
	 */
	public static void addSpinnerStringValues(Spinner spinner,
			final List<String> stringList, final boolean defaultValue, Context context) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.spinner_item, stringList) {

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				if (defaultValue) {
					View v = null;
					if (position == 0) {
						TextView tv = new TextView(getContext());
						tv.setHeight(0);
						tv.setVisibility(View.GONE);
						v = tv;
					} else {
						v = super.getDropDownView(position, null, parent);
					}
					return v;
				} else {
					return super.getDropDownView(position, null, parent);
				}

			}
		};

		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		spinner.setAdapter(adapter);
	}

	/**
	 * Set a list of integers into a spinner.
	 * 
	 * @param spinner
	 * @param integerList
	 */
	public void addSpinnerIntegerValues(Spinner spinner,
			List<Integer> integerList) {

		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
				R.layout.spinner_item, integerList);

		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		spinner.setAdapter(adapter);
	}

	/**
	 * Get string from object
	 * 
	 * @param obj
	 * @return
	 */
	public String getInternacionalizableStringValue(Internacionalizable obj) {

		Context context = getApplicationContext();

		int resource = context.getResources().getIdentifier(
				"int_" + obj.getI18ln(), "string", context.getPackageName());

		return context.getResources().getText(resource).toString();
	}

	/**
	 * Get position of a item by its value
	 * 
	 * @param spinner
	 * @param myString
	 * @return
	 */
	public int getIndex(Spinner spinner, String myString) {

		int index = 0;

		for (int i = 0; i < spinner.getCount(); i++) {
			if (spinner.getItemAtPosition(i).equals(myString)) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * Formatted text
	 * 
	 * @param text
	 * @param activationsCount
	 * @return
	 */
	public String setFormattedText(String text, String required) {

		String label = text + " <font color='red'><small>";
		label += required;
		label += "</small></font>";

		return label;
	}
	
	public int getResourceIdByStringIdentifier(String identifier) {
		return getResources().getIdentifier(identifier, "string",
				getPackageName());
	}
	
	public Language getLocaleFromString(String language){
		language = language != null && !language.isEmpty() ? language.replaceAll("\"", "") : "en";
		
		if (language.equalsIgnoreCase("西班牙") || 
				language.equalsIgnoreCase("Espanhol") || 
				language.equalsIgnoreCase("Spanish") || 
				language.equalsIgnoreCase("es") || 
				language.equalsIgnoreCase("Español")) {
			return Language.SPANISH;
		} else if (language.equalsIgnoreCase("中国") || 
				language.equalsIgnoreCase("Chinês") || 
				language.equalsIgnoreCase("Chinese") || 
				language.equalsIgnoreCase("zh") || 
				language.equalsIgnoreCase("Chino")) {
			return Language.CHINESE;
		} else if (language.equalsIgnoreCase("葡萄牙") || 
				language.equalsIgnoreCase("Português") || 
				language.equalsIgnoreCase("Portuguese") || 
				language.equalsIgnoreCase("pt") || 
				language.equalsIgnoreCase("Portugués")) {
			return Language.PORTUGUESE;
		} else {
			return Language.ENGLISH; 
		}
	}
	
	/**
	 * Show date according to user locale
	 * 
	 * @param locale
	 * @param date
	 * @return
	 */
	public static String getFormatedDate(String locale, Date date) {
		
		locale = locale == null ? "en" : locale;

		// default pattern
		String pattern = "MM/dd/yyyy HH:mm";
		if (locale.equals("es") || locale.equals("pt")) {
			pattern = "dd/MM/yyyy HH:mm";
		} else if (locale.equals("zh")) {
			pattern = "yyyy/MM/dd HH:mm";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * Get date according to user locale
	 * 
	 * @param locale
	 * @param date
	 * @return
	 */
	public static Date getDateFromString(String locale, String date) {
		
		locale = locale == null ? "en" : locale;

		// default pattern
		String pattern = "MM/dd/yyyy HH:mm";
		if (locale.equals("es") || locale.equals("pt")) {
			pattern = "dd/MM/yyyy HH:mm";
		} else if (locale.equals("zh")) {
			pattern = "yyyy/MM/dd HH:mm";
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			Log.i("BasicActivity", "Error parsing date", e);
			return new Date();
		}
	}
	
	/**
	 * Starts details activity
	 */
	public void goToDetailsActivity(Integer workOrderId, Integer activationId, Integer tabIndex) {
		
		Context context = getApplicationContext();

		Intent intent = new Intent(context,
				ActivationDetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		// set intent parameters
		Bundle params = new Bundle();
		params.putInt("workOrderId", workOrderId);
		params.putInt("activationId", activationId);
		params.putInt("tabIndex", tabIndex);

		// add parameters to intent
		intent.putExtras(params);

		startActivity(intent);
	}
	
	/**
	 * Starts the alarm manager that will send the engineers gps location
	 * to the server from time to time based on the geospatial configuration
	 * in the business configuration.
	 * @throws ApplicationException 
	 */
	public void startEngineerTracking(){
		try {
			EngineerTracker tracker = new EngineerTracker(getApplicationContext());
			tracker.startEngineerTrackingLocation();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Once the engineers log off, the system will terminate the alarm that was established
	 * upon the login.
	 * 
	 * @param view
	 */
	public void stopEngineerTracking(View view) {
		EngineerTracker tracker = new EngineerTracker(getApplicationContext());
		tracker.stopEngineerTrackingLocation(view);
	}
	
	/**
	 * Check if app version accepts server version
	 * 
	 * @param serverInfo
	 * @return
	 */
	public boolean isUpdated(ServerInformationDto serverInfo) {		
		// get current app version (e.g.: 1.0.0.rev12345) 
		PackageInfo pinfo = null;
		String versionName = "";
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
			
			int firstDot = versionName.indexOf(".");
			int secondDot = versionName.indexOf(".", firstDot + 1);
			versionName = versionName.substring(0, secondDot); 

		} catch (NameNotFoundException e) {
			versionName = "1.0";
		}
		
		// versions accepted from server (e.g.: 1.0.x, 1.1.x)
		List<String> androidVersions = serverInfo.getAndroidVersions() == null ? new ArrayList<String>() : serverInfo.getAndroidVersions();
		for (String androidVersion : androidVersions) {
			int firstDot = androidVersion.indexOf(".");
			int secondDot = androidVersion.indexOf(".", firstDot + 1);
			String version = androidVersion.substring(0, secondDot);
			
			if (versionName.equals(version)) {
				return true;
			}
		}
		return false;
	}
}
