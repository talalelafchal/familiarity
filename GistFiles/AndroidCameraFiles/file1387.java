/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.educare.cop.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.educare.cop.model.CodelabPreferences;
import com.educare.cop.model.FriendlyMessage;
import com.educare.cop.R;
import com.educare.cop.model.Group;
import com.educare.cop.service.MyDownloadService;
import com.educare.cop.util.Constants;
import com.educare.cop.util.SettingsManager;
import com.educare.cop.views.RobotoRegularFontTextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public RobotoRegularFontTextView messageTextView, attachmentTextView;
        public RobotoRegularFontTextView messengerTextView;
        public RobotoRegularFontTextView timeTextView;
        public LinearLayout messageContentLayout;
        public RelativeLayout messageRootLayout;
        public CircleImageView messengerImageView;

        public View divider;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (RobotoRegularFontTextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (RobotoRegularFontTextView) itemView.findViewById(R.id.messengerTextView);
            timeTextView = (RobotoRegularFontTextView) itemView.findViewById(R.id.timeTextView);
            messageContentLayout = (LinearLayout) itemView.findViewById(R.id.message_content_layout);
            messageRootLayout = (RelativeLayout) itemView.findViewById(R.id.message_root_layout);
            divider = (View) itemView.findViewById(R.id.divider);

            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            attachmentTextView = (RobotoRegularFontTextView) itemView.findViewById(R.id.attachmentTextView);

        }
    }

    private String userId = null;

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mDownloadReceiver;
    private ProgressDialog mProgressDialog;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;

    // [START declare_ref]
    private StorageReference mStorageRef;

    private Context mContext = null;
    private static final String TAG = "ChatActivity";
    public static String MESSAGES_CHILD = null;
    private static final int REQUEST_INVITE = 1;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;

    private Button mSendButton, attachButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
//    private AdView mAdView;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private GoogleApiClient mGoogleApiClient;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        userId = SettingsManager.getSetting("current_user_id", null, this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {

            if (extras.getString("groupId")!=null && extras.getString("groupName")!=null){
                group = new Group();
                group.setGroupId(extras.getString("groupId"));
                group.setGroupName(extras.getString("groupName"));
                MESSAGES_CHILD = "chatMessages/"+group.getGroupId();
            } else {
                group = (Group) extras.getSerializable(HomeFragment.GROUP_NAME);
                MESSAGES_CHILD = "chatMessages/"+group.getGroupId();
            }

        }

        setupActionBar();
        subscribeToGroupPush();

//        ActionBar ab = getActionBar();
//        android.support.v7.app.ActionBar ab2 = getSupportActionBar();
//        ab2.setTitle(group.getGroupName());

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        onFirebaseDisconnected();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername =  SettingsManager.getSetting("current_user_fullname", "", this);
            mPhotoUrl = SettingsManager.getSetting("current_user_profile_pic", "", this);
        }

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        // Download receiver
        mDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "downloadReceiver:onReceive:" + intent);
                hideProgressDialog();

                if (MyDownloadService.ACTION_COMPLETED.equals(intent.getAction())) {
                    String path = intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH);
                    long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                    // Alert success
                    showMessageDialog("Success", String.format(Locale.getDefault(),
                            "%d bytes downloaded from %s", numBytes, path));
                }

                if (MyDownloadService.ACTION_ERROR.equals(intent.getAction())) {
                    String path = intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH);

                    // Alert failure
                    showMessageDialog("Error", String.format(Locale.getDefault(),
                            "Failed to download from %s", path));
                }
            }
        };


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                        FriendlyMessage.class,
                        R.layout.item_message,
                        MessageViewHolder.class,
                        mFirebaseDatabaseReference.child(MESSAGES_CHILD).orderByChild("creationDate")) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                if (friendlyMessage.getName().equals(SettingsManager.getSetting("current_user_fullname", "", mContext))) {
                    viewHolder.messengerTextView.setVisibility(View.GONE);
                    viewHolder.divider.setVisibility(View.GONE);
                    viewHolder.messageTextView.setTextColor(Color.WHITE);

//                    RelativeLayout.LayoutParams imageViewlayoutParams = new RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.WRAP_CONTENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    imageViewlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//                    viewHolder.messengerImageView.setLayoutParams(imageViewlayoutParams);

                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    viewHolder.messageRootLayout.setLayoutParams(layoutParams);
                    viewHolder.messageContentLayout.setBackgroundResource(R.drawable.round_rect_shape_out);
                } else {

                    viewHolder.messengerTextView.setVisibility(View.VISIBLE);
                    viewHolder.divider.setVisibility(View.VISIBLE);
                    viewHolder.messageTextView.setTextColor(getResources().getColor(R.color.color_cop_app_theme_charcoal_grey));

                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    viewHolder.messageRootLayout.setLayoutParams(layoutParams);
                    viewHolder.messageContentLayout.setBackgroundResource(R.drawable.round_rect_shape_in);
                }

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());

                if (!friendlyMessage.getAttachmentType().equals(Constants.ATTACHMENT_TYPE_NONE)) {
                    viewHolder.attachmentTextView.setText(Html.fromHtml("<a href='"+friendlyMessage.getAttachmentPath()+"'>Show Attachment</a>"));
                    viewHolder.attachmentTextView.setMovementMethod(LinkMovementMethod.getInstance());
                    viewHolder.attachmentTextView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.attachmentTextView.setVisibility(View.GONE);
                }

