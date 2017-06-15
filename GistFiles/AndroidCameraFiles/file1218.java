import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.bewei.flipcard.R;
import id.bewei.flipcard.database.UserDAO;
import id.bewei.flipcard.image.CircleTransform;
import id.bewei.flipcard.model.FCUser;
import id.bewei.flipcard.rest.FlipCardClient;
import id.bewei.flipcard.session.SessionManager;

public class EditProfileFragment extends Fragment implements OnClickListener {

    public static final String TAG = "EditProfileFragment";
    public static final String NO_INTERNET_CONNECTION = "No Internet Connection";
    public static final String DELIMITER = "||";
    public static final String DELIMITER_ESCAPED = "\\|\\|";
    private static final Integer SELECT_PROFILE_IMAGE = 100;
    private static final Integer TAKE_PROFILE_IMAGE = 101;
    private static final Integer SELECT_BACKGROUND_IMAGE = 200;
    private static final Integer TAKE_BACKGROUND_IMAGE = 201;
    private static final Integer CROP_PROFILE_PICTURE = 300;

    private String textAppName;
    private String textDiscardChangesConfirmation;
    private String textYes;
    private String textNo;
    private String textSelectImage;
    private String textSelectFromGallery;
    private String textTakeFromCamera;

    private ProgressDialog mProgressDialog;

    private ImageView mImageViewProfilePicture;
    private ImageView mImageViewCardBackground;
    private Button mButtonChangePicture;
    private Button mButtonChangeBackground;
    private MaterialEditText mEditTextName;
    private MaterialEditText mEditTextEmail;
    private MaterialEditText mEditTextPhone;
    private MaterialEditText mEditTextSecondaryEmail;
    private MaterialEditText mEditTextSecondaryPhone;
    private MaterialEditText mEditTextTwitter;
    private MaterialEditText mEditTextFacebook;
    private MaterialEditText mEditTextLinkedIn;
    private MaterialEditText mEditTextCompanyName;
    private MaterialEditText mEditTextCompanyEmail;
    private MaterialEditText mEditTextCompanyPhone;
    private MaterialEditText mEditTextCompanyFax;
    private MaterialEditText mEditTextCompanyAddress;
    private MaterialEditText mEditTextCompanyWebsite;
    private Button mButtonSave;
    private Button mButtonCancel;

    private SessionManager session;
    private UpdateProfileAsyncTask updateProfileAsyncTask;
    private UserDAO userDAO;
    private FCUser user;
    private File profileImage;
    private File backgroundImage;
    private Boolean isProfileChanged = false;
    private Boolean isPictureChanged = false;
    private Boolean isBackgroundChanged = false;

    private View rootView;
    private Context mContext;
    private Uri mCapturedProfileImageURI;
    private Uri mCapturedBackgroundImageURI;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mContext = getActivity().getApplicationContext();

        // Session Manager
        session = new SessionManager(mContext);

        // Get texts from Resources
        loadTexts();

        loadViews();

        setListeners();

        loadProfile();

