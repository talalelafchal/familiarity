public class SettingsActivity
    extends PlusSimpleFragmentActivity
{

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// app specific data
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

private ViewHolder viewHolder;

@Override
protected void onCreate(Bundle savedInstanceState) {

  super.onCreate(savedInstanceState);

  // setup UI stuff
  viewHolder = new ViewHolder();

}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// abstract method impl
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** called at the end of {@link #onCreate} execution */
@Override
protected void onCreateComplete() {
  // stop the PlusService from running as long as this screen is up
  // since user might disconnect from PlusClient in this screen
  PlusService.cancelRecurringAlarm(this);
}

/** {@link PlusClient} disconnect handler */
@Override
protected void onPlusClientSignout() {
  AndroidUtils.showToastLong(getApplicationContext(), "PlusClient is disconnected");
}

/** {@link PlusClient} connected handler */
@Override
protected void onPlusClientSignin() {
  // start the PlusService &
  // schedule recurring alarm to keep it running
  PlusService.startServiceNow(getAppData());
}

/** {@link PlusClient} revoke access handler */
@Override
protected void onPlusClientRevokeAccess() {
  // plusClient is now disconnected and access has been revoked. Trigger app logic to comply with the
  // developer policies
  AndroidUtils.showToastShort(getApplicationContext(),
                              "Todo - Access revoked ... should delete data now");
}

/** if you have a progress bar widget, this tells you when to show or hide it */
@Override
protected void onPlusClientBlockingUI(boolean show) {
  viewHolder._showProgressBar(show);
}

/**
 * if you have signin/connect, signout/disconnect, revokeaccess buttons, this lets you know
 * when their states need to be updated
 */
@Override
protected void updateConnectButtonState() {
  viewHolder._updateButtonState();
}

@Override
protected void handleActivityResult(int request, int response, Intent intent) {}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// token stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
/**
 * <ol>
 * <li><a href="http://goo.gl/9mH8T">more info on validating tokens with tokeninfo endpoint</a>.
 * <li><a href="http://goo.gl/qV0Sj9">more info on using GoogleAuthUtil to get access token</a>.
 * </ol>
 */
private void _retrieveToken() {

  AsyncTask task = new AsyncTask() {
    protected void onPostExecute(Object o) {
      viewHolder.btn_retrievetoken.setEnabled(true);
    }

    protected void onPreExecute() {
      viewHolder.btn_retrievetoken.setEnabled(false);
    }

    @Override
    protected Object doInBackground(Object... params) {
      try {

        String requestedOAuth2Scopes = PlusUtils.getOauthScopes(getApplicationContext());
        AndroidUtils.log(IconPaths.Social, "OAuth2 scopes requested", requestedOAuth2Scopes);

        // We can retrieve the token to check via tokeninfo or to pass to a service-side application.
        String token = GoogleAuthUtil.getToken(getAppData(),
                                               plusClient.getAccountName(),
                                               requestedOAuth2Scopes);

        AndroidUtils.log(IconPaths.Social, "OAuth2 auth token ", token);

        // resolve token via tokeninfo endpoint
        String tokenInfoEndpoint =
            String.format("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=%s",
                          token);

        HttpClient hc = getAppData().netClient.getHttpClient();
        HttpResponse resp = hc.execute(new HttpGet(tokenInfoEndpoint));
        String tokenInfoJSONString = EntityUtils.toString(resp.getEntity());

        AndroidUtils.log(IconPaths.Social, "OAuth2 token info", tokenInfoJSONString);

        PopupInfoDialogFragment newFragment = new PopupInfoDialogFragment(tokenInfoJSONString);
        newFragment.show(getSupportFragmentManager(),
                         PopupInfoDialogFragment.class.getSimpleName());

      }
      catch (Exception e) {
        AndroidUtils.logErr(IconPaths.Social, "problem getting OAuth2 auth token", e);
      }
      return null;
    }
  };
  task.execute((Void) null);

}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// view holder stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public class ViewHolder {

  private TextView lbl_signin_status;
  public  Button   btn_signin;
  public  Button   btn_signout;
  public  Button   btn_revokeaccess;
  public  Button   btn_retrievetoken;
  public  Button[] btnRay;

  public ViewHolder() {
    setContentView(R.layout.settingsactivity);
    btn_signin = (Button) findViewById(R.id.btn_sign_in);
    btn_signout = (Button) findViewById(R.id.btn_sign_out);
    btn_revokeaccess = (Button) findViewById(R.id.btn_revoke_access);
    btn_retrievetoken = (Button) findViewById(R.id.btn_retrieve_token);
    lbl_signin_status = (TextView) findViewById(R.id.lbl_signin_status);
    btnRay = new Button[]{btn_signin, btn_signout, btn_revokeaccess, btn_retrievetoken};
    _wireButtons();
  }

  private void _showProgressBar(boolean enabled) {
    // signin is in progress ... so disable everything
    if (enabled) {
      lbl_signin_status.setVisibility(View.VISIBLE);
      for (Button button : btnRay) {button.setEnabled(false);}
    }
    // signin is done ... so enable everything
    else {
      lbl_signin_status.setVisibility(View.GONE);
      for (Button button : btnRay) {button.setEnabled(true);}
    }
  }

  private void _wireButtons() {
    // wire the sign-in button
    btn_signin.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        signIn();
      }
    });

    // wire the sign-out button
    btn_signout.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        signOut();
      }
    });

    // wire the revoke-access button
    btn_revokeaccess.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        revokeAccess();
      }
    });

    // wire the retrieve token button
    btn_retrievetoken.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        _retrieveToken();
      }
    });
  }

  private void _updateButtonState() {
    if (plusClient.isConnected()) {
      // connected
      btn_signin.setVisibility(View.GONE);
      btn_signout.setVisibility(View.VISIBLE);
      btn_revokeaccess.setVisibility(View.VISIBLE);
      btn_retrievetoken.setVisibility(View.VISIBLE);
    }
    else {
      // not connected
      btn_signout.setVisibility(View.GONE);
      btn_revokeaccess.setVisibility(View.GONE);
      btn_retrievetoken.setVisibility(View.GONE);
      btn_signin.setVisibility(View.VISIBLE);
    }
  }

}// end class ViewHolder

}// end class SettingsActivityWithFragment