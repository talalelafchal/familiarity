public abstract class PlusSimpleFragmentActivity
    extends FragmentActivity
    implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener
{

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// abstract methods to impl
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** called at the end of {@link #onCreate} execution */
protected abstract void onCreateComplete();

/** {@link PlusClient} disconnect handler */
protected abstract void onPlusClientRevokeAccess();

/** {@link PlusClient} connected handler */
protected abstract void onPlusClientSignin();

/** {@link PlusClient} revoke access handler */
protected abstract void onPlusClientSignout();

/** if you have a progress bar widget, this tells you when to show or hide it */
protected abstract void onPlusClientBlockingUI(boolean show);

/**
 * if you have signin/connect, signout/disconnect, revokeaccess buttons, this lets you know
 * when their states need to be updated
 */
protected abstract void updateConnectButtonState();

/**
 * allows you to handle your own {@link #onActivityResult(int, int, Intent)} that can't
 * be automatically handled by the {@link PlusClient}.
 */
protected abstract void handleActivityResult(int request, int response, Intent intent);

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// plusclient data
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** A magic number we will use to know that our sign-in error resolution activity has completed */
public static final int OUR_REQUEST_CODE = 49404;
/** A flag to stop multiple dialogues appearing for the user */
public boolean          autoResolveOnFail;
/** this the object that connects to the Google Play Services */
public PlusClient       plusClient;
/**
 * this is not null if the {@link #onConnectionFailed(ConnectionResult)} ran.
 * if this is null then the connect method is still running.
 */
public ConnectionResult mConnectionResult;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// activity lifecycle stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

protected void onCreate(Bundle savedInstanceState) {

  super.onCreate(savedInstanceState);

  // plus client setup stuff
  plusClient = new PlusClient.Builder(this, this, this)
      .setScopes(PlusUtils.getScopeArray(this))
      .setVisibleActivities(PlusUtils.getMomentTypeArray(this))
      .build();

  onCreateComplete();

}

/** sign in the user */
public void signIn() {
  if (!plusClient.isConnected()) {
    // Show the dialog as we are now signing in.
    _setProgressBarVisible(true);
    // Make sure that we will start the resolution (e.g. fire the intent and pop up a dialog for the user)
    // for any errors that come in.
    autoResolveOnFail = true;
    // We should always have a connection result ready to resolve, so we can start that process.
    if (mConnectionResult != null) {
      _startResolution();
    }
    else {
      // If we don't have one though, we can start connect in
      // order to retrieve one.
      _initiatePlusClientConnect();
    }
  }

  updateConnectButtonState();
}

/**
 * feedback from sam: About the connected and connecting: whenever you call plusClient.connect()
 * surround it with if(!connected and !connecting) { ... }.
 * Even if you're 100% sure this if check will always pass, it makes your class more
 * extensible.  That's another thing that I thought I didn't need from PCF and it
 * turned out to solve some edge cases.
 */
private void _initiatePlusClientConnect() {
  if (!plusClient.isConnected() && !plusClient.isConnecting()) {
    plusClient.connect();
  }
}

/**
 * feedback from sam: And for disconnect, make sure to only call it if(connected),
 * otherwise it can throw and error (or it at least logs one).
 */
private void _initiatePlusClientDisconnect() {
  if (plusClient.isConnected()) { plusClient.disconnect(); }
}

/** sign out the user (so they can switch to another account) */
public void signOut() {

  // We only want to sign out if we're connected.
  if (plusClient.isConnected()) {
    // Clear the default account in order to allow the user to potentially choose a different account from the
    // account chooser.
    plusClient.clearDefaultAccount();

    // Disconnect from Google Play Services, then reconnect in order to restart the process from scratch.
    _initiatePlusClientDisconnect();

    AndroidUtils.showToastShort(getApplicationContext(),
                                "Sign out successful!");

  }

  updateConnectButtonState();
}

/** Prior to disconnecting, run clearDefaultAccount() */
public void revokeAccess() {

  if (plusClient.isConnected()) {
    // Clear the default account as in the Sign Out.
    plusClient.clearDefaultAccount();

    // Go away and revoke access to this entire application. This will call back to onAccessRevoked when it is
    // complete as it needs to go away to the Google authentication servers to revoke all token.
    plusClient.revokeAccessAndDisconnect(new PlusClient.OnAccessRevokedListener() {
      public void onAccessRevoked(ConnectionResult result) {
        updateConnectButtonState();
        onPlusClientRevokeAccess();
      }
    });
  }

}

@Override
protected void onStart() {
  super.onStart();
  _initiatePlusClientConnect();
}

@Override
protected void onStop() {
  super.onStop();
  _initiatePlusClientDisconnect();
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// plusclient isConnecting?
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public boolean plusClientIsConnecting = false;

public boolean isPlusClientConnecting() {return plusClientIsConnecting;}

private void _setProgressBarVisible(boolean flag) {
  plusClientIsConnecting = flag;
  onPlusClientBlockingUI(flag);
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// plusclient orchestration
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * A helper method to flip the mResolveOnFail flag and start the resolution
 * of the ConnenctionResult from the failed connect() call.
 */
private void _startResolution() {
  try {
    // Don't start another resolution now until we have a result from the activity we're about to start.
    autoResolveOnFail = false;
    // If we can resolve the error, then call start resolution and pass it an integer tag we can use to track.
    // This means that when we get the onActivityResult callback we'll know its from being started here.
    mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
  }
  catch (IntentSender.SendIntentException e) {
    // Any problems, just try to connect() again so we get a new ConnectionResult.
    mConnectionResult = null;
    _initiatePlusClientConnect();
  }
}

/**
 * connection failed, and this is the result of the resolution attempt by plusclient
 *
 * @see #onConnectionFailed(ConnectionResult)
 */
@Override
protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
  updateConnectButtonState();
  if (requestCode == OUR_REQUEST_CODE && responseCode == RESULT_OK) {
    // If we have a successful result, we will want to be able to resolve any further errors, so turn on
    // resolution with our flag.
    autoResolveOnFail = true;
    // If we have a successful result, lets call connect() again. If there are any more errors to
    // resolve we'll get our onConnectionFailed, but if not, we'll get onConnected.
    _initiatePlusClientConnect();
  }
  else if (requestCode == OUR_REQUEST_CODE && responseCode != RESULT_OK) {
    // If we've got an error we can't resolve, we're no longer in the midst of signing in, so we can stop
    // the progress spinner.
    _setProgressBarVisible(false);
  }
  else {
    handleActivityResult(requestCode, responseCode, intent);
  }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// plusclient callbacks
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** connection successful; called by PlusClient */
@Override
public void onConnected(Bundle connectionHint) {
  updateConnectButtonState();
  _setProgressBarVisible(false);
  String emailAddress = plusClient.getAccountName();
  StringBuilder sb = new StringBuilder();
  sb.append("PlusClient is connected. User email: ").append(emailAddress);
  AndroidUtils.showToastLong(getApplicationContext(), sb.toString());
  onPlusClientSignin();
}

/** connection ended successfully; called by PlusClient */
@Override
public void onDisconnected() {
  updateConnectButtonState();
  onPlusClientSignout();
}

/**
 * connection failed for some reason; called by PlusClient
 * try and resolve
 *
 * @see #onActivityResult(int, int, Intent)
 */
@Override
public void onConnectionFailed(ConnectionResult result) {

  updateConnectButtonState();

  // Most of the time, the connection will fail with a user resolvable result. We can store that in our
  // mConnectionResult property ready for to be used when the user clicks the sign-in button.
  if (result.hasResolution()) {
    mConnectionResult = result;
    if (autoResolveOnFail) {
      // This is a local helper function that starts the resolution of the problem, which may be
      // showing the user an account chooser or similar.
      _startResolution();
    }
  }

}

}//end class PlusSimpleFragmentActivity