        return rootView;
    }

    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.edit_profile_btn_change_picture:
                chooseProfilePicture();
                break;
            case R.id.edit_profile_btn_change_background:
                chooseBackgroundPicture();
                break;
            case R.id.edit_profile_btn_save:
                saveProfile();
                break;
            case R.id.edit_profile_btn_cancel:
                cancelEdit();
        }
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_edit_profile) {
			Log.v("Action Menu", "Action Edit Profile");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if ( requestCode == SELECT_PROFILE_IMAGE ) {

            if (intent != null) {
                Cursor cursor = getActivity().getContentResolver().query(intent.getData(), null, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                String fileSrc = cursor.getString(columnIndex);

                Bitmap bitmapPreview = BitmapFactory.decodeFile(fileSrc);

//                Drawable drawable = Drawable.createFromPath(fileSrc);
//                mImageViewProfilePicture.setBackground(drawable);

                mImageViewProfilePicture.setImageBitmap(bitmapPreview);

                profileImage = new File(fileSrc);

                cursor.close();

                isPictureChanged = true;
            }

        }

        else if ( requestCode == TAKE_PROFILE_IMAGE ) {

//            String capturedImagePath = getRealPathFromURI(mCapturedProfileImageURI);
            performCrop(mCapturedProfileImageURI);

        }

        else if ( requestCode == SELECT_BACKGROUND_IMAGE ) {

            if (intent != null) {
                Cursor cursor = getActivity().getContentResolver().query(intent.getData(), null, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                String fileSrc = cursor.getString(columnIndex);

                Bitmap bitmapPreview = BitmapFactory.decodeFile(fileSrc);

                mImageViewCardBackground.setImageBitmap(bitmapPreview);

                backgroundImage = new File(fileSrc);

                cursor.close();

                isBackgroundChanged = true;
            }

        }

        else if ( requestCode == TAKE_BACKGROUND_IMAGE ) {

            // First Method
            String capturedImagePath = getRealPathFromURI(mCapturedBackgroundImageURI);

            Log.d(TAG, "Captured Bgr Image Path : " + capturedImagePath);

            backgroundImage = new File(capturedImagePath);

            Bitmap backgroundBitmap = BitmapFactory.decodeFile(capturedImagePath);

            mImageViewCardBackground.setImageBitmap(backgroundBitmap);

            // Second Method

//            if (intent != null) {
//                Cursor cursor = getActivity().getContentResolver().query(intent.getData(), null, null, null, null);
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//
//                String fileSrc = cursor.getString(columnIndex);
//
//                Log.d(TAG, "File Source : " + fileSrc);
//
//                Bitmap bitmapPreview = BitmapFactory.decodeFile(fileSrc);
//
//                mImageViewCardBackground.setImageBitmap(bitmapPreview);
//
//                backgroundImage = new File(fileSrc);
//
//                cursor.close();
//
//                isBackgroundChanged = true;
//            }

        }

        else if ( requestCode == CROP_PROFILE_PICTURE ) {

            if (intent != null) {
                Cursor cursor = getActivity().getContentResolver().query(intent.getData(), null, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                String fileSrc = cursor.getString(columnIndex);

                Bitmap bitmapPreview = BitmapFactory.decodeFile(fileSrc);

                mImageViewProfilePicture.setImageBitmap(bitmapPreview);

                profileImage = new File(fileSrc);

                cursor.close();

                isPictureChanged = true;
            }

        }
    }

    /**
     * This method is used to get real path of file from from uri
     *
     * @param contentUri <description>The Content URI</description>
     * @return String
     */
    //----------------------------------------
    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadTexts() {
        textAppName = mContext.getString(R.string.app_name);
        textDiscardChangesConfirmation = mContext.getString(R.string.discard_changes_confirmation);
        textYes = mContext.getString(R.string.yes);
        textNo = mContext.getString(R.string.no);
        textSelectImage = mContext.getString(R.string.select_image);
        textTakeFromCamera = mContext.getString(R.string.take_from_camera);
        textSelectFromGallery = mContext.getString(R.string.select_from_gallery);
    }

    private void loadViews() {
        mImageViewProfilePicture    = (ImageView) rootView.findViewById(R.id.edit_profile_picture);
        mImageViewCardBackground    = (ImageView) rootView.findViewById(R.id.edit_profile_background);
        mButtonChangePicture        = (Button) rootView.findViewById(R.id.edit_profile_btn_change_picture);
        mButtonChangeBackground     = (Button) rootView.findViewById(R.id.edit_profile_btn_change_background);
        mEditTextName               = (MaterialEditText) rootView.findViewById(R.id.edit_profile_name);
        mEditTextEmail              = (MaterialEditText) rootView.findViewById(R.id.edit_profile_primary_email);
        mEditTextSecondaryEmail     = (MaterialEditText) rootView.findViewById(R.id.edit_profile_secondary_email);
        mEditTextPhone              = (MaterialEditText) rootView.findViewById(R.id.edit_profile_phone);
        mEditTextSecondaryPhone     = (MaterialEditText) rootView.findViewById(R.id.edit_profile_secondary_phone);
        mEditTextTwitter            = (MaterialEditText) rootView.findViewById(R.id.edit_profile_twitter);
        mEditTextFacebook           = (MaterialEditText) rootView.findViewById(R.id.edit_profile_facebook);
        mEditTextLinkedIn           = (MaterialEditText) rootView.findViewById(R.id.edit_profile_linkedin);
        mEditTextCompanyName        = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_name);
        mEditTextCompanyEmail       = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_email);
        mEditTextCompanyPhone       = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_phone);
        mEditTextCompanyFax         = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_fax);
        mEditTextCompanyAddress     = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_address);
        mEditTextCompanyWebsite     = (MaterialEditText) rootView.findViewById(R.id.edit_profile_company_website);
        mButtonSave                 = (Button) rootView.findViewById(R.id.edit_profile_btn_save);
        mButtonCancel               = (Button) rootView.findViewById(R.id.edit_profile_btn_cancel);
    }

    private void setListeners() {
        mButtonChangePicture.setOnClickListener(this);
        mButtonChangeBackground.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
    }

    private void loadProfile() {

        Bundle args = getArguments();
        user = args.getParcelable(ProfileFragment.KEY_USER);

        String httpString = "https://";
        String imageUrl = "";
        String backgroundUrl = "";

        if ( user.getImageUrl().contains(httpString) ) {
            imageUrl = user.getImageUrl();
        }
        else {
            imageUrl = FlipCardClient.BASE_URL_SLASH + user.getImageUrl();
        }

        if ( user.getBackgroundUrl().contains(httpString) ) {
            backgroundUrl = user.getBackgroundUrl();
        }
        else {
            backgroundUrl = FlipCardClient.BASE_URL_SLASH + user.getBackgroundUrl();
        }

        CircleTransform circleTransform = new CircleTransform();

        if (isPictureChanged) {
            // Load profile picture image
            Picasso.with(mContext)
                    .load(profileImage)
                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(circleTransform)
                    .into(mImageViewProfilePicture);
        }
        else {
            // Load profile picture image
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_account_circle_black_48dp)
                    .resize(250, 250)
                    .centerCrop()
                    .transform(circleTransform)
                    .into(mImageViewProfilePicture);
        }

        if (isBackgroundChanged) {
            // Load background picture image
            Picasso.with(mContext)
                    .load(backgroundImage)
                    .into(mImageViewCardBackground);
        }
        else {
            // Load background picture image
            Picasso.with(mContext)
                    .load(backgroundUrl)
                    .into(mImageViewCardBackground);
        }

        String unParsedEmailAddress = user.getEmailAddress();
        String[] emailAddress = unParsedEmailAddress.split(DELIMITER_ESCAPED);

        Log.v(TAG, "Length Email Address : " + emailAddress.length);

        String unParsedPhone = user.getPhone();
        String[] phones = unParsedPhone.split(DELIMITER_ESCAPED);

        mEditTextName.setText(user.getName());
        mEditTextEmail.setText(emailAddress[0]);
        Log.v(TAG, "Email Address : " + emailAddress[0]);

        if (emailAddress.length > 1) {
            mEditTextSecondaryEmail.setText(emailAddress[1]);
        }

        if (phones.length > 0) {
            mEditTextPhone.setText(phones[0]);
        }

        if (phones.length > 1) {
            mEditTextSecondaryPhone.setText(phones[1]);
        }

        mEditTextTwitter.setText(user.getTwitter());
        mEditTextFacebook.setText(user.getFacebook());
        mEditTextLinkedIn.setText(user.getLinkedIn());
        mEditTextCompanyName.setText(user.getCompanyName());
        mEditTextCompanyEmail.setText(user.getCompanyEmail());
        mEditTextCompanyPhone.setText(user.getCompanyPhone());
        mEditTextCompanyFax.setText(user.getCompanyFax());
        mEditTextCompanyAddress.setText(user.getCompanyAddress());
        mEditTextCompanyWebsite.setText(user.getCompanyWebsite());

    }

    private void cancelEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(textAppName);
        builder.setMessage(textDiscardChangesConfirmation);
        builder.setNegativeButton(textNo, null);
        builder.setPositiveButton(textYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        builder.create();
        builder.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showSnackbars(String message) {
        SnackbarManager.show(
                Snackbar.with(mContext)
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .swipeToDismiss(true)
                        .text(message), this.getActivity());
    }

    private void chooseProfilePicture() {
        String[] items = new String[] { textTakeFromCamera, textSelectFromGallery};
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(textSelectImage);
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                // Take from camera
                if (item == 0) {
                    takeProfilePictureFromCamera();
                }
                // Pick from gallery
                else {
                    chooseProfilePictureFromGallery();
                }
            }
        });
        builder.create();
        builder.show();

