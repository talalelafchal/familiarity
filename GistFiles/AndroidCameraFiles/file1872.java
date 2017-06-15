package mobile;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mobile.core.LoginActivity;
import mobile.core.ReleaseNotesActivity;
import mobile.core.SettingsActivity;
import mobile.core.SettingsChangePasswordActivity;
import mobile.core.SettingsForgotPasswordActivity;
import mobile.core.SettingsStandbyActivity;
import mobile.core.SiteMapActivity;
import mobile.dao.local.SqlLiteDatabaseHandler;
import mobile.dao.local.SqlLiteStorageKey;
import mobile.exception.ApplicationException;
import mobile.model.core.SessionInformationDTO;
import mobile.model.remote.ServerInformationDto;
import mobile.service.ConnectivityService;
import mobile.service.facade.AssignmentService;
import mobile.service.facade.ChecklistService;
import mobile.service.facade.OperationsService;
import mobile.service.facade.ProjectService;
import mobile.service.facade.StandbyService;
import mobile.utils.Logger;

public abstract class TemplateActivity extends BasicActivity {

	// Magic Number
	private static final int TWENTYSIX = 26;

	public static final int SEVERITY_WARNING = 0;
	public static final int SEVERITY_INFO = 1;
	public static final int SEVERITY_ERROR = 2;
	public static final int SEVERITY_SEVERE = 3;

	protected static ContextMenu contextMenu;

	private List<Integer> menuItemsIdList;

	// log
	private static Logger LOG;

	private static LayoutInflater inflater;

	private static Resources resources;

	private static String packageName;
	
	private static Context context;

	/**
	 * Inflates views in a view template
	 * 
	 * @param savedInstanceState
	 * @param ids
	 * @param menuItemsIds
	 * @param isBackButton
	 */
	public void onCreate(Bundle savedInstanceState, List<Integer> ids,
			List<Integer> menuItemsIds, Boolean isBackButton,
			Integer extraHeaderId) {

		// Create layout inflator object to inflate toast.xml file
		inflater = getLayoutInflater();
		// app resources
		resources = getResources();
		// package name
		packageName = getPackageName();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		LOG = Logger.getInstance(getApplicationContext());

		super.onCreate(savedInstanceState);

		setContentView(R.layout.template);

		// Setup the UI to hide the keyboard when touched outside and edit text
		setupUI(findViewById(R.id.main_layout));

		// change button icon and add listener
		if (isBackButton != null && isBackButton) {
			addBackButton();
		}

		// get inflater
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		// INFLATE EXTRA HEADER
		if (extraHeaderId != null) {
			LinearLayout extraHeaderOneLayout = (LinearLayout) findViewById(R.id.extra_header_one);
			layoutInflater.inflate(extraHeaderId, extraHeaderOneLayout);
		}

		// INFLATE MAIN VIEW
		// get linear layout where the view will be inflated
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.wrapper_no_scroll);

		// inflate views
		for (Integer id : ids) {
			layoutInflater.inflate(id, mainLayout);
		}

		// add on click listener to menu button
		menuItemsIdList = menuItemsIds;
		addListenerOnButton();
		
		context = getApplicationContext();

