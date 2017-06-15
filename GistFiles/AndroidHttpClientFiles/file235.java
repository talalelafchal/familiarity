package software.is.com.icommunity.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import software.is.com.icommunity.AlertDialogManager;
import software.is.com.icommunity.Base64;
import software.is.com.icommunity.IcrmApp;
import software.is.com.icommunity.MainActivity;
import software.is.com.icommunity.PrefManager;
import software.is.com.icommunity.R;
import software.is.com.icommunity.adapter.GroupBasesAdapter;
import software.is.com.icommunity.event.ActivityResultBus;
import software.is.com.icommunity.event.ApiBus;
import software.is.com.icommunity.event.GetGroupReceivedEvent;
import software.is.com.icommunity.event.GetGroupRequestedEvent;
import software.is.com.icommunity.model.PostGroup;

public class PostActivity extends AppCompatActivity implements OnClickListener {
    private Button mTakePhoto, choose_photo;
    private Button btn_upload;
    private ImageView mImageView;
    private static final String TAG = "upload";
    private static int RESULT_LOAD_IMG = 1;
    ArrayList<PostGroup> list = new ArrayList<>();
    EditText et_title, et_conten;
    ListView listView1;
    MyCustomAdapter dataAdapter = null;
    String title;
    String content;
    String userCode;
    String imgProfOwner;
    private Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;
    String ba1 = "";
    String imgOwner;
    MyCustomAdapter myCustomAdapter;
    AlertDialogManager alert = new AlertDialogManager();
    public static String URL = "http://todayissoftware.com/i_community/add_news.php";
    //public static String URL = "http://192.168.1.141/i_community/add_news.php";
    private Toolbar toolbar;
    PrefManager prefManager;
    Bitmap bm;
    String Owner;
    String userPostCode;
    String usernameGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_news_test);
        prefManager = IcrmApp.getPrefManager();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        imgOwner = getIntent().getStringExtra("img_profile_owner");
