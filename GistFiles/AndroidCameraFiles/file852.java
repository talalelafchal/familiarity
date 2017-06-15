package com.eventify.android;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
import com.eventify.android.models.EventifyUser;
import com.eventify.android.utils.AsyncCallback;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
 
import org.json.JSONObject;
 
import java.lang.ref.WeakReference;
 
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
 
/**
 * Created by Greg on 5/22/15.
 */
public class LoginActivity extends Activity
{
    @InjectView(R.id.image_view_eventify_logo) ImageView mEventifyLogoImageView;
    @InjectView(R.id.button_facebook_sign_in) Button mFacebookSignInButton;
    @InjectView(R.id.text_view_facebook_permission) TextView mFacebookPermissionsTextView;
 
    private final static long SPLASH_SCREEN_VISIBILITY_MILLIS = 1000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
 
        new Handler().postDelayed(new SplashScreenRunnable(
                new WeakReference<>(this)), SPLASH_SCREEN_VISIBILITY_MILLIS);
 
    }
 
    @Override
    protected void onResume()
    {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }
 
    @Override
    protected void onPause()
    {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
 
    @OnClick(R.id.button_facebook_sign_in)
    public void onFacebookLoginButtonClicked()
    {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback()
        {
            @Override
            public void done(final ParseUser parseUser, ParseException e)
            {
                if (e == null)
                {
                    fetchUserFacebookId(new AsyncCallback<String>()
                    {
                        @Override
                        public void onAsyncOperationCompleted(String result)
                        {
                            if (parseUser != null && result != null)
                            {
                                onFBIdReady(result);
                            }
                            else
                            {
                                if (LoginActivity.this != null)
                                    Toast.makeText(LoginActivity.this, R.string.log_in_error, Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
                else
                {
                    e.printStackTrace();
                    if (LoginActivity.this != null)
                        Toast.makeText(LoginActivity.this, R.string.log_in_error, Toast.LENGTH_SHORT);
                }
            }
        });
    }
 
    private void fetchUserFacebookId(final AsyncCallback<String> userIdCallback)
    {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse)
                    {
                        if (userIdCallback != null)
                        {
                            if (jsonObject != null)
                                userIdCallback.onAsyncOperationCompleted(jsonObject.optString("id"));
                            else
                                userIdCallback.onAsyncOperationCompleted(null);
                        }
                    }
                });
 
        Bundle params = new Bundle();
        params.putString("fields", "id");
        request.setParameters(params);
        request.executeAsync();
    }
 
    private void onFBIdReady(String id)
    {
        EventifyUser.getCurrentUser().setFacebookId(id);
        EventifyUser.getCurrentUser().saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                navigateToMain();
            }
        });
    }
 
    private void onSplashScreenFinished()
    {
        if (ParseUser.getCurrentUser() != null)
        {
            navigateToMain();
        }
        else
        {
            animateUIForLogin();
        }
    }
 
    private void navigateToMain()
    {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
 
    private void animateUIForLogin()
    {
        float padding = dpToPx(45);
        float logoPosition = ViewHelper.getY(mFacebookSignInButton) - mFacebookSignInButton.getHeight() - padding;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mEventifyLogoImageView, "y", logoPosition);
        animator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }
 
            @Override
            public void onAnimationEnd(Animator animation)
            {
                mFacebookSignInButton.setVisibility(View.VISIBLE);
                mFacebookPermissionsTextView.setVisibility(View.VISIBLE);
            }
 
            @Override
            public void onAnimationCancel(Animator animation)
            {
            }
 
            @Override
            public void onAnimationRepeat(Animator animation)
            {
            }
        });
 
        animator.start();
    }
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data); // In case the user doesn't have FB installed
    }
 
    private float dpToPx(int dp)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
 
    private static class SplashScreenRunnable implements Runnable
    {
        private WeakReference<LoginActivity> mLoginActivity;
 
        public SplashScreenRunnable(WeakReference<LoginActivity> mLoginActivity)
        {
            this.mLoginActivity = mLoginActivity;
        }
 
        @Override
        public void run()
        {
            LoginActivity activity = mLoginActivity.get();
            if (activity != null)
            {
                activity.onSplashScreenFinished();
            }
        }
    }
}