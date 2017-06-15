package com.dipoletech.unnmobile;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.dipoletech.unnmobile.adapters.CroppingOptionAdapter;
import com.dipoletech.unnmobile.adapters.SelectFileOptionAdapter;
import com.dipoletech.unnmobile.model.CroppingOption;
import com.dipoletech.unnmobile.model.FileOptions;
import com.dipoletech.unnmobile.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileChooser extends AppCompatActivity {
    private int CAMERA_CODE = 100;
    private Uri mAttachmentUri;
    private int CROPPING_CODE = 101;
    private int GALLERY_CODE = 102;
    private File outputFile, file;
    private String TAG = FileChooser.class.getSimpleName();
    boolean crop  = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the intent
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            crop = bundle.getBoolean(Utility.CROP);
        }

        //give the activity a simply transparent or translucent background.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //init the file
        outputFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        //call the select file option method
        selectFileOptions();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCELED)
        {
            setResult(RESULT_CANCELED, null);
            finish();

        }else if(requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {
            mAttachmentUri = data.getData();
            System.out.println("Gallery Image URI : " + mAttachmentUri);
            if (crop) {
                cropImage();
            }else {
                setResultAndExit(GALLERY_CODE);
            }

        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {

            System.out.println("Camera Image URI : " + mAttachmentUri);
            if (crop) {
                cropImage();
            }else {
                setResultAndExit(CAMERA_CODE);
            }
        } else if (requestCode == CROPPING_CODE) {
            //here i shall set the result and finish the activity
            setResultAndExit(CROPPING_CODE);

        }
    }

    private void setResultAndExit(int code) {
        try {
//            Log.v(TAG, "Output File Length: " + outputFile.length());
            if (outputFile.exists() && outputFile.length()!=0 && code == CROPPING_CODE) {
                Uri imageUri = Uri.parse(outputFile.getPath());
                // here i want to return the bitmap
                Log.v(TAG, "Uri From Output File: "+imageUri.toString());
                Intent rIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Utility.IMAGE_PARCEL_URL, imageUri.toString());
                rIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, rIntent);
                finish();
            } else if (mAttachmentUri!=null && code == CAMERA_CODE){
                Uri imageUri = Uri.parse(mAttachmentUri.getPath());
                // here i want to return the bitmap
                Log.v(TAG, "Attachment Uri Camera Code: "+ imageUri.toString());
                Intent rIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Utility.IMAGE_PARCEL_URL, imageUri.toString());
                rIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, rIntent);
                finish();

            }else if(mAttachmentUri!=null && code==GALLERY_CODE){
                // here i want to return the bitmap
                Log.v(TAG, "Attachment Uri Path: " + getRealPathFromUri(this, mAttachmentUri));
                Intent rIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Utility.IMAGE_PARCEL_URL, getRealPathFromUri(this, mAttachmentUri));
                rIntent.putExtras(bundle);
                setResult(Activity.RESULT_OK, rIntent);
                finish();
            }else {
                Toast.makeText(this, "Error while saving image: File Not Found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void selectFileOptions() {
        final ArrayList<FileOptions> options = new ArrayList<>();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(), "temp1.jpg");
        mAttachmentUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mAttachmentUri);
        FileOptions options1 = new FileOptions("Capture Photo Now", ContextCompat.getDrawable(this, R.drawable.ic_action_camera), intent);

        //second option
        FileOptions options2 = new FileOptions("Choose Image from Gallery", ContextCompat.getDrawable(this, R.drawable.ic_action_folder_open),
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        FileOptions options3 = new FileOptions("                          Cancel", null, null);
        options.add(options1);
        options.add(options2);
        options.add(options3);

        SelectFileOptionAdapter adapter = new SelectFileOptionAdapter(this, options);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("                 Add A File!");

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                      startActivityForResult(options.get(i).appIntent, CAMERA_CODE);

                        break;
                    case 1:
                        startActivityForResult(options.get(i).appIntent, GALLERY_CODE);

                        break;
                    case 2:
                        dialogInterface.dismiss();
                        setResult(RESULT_CANCELED, null);
                        finish();
                        break;
                }

            }
        });

        builder.show();
    }


    //used to crop the images wen the size is much
    private void cropImage() {

        AlertDialog.Builder builder = null;

        final ArrayList<CroppingOption> cropOptions = new ArrayList();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Can't find image cropping app", Toast.LENGTH_SHORT).show();

        } else {
            intent.setData(mAttachmentUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 700);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));

            if (size == 1) {
                Intent intent1 = new Intent(intent);
                ResolveInfo res = list.get(0);

                intent1.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

               startActivityForResult(intent1, CROPPING_CODE);

            } else {
                for (ResolveInfo res : list) {
                    final CroppingOption co = new CroppingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CroppingOptionAdapter adapter = new CroppingOptionAdapter(getApplicationContext(), cropOptions);

                builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Cropping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        startActivityForResult(cropOptions.get(item).appIntent, CROPPING_CODE);

                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mAttachmentUri != null) {
                            getContentResolver().delete(mAttachmentUri, null, null);
                            mAttachmentUri = null;
                        }
                    }
                });

                builder.create();
            }
        }

        if (builder != null) {
            builder.show();
        }
    }//end cropping function


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor != null ? cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) : 0;
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor != null ? cursor.getString(column_index) : null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
