package mobile.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import mobile.Authentication;
import mobile.MainActivity;
import mobile.R;
import mobile.RemoteRequestTask;
import mobile.TemplateActivity;
import mobile.dao.local.SqlLiteDatabaseHandler;
import mobile.dao.local.SqlLiteStorageKey;
import obile.exception.ApplicationException;
import mobile.exception.HttpException;
import mobile.model.core.Language;
import mobile.model.core.SessionInformationDTO;
import mobile.model.core.SupportedInstances;
import mobile.model.remote.MobileInformationDto;
import mobile.model.remote.SecurityHandler;
import mobile.model.remote.ServerInformationDto;
import mobile.model.remote.UserSettings;
import mobile.service.ConnectivityService;
import mobile.service.facade.AssignmentService;
import mobile.service.facade.ChecklistService;
import mobile.service.facade.MobileInformationService;
import mobile.service.facade.ProjectService;
import mobile.service.facade.SecurityService;
import mobile.service.facade.StandbyService;

public class LoginActivity extends TemplateActivity {

	private SecurityService securityService;
	private EditText loginTextView;
	private EditText passwordTextView;
	private TextView copyrightTextView;
	private TextView loginLabel;
	private TextView passwordLabel;
	private Button loginButton;
	private static Spinner instanceSpinner;
	private Spinner languageSpinner;
	private ImageView languageIcon;

	// DAO
	private SqlLiteDatabaseHandler dao;

	private AssignmentService assignmentService;

	private ProjectService projectService;
	
	private StandbyService standbyService;
	
	private ChecklistService checklistService;
	
	// language
	private String sysLanguage;
	
	// GCM variables
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private GoogleCloudMessaging gcm = null;
	private String regid = null;
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final String TAG = "LoginActivity";
	private static final String SENDER_ID = "111";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		securityService = new SecurityService(getApplicationContext());

		assignmentService = new AssignmentService();

		projectService = new ProjectService();
		
		standbyService = new StandbyService();
		
		checklistService = new ChecklistService();

		// list of view's IDs to be displayed
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(R.layout.login);

		// list of menu items to be displayed in menu
		List<Integer> menuItemsIds = new ArrayList<Integer>();
		menuItemsIds.add(R.id.forgot_password);
		menuItemsIds.add(R.id.change_password);
		menuItemsIds.add(R.id.release_notes);

		// inflate views
		super.onCreate(savedInstanceState, ids, menuItemsIds, false, null);
		
		// keyboard adjust
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		// set DAO value
		dao = SqlLiteDatabaseHandler.getInstance(getApplicationContext());

		// get view's elements
		getComponents();

		// list of instances
		addSpinnerValues(instanceSpinner,
				SupportedInstances.getInstancesList(isDebugMode()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// verify if there is a user in the session information and redirect if
		// necessary.
		if (SessionInformationDTO.getInstance().getLogin() != null) {
			Intent intent = new Intent(getApplicationContext(),
					ActivationBoxesActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
		} else {
			setupSystemLanguage();
			
			// fill fields, if needed
			fillFields();
		}
	}
	
	public void setupSystemLanguage(){
		// invoke language spinner from image click
		languageIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				languageSpinner.performClick();
			}
		});

		// create language spinner
		createLanguageSpinner();
		