//                SimpleDateFormat fromUser = new SimpleDateFormat(Constants.DATE_FORMAT_IN_FIREBASE);
                SimpleDateFormat myFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAYED_IN_APP);
                String reformattedStr = null;
                try {
                    if (friendlyMessage.getCreationDateLong()!=null) {

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(friendlyMessage.getCreationDateLong());
                        System.out.println(c.getTime());

                        reformattedStr = myFormat.format(c.getTime());
                        viewHolder.timeTextView.setText(reformattedStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ChatActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                } else {
//                    Toast.makeText(ChatActivity.this, "New messages",
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Initialize and request AdMob ad.
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", new Long(DEFAULT_MSG_LENGTH_LIMIT));

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        attachButton = (Button) findViewById(R.id.attachButton);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();

            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    hideProgressDialog();
                    updateUI(mFirebaseAuth.getCurrentUser());
                    Log.w(TAG, "File URI is null");
                }
            }
        });
    }

//    public void onFirebaseDisconnected(){
//        mFirebaseDatabaseReference.child("users").child(userId).child("isOnline").setValue("true");
//        mFirebaseDatabaseReference.child("users").child(userId).child("isOnline").onDisconnect().setValue("false");
//    }

    @Override
    public void onPause() {
        super.onPause();
//        if (mFirebaseDatabaseReference!=null){
//            mFirebaseDatabaseReference.child("users").child(userId).child("isOnline").setValue("false");
//        }

    }

    @Override
    public void onResume() {
        super.onResume();
//        if (mFirebaseDatabaseReference!=null){
//            mFirebaseDatabaseReference.child("users").child(userId).child("isOnline").setValue("true");
//        }

    }


    private void setupActionBar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(group.getGroupName());
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateUI(mAuth.getCurrentUser());

        // Register download receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mDownloadReceiver, MyDownloadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDownloadReceiver);
    }


    public void subscribeToGroupPush(){
        FirebaseMessaging.getInstance().subscribeToTopic(group.groupId);
        Log.d(TAG, "Subscribed to "+ group.groupId +" topic");
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    // [START upload_from_uri]
    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("photos")
                .child(fileUri.getLastPathSegment());
        // [END get_child_ref]

        // Upload file to Firebase Storage
        // [START_EXCLUDE]
        showProgressDialog();
        // [END_EXCLUDE]
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        updateUI(mFirebaseAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        Toast.makeText(ChatActivity.this, "Error: upload failed",
                                Toast.LENGTH_SHORT).show();
                        updateUI(mFirebaseAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END upload_from_uri]

    @AfterPermissionGranted(RC_STORAGE_PERMS)
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage),
                    RC_STORAGE_PERMS, perm);
            return;
        }

        // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location
        File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
        mFileUri = Uri.fromFile(file);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // Launch intent
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
    }


    private void beginDownload() {
        // Get path
        String path = "photos/" + mFileUri.getLastPathSegment();

        // Kick off download service
        Intent intent = new Intent(this, MyDownloadService.class);
        intent.setAction(MyDownloadService.ACTION_DOWNLOAD);
        intent.putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path);
        startService(intent);

        // Show loading spinner
        showProgressDialog();
    }

    private void updateUI(FirebaseUser user) {

        FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername,
                mPhotoUrl);
        friendlyMessage.setSenderId(SettingsManager.getSetting("current_user_id", null, this));
        friendlyMessage.setGroupId(group.getGroupId());

        if (mDownloadUrl!=null) {
            friendlyMessage.setAttachmentPath(mDownloadUrl.toString());
            friendlyMessage.setAttachmentType(Constants.ATTACHMENT_TYPE_IMAGE);
        }

        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mMessageEditText.setText("");
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
        mDownloadUrl = null;
        mFileUri = null;
        attachButton.setBackgroundColor(Color.RED);

        // Signed in or Signed out
//        if (user != null) {
//            findViewById(R.id.layout_signin).setVisibility(View.GONE);
//            findViewById(R.id.layout_storage).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.layout_signin).setVisibility(View.VISIBLE);
//            findViewById(R.id.layout_storage).setVisibility(View.GONE);
//        }
//
//        // Download URL and Download button
//        if (mDownloadUrl != null) {
//            ((TextView) findViewById(R.id.picture_download_uri))
//                    .setText(mDownloadUrl.toString());
//            findViewById(R.id.layout_download).setVisibility(View.VISIBLE);
//        } else {
//            ((TextView) findViewById(R.id.picture_download_uri))
//                    .setText(null);
//            findViewById(R.id.layout_download).setVisibility(View.GONE);
//        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
//            case R.id.crash_menu:
//                FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");
//                causeCrash();
//                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                mUsername = ANONYMOUS;
                mPhotoUrl = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
//            case R.id.fresh_config_menu:
//                fetchConfig();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " + e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
//                    uploadFromUri(mFileUri);
                    attachButton.setBackgroundColor(Color.BLUE);
                } else {
                    attachButton.setBackgroundColor(Color.RED);
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
        Log.d(TAG, "FML is: " + friendly_msg_length);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}


    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading ...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