//        Toast.makeText(PostActivity.this, imgOwner, Toast.LENGTH_SHORT).show();
        userCode = prefManager.userCode().getOr("GEN")
                .substring(prefManager.userCode().getOr("@00000")
                        .lastIndexOf("@") + 1);
        Owner = prefManager.nameGroup().getOr("");
        imgProfOwner = prefManager.imgProfOwner().getOr("ไม่มา");
        Log.e("aaaaaa",imgProfOwner);
        ApiBus.getInstance().postQueue(new GetGroupRequestedEvent("0000017"));
       // Toast.makeText(PostActivity.this, Owner, Toast.LENGTH_SHORT).show();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mTakePhoto = (Button) findViewById(R.id.take_photo);
        choose_photo = (Button) findViewById(R.id.choose_photo);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        mImageView = (ImageView) findViewById(R.id.imageview);
        et_title = (EditText) findViewById(R.id.et_title);
        et_conten = (EditText) findViewById(R.id.et_conten);
        et_conten.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        listView1 = (ListView) findViewById(R.id.listView1);
        mTakePhoto.setOnClickListener(this);
        //Toast.makeText(PostActivity.this, userCode, Toast.LENGTH_SHORT).show();
        choose_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        //et_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("โพสต์หัวข้อ");
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);

        }
        btn_upload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                title = et_title.getText().toString();
                content = et_conten.getText().toString();
                Log.e("ddddd",usernameGroup);

                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setTitle("คุณต้องการโพสข่าวหรือไม่?")
                        .setMessage("หาก ตกลง ระบบจะทำการอัปโหลดข้อมูล")
                        .setNegativeButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (title.trim().length() > 0) {
                                    Log.e("path", "----------------" + picturePath);
                                    Log.e("ฟฟฟฟฟฟ",userCode);
                                    Log.e("zzzzz",usernameGroup);
                                    // Image
                                    if (bm != null) {
                                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                                        byte[] ba = bao.toByteArray();
                                        ba1 = Base64.encodeBytes(ba);

                                        Log.e("base64", "-----" + ba1);
                                    } else {
                                        ba1 = "";
                                    }


                                    // Upload image to server
                                    new uploadToServer().execute();

                                } else {
                                    // user doen't filled that data
                                    // ask him to fill the form
                                    alert.showAlertDialog(PostActivity.this, "ใส่รายละเอียด", "คุณยังไม่ได้ใส่รายละเอียด",false);
                                }

                            }
                        })
                        .setPositiveButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss dialog and open cart
                                dialog.dismiss();

                            }
                        }).create().show();

            }
        });
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        switch (id) {
            case R.id.take_photo:
                takePhoto();
                break;
        }
    }

    private void takePhoto() {
//        if (getApplicationContext().getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_CAMERA)) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent, 100);
//
//
//        } else {
//            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
//        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onActivityResult-------: " + this);
        Bitmap bitmap;
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mImageView.setVisibility(View.VISIBLE);

//            selectedImage = data.getData();
//
//            if (selectedImage != null) {
////                photo = (Bitmap) data.getExtras().get("data");
////
////                // Cursor to get image uri to display
////
////                String[] filePathColumn = {MediaStore.Images.Media.DATA};
////                Cursor cursor = getContentResolver().query(selectedImage,
////                        filePathColumn, null, null, null);
////                cursor.moveToFirst();
////
////                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                picturePath = cursor.getString(columnIndex);
////                cursor.close();
////
////                Bitmap photo = (Bitmap) data.getExtras().get("data");
////                mImageView.setImageBitmap(photo);
//            }

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

                bm =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);

                mImageView.setImageBitmap(bitmap);
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
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            bm = BitmapFactory.decodeFile(picturePath);

        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume--------: " + this);
        ActivityResultBus.getInstance().register(this);
        ApiBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        ActivityResultBus.getInstance().unregister(this);
        ApiBus.getInstance().unregister(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    public class uploadToServer extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd = new ProgressDialog(PostActivity.this);

        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("กรุณารอสักครู่กำลังอัพโหลด...");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", ba1));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            nameValuePairs.add(new BasicNameValuePair("title", title));
            nameValuePairs.add(new BasicNameValuePair("details", content));
            nameValuePairs.add(new BasicNameValuePair("create_date", title));
            nameValuePairs.add(new BasicNameValuePair("myfile", content));
            nameValuePairs.add(new BasicNameValuePair("name_group", usernameGroup));
            nameValuePairs.add(new BasicNameValuePair("image_profile",imgProfOwner));
            Log.e("Create---------", prefManager.picture().getOr(""));
            nameValuePairs.add(new BasicNameValuePair("image_profile",imgOwner));
            //try {
                nameValuePairs.add(new BasicNameValuePair("create_by", Owner));
            Log.e("Owner-----",Owner);
            //}catch (Exception e){
                //Toast.makeText(PostActivity.this, prefManager.userName().getOr(""), Toast.LENGTH_SHORT).show();
            //}
            nameValuePairs.add(new BasicNameValuePair("vendor_code", userCode));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL);
                //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                String st = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);
                Log.e("response", st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";

        }
        int status = 0;
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("result", result);
            pd.hide();
            pd.dismiss();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra("userCode",userCode);
            startActivity(i);
            finish();
        }
    }

    public void onBackPressed(int status) {
        // do nothing.
        //Intent intent = new Intent(SocialPlugIns.this, MainActivity.class);
        //Log.i("Hello", "This is Coomon Log");
        //startActivity(intent);
        //return;
    }

    @Subscribe
    public void GetList(final GetGroupReceivedEvent event) {
        if (event != null) {
            Log.e("event", event.getPost().getPost().get(0).getGroup_name());
            for (int i = 0; i < event.getPost().getPost().size(); i++) {
                list.add(event.getPost());
            }

            dataAdapter = new MyCustomAdapter(this,
                    R.layout.country_info, list);
//            Log.e("BGGG", event.getPost().getBg() + "");
            listView1.setAdapter(dataAdapter);
            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // When clicked, show a toast with the TextView text
                    PostGroup country = (PostGroup) parent.getItemAtPosition(position);

                    Log.e("sssss",usernameGroup);
                    Toast.makeText(getApplicationContext(), country.getPost().get(position).getUser_owner(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    private class MyCustomAdapter extends ArrayAdapter<PostGroup> {

        private ArrayList<PostGroup> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<PostGroup> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<PostGroup>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        PostGroup country = (PostGroup) cb.getTag();
                        usernameGroup = country.getPost().get(position).getUsername();
                        userCode = country.getPost().get(position).getUser_owner();
                        Log.e("aaaa",usernameGroup);
                        country.setSelected(cb.isChecked());

                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            PostGroup country = countryList.get(position);
            holder.name.setText(country.getPost().get(position).getGroup_name());
            holder.name.setChecked(country.isSelected());
            holder.name.setTag(country);

            return convertView;

        }

    }


}