		try {
			if (!dao.keyExists(SqlLiteStorageKey.USER_LANGUAGE)){				
				dao.createKeyValue(SqlLiteStorageKey.USER_LANGUAGE, "en");
				switchLanguage("en");
				changeSpinnerLanguage("en");
			} else {
				sysLanguage = dao.getNativeValueForKey(SqlLiteStorageKey.USER_LANGUAGE);
				switchLanguage(sysLanguage);
				changeSpinnerLanguage(sysLanguage);
			}
		} catch (ApplicationException e) {
			Log.i("LoginActivity", "Exception while setting up system language", e);
		}
	}
	
	public void changeSpinnerLanguage(String language){
		int pos = 0;
		for (int i = 0; i < 4 ; i++){
			if (getLocaleFromString((String) languageSpinner.getItemAtPosition(i)) == 
					getLocaleFromString(language)){
				pos = i;
			}
		}				
		languageSpinner.setSelection(pos);
	}

	/**
	 * Get screen components
	 */
	private void getComponents() {
		loginTextView = (EditText) findViewById(R.id.loginUsernameEditText);
		passwordTextView = (EditText) findViewById(R.id.loginPasswordEditText);
		instanceSpinner = (Spinner) findViewById(R.id.loginInstanceSpinner);
		languageSpinner = (Spinner) findViewById(R.id.login_language_spinner);
		languageIcon = (ImageView) findViewById(R.id.login_image_language);
		loginButton = (Button) findViewById(R.id.login_button);
		copyrightTextView = (TextView) findViewById(R.id.copyright);
		loginLabel = (TextView) findViewById(R.id.login_label);
		passwordLabel = (TextView) findViewById(R.id.password_label);
	}

	/**
	 * 
	 */
	private void fillFields() {

		// if the login activity was called from
		// forgot password activity, the username and
		// server instance will be filled
		Bundle params = getIntent().getExtras();

		if (params != null) {

			// username
			String username = params.getString("username");

			if (username != null) {
				loginTextView.setText(username);
			}

			// instance
			String instance = params.getString("serverInstance");

			if (instance != null) {
				SupportedInstances serverInstance = getObjectFromInternacionalized(
						SupportedInstances.getInstancesList(isDebugMode()),
						instance);

				Context applicationContext = getApplicationContext();

				int resource = applicationContext.getResources().getIdentifier(
						"int_" + serverInstance.getI18ln(), "string",
						applicationContext.getPackageName());

				String value = applicationContext.getResources()
						.getText(resource).toString();

				final int index = getIndex(instanceSpinner, value);

				instanceSpinner.post(new Runnable() {
			        @Override
			        public void run() {
			        	instanceSpinner.setSelection(index);
			        }
			    });
			}
		}
	}

	/**
	 * Fired when user taps on login button
	 * 
	 * @param view
	 */
	public void loginAction(View view) {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		// if connected
		if (netInfo != null && netInfo.isConnected()) {
			// validate if login is not empty
			boolean loginValid = !(loginTextView.getText().toString() == null || loginTextView
					.getText().toString().trim().length() == 0);

			// validate if password is not empty
			boolean passwordValid = !(passwordTextView.getText().toString() == null || passwordTextView
					.getText().toString().trim().length() == 0);

			if (!loginValid) {
				error(getApplicationContext(), R.string.login_required_error,
						null, Toast.LENGTH_SHORT);
			}

			if (!passwordValid) {
				error(getApplicationContext(),
						R.string.password_required_error, null,
						Toast.LENGTH_SHORT);
			}

			// attempt login if both fields are filled
			if (loginValid && passwordValid) {
				// clear everything before login
				Authentication.logoff(getApplicationContext());
				login();
			}
		} else {
			error(getApplicationContext(),
					R.string.exception_io_error_connection, null,
					Toast.LENGTH_SHORT);
		}
	}

	/**
	 * Try to login
	 */
	private void login() {
		final Context applicationContext = getApplicationContext();
		final SupportedInstances instance = getObjectFromInternacionalized(
				SupportedInstances.getInstancesList(isDebugMode()),
				instanceSpinner.getSelectedItem().toString());

		new RemoteRequestTask<Integer>(this, true, null) {

			// Grab authentication values
			String login = loginTextView.getText().toString();
			String password = passwordTextView.getText().toString();

			@Override
			public Integer executeTask() throws ApplicationException {
				return doLogin(login, password, instance);
			}

			@Override
			public void completed(Integer result) {
				switch (result) {
					case 1:
						// not connected
						error(applicationContext, R.string.exception_io_error_connection, null, Toast.LENGTH_LONG);
						break;
					case 2:
						// not up to date
						error(applicationContext, R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
						break;
					case 3:
						// unable to login
						error(applicationContext, R.string.unable_login, null, Toast.LENGTH_LONG);
						break;
					case 4:
						// everything is ok
						// save selected language
						saveLanguagePreference();
	
						Intent intent = new Intent(applicationContext, ActivationBoxesActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);
						break;
	
					default:
						error(applicationContext, R.string.unable_login, null, Toast.LENGTH_LONG);
						break;
				}
			}

			@Override
			public void onException(final ApplicationException ex) {
				handleException(ex, login);
			}
		}.start();

	}
	
	/**
	 * Remote task to perform login
	 * 
	 * @param login
	 * @param password
	 * @param instance
	 * @return
	 * @throws ApplicationException 
	 */
	private Integer doLogin(String login, String password, SupportedInstances instance) throws ApplicationException {
		
		ServerInformationDto serverInfo = ConnectivityService.getServerInfo(instance);
		
		// if connected
		if (serverInfo != null) {
			
			// login message
			Message progressMsgLogin = new Message();
			progressMsgLogin.arg1 = R.string.login_loading_login;
			RemoteRequestTask.getPbHandle().sendMessage(progressMsgLogin);
			
			// if is up to date
			if (isUpdated(serverInfo)) {
				
				SecurityHandler securityHandler = securityService.login(
						instance, loginTextView.getText().toString(),
						passwordTextView.getText().toString(),
						getApplicationContext());

				Boolean result = Authentication.login(getApplicationContext(),
						login, password, instance, securityHandler);

				// if no exception on login service
				if (result) {

					// register phone for push notifications
					if (checkPlayServices()) {
						gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);

						// check if device is already registered to get ID
						regid = getRegistrationId(getApplicationContext());

						if (regid.isEmpty()) {
							// push notifications message
							Message progressMsgPush = new Message();
							progressMsgPush.arg1 = R.string.login_registering_phone;
							RemoteRequestTask.getPbHandle().sendMessage(progressMsgPush);

							registerInBackground();							
						}
					}

					// project message
					Message progressMsgProject = new Message();
					progressMsgProject.arg1 = R.string.login_loading_project;
					RemoteRequestTask.getPbHandle().sendMessage(progressMsgProject);

					// get project info
					projectService.refreshProjectInformation();

					// standby message
					Message progressMsgStandby = new Message();
					progressMsgStandby.arg1 = R.string.login_loading_standby;
					RemoteRequestTask.getPbHandle().sendMessage(progressMsgStandby);

					// get standby status
					standbyService.loadStandbyStatus();

					// assignment message
					Message progressMsgAssignment = new Message();
					progressMsgAssignment.arg1 = R.string.login_loading_assignment;
					RemoteRequestTask.getPbHandle().sendMessage(progressMsgAssignment);

					// get activations and save on local DB
					List<Integer> checklistIds = assignmentService.refreshAssignments(true, null);

					// if there is at least one checklist, load checklist
					if (checklistIds != null && checklistIds.size() > 0) {
						// checklist message
						Message progressMsgChecklist = new Message();
						progressMsgChecklist.arg1 = R.string.login_loading_checklist;
						RemoteRequestTask.getPbHandle().sendMessage(progressMsgChecklist);

						// get all assignments checklist and insert on local DB (key/value)
						checklistService.insertChecklist(checklistIds);
					}
				} else {
					// unable to login
					return 3;
				}
			} else {
				// not up to date
				return 2;
			}
		} else {
			// not connected
			return 1;
		}
		// everything is ok
		return 4;
	}
	
	/**
	 * Handle exception on login action
	 * 
	 * @param ex
	 * @param login
	 */
	private void handleException(ApplicationException ex, String login) {
		if (ex instanceof HttpException) {
			HttpException e = (HttpException) ex;

			switch (e.getHttpStatus()) {
				case 401:
					error(getApplicationContext(), R.string.login_wrong_username_password, ex, Toast.LENGTH_LONG);
					break;
				case 403:
					error(getApplicationContext(), R.string.login_wrong_instance, ex, Toast.LENGTH_LONG);
					break;
				case 404:
					error(getApplicationContext(), R.string.exception_http_error_not_found, ex, Toast.LENGTH_LONG);
					break;
				case 406:
					// create intent
					Intent changePasswordIntent = new Intent(getApplicationContext(),
							SettingsChangePasswordActivity.class);
					changePasswordIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

					// set intent parameters
					Bundle params = new Bundle();
					params.putString("username", login);
					params.putString("serverInstance", instanceSpinner.getSelectedItem().toString());

					// add parameters to intent
					changePasswordIntent.putExtras(params);

					// start activity
					startActivity(changePasswordIntent);
					break;
				default:
					error(getApplicationContext(), null, ex, Toast.LENGTH_LONG);
					break;
			}
		}
	}

	/**
	 * Save language preference according to the selected value in the view
	 */
	public void saveLanguagePreference() {
		Language selectedLanguage = getObjectFromInternacionalized(
				Language.getInstancesList(), languageSpinner.getSelectedItem()
						.toString());
		UserSettings userSettings = SessionInformationDTO.getInstance()
				.getSecurityHandler().getCurrentUser().getUserSettings();

		if (userSettings == null) {
			SessionInformationDTO.getInstance().getSecurityHandler()
					.getCurrentUser().setUserSettings(new UserSettings());
			SessionInformationDTO.getInstance().getSecurityHandler()
					.getCurrentUser().getUserSettings()
					.setLocale(selectedLanguage.getLocale());
		}

		try {
			dao.createKeyValue(SqlLiteStorageKey.USER_INFORMATION,
					SessionInformationDTO.getInstance());
		} catch (ApplicationException e) {
			error(getApplicationContext(), R.string.unable_save_config, e,
					Toast.LENGTH_SHORT);
			Log.i("LoginActivity", "Exception while saving language preference", e);
		}
	}

	/**
	 * Load language spinner and add item select listener
	 */
	private void createLanguageSpinner() {
		// list of languages
		addSpinnerValues(languageSpinner, Language.getInstancesList());

		// set select event for language spinner
		languageSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				
				String selectedLanguage = languageSpinner.getSelectedItem().toString();
				
				if (selectedLanguage == null){
					languageIcon.setImageResource(R.drawable.flag_en);
					changeLanguage("en");
				} else {
					switchLanguage(languageSpinner
							.getSelectedItem().toString());
				}
				
				updateView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
	}
	
	private void switchLanguage(String selectedLanguage) {
		Language targetedLanguage = getLocaleFromString(selectedLanguage);
		
		switch (targetedLanguage) {
			case CHINESE:
				languageIcon.setImageResource(R.drawable.flag_zh);
				changeLanguage("zh");
				saveNewLocale("zh");
				Locale.setDefault(new Locale("zh"));
				break;
			case PORTUGUESE:
				languageIcon.setImageResource(R.drawable.flag_pt);
				changeLanguage("pt");
				saveNewLocale("pt");
				Locale.setDefault(new Locale("pt"));
				break;
			case SPANISH:
				languageIcon.setImageResource(R.drawable.flag_es);
				changeLanguage("es");
				saveNewLocale("es");
				Locale.setDefault(new Locale("es"));
				break;
			case ENGLISH:
				languageIcon.setImageResource(R.drawable.flag_en);
				changeLanguage("en");
				saveNewLocale("en");
				Locale.setDefault(new Locale("en"));
				break; 
			default:
				languageIcon.setImageResource(R.drawable.flag_en);
				changeLanguage("en");
				saveNewLocale("en");
				Locale.setDefault(new Locale("en"));
				break;
		}
		updateView();
	}
	
	/**
	 * Save user settings on local DB
	 */
	private void saveNewLocale(String locale) {
		// set object
		Language newLanguage = getLocaleFromString(locale);
		
		// update JSON in local DB
		try {
			dao.updateKeyValue(SqlLiteStorageKey.USER_LANGUAGE, newLanguage.getLocale());
			changeLanguage(locale);
		} catch (ApplicationException e) {
			Log.i("LoginActivity", "Exception while saving new locale", e);
		}
	}
	
	/**
	 * Update the view with the selected language
	 */
	private void updateView() {
		loginLabel.setText(R.string.login_label);		
		passwordLabel.setText(R.string.password_label);
		loginButton.setText(R.string.login_action);
		
		PackageInfo pinfo = null;
		String versionName = "";
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			versionName = getString(R.string.app_version);
		}
		copyrightTextView.setText(getString(R.string.app_name) + " " + versionName);
		
		instanceSpinner.setAdapter(null);
		addSpinnerValues(instanceSpinner,
				SupportedInstances.getInstancesList(isDebugMode()));
	}

	/**
	 * Check if device supports google play services
	 * 
	 * @return
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			Log.d(TAG, "This device is not supported - Google Play Services.");
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences();
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration ID not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}

		return registrationId;
	}

	private SharedPreferences getGCMPreferences() {
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.i(TAG, "Could not get package name: ", e);
			return -1;
		}
	}

	private void registerInBackground() {
		if (gcm == null) {
			gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
		}
		try {
			// get device information
			String deviceInformation = "";
			deviceInformation += "SDK VERSION: " + android.os.Build.VERSION.SDK_INT + " - ";
			deviceInformation += "MODEL: " + android.os.Build.MODEL + " - ";
			deviceInformation += "BRAND: " + android.os.Build.BRAND + " - ";
			deviceInformation += "DEVICE: " + android.os.Build.DEVICE + " - ";
			deviceInformation += "PRODUCT: " + android.os.Build.PRODUCT + " - ";
			deviceInformation += "OS VERSION: " + System.getProperty("os.version");
			
			// get registration ID for device
			regid = gcm.register(SENDER_ID);

			// create object					
			MobileInformationDto mobileInfo = new MobileInformationDto();
			mobileInfo.setPushNotificationKey(regid);
			mobileInfo.setDeviceInformation(deviceInformation);

			// update push notification key
			MobileInformationService mobileInformationService = new MobileInformationService();
			mobileInformationService.updateMobileInfo(mobileInfo);
		} catch (IOException e) {
			Log.e(TAG, "Mobile information Service: update failed", e);
		} catch (ApplicationException e) {
			Log.e(TAG, "Mobile information Service: update failed", e);
		}  
	}
}