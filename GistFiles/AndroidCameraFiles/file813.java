package com.provagroup.legit.ui.settings.profile;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.provagroup.legit.ProvaLegitApplication;
import com.provagroup.legit.R;
import com.provagroup.legit.imagepicker.model.Image;
import com.provagroup.legit.request.LegitWampClient;
import com.provagroup.legit.request.WampApiFieldKeys;
import com.provagroup.legit.service.ConnectivityReceiver;
import com.provagroup.legit.session.ProvaSession;
import com.provagroup.legit.session.ProvaUser;
import com.provagroup.legit.utiles.Constants;
import com.provagroup.legit.utiles.ImagePicker;
import com.provagroup.legit.utiles.ProvaUtile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.provagroup.legit.R.id.city;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

public class EditProfileActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String PLEASE_FILL_ALL_FIELDS = "Please, fill all the fields";
    private static final String PLEASE_PROVIDE_CORRECT_DETAILS = "Please, provide correct details";
    private static final String PROFILE_UPDATED_SUCCESSFULLY = "Your Profile Updated Successfully";
    private static final String TAG = EditProfileActivity.class.getName();
    static Bitmap croppedBitmap;
    //    static Bitmap profileImage;
//    static Bitmap coverImage;
    static boolean flagcover;
    static boolean flagprofile;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.first_name)
    EditText FirstName;
    @Bind(R.id.last_name)
    EditText LastName;
    @Bind(city)
    EditText City;
    @Bind(R.id.state)
    EditText State;
    @Bind(R.id.email)
    TextView Email;
    @Bind(R.id.edit_cover)
    TextView EditCover;
    @Bind(R.id.edit_profile)
    TextView EditProfile;
    @Bind(R.id.profile_image)
    ImageView ProfileImage;
    @Bind(R.id.cover_image)
    ImageView CoverImage;
    @Bind(R.id.thumbnail)
    ImageView Thumbnail;
    Image image;
    int imagemode;
    ProvaSession session;
    ProvaUser userData;
    String firstName;
    String lastName;
    String location;
    String email;
    HashMap map;
    LegitWampClient client;
    File filePath;
    byte[] cover;
    byte[] profile;
    File coverPictures;
    File profilePictures;
    String sharedcity;
    String sharedstate;
    private Typeface tfb;
    private Typeface tf;
    private Dialog progressBar;
    private boolean noExecptionCaught = true;
    private boolean isInternetPresent;
    private ProvaUser provaUser;
    private int imageMode;
    private ArrayList<Image> images = new ArrayList<>();
    private int REQUEST_CODE_PICKER = 2000;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private String state;
    private String uploadUrl;
    private String userName;
    private UpdateProfileAsync updateProfileAsync;

    public static void startResultActivity(Bitmap circularBitmap) {
        croppedBitmap = circularBitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        tf = Typeface.createFromAsset(getAssets(), String.valueOf(Constants.GADUGI));
        tfb = Typeface.createFromAsset(getAssets(), String.valueOf(Constants.GADUGIBOLD));
        setFont();
        session = new ProvaSession(this);
        isInternetPresent = ConnectivityReceiver.isConnected();
        userData = session.getProvaUser();
        userName = session.getProvaUser().getUser();
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // Check if the smartphone has NFC
        if (mAdapter == null) {
            ProvaUtile.showOrangeToast("NFC not supported.", getApplicationContext(), false);
        } else {
            // Check if NFC is enabled
            if (!mAdapter.isEnabled()) {
                ProvaUtile.showOrangeToast("Enable NFC before using the app.", getApplicationContext(), false);
            }
        }
        Intent intent = getIntent();
        cover = getIntent().getByteArrayExtra("cover");
        profile = getIntent().getByteArrayExtra("profile");
        SharedPreferences prefs = getSharedPreferences("address", MODE_PRIVATE);
        if (prefs != null) {
            sharedcity = prefs.getString("city", null);//"No name defined" is the default value.
            sharedstate = prefs.getString("state", null); //0 is the default value.
        }

        if (userData != null) {
            FirstName.setText(userData.getFirst());
            FirstName.setSelection(FirstName.getText().length());
            LastName.setText(userData.getLast());
            LastName.setSelection(LastName.getText().length());
            Email.setText(userData.getEmail());
            if (userData.getLocation() != null && userData.getState() != null) {
                City.setText(userData.getLocation());
                City.setSelection(City.getText().length());
                State.setText(userData.getState());
                State.setSelection(State.getText().length());
            } else if (sharedcity != null && sharedstate != null) {
                City.setText(sharedcity);
                City.setSelection(City.getText().length());
                State.setText(sharedstate);
                State.setSelection(State.getText().length());
            }
        }
        setSupportActionBar(toolbar);
        initToolBar();

    }

    private void setImages() {
        if (userName == null) {
            return;
        }
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.image_path_name), Context.MODE_PRIVATE);
        coverPictures = new File(directory, userName + "_prova_cover_image.jpg");
        profilePictures = new File(directory, userName + "_prova_profile_image.jpg");
        if (profilePictures.exists()) {
            ProfileImage.setImageBitmap(BitmapFactory.decodeFile(profilePictures.getAbsolutePath()));
        } /*else if (session.getProvaUser().getCoverPicture() != null) {
            Glide.with(this)
                    .load(session.getProvaUser().getProfilePicture())
                    .placeholder(R.color.grey)
                    .error(R.drawable.image_placeholder)
                    .into(ProfileImage);
        }*/
        if (coverPictures.exists()) {
            Thumbnail.setVisibility(View.GONE);
            CoverImage.setImageBitmap(BitmapFactory.decodeFile(coverPictures.getAbsolutePath()));
            CoverImage.setScaleType(ImageView.ScaleType.FIT_XY);
            CoverImage.setAdjustViewBounds(true);
        } /*else if (session.getProvaUser().getProfilePicture() != null) {
            Glide.with(this)
                    .load(session.getProvaUser().getCoverPicture())
                    .placeholder(R.color.grey)
                    .error(R.drawable.image_placeholder)
                    .into(CoverImage);
            Thumbnail.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "In the onStart() event");
    }

    @Override
    public void onResume() {
        super.onResume();
        setImages();
        Log.d(TAG, "In the onResume() event");
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
        ProvaLegitApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "In the onPause() event");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "In the onStop() event");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "In the onDestroy() event");
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);

    }

    @OnClick(R.id.profile_image)
    public void editProfile() {
        start(1);
    }

    @OnClick(R.id.cover_image)
    public void coverImage() {
        start(2);
    }

    private void initToolBar() {

        ActionBar ab = getSupportActionBar();
        ab.show();
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.back);
        ab.setDisplayShowHomeEnabled(true);

        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());

        // Create a LayoutParams for TextView
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, // Width of TextView
                ActionBar.LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);

        // Set text to display in TextView
        tv.setText(R.string.edit_profile); // ActionBar title text
        tv.setTextColor(Color.rgb(233, 235, 242));

        tv.setTypeface(tfb);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(tv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        Typeface typefaceGadugiBold = Typeface.createFromAsset(getAssets(), String.valueOf(Constants.GADUGIBOLD));

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);

        MenuItem saveItem = menu.findItem(R.id.action_save);
        TextView tv = new TextView(this);
        tv.setText(R.string.menu_save);
        tv.setTypeface(typefaceGadugiBold);
        tv.setTextColor(Color.rgb(233, 235, 242));
        tv.setPadding(30, 30, 30, 30);
        tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
        saveItem.setActionView(tv);
        View view = MenuItemCompat.getActionView(saveItem);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                updateProfile();
                break;
            default:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFont() {
        FirstName.setTypeface(tf);
        LastName.setTypeface(tf);
        City.setTypeface(tf);
        State.setTypeface(tf);
        Email.setTypeface(tf);
        EditCover.setTypeface(tfb);
        EditProfile.setTypeface(tfb);

    }

    // Recomended builder
    public void start(int image) {
        ImagePicker.create(this)
                .folderMode(true) // set folder mode (false by default)
                .imageTitle("Gallery") // image selection title
                .single() // single mode
                .multi() // multi mode (default mode)
                .image(image)
                .limit(1) // max images can be selected (999 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .origin(images) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            imagemode = data.getIntExtra("image", 0);
            for (int i = 0, l = images.size(); i < l; i++) {
                image = images.get(i);
                userData.setProfilePicture(image.getPath());
                if (imagemode == 1) {
                    userData.setProfilePicture(image.getPath());
                    viewImage(ProfileImage);
                } else {
                    userData.setCoverPicture(image.getPath());
                    viewImage(CoverImage);
                }
            }
        }
    }

    public void viewImage(ImageView view) {
        if (userData != null) {
            new ProvaSession(this).setProvaUSer(userData);
        }
        Thumbnail.setVisibility(View.GONE);
        view.setImageBitmap(croppedBitmap);
    }

    public void updateProfile() {
        firstName = FirstName.getText().toString();
        lastName = LastName.getText().toString();
        location = City.getText().toString().trim();
        state = State.getText().toString().trim();
        boolean cancel = false;
        Tag tag = null;
        if (isInternetPresent) {
            if (TextUtils.isEmpty(firstName) ||
                    TextUtils.isEmpty(lastName) ||
                    TextUtils.isEmpty(location) ||
                    TextUtils.isEmpty(state)) {
                cancel = true;
            }
            if (cancel) {
                ProvaUtile.showOrangeToast("Please Fill All The Fields...", this.getBaseContext(), false);
            } else {
//                if (flagcover || flagprofile) {
//                    new UploadMediaAsync().execute();
//                } else {
                saveProfileApiCall();
//                }
            }

        } else {
            ProvaUtile.showOrangeToast(getString(R.string.no_internet), this.getBaseContext(), false);
        }

    }

    private void saveProfileApiCall() {
        updateProfileAsync = new UpdateProfileAsync();
        updateProfileAsync.execute();
    }


    private void showDialog() {
        if (EditProfileActivity.this != null) {
            progressBar = new Dialog(EditProfileActivity.this, android.R.style.Theme_Translucent);
            progressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //here we set layout of progress dialog
            progressBar.setContentView(R.layout.small_progress_dialog);
            progressBar.setCancelable(false);
            progressBar.show();
        }
    }

    public String photoUpload(File path) {
        try {
            File testPic = path;
            if (!testPic.exists()) {
                fail("The test image does not exist " + testPic);
            }
            if (userData != null) {
                LegitWampClient c = new LegitWampClient(userData.getUri(),
                        userData.getRealm(), userData.getEmail(), userData.getPassword());

                // upload image for asset
                Map<String, Object> args = new HashMap<>();
                args.put(WampApiFieldKeys.MEDIA_TYPE, WampApiFieldKeys.MEDIA_PHOTO_JPG);
                Map<String, Object> ret = c.uploadMedia(args, testPic);

                // test that the image was uploaded and the asset updated with the uploaded image.
                assertNotNull(ret);
                assertNotNull(ret.get(WampApiFieldKeys.MEDIA_URL));

                try {
                    final String url = (String) ret.get(WampApiFieldKeys.MEDIA_URL);
                    return url;
                } catch (Exception e) {
                    fail("Failed to confirm URL exists and file matches upload");
                } finally {
                }
            } else {
                ProvaUtile.showOrangeToast("Please try again later", EditProfileActivity.this, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        isInternetPresent = isConnected;
        if (!isConnected) {
            if (updateProfileAsync != null) {
                updateProfileAsync.cancel(true);
            }
            if ((progressBar != null) && progressBar.isShowing()) {
                progressBar.dismiss();
                progressBar = null;
            }
            ProvaUtile.showOrangeToast(getString(R.string.no_internet), this, false);
        }
    }

    /**
     * Simple address generation for a user.
     *
     * @param city
     * @param state
     * @param country
     * @return
     */
    private Map<String, Object> getAddress(String city, String state, String country) {
        Map<String, Object> m = new HashMap<>();
        m.put(WampApiFieldKeys.UA_TYPE, "home");
        m.put(WampApiFieldKeys.UA_COUNTRY, country);
        if (state != null) {
            m.put(WampApiFieldKeys.UA_STATE, state);
        }
        if (state != null) {
            m.put(WampApiFieldKeys.UA_CITY, city);
            m.put(WampApiFieldKeys.UA_ZIP_CODE, "00001");
            m.put(WampApiFieldKeys.UA_STREET, "1 Ave K");
        }
        return m;
    }

    public class UpdateProfileAsync extends AsyncTask<Void, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected Object doInBackground(Void... voids) {
            HashMap data = new HashMap();
            if (userData != null) {
                client = new LegitWampClient(userData.getUri(),
                        userData.getRealm(), userData.getEmail(), userData.getPassword());

                Map<String, Object> u = client.getLoginUser();
                if (u != null) {
                    u.put(WampApiFieldKeys.U_FIRST, firstName);
                    u.put(WampApiFieldKeys.U_LAST, lastName);



                    //setup address object

                    Map<String, Object> m = new HashMap<>();
                    m.put(WampApiFieldKeys.UA_TYPE, "home");
                    m.put(WampApiFieldKeys.UA_COUNTRY, "US");
                    m.put(WampApiFieldKeys.UA_STATE, state);
                    m.put(WampApiFieldKeys.UA_CITY, city);

                    // create new address list
                    List<Map<String, Object>> nadds = new ArrayList<>();
                    nadds.add(m);


                    Map<String, Object> userObject = client.updateUser(u);


                    try {
                        return userObject;

                    } catch (Exception ie) {
                        new Thread() {
                            public void run() {
                                EditProfileActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        noExecptionCaught = false;
                                        //Do your UI operations like dialog opening or Toast here
                                        ProvaUtile.showOrangeToast("Please try Again", EditProfileActivity.this, false);
                                    }
                                });
                            }
                        }.start();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            try {
                progressBar.dismiss();
            } catch (final IllegalArgumentException e) {
            } catch (final Exception e) {
            }
            if (result != null && noExecptionCaught) {
                closeAlertDialog(result);
            } else if (noExecptionCaught) {
                noExecptionCaught = false;
                try {
                    progressBar.dismiss();
                } catch (final IllegalArgumentException e) {
                } catch (final Exception e) {
                }
            } else {
                ProvaUtile.showOrangeToast("Please try Again", EditProfileActivity.this, false);
            }

        }

        public void closeAlertDialog(Object object) {
            provaUser = session.getProvaUser();
            Map<String, Object> hashMap = (HashMap) object;
            try {
                progressBar.dismiss();
            } catch (final IllegalArgumentException e) {
            } catch (final Exception e) {
            }

            if (hashMap != null && noExecptionCaught) {
                if (provaUser != null) {
                    if (hashMap.get("email") != null) {
                        provaUser.setEmail(hashMap.get("email").toString());
                    }

                    if (hashMap.get("first") != null) {
                        provaUser.setFirst(hashMap.get("first").toString());
                    }

                    if (hashMap.get("last") != null) {
                        provaUser.setLast(hashMap.get("last").toString());
                    }

                    if (hashMap.get("mid") != null) {
                        provaUser.setMid(hashMap.get("mid").toString());
                    }

                    if (hashMap.get("pimg") != null) {
                        provaUser.setProfilePicture(hashMap.get("pimg").toString());
                    }

                    if (hashMap.get("cimg") != null) {
                        provaUser.setCoverPicture(hashMap.get("cimg").toString());
                    }
                    SharedPreferences.Editor editor = getSharedPreferences("address", MODE_PRIVATE).edit();

                    if (!TextUtils.isEmpty(location)) {
                        location = location.substring(0, 1).toUpperCase() + location.substring(1);
                        editor.putString("city", location);
                        provaUser.setLocation(location);
                    } else {
                        provaUser.setLocation(null);

                    }

                    if (!TextUtils.isEmpty(location.trim())) {
                        state = state.substring(0, 1).toUpperCase() + state.substring(1);
                        editor.putString("state", state);
                        editor.commit();
                        provaUser.setState(state);
                    } else {
                        provaUser.setState(null);
                    }

                    new ProvaSession(getApplicationContext()).setProvaUSer(provaUser);
                }
                ProvaUtile.showOrangeToast(PROFILE_UPDATED_SUCCESSFULLY, EditProfileActivity.this, false);
                noExecptionCaught = true;
                onBackPressed();

            } else if (noExecptionCaught) {
                ProvaUtile.showOrangeToast("Please try Again", EditProfileActivity.this, false);
                noExecptionCaught = true;
            }
        }

    }

    public class UploadMediaAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String url = null;
            String cover = coverPictures.getAbsolutePath();
            String profile = profilePictures.getAbsolutePath();
            try {
                if (flagprofile) {
                    url = photoUpload(profilePictures);
                    session.setProfileImageUrl(url);
                }
                if (flagcover) {
                    url = photoUpload(coverPictures);
                    session.setCoverImageUrl(url);
                }
                return url;

            } catch (Exception ie) {
                new Thread() {
                    public void run() {
                        EditProfileActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                noExecptionCaught = false;
                                //Do your UI operations like dialog opening or Toast here
                                ProvaUtile.showOrangeToast("Please try Again", EditProfileActivity.this, false);
                            }
                        });
                    }
                }.start();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (EditProfileActivity.this != null && progressBar != null) {
                progressBar.dismiss();
            }
            if (result != null && noExecptionCaught) {
//                flagprofile = false;
//                flagcover = false;
                new UpdateProfileAsync().execute();
            } else if (noExecptionCaught) {
                noExecptionCaught = false;
                try {
                    progressBar.dismiss();
                } catch (final IllegalArgumentException e) {
                } catch (final Exception e) {
                }
            }

        }

    }

}
