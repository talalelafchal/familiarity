package mobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import mobile.core.ActivationBoxesActivity;
import mobile.core.LoginActivity;
import mobile.dao.local.SqlLiteDatabaseHandler;
import mobile.dao.local.SqlLiteStorageKey;
import mobile.exception.ApplicationException;
import mobile.model.core.SessionInformationDTO;
import mobile.model.core.SupportedInstances;
import mobile.model.remote.SecurityHandler;
import mobile.service.SessionInformation;

public class MainActivity extends BasicActivity {
	
	// Splash screen timer
	private static final int SPLASH_TIME_OUT = 3000;
	private ImageView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		image = (ImageView) findViewById(R.id.imgLogo);

		// initialize the sessions
		if (savedInstanceState == null) {
			getSessionHandler().initializeSession();
		}
		
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_fadein_img_view);
		image.startAnimation(animation);

		new Handler().postDelayed(new Runnable() {
			/**
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */
			@Override
			public void run() {
				SqlLiteDatabaseHandler sqlHandler = SqlLiteDatabaseHandler.getInstance(getApplicationContext());
				// verify if there is a user in the session information and redirect if necessary
				if (sqlHandler.keyExists(SqlLiteStorageKey.USER_INFORMATION)) {
					// user is already logged in
					setupUserInformation(sqlHandler);
					// go to inbox tab
					Intent intent = new Intent(MainActivity.this, ActivationBoxesActivity.class);
					startActivity(intent);
				} else {
					// This method will be executed once the timer is over - Start your app main activity
					Intent i = new Intent(MainActivity.this, LoginActivity.class);
					i.putExtra("caller", "mobile.MainActivity");
					startActivity(i);
				}
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	private void setupUserInformation(SqlLiteDatabaseHandler sqlHandler) {
		try {
			// set session information again
			SessionInformationDTO userInfoDB = sqlHandler.getObjectForKey(SqlLiteStorageKey.USER_INFORMATION);

			String login = userInfoDB.getLogin();
			String password = userInfoDB.getPassword();
			SupportedInstances instance = userInfoDB.getServerIntance();
			SecurityHandler securityHandler = userInfoDB.getSecurityHandler();

			SessionInformationDTO sessionInformationDTO = SessionInformationDTO.getInstance();
			sessionInformationDTO.setLogin(login);
			sessionInformationDTO.setPassword(password);
			sessionInformationDTO.setServerIntance(instance);
			sessionInformationDTO.setSecurityHandler(securityHandler);

			SessionInformation sessionInformation = SessionInformation.getInstance();
			sessionInformation.setLogin(login);
			sessionInformation.setPassword(password);
			sessionInformation.setServerIntance(instance);
			sessionInformation.setSecurityHandler(securityHandler);
			sessionInformation.setContext(getApplicationContext());
		} catch (ApplicationException e) {
			Log.i("MainActivity", "Exception while setting session information", e);
		}
	}
}
