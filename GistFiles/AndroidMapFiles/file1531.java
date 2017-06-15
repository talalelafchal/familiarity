/* DroidForm.java */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DroidForm {

	private Map<EditText,Boolean> requiredFields;
	private Iterator<Entry<EditText,Boolean>> iterator;
	private Context context;
	
	public DroidForm(Context context) {
	    this.context = context;
	    this.requiredFields = new HashMap<EditText,Boolean>();
	}
	
	public void addField(int id, Boolean required) {
		EditText field = (EditText) ((Activity) context).findViewById(id);
		this.requiredFields.put(field, required);
	}
	
	public boolean formValid() {
		iterator = requiredFields.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<EditText,Boolean> field = (Map.Entry<EditText,Boolean>)iterator.next();
		    if(field.getValue() == true && field.getKey().getText().toString().isEmpty()) {
		    	return false;
		    } 
		}
		return true;
	}
	
	public String valueOf(int id) {
		iterator = requiredFields.entrySet().iterator();
		View view = ((Activity) context).findViewById(id);
		while(iterator.hasNext()) {
			Map.Entry<EditText,Boolean> nextField = (Map.Entry<EditText,Boolean>)iterator.next();
			if(nextField.getKey() == view) {
				return nextField.getKey().getText().toString();
			}
		}
		return null;
	}
	
	public void displayErrorMessages() {
	}

}

/* Usage example - LoginActivity.java */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.intriguos.aid.classes.DroidForm;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AidActivity {

	private DroidForm loginForm;
	private Button loginButton;
	private Button signupButton;
	private Intent signupIntent;
	private Intent mainIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
	
		loginButton = (Button) findViewById(R.id.login_button);
		signupButton = (Button) findViewById(R.id.signup_button);
		
		loginForm = new DroidForm(this);
	    
		loginForm.addField(R.id.username_field, true);
		loginForm.addField(R.id.password_field, true);	
		
		signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
	
		setSubmitLoginListener();
	
		addClickListenerAndFinish(signupButton, signupIntent);
	}
	
	private void setSubmitLoginListener() {
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (loginForm.formValid() == true) {
					loginUser();
				} else {
					loginForm.displayErrorMessages();
				}
			}
		});
	}
	
	protected void loginUser() {	
		ParseUser.logInInBackground(loginForm.valueOf(R.id.username_field), loginForm.valueOf(R.id.password_field), new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					mainIntent = new Intent(LoginActivity.this, AidActivity.class);
					startActivityAndFinish(mainIntent);
				} else {
					e.printStackTrace();
				}		
			}
		});
	}
}