package sagarpreet97.reminder;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddListview_activity extends AppCompatActivity {

    //private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2 ;
    ImageView ivImage ;
    String userChoosenTask;
    Button add , alarm ;
    EditText mtitle , mdesc ;
    String title , desc ;
    byte[] image=null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listview_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mtitle = (EditText) findViewById(R.id.editText);
        mdesc = (EditText) findViewById(R.id.editText2);

        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mtitle.getText().toString();
                desc = mdesc.getText().toString();
                if (image == null) {
                    Toast.makeText(AddListview_activity.this, "Add picture ", Toast.LENGTH_LONG);

                }
                //DbBitmapUtility u=new DbBitmapUtility() ;
                Intent i = new Intent();
                i.putExtra("image", image);
                i.putExtra("title", title);
                i.putExtra("desc", desc);
//                DatabaseHelper helper = new DatabaseHelper(AddListview_activity.this);
//                SQLiteDatabase db = helper.getWritableDatabase();
//
//                    ContentValues cv = new ContentValues();
//                    cv.put(DatabaseHelper.KEY_TITLE, title );
//                    cv.put(DatabaseHelper.KEY_DESC , desc );
//                    //cv.put(CNOpenHelper.BATCH_INSTRUCTOR_NAME, b.getInstructorName());
//                    cv.put(DatabaseHelper.KEY_IMAGE , image );
//
//                    db.insert(DatabaseHelper.DB_TABLE, null, cv);

                setResult(2, i);
                finish();

            }
        });
        Button load = (Button) findViewById(R.id.buttonLoadPicture);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(
//                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
                selectImage();

            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);


        alarm = (Button) findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddListview_activity.this);
                builder.setTitle("Add Date and Time!");
                View view = LayoutInflater.from(AddListview_activity.this).inflate(R.layout.date_and_time_add, null);
                builder.setView(view);
                Button date, time;
                date = (Button) view.findViewById(R.id.date);
                time = (Button) view.findViewById(R.id.time);
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                });
                time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePickerDialog(v);
                    }
                }
                );
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            builder.show() ;
            }

        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
        public void showTimePickerDialog(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            //newFragment.show(getSupportFragmentManager(), "timePicker");
            newFragment.show(getFragmentManager() , "timePicker");
        }

//    public void addEntry( String name, byte[] image) throws SQLiteException {
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues cv = new  ContentValues();
//        cv.put(KEY_TITLE,    name);
//        cv.put(KEY_IMAGE,   image);
//        database.insert( DB_TABLE, null, cv );
//    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddListview_activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AddListview_activity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
//code for deny
                }
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         image = DbBitmapUtility.getBytes(bm) ;
        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         image = DbBitmapUtility.getBytes(thumbnail) ;
        ivImage.setImageBitmap(thumbnail);
    }



}

