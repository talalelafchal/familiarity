package software.is.com.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gcm.GCMRegistrar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import software.is.com.myapplication.AlertDialogManager;
import software.is.com.myapplication.ConnectionDetector;
import software.is.com.myapplication.IcrmApp;
import software.is.com.myapplication.MainActivity;
import software.is.com.myapplication.PrefManager;
import software.is.com.myapplication.R;
import software.is.com.myapplication.RoundedTransformation;
import software.is.com.myapplication.ServerUtilities;
import software.is.com.myapplication.WakeLocker;

import static software.is.com.myapplication.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static software.is.com.myapplication.CommonUtilities.EXTRA_MESSAGE;
import static software.is.com.myapplication.CommonUtilities.SENDER_ID;

public class RegisterActivity extends Activity {
    Button btn_signup;

    PrefManager pref;
    EditText input_name;
    EditText input_email;
    EditText input_password;
    EditText input_invite;
    ImageView img_avatar;

    Bitmap bm;
    String picturePath;
    String username;
    String email;
    String password;
    String invite;
    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;
    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();
    Dialog loadingDialog;
    // Connection detector
    ConnectionDetector cd;
    String regId;
    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        input_name = (EditText) findViewById(R.id.input_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        pref = IcrmApp.getPrefManager();
        loadingDialog = new Dialog(RegisterActivity.this, R.style.FullHeightDialog);
        loadingDialog.setContentView(R.layout.dialog_loading);
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);


        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        regId = GCMRegistrar.getRegistrationId(this);

        Log.e("aaaa", regId + "");
        btn_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfile();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Bitmap bitmap;
        if (requestCode == 100 && resultCode == RESULT_OK) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;

                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_LONG).show();

                return;

            }

            try {

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

                bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

                img_avatar.setImageBitmap(bitmap);

                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                && null != data) {
            // Get the Image from data

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            img_avatar.setVisibility(View.VISIBLE);
            img_avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            bm = BitmapFactory.decodeFile(picturePath);

        }
    }

    private void uploadProfile() {

        email = input_email.getText().toString();
        username = input_name.getText().toString();
        password = input_password.getText().toString();
        loadingDialog.show();

        cd = new ConnectionDetector(getApplicationContext());

        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(RegisterActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }



        pref.email().put(email);
        pref.passWord().put(password);
        pref.name().put(username);
        pref.token().put(regId);
        pref.isLogin().put(true);
        pref.commit();

        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Toast.makeText(getApplicationContext(), "เข้าสู่ระบบ", Toast.LENGTH_LONG).show();
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                Toast.makeText(getApplicationContext(), "กรุณาต่ออินเทอร์เน็ต", Toast.LENGTH_SHORT).show();
            }

        }

        final Context context = this;
        mRegisterTask = new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user
                ServerUtilities.register(context, username, email, regId);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mRegisterTask = null;
            }

        };
        mRegisterTask.execute(null, null, null);


        Charset chars = Charset.forName("UTF-8");
        String url = "http://todayissoftware.com//i_community/register_gcm.php";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", email);
        params.put("regId", regId);
        params.put("vendor_code","null");

        AQuery aq = new AQuery(getApplication());
        aq.ajax(url, params, JSONObject.class, this, "registerCb");
    }

    public void registerCb(String url, JSONObject json, AjaxStatus status)
            throws JSONException {
        Log.e("return", json.toString(4));

        int success = json.getInt("success");
        Log.e("ddd", success + "");
        String name;
        String email;
        String code;
        JSONArray jsonArray = json.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            Log.e("ddddd",explrObject+"");
            name = explrObject.optString("nameth");
            email = explrObject.optString("email");
            code = explrObject.optString("code");
            Log.e("name",name);
            Log.e("email",email);
            Log.e("code",code);
        }

        if (success == 0) {
//            Toast.makeText(getApplicationContext(), "กรอก pass หรือ Password ผิด", Toast.LENGTH_SHORT).show();
        }
        if (success == 1) {
            loadingDialog.dismiss();
            Toast.makeText(getApplicationContext(), "เข้าสู่รับบสำเร็จ", Toast.LENGTH_SHORT).show();
            Intent intentMain = new Intent(getApplicationContext().getApplicationContext(), MainActivity.class);
            startActivity(intentMain);
            finish();
            pref.isLogin().put(true);
            pref.commit();

        }


    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message

            //Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();


            // Releasing wake lock
            WakeLocker.release();
        }
    };

    public void selectAvatar() {
        final CharSequence[] items = {"Choose from Gallery", "Take from Camera",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        builder.setTitle("Update avatar");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Gallery")) {
                    pickImage();
                } else if (items[item].equals("Take from Camera")) {
                    pickCamera();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void pickImage() {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void pickCamera() {
        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(f));

        startActivityForResult(intent,
                100);
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("Receiver Error", e.getMessage());
        }
        super.onDestroy();
    }
}