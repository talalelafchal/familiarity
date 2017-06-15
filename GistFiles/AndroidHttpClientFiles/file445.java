package com.google.plus.sample.ShareTest;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

// Example of a somewhat generic client+server Google API access setup.
public class CodeActivity extends Activity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String SERVER_CLIENT_ID = "YOUR_SERVER_CLIENT_ID";
    public static final String CHECK_SESSION_URL = "HTTPS_ID_TOKEN_URL";
    public static final String CODE_URL = "HTTPS_CODE_URL";

    // Various Constants
    private static final String TAG = "TestCodeActivity";
    private static final String NO_REFRESH = "NO_REFRESH";
    private static final String ACCT_NAME = "accountName";
    private final String SHARED_PREFS = "GSigninAcct";
    private final int ACCOUNT_PICKER_REQUEST = 529542;
    private final int PERMISSION_REQ = 42303;
    private final int REQUEST_CODE_RESOLVE_ERR = 391033;

    // These vars track our state.
    private GoogleApiClient mGoogleApiClient;
    private String mAccountName;
    private ConnectionResult mConnectionResult;
    private String mSessionCookie;
    private HashMap<String, Boolean> mUserHasRefreshOnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
        mUserHasRefreshOnServer = new HashMap<>();
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        mAccountName = settings.getString(ACCT_NAME, null);
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // This starts the flow! Go to onConnected or onConnectionFailed
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Signed in with client session.");
        if (mAccountName == null) {
            // If we have no account name, store the one from Plus.Accounts
            setAccountName(Plus.AccountApi.getAccountName(mGoogleApiClient));
        }
        if (mSessionCookie == null) {
            // If the server hasn't got a refresh token, grab it.
            if(mUserHasRefreshOnServer.containsKey(mAccountName) &&
                    mUserHasRefreshOnServer.get(mAccountName) == Boolean.FALSE) {
                getCode();
            } else {
                // Otherwise, just get the session.
                getSession();
            }
        } else {
            // Mark the user as signed in, hide the button, and do
            // normal signed in type things!
            Log.d(TAG, "Signed In with server session.");
            findViewById(R.id.codesignin).setVisibility(View.GONE);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Disable any Google Play Services related functionality until
        // onConnected or onConnectionFailed.
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mAccountName != null &&
                mUserHasRefreshOnServer.containsKey(mAccountName) &&
                mUserHasRefreshOnServer.get(mAccountName) == Boolean.FALSE) {
            // If we've established a user, and the user does not have a
            // refresh token on the server, retrieve a code.
            getCode();
        } else if (mAccountName != null &&
                mUserHasRefreshOnServer.containsKey(mAccountName) &&
                mUserHasRefreshOnServer.get(mAccountName) == Boolean.TRUE &&
                mConnectionResult == null) {
            // Otherwise, store this just in case.
            mConnectionResult = connectionResult;
        }
        // Most of the time we do nothing here - go to onClickSignIn
    }

    // This will be hooked up to a button in the UI!
    public void onClickSignIn(View v) {
        if (mAccountName == null) {
            // Grab a Google account from the device.
            Intent intent = AccountPicker.newChooseAccountIntent(
                    null, null, new String[]{"com.google"},
                    false, null, null, null, null
            );
            startActivityForResult(intent, ACCOUNT_PICKER_REQUEST);
            // See onActivityResult
        } else if (!mUserHasRefreshOnServer.containsKey(mAccountName)) {
            // If we know the account, but don't know the session state, request it.
            getSession();
        } else if(mConnectionResult != null) {
            // If we have an account, and still end up here, just go
            // and resolve the normal sign in flow.
            try {
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
            } catch(IntentSender.SendIntentException ex) {
                // Get new result.
                mConnectionResult = null;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onClickSignOut(View v) {
        setAccountName(null);
        mSessionCookie = null;
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACCOUNT_PICKER_REQUEST) {
            // If we have an account name, see if the server has a refresh token
            // by starting a session.
            if(resultCode == RESULT_OK) {
                setAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                getSession();
            }
        } else if (requestCode == PERMISSION_REQ) {
            if(resultCode == RESULT_OK) {
                // If we were in the middle of the code retrieval, carry on
                Log.d(TAG, "Resolved permissions, now retrieving");
                getCode();
            }
        } else if (requestCode == REQUEST_CODE_RESOLVE_ERR) {
            // Normal PlusClient sign in.
            mConnectionResult = null;
            if(resultCode == RESULT_OK) {
                buildGoogleApiClient();
            }
        }
    }

    private void setAccountName(String name) {
        mAccountName = name;
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ACCT_NAME, mAccountName);
        editor.commit();
    }

    private void buildGoogleApiClient() {
        GoogleApiClient.Builder b = new GoogleApiClient.Builder(this, this, this);
        b.addApi(Plus.API);
        b.addScope(Plus.SCOPE_PLUS_LOGIN);
        if(mAccountName != null) {
            b.setAccountName(mAccountName);
        }
        mGoogleApiClient = b.build();
    }

    private void createSession(String cookie) {
        Log.d(TAG, "Cookie received: " + cookie);
        if(cookie.equals(NO_REFRESH)) {
            mSessionCookie = null;
            mUserHasRefreshOnServer.put(mAccountName, false);
        } else {
            mSessionCookie = cookie;
            mUserHasRefreshOnServer.put(mAccountName, true);
        }
        // Reconnect the GoogleApiClient.
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private void getSession() {
        if (mSessionCookie != null) {
            // We already have a session!
            return;
        }
        CheckIdTokenTask task = new CheckIdTokenTask();
        task.execute(mAccountName);
    }

    private void getCode() {
        RetrieveCodeTask task = new RetrieveCodeTask();
        task.execute(mAccountName);
    }

    private class CheckIdTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {

            // 1. First we retrieve the ID token using the defined account
            // and server client ID.
            Context c = getApplicationContext();
            String token;
            try {
                String scope = "audience:server:client_id:" + SERVER_CLIENT_ID;
                token = GoogleAuthUtil.getToken(c, strings[0], scope);
            } catch (GooglePlayServicesAvailabilityException playEx) {
                return null;
            } catch (UserRecoverableAuthException userAuthEx) {
                return null;
            } catch (IOException transientEx) {
                return null;
            } catch (GoogleAuthException authEx) {
                return null;
            }

            // 2. Now we can send it to the server to see if we have a
            // valid refresh token already, but couldn't sign-in for other
            // reasons.
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            try {
                response = client.execute(new HttpGet(CHECK_SESSION_URL + token));
            } catch (ClientProtocolException e) {
                return null;
            } catch (IOException transientEx) {
                return null;
            }
            StatusLine statusLine = response.getStatusLine();
            Log.d(TAG, statusLine.toString());
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch(IOException ioEx) {
                    return null;
                }
            }
            return NO_REFRESH;
        }

        @Override
        protected void onPostExecute(String cookie) {
            // Create a session with our cookie if possible.
            super.onPostExecute(cookie);
            if (cookie != null) {
                createSession(cookie);
            }
        }
    }

    private class RetrieveCodeTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            Context c = getApplicationContext();
            Bundle appActivities = new Bundle();
            String scopes = "oauth2:server:client_id:"
                    + SERVER_CLIENT_ID
                    + ":api_scope:"
                    + Scopes.PLUS_LOGIN; // You must have matching scopes everywhere!
            String code;
            try {
                code = GoogleAuthUtil.getToken(
                        c,                             // Context context
                        strings[0],                    // String accountName
                        scopes,                        // String scope
                        appActivities                  // Bundle bundle
                );
            }  catch (IOException transientEx) {
                return null;
            } catch (UserRecoverableAuthException e) {
                // Needs sign in, so fire it! This will likely happen the
                // first time. Results go to onActivityResult
                startActivityForResult( e.getIntent(), PERMISSION_REQ);
                return null;
            } catch (GoogleAuthException authEx) {
                return null;
            }

            // We've retrieved a code successfully, so we can send it to the server.
            // Assuming all is good the server will exchange it and give us a
            // session cookie.
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;

            if (code != null) {
                // Ensure we don't get the same code again if we have to try again.
                GoogleAuthUtil.invalidateToken(c,code);
                try {
                    response = client.execute(new HttpGet(CODE_URL + code));
                } catch(IOException e) {
                    // We could be smarter and retry here with a backoff, but
                    // for this sample we'll just give up.
                    return null;
                }
                StatusLine statusLine = response.getStatusLine();
                Log.d(TAG, statusLine.toString());
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    try {
                        return EntityUtils.toString(response.getEntity());
                    } catch(IOException ioEx) {
                        return null;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String cookie) {
            // Again, store the cookie if valid.
            super.onPostExecute(cookie);
            if (cookie != null) {
                createSession(cookie);
            }
        }
    }
}