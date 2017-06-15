package mobile.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import mobile.Authentication;
import mobile.R;
import mobile.RemoteRequestTask;
import mobile.TemplateActivity;
import mobile.exception.ApplicationException;
import mobile.model.core.SupportedInstances;
import mobile.model.remote.ServerInformationDto;
import mobile.service.ConnectivityService;
import mobile.service.facade.SecurityService;

public class SettingsChangePasswordActivity extends TemplateActivity {

	private EditText usernameEditText;
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private EditText confirmNewPasswordEditText;
	private EditText securityQuestionEditText;
	private TextView hintTextView;
	private LinearLayout hintLayout;
	private Spinner instanceSpinner;

	private SecurityService securityService;

	private SupportedInstances serverInstance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		securityService = new SecurityService(getApplicationContext());

		// list of view's IDs to be displayed
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(R.layout.settings_change_password);

		// list of menu items to be displayed in menu
		List<Integer> menuItemsIds = new ArrayList<Integer>();
		menuItemsIds.add(R.id.forgot_password);
		menuItemsIds.add(R.id.release_notes);
		
		// inflate views
		super.onCreate(savedInstanceState, ids, menuItemsIds, true, null);

		// bind fields
		getComponents();
		
		// list of instances
		addSpinnerValues(instanceSpinner,
				SupportedInstances.getInstancesList(isDebugMode()));

		Bundle params = getIntent().getExtras();

		if (params != null){
			hintLayout.setVisibility(View.VISIBLE);
			usernameEditText.setText(params.getString("username"));
			hintTextView.setText(getString(R.string.settings_change_password_hint));

			serverInstance = getObjectFromInternacionalized(
					SupportedInstances.getInstancesList(isDebugMode()),
					params.getString("serverInstance"));

			// disable username field
			usernameEditText.setInputType(InputType.TYPE_NULL);
		} else {
			instanceSpinner.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	/**
	 * Get screen components
	 */
	private void getComponents() {
		usernameEditText = (EditText) findViewById(R.id.settings_change_password_username);
		oldPasswordEditText = (EditText) findViewById(R.id.settings_change_password_old_password);
		newPasswordEditText = (EditText) findViewById(R.id.settings_change_password_new_password);
		confirmNewPasswordEditText = (EditText) findViewById(R.id.settings_change_password_confirm_new_password);
		securityQuestionEditText = (EditText) findViewById(R.id.settings_change_password_security_question);
		hintTextView = (TextView) findViewById(R.id.settings_change_password_hint);
		hintLayout = (LinearLayout) findViewById(R.id.settings_change_password_hint_layout);
		instanceSpinner = (Spinner) findViewById(R.id.settings_change_password_instance_spinner);
	}

	/**
	 * Fired when user taps on save button
	 * @param view
	 */
	public void save(View view) {
		final Context applicationContext = getApplicationContext();
		if (checkEmptyValues()) {
			if (checkPassword()) {
				
				final String username = usernameEditText.getText().toString();
				final String oldPassword = oldPasswordEditText.getText().toString();
				final String newPassword = newPasswordEditText.getText().toString();
				final String securityQuestion = securityQuestionEditText.getText().toString();
				if (serverInstance == null) {
					serverInstance = getObjectFromInternacionalized(
							SupportedInstances.getInstancesList(isDebugMode()),
							instanceSpinner.getSelectedItem().toString());
				}
				
				ServerInformationDto serverInfo = ConnectivityService.getServerInfo(serverInstance);
				// if connected
				if (serverInfo != null) {
					if (isUpdated(serverInfo)) {
						new RemoteRequestTask<Boolean>(this, true, applicationContext.getString(R.string.saving)) {
							@Override
							public Boolean executeTask() throws ApplicationException {
								
								return securityService.changePassword(serverInstance, username, oldPassword, newPassword, securityQuestion);
							}
							@Override
							public void completed(Boolean result) {
								if (result) {
									clearFields();
									info(applicationContext, R.string.save_success, Toast.LENGTH_SHORT);
									
									Intent loginScreen = new Intent(applicationContext, LoginActivity.class);;
									loginScreen.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
									startActivity(loginScreen);
								} else {
									error(applicationContext, R.string.login_wrong_username_password, null, Toast.LENGTH_SHORT);
								}
							}
							@Override
							public void onException(ApplicationException ex) {
								error(applicationContext, R.string.settings_change_password_error, ex, Toast.LENGTH_SHORT);
							}
						}.start();
					} else {
						TemplateActivity.error(getApplicationContext(), R.string.app_version_not_up_to_date, null, Toast.LENGTH_LONG);
						// log off
						Authentication.logoff(getApplicationContext());
						// clear session information
						clearSessionInformation();

						// goes to login screen
						Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
						loginScreen.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(loginScreen);
					}
				} else {
					error(applicationContext, R.string.exception_io_error_connection, null, Toast.LENGTH_LONG);
				}
			} else {
				error(applicationContext, R.string.settings_change_password_password_mismatch, null, Toast.LENGTH_SHORT);
			}
		} else {
			error(applicationContext, R.string.settings_change_password_all_mandatory_fields, null, Toast.LENGTH_SHORT);
		}
	}

	/**
	 * Clear fields on success
	 */
	private void clearFields() {

		Bundle params = getIntent().getExtras();

		if (params == null) {
			usernameEditText.getText().clear();
		}

		oldPasswordEditText.getText().clear();
		newPasswordEditText.getText().clear();
		confirmNewPasswordEditText.getText().clear();
		securityQuestionEditText.getText().clear();
	}

	/**
	 * Check if all fields were filled
	 * @return
	 */
	private boolean checkEmptyValues() {

		String username = usernameEditText.getText().toString();
		String oldPassword = oldPasswordEditText.getText().toString();
		String newPassword = newPasswordEditText.getText().toString();
		String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
		String securityQuestion = securityQuestionEditText.getText().toString();

		boolean usernameValid = !(username == null || username.trim().length() == 0);
		boolean oldPasswordValid = !(oldPassword == null || oldPassword.trim().length() == 0);
		boolean newPasswordValid = !(newPassword == null || newPassword.trim().length() == 0);
		boolean confirmNewPasswordValid = !(confirmNewPassword == null || confirmNewPassword.trim().length() == 0);
		boolean securityQuestionValid = !(securityQuestion == null || securityQuestion.trim().length() == 0);

		return (usernameValid && oldPasswordValid && newPasswordValid && confirmNewPasswordValid && securityQuestionValid);
	}

	/**
	 * Check if new password and confirm new password match
	 * @return
	 */
	private boolean checkPassword() {

		String newPassword = newPasswordEditText.getText().toString();
		String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

		return newPassword.equals(confirmNewPassword);
	}

}