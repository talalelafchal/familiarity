
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.techpalle.karan.doubtlist.R;
import com.techpalle.karan.doubtlist.model.User;
import com.techpalle.karan.doubtlist.ui.BaseActivity;
import com.techpalle.karan.doubtlist.utils.Constants;
import com.techpalle.karan.doubtlist.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Sign up screen and functionality of the app
 */
public class CreateAccountActivity extends BaseActivity implements View.OnClickListener{
    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private EditText mEditTextUsernameCreate, mEditTextEmailCreate, mEditTextPasswordCreate;
    private Button mButtonCreateAccountFinal;
    private TextView mTextViewSignIn;
    private Firebase mFirebaseRef;
    private String mUserName, mUserEmail, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        /**
         * Create Firebase references
         */
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup the progress dialog
         */
        initializeScreen();
    }

    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);
        mTextViewSignIn = (TextView) findViewById(R.id.tv_sign_in);
        mButtonCreateAccountFinal = (Button) findViewById(R.id.btn_create_account_final);
        LinearLayout linearLayoutCreateAccountActivity = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Progress Dialog Loading");
        mAuthProgressDialog.setMessage("Creating user with firebase");
        mAuthProgressDialog.setCancelable(false);

        /* Register the event listeners on sign in button and create account view */
        mTextViewSignIn.setOnClickListener(this);
        mButtonCreateAccountFinal.setOnClickListener(this);
    }

    /**
     * onClick listeners implemented for the sign in view and create account button
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == mButtonCreateAccountFinal.getId()){
            onCreateAccountPressed();
        } else if(view.getId() == mTextViewSignIn.getId()){
            onSignInPressed();
        }
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Create new account using Firebase email/password provider
     */
    public void onCreateAccountPressed() {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = mEditTextPasswordCreate.getText().toString();

        /**
         * Check that email and user name are okay
         */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validUserName = isUserNameValid(mUserName);
        boolean validPassword = isPasswordValid(mPassword);
        if (!validEmail || !validUserName || !validPassword) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified email and password
         */
        mFirebaseRef.createUser(mUserEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                /* Dismiss the progress dialog */
                mAuthProgressDialog.dismiss();
                showToast("Successfully created");
                createUserInFirebaseHelper();
                finish();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                /* Error occurred, log the error and dismiss the progress dialog */
                showToast("Error occurred: "+firebaseError);
                mAuthProgressDialog.dismiss();
                /* Display the appropriate error message */
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                    mEditTextEmailCreate.setError("Email already taken");
                } else {
                    showToast(firebaseError.getMessage());
                }

            }
        });
    }

    /**
     * Creates a new user in Firebase from the Java POJO
     */
    private void createUserInFirebaseHelper() {
        final String encodedEmail = Utils.encodeEmail(mUserEmail);
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);

        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null) {
                 /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User newUser = new User(mUserName, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(LOG_TAG, "Error occurred: " + firebaseError.getMessage());
            }
        });

    }


    private boolean isEmailValid(String email) {

        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format("%s is not a valid email",
                    email));
            return false;
        }
        return true;
    }

    private boolean isUserNameValid(String userName) {
        if (userName.equals("")) {
            mEditTextUsernameCreate.setError("This cannot be empty");
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 6) {
            mEditTextPasswordCreate.setError("Please enter a password that is at least 6 characters long");
            return false;
        }
        return true;
    }

    /**
     * Show error toast to users
     */
    private void showToast(String message) {
        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_LONG).show();
    }


}
