package com.barkhappy;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.barkhappy.profile.creation.DogProfileViewActivity;
import com.barkhappy.utils.FontUtils;
import com.barkhappy.utils.Fonts;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class BHSignupActivity extends Activity
{
    @InjectView(R.id.email_edit_text) EditText emailEditText;
    @InjectView(R.id.password_edit_text) EditText edt_password;
    @InjectView(R.id.confirm_password_edit_text) EditText edt_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bhsignup);
        ButterKnife.inject(this);
        FontUtils.getInstance().overrideFonts(findViewById(R.id.layout_signup), Fonts.LIGHT);
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpClicked()
    {
        if (!checkEditText()) return;

        final ProgressDialog mProgressDialog = ProgressDialog.show(this, null, "Signing up... ", false);
        final ParseObject userInfo = new ParseObject("UserInfo");
        userInfo.put("firstname", "noName");
        userInfo.put("online", false);
        userInfo.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e == null)
                {
                    final ParseObject dog = new ParseObject("DogInfo");
                    dog.put("owner", userInfo);
                    dog.put("name", "noName");
                    dog.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            if (e == null)
                            {
                                final ParseUser user = new ParseUser();
                                final String email = emailEditText.getText().toString().trim();
                                final String password = edt_password.getText().toString().trim();
                                user.setUsername(email);
                                user.setPassword(password);
                                user.setEmail(email);
                                user.put("userInfoPointer", userInfo);
                                user.signUpInBackground(new SignUpCallback()
                                {
                                    @Override
                                    public void done(ParseException e)
                                    {
                                        if (mProgressDialog != null)
                                            mProgressDialog.cancel();

                                        if (e != null)
                                        {
                                            Toast.makeText(BHSignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            ParseRelation<ParseObject> rel = userInfo.getRelation("myDogs");
                                            rel.add(dog);
                                            userInfo.saveInBackground();

                                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                            installation.put("userPointer", user);
                                            installation.saveInBackground();

                                            user.logInInBackground(email, password, new LogInCallback()
                                            {
                                                @Override
                                                public void done(ParseUser user, ParseException e)
                                                {
                                                    if (e != null)
                                                    {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                            Intent intent = new Intent(BHSignupActivity.this, DogProfileViewActivity.class);
                                            intent.putExtra("update", "Init");
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                            BHSignupActivity.this.finish();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                if (mProgressDialog != null)
                                    mProgressDialog.cancel();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else
                {
                    if (mProgressDialog != null)
                        mProgressDialog.cancel();
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean checkEditText()
    {
        String s_email = emailEditText.getText().toString().trim();
        String s_password = edt_password.getText().toString().trim();
        String s_confirm_password = edt_confirm_password.getText().toString().trim();
        if (s_email.length() == 0 || s_password.length() == 0 || s_confirm_password.length() == 0)
        {
            Toast.makeText(this, getString(R.string.ui_login_insufficient_data), Toast.LENGTH_LONG).show();
            return false;
        }
        if (!s_password.equals(s_confirm_password))
        {
            Toast.makeText(this, getString(R.string.ui_login_password_not_confirmed), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