//        new MaterialDialog.Builder(getActivity())
//                .title(textSelectImage)
//                .items(items)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                        // Take from camera
//                        if (which == 0) {
//                            takeProfilePictureFromCamera();
//                        }
//                        // Pick from gallery
//                        else {
//                            chooseProfilePictureFromGallery();
//                        }
//                    }
//                })
//                .show();

    }

    // Take Profile Picture with Camera
    private void takeProfilePictureFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "flipcard_picture_temp");

        mCapturedProfileImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedProfileImageURI);

        startActivityForResult(photoPickerIntent, TAKE_PROFILE_IMAGE);
    }

    // Choose Profile Picture from Gallery and Crop
    private void chooseProfilePictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("aspectX", 1);
        photoPickerIntent.putExtra("aspectY", 1);
//        photoPickerIntent.putExtra("outputX", 200);
//        photoPickerIntent.putExtra("outputY", 200);

        try {
            startActivityForResult(photoPickerIntent, SELECT_PROFILE_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void chooseBackgroundPicture() {
        String[] items = new String[] { textTakeFromCamera, textSelectFromGallery};
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(textSelectImage);
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                // Take from camera
                if (item == 0) {
                    takeBackgoundPictureFromCamera();
                }
                // Pick from gallery
                else {
                    chooseBackgroundPictureFromGallery();
                }
            }
        });
        builder.create();
        builder.show();
    }

    // Take Background Picture from Camera
    private void takeBackgoundPictureFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "flipcard_background_temp");

        mCapturedBackgroundImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent photoPickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedBackgroundImageURI);
