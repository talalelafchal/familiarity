//add to the same directory as MainActivity.java

package com.yourApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

public class FacebookLoginModule extends ReactContextBaseJavaModule {

    private final String CALLBACK_TYPE_SUCCESS = "success";
    private final String CALLBACK_TYPE_ERROR = "error";
    private final String CALLBACK_TYPE_CANCEL = "cancel";

    private Context mActivityContext;
    private CallbackManager mCallbackManager;
    private Callback mTokenCallback;

    public FacebookLoginModule(ReactApplicationContext reactContext, Context activityContext) {
        super(reactContext);

        mActivityContext = activityContext;

        FacebookSdk.sdkInitialize(activityContext.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject me, GraphResponse response) {
                                        if (mTokenCallback != null) {

                                            FacebookRequestError error = response.getError();

                                            if (error != null) {

                                                WritableMap map = Arguments.createMap();
                                                map.putString("errorType", error.getErrorType());
                                                map.putString("message", error.getErrorMessage());
                                                map.putString("recoveryMessage", error.getErrorRecoveryMessage());
                                                map.putString("userMessage", error.getErrorUserMessage());
                                                map.putString("userTitle", error.getErrorUserTitle());
                                                map.putInt("code", error.getErrorCode());
                                                consumeCallback(CALLBACK_TYPE_ERROR, map);

                                            } else {

                                                WritableMap map = Arguments.createMap();
                                                System.out.println("Success");
                                                String jsonresult = String.valueOf(me);
                                                System.out.println("JSON Result" + jsonresult);
                                                map.putString("email", me.optString("email"));
                                                map.putString("token", loginResult.getAccessToken().getToken());
                                                map.putString("expiration", String.valueOf(loginResult.getAccessToken().getExpires()));
                                                consumeCallback(CALLBACK_TYPE_SUCCESS, map);
                                            }
                                        } else {

                                            WritableMap map = Arguments.createMap();
                                            map.putString("message", "Insufficient permissions");
                                            consumeCallback(CALLBACK_TYPE_ERROR, map);
                                        }
                                    }
                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        if (mTokenCallback != null) {
                            consumeCallback(CALLBACK_TYPE_CANCEL, Arguments.createMap());
                        }
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (mTokenCallback != null) {

                            WritableMap map = Arguments.createMap();
                            map.putString("message", exception.getMessage());
                            consumeCallback(CALLBACK_TYPE_ERROR, map);
                        }
                        if (exception != null) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    }

    private void consumeCallback(String type, WritableMap map) {
        if (mTokenCallback != null) {
            map.putString("type", type);
            map.putString("provider", "facebook");

            mTokenCallback.invoke(map);
            mTokenCallback = null;
        }
    }

    @Override
    public String getName() {
        return "FacebookLoginModule";
    }

    @ReactMethod
    public void pickAccount(final Callback callback) {
        if (mTokenCallback != null) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            WritableMap map = Arguments.createMap();

            if (accessToken != null) {
                map.putString("token", AccessToken.getCurrentAccessToken().getToken());
                map.putString("expiration", String.valueOf(AccessToken.getCurrentAccessToken()));
                map.putBoolean("cache", true);
                consumeCallback(CALLBACK_TYPE_SUCCESS, map);
            } else {
                map.putString("message", "Cannot register multiple callbacks");
                consumeCallback(CALLBACK_TYPE_CANCEL, map);
            }
        }

        mTokenCallback = callback;

        //set read permissions https://developers.facebook.com/docs/facebook-login/permissions/v2.5
        LoginManager.getInstance().logInWithReadPermissions(
                (Activity) mActivityContext,
                Arrays.asList("public_profile", "email"));
    }

    @ReactMethod
    public void getCurrentToken(final Callback callback) {
        callback.invoke(AccessToken.getCurrentAccessToken().getToken());
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        return mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