		// load last logged user configuration
		loadLocale();
	}

	@Override
	protected void onResume() {
		PackageManager packageManager = this.getPackageManager();

		try {
			ActivityInfo info = packageManager.getActivityInfo(
					this.getComponentName(), 0);

			// name = mobile.core.SomeActivity
			String activity = info.name.substring(TWENTYSIX);

			SqlLiteDatabaseHandler sqlHandler = SqlLiteDatabaseHandler
					.getInstance(getApplicationContext());

			// verify if there is a user in the session information
			// and redirect if necessary.
			if (activity != null
					&& !activity.equals("LoginActivity")
					&& !activity.equals("SettingsForgotPasswordActivity")
					&& !activity.equals("ReleaseNotesActivity")
					&& !activity.equals("SettingsChangePasswordActivity")
					&& !sqlHandler 
							.keyExists(SqlLiteStorageKey.USER_INFORMATION)) {
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}

		} catch (NameNotFoundException e) {
			Log.i("TemplateActivity", "Exception while getting activity name", e);
		}
		
		// load last logged user configuration
		loadLocale();

		super.onResume();

		setupUI(findViewById(R.id.main_layout));
	}

	/**
	 * Add back icon to button on navigation bar and its action
	 */
	private void addBackButton() {

		// get image button
		ImageButton img = (ImageButton) findViewById(R.id.menuBarLogoImageButton);

		// set back icon
		img.setImageResource(R.drawable.logo_seta);

		// set listener
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// back to previous screen
				finish();
				overridePendingTransition(0, 0);
			}
		});
	}

	/**
	 * Add listener to menu button that shows a context menu
	 */
	private void addListenerOnButton() {

		// get image button
		ImageButton menuButton = (ImageButton) findViewById(R.id.template_menu_button);

		// register context menu for button menuButton
		registerForContextMenu(menuButton);

		// on click listener
		menuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// open context menu => calls onCreateContextMenu
				openContextMenu(view);
			}
		});
	}
	
	public static void setStandbyMenuItemTitle() {
		MenuItem standbyItem = contextMenu.findItem(R.id.standby);
		
		// if in standby, red icon, else green icon
		if (StandbyService.isInStandby()) {
			standbyItem.setTitle(R.string.release_standby);
		} else {
			standbyItem.setTitle(R.string.standby);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		contextMenu = menu;

		// create context menu from xml
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, contextMenu);

		// set text for standby menu item
		setStandbyMenuItemTitle();
		
		// set menu items visibility
		setMenuItemsVisibility(menuItemsIdList);
	}

	/**
	 * All menu items starts in the "state" invisible. The activity sends the
	 * list of ids of menu items that will be displayed
	 * 
	 * @param menuItemsIds
	 */
	private void setMenuItemsVisibility(List<Integer> menuItemsIds) {
		if (menuItemsIds != null) {
			for (Integer id : menuItemsIds) {
				MenuItem item = contextMenu.findItem(id);
				item.setVisible(true);
			}
		}
	}

	/**
	 * Listener for each menu item == navigation
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent nextScreen = null;
		boolean goToScreen = true;

		/**
		 * To avoid 'case expressions must be constant expressions' error, if-else instead of switch
		 * http://stackoverflow.com/questions/9092712/switch-case-statement-error-case-expressions-must-be-constant-expression
		 */
		int itemId = item.getItemId();
		if (itemId == R.id.forgot_password) {
			nextScreen = new Intent(context,
					SettingsForgotPasswordActivity.class);
		} else if (itemId == R.id.change_password) {
			nextScreen = new Intent(context,
					SettingsChangePasswordActivity.class);
		} else if (itemId == R.id.release_notes) {
			// go to release notes activity
			nextScreen = new Intent(context, ReleaseNotesActivity.class);
		} else if (itemId == R.id.logoff) {
			if (Authentication.logoff(context)) {
				// clear session information
				clearSessionInformation();

				// move user to the login screen
				nextScreen = new Intent(context, LoginActivity.class);
			} else {
				// display error if it were unable to log off
				error(context, R.string.unable_logoff, null, Toast.LENGTH_LONG);
				return false;
			}
		} else if (itemId == R.id.refresh) {
			ServerInformationDto serverInfo = ConnectivityService.getServerInfo(SessionInformationDTO.getInstance().getServerIntance());
			// if connected, refresh
			if (serverInfo != null) {
				if (isUpdated(serverInfo)) {
					goToScreen = false;
					refreshAppRemoteTask();
				} else {
					goToScreen = true;
					error(context, R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
					// log off
					Authentication.logoff(getApplicationContext());
					// clear session information
					clearSessionInformation();
					nextScreen = new Intent(context, LoginActivity.class);
				}
			} else {
				error(context, R.string.exception_io_error_connection, null, Toast.LENGTH_LONG);
				return false;
			}
		} else if (itemId == R.id.settings) {
			ServerInformationDto serverInfo = ConnectivityService.getServerInfo(SessionInformationDTO.getInstance().getServerIntance());
			// if connected, go to settings
			if (serverInfo != null) {
				if (isUpdated(serverInfo)) {
					nextScreen = new Intent(context, SettingsActivity.class);
				} else {
					error(context, R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
					// log off
					Authentication.logoff(getApplicationContext());
					// clear session information
					clearSessionInformation();
					nextScreen = new Intent(context, LoginActivity.class);
				}
			} else {
				error(context, R.string.exception_io_error_connection, null, Toast.LENGTH_LONG);
				return false;
			}
		} else if (itemId == R.id.standby) {
			ServerInformationDto serverInfo = ConnectivityService.getServerInfo(SessionInformationDTO.getInstance().getServerIntance());
			// if connected, standby
			if (serverInfo != null) {
				if (isUpdated(serverInfo)) {
					nextScreen = new Intent(context, SettingsStandbyActivity.class);
				} else {
					error(context, R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
					// log off
					Authentication.logoff(getApplicationContext());
					// clear session information
					clearSessionInformation();
					nextScreen = new Intent(context, LoginActivity.class);
				}
			} else {
				error(context, R.string.exception_io_error_connection, null,
						Toast.LENGTH_LONG);
				return false;
			}
		} else if (itemId == R.id.site_map) {
			ServerInformationDto serverInfo = ConnectivityService.getServerInfo(SessionInformationDTO.getInstance().getServerIntance());
			// if connected, site map
			if (serverInfo != null) {
				if (isUpdated(serverInfo)) {
					nextScreen = new Intent(context, SiteMapActivity.class);
				} else {
					error(context, R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
					// log off
					Authentication.logoff(getApplicationContext());
					// clear session information
					clearSessionInformation();
					nextScreen = new Intent(context, LoginActivity.class);
				}
			} else {
				error(context, R.string.exception_io_error_connection, null, Toast.LENGTH_LONG);
				return false;
			}
		}
		
		if (goToScreen) {
			nextScreen.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			// goes to another screen
			startActivity(nextScreen);
		}
		return true;
	}
	
	/**
	 * Remote task to refresh app
	 */
	private void refreshAppRemoteTask() {
		new RemoteRequestTask<Void>(this, true, null) {
			@Override
			public Void executeTask() throws ApplicationException {
				return refreshApp();
			}

			@Override
			public void completed(Void result) {
				Intent intent = getIntent();
			    finish();
			    overridePendingTransition(0, 0);
			    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			    startActivity(intent);
				info(context, R.string.refresh_success, Toast.LENGTH_SHORT);
			}

			@Override
			public void onException(final ApplicationException ex) {
				error(context, null, ex, Toast.LENGTH_LONG);
				Log.i("TemplateActivity", "Exception while refreshing app", ex);
			}
		}.start();
	}
	
	/**
	 * Refresh app
	 */
	private Void refreshApp() {
		OperationsService operationService = new OperationsService(context);
		AssignmentService assignmentService = new AssignmentService();
		ChecklistService checklistService = new ChecklistService();
		ProjectService projectService = new ProjectService();
		try {
			// project message
			Message progressMsgProject = new Message();
			progressMsgProject.arg1 = R.string.refreshing_project_info;
			RemoteRequestTask.getPbHandle().sendMessage(progressMsgProject);
			
			// get project info
			projectService.refreshProjectInformation();
			
			// operations message
			Message progressMsg = new Message();
			progressMsg.arg1 = R.string.refreshing;
			RemoteRequestTask.getPbHandle().sendMessage(progressMsg);
			
			// send all operation from queue to server
			operationService.refresh(RemoteRequestTask.getPbHandle());
			
			// assignment message
			Message progressMsgAssignment = new Message();
			progressMsgAssignment.arg1 = R.string.refreshing_assignment;
			RemoteRequestTask.getPbHandle().sendMessage(progressMsgAssignment);

			// refresh assignments table
			List<Integer> checklistIds = assignmentService.refreshAssignments(false, null);
			
			// if there is at least one checklist, load checklist
			if (checklistIds != null && checklistIds.size() > 0) {
				// checklist message
				Message progressMsgChecklist = new Message();
				progressMsgChecklist.arg1 = R.string.refreshing_checklist;
				RemoteRequestTask.getPbHandle().sendMessage(progressMsgChecklist);
				// get all assignments checklist and insert on local DB (key/value)
				checklistService.insertChecklist(checklistIds);
			}
		} catch (ApplicationException e) {
			Log.i("TemplateActivity", "Exception while refreshing app", e);
		}
		return null;
	}

	/**
	 * Verify where the user touched in the view to hide keyboard accordingly to
	 * the component clicked
	 * 
	 * @param view
	 */
	public void setupUI(View view) {
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					hideKeyboard(TemplateActivity.this);
					return false;
				}
			});
		}

		// If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}

	/**
	 * Hide keyboard when clicked outside of an edit text component
	 * 
	 * @param activity
	 */
	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);

		if (activity.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(), 0);
		}
	}

	/**
	 * Trigger warning toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void warning(Context context, Integer message, int toastLength) {
		makeToast(context, message, SEVERITY_WARNING, toastLength).show();
	}

	/**
	 * Trigger info toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void info(Context context, Integer message, int toastLength) {
		makeToast(context, message, SEVERITY_INFO, toastLength).show();
	}

	/**
	 * Trigger error toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void error(Context context, Integer message,
			ApplicationException ex, int toastLength) {

		Integer msg = message;

		if (ex != null) {
			// log exception on file
			LOG.error(ex);

			if (msg == null) {
				msg = resources.getIdentifier(ex.getI18ln(), "string",
						packageName);
			}
		}

		makeToast(context, msg, SEVERITY_ERROR, toastLength).show();
	}

	/**
	 * Trigger severe toast
	 * 
	 * @param context
	 * @param message
	 */
	public static void severe(Context context, Integer message, String test,
			int toastLength) {
		makeToast(context, message, SEVERITY_SEVERE, toastLength).show();
	}

	/**
	 * Custom toast, display message based on messageId
	 * 
	 * @param context
	 * @param message
	 * @param severity
	 * @return
	 */
	private static Toast makeToast(Context context, Integer message,
			int severity, int toastLength) {

		// Call custom_toast.xml file for toast layout and create new toast
		// setting the context
		View toastRoot = inflater.inflate(R.layout.custom_toast, null);
		Toast toast = new Toast(context);

		// Grab custom textview where the text will be replaced
		TextView text = (TextView) toastRoot.findViewById(R.id.toast_textview);

		// Grab custom image to change according to the severity
		ImageView image = (ImageView) toastRoot.findViewById(R.id.toast_image);

		// Set icon accordingly to the severity
		switch (severity) {
		case SEVERITY_WARNING:
			image.setImageResource(R.drawable.warning_icon);
			break;
		case SEVERITY_INFO:
			image.setImageResource(R.drawable.information_icon);
			break;
		case SEVERITY_ERROR:
			image.setImageResource(R.drawable.error_icon);
			break;
		case SEVERITY_SEVERE:
			image.setImageResource(R.drawable.several_error_icon);
			break;
		}

		// Compose toast
		text.setText(message);
		toast.setView(toastRoot);
		toast.setDuration(toastLength);

		return toast;
	}

	// Controlling languages in the system

	/**
	 * This is responsible for changing the language when requested.
	 * 
	 * @param lang
	 */
	public static void changeLanguage(String lang) {
		// locale for language
		Locale currentLocale = new Locale(lang);
		saveLocale(lang);
		Locale.setDefault(currentLocale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = currentLocale;
		context.getResources().updateConfiguration(config,
				context.getResources().getDisplayMetrics());
	}

	public static void saveLocale(String lang) {
		String langPref = "Language";
		SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}

	/**
	 * Load last logged user configuration. If no configuration is found, load
	 * the default configuration, the is the strings.xml in the folder values/.
	 */
	public static void loadLocale() {
		String langPref = "Language";
		SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		String language = prefs.getString(langPref, "");
		changeLanguage(language);
	}

	/**
	 * Clear session information after log off
	 */
	public void clearSessionInformation() {
		SessionInformationDTO session = SessionInformationDTO.getInstance();
		session.setConnected(false);
		session.setLogin(null);
		session.setPassword(null);
		session.setSecurityHandler(null);
		session.setServerIntance(null);
	}
}