//        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        startActivityForResult(photoPickerIntent, TAKE_BACKGROUND_IMAGE);
    }

    // Choose Background Picture from Gallery and Crop
    private void chooseBackgroundPictureFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");

        startActivityForResult(photoPickerIntent, SELECT_BACKGROUND_IMAGE);
    }

    private void performCrop(Uri picUri) {
        try {
            int aspectX = 1;
            int aspectY = 1;

            String outputUriString = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(picUri, "image/*");
            intent.putExtra("scale", "true");
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("scaleUpIfNeeded", true);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUriString);

            startActivityForResult(intent, CROP_PROFILE_PICTURE);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop action!";
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        if ( isNetworkAvailable() ) {
            updateProfileAsyncTask = new UpdateProfileAsyncTask();

            Map map = buildMapParam();

            updateProfileAsyncTask.execute(map);
        }
        else {
            showSnackbars(NO_INTERNET_CONNECTION);
        }
    }

    private Map buildMapParam() {
        updateUser();
        Map<String, Object> map = new HashMap<>();
        map.put(UpdateProfileAsyncTask.KEY_USER, user);

        if (isPictureChanged) {
            map.put(UpdateProfileAsyncTask.KEY_IMAGE, profileImage);
        }

        if (isBackgroundChanged) {
            map.put(UpdateProfileAsyncTask.KEY_BACKGROUND, backgroundImage);
        }

        return map;
    }

    private void updateUser() {
        user.setName(mEditTextName.getText().toString());

        String emailAddress = mEditTextEmail.getText().toString() + DELIMITER + mEditTextSecondaryEmail.getText().toString();
        user.setEmailAddress(emailAddress);

        String phone = mEditTextPhone.getText().toString() + DELIMITER + mEditTextSecondaryPhone.getText().toString();
        user.setPhone(phone);

        user.setTwitter(mEditTextTwitter.getText().toString());
        user.setFacebook(mEditTextFacebook.getText().toString());
        user.setLinkedIn(mEditTextLinkedIn.getText().toString());
        user.setCompanyName(mEditTextCompanyName.getText().toString());
        user.setCompanyEmail(mEditTextCompanyEmail.getText().toString());
        user.setCompanyPhone(mEditTextCompanyPhone.getText().toString());
        user.setCompanyFax(mEditTextCompanyFax.getText().toString());
        user.setCompanyAddress(mEditTextCompanyAddress.getText().toString());
        user.setCompanyWebsite(mEditTextCompanyWebsite.getText().toString());
    }

    private class UpdateProfileAsyncTask extends AsyncTask<Map, Void, String> {

        public static final String TAG = "UploadImageAsyncTask";
        public static final String KEY_USER = "user";
        public static final String KEY_USER_ID = "userid";
        public static final String KEY_PROFILE = "profile";
        public static final String KEY_IMAGE = "image";
        public static final String KEY_BACKGROUND = "background";
        public static final String KEY_RESPONSE_ERROR = "error";
        public static final String KEY_RESPONSE_MESSAGE = "message";
        public static final String KEY_RESPONSE_USER = "user";

        @Override
        protected String doInBackground(Map... maps) {
            return updateProfile(maps[0]);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "Updating profile...");
        }

        @Override
        protected void onPostExecute(String response) {
            Log.v(TAG, "Response : " + response);

            try {
                JSONObject responseJSON = new JSONObject(response);

                if (responseJSON.getInt(KEY_RESPONSE_ERROR) == 0) {

                    user = new FCUser(responseJSON.getJSONObject(KEY_RESPONSE_USER));

                    session.createLoginSession(user);

                    UserDAO userDAO = new UserDAO(mContext);
                    userDAO.updateUser(user);
                    userDAO.close();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();

            // Finish the activity
            getActivity().finish();
        }

        private String updateProfile(Map map) {

            File image = (File) map.get(KEY_IMAGE);
            File background = (File) map.get(KEY_BACKGROUND);
            FCUser user = (FCUser) map.get(KEY_USER);

            String userJSONString = user.toJSONUpdate().toString();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(FlipCardClient.URL_API_PROFILE);
            HttpResponse httpResponse;
            HttpEntity httpEntity;
            String response = "";

            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

                // Image
                if (image != null) {
                    FileBody fileBodyImage = new FileBody(image);
                    builder.addPart(KEY_IMAGE, fileBodyImage);
                }

                // Background
                if (background != null) {
                    FileBody fileBodyBackground = new FileBody(background);
                    builder.addPart(KEY_BACKGROUND, fileBodyBackground);
                }

                // User ID
                String userId = String.valueOf( user.getId() );
                builder.addTextBody(KEY_USER_ID, userId, ContentType.TEXT_PLAIN);
//                Log.v(TAG, "User ID : " + userId);
//                StringBody stringBodyUserId = new StringBody( userId, ContentType.MULTIPART_FORM_DATA );
//                builder.addPart(KEY_USER_ID, stringBodyUserId);

                // User Profile JSON String
                builder.addTextBody(KEY_PROFILE, userJSONString, ContentType.TEXT_PLAIN);
//                Log.v(TAG, "User JSON String : " + userJSONString);
//                StringBody stringBodyProfileJSON = new StringBody( userJSONString, ContentType.MULTIPART_FORM_DATA );
//                builder.addPart(KEY_PROFILE, stringBodyProfileJSON);

                HttpEntity entity = builder.build();

                httpPost.setEntity(entity);

                httpResponse = httpClient.execute(httpPost);

                httpEntity = httpResponse.getEntity();

                response = EntityUtils.toString(httpEntity);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

    }
}
