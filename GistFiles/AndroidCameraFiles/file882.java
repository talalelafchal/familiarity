package com.example.fm;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by MKSoft01 on 10/17/13.
 */
public class DriverManagement extends Activity {

    final int PIC_CROP = 2;
    //captured picture uri
    private Uri picUri;
    final int CAMERA_CAPTURE = 1;
    Button AddDriver,SaveDriver, Cancel;
    EditText ManDriDriName,ManDriDriType,ManDriPhNo,ManDriAddr,ManDriLic,ManDriLicEndDate,ManDriInsu;



    ImageView DriverPhoto;
    AnandDB db;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_drivers);

        db=new AnandDB(this);
        db.open();
        AddDriver=(Button)findViewById(R.id.ManDribtnNewD);
DriverPhoto=(ImageView)findViewById(R.id.manDriImVDriver);
        SaveDriver=(Button)findViewById(R.id.manDriBtnSave);
        Cancel=(Button)findViewById(R.id.manDriBtnCancel);

       ManDriDriName=(EditText)findViewById(R.id.ManDriDriName);
        ManDriDriType=(EditText)findViewById(R.id.ManDriDriType);
        ManDriPhNo=(EditText)findViewById(R.id.ManDriPhNo);
        ManDriAddr=(EditText)findViewById(R.id.ManDriAddr);
        ManDriLic=(EditText)findViewById(R.id.ManDriLic);
        ManDriLicEndDate=(EditText)findViewById(R.id.ManDriLicEndDate);
        ManDriInsu=(EditText)findViewById(R.id.ManDriInsu);

        SaveDriver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // Get the email and password and save it to Database into a seperate table (for retrieval)

                String DriverName=ManDriDriName.getText().toString();
                String DriverType=ManDriDriType .getText().toString();
                String DriverPhNo=ManDriPhNo.getText().toString();
                String DriverAddress= ManDriAddr.getText().toString();
                String DriverLicenseNo= ManDriLic.getText().toString();
                String DriverLicExpiryDate= ManDriLicEndDate.getText().toString();
                String DriverInsurance= ManDriInsu.getText().toString();


                if(DriverName.equals("") || DriverType.equals("") || DriverPhNo.equals("") )
                {
                    Toast.makeText(getApplicationContext(), "Fields Cannot be Empty", 0).show();
                }
                else
                {
                    ContentValues cv=new ContentValues();
                    cv.put("KEY_Driver_Name", DriverName);
                    cv.put("KEY_Driver_Type", DriverType);
                    cv.put("KEY_Driver_Phone", DriverPhNo);
                    cv.put("KEY_Driver_Address", DriverAddress);
                    cv.put("KEY_Driver_LicenseNo", DriverLicenseNo);
                    cv.put("KEY_Driver_LicenseEnd", DriverLicExpiryDate);
                    cv.put("KEY_Driver_Insurance", DriverInsurance);
                    db.Insertointotable(cv, AnandDB.T_ManageDriver);
                    Intent intent=new Intent(getApplicationContext(),Manage.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Data Saved Successfull", 0).show();

                }
                }
        });
        DriverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
//                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
        );


    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            //user is returning from capturing an image using the `camera
//            if(requestCode == CAMERA_CAPTURE){
//                //get the Uri for the captured image
//                picUri = data.getData();
//                //carry out the crop operation
//                performCrop();
//            }
//            //user is returning from cropping the image
//            else if(requestCode == PIC_CROP){
//                //get the returned data
//                Bundle extras = data.getExtras();
//                //get the cropped bitmap
//                Bitmap thePic = extras.getParcelable("data");
//                //retrieve a reference to the ImageView
//                ImageView picView = (ImageView)findViewById(R.id.manDriImVDriver);
//                //display the returned cropped image
//                picView.setImageBitmap(thePic);
//            }
//        }
//    }

    /**
     * Helper method to carry out crop operation
     */
//    private void performCrop(){
//        //take care of exceptions
//        try {
//            //call the standard crop action intent (the user device may not support it)
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            //indicate image type and Uri
//            cropIntent.setDataAndType(picUri, "image/*");
//            //set crop properties
//            cropIntent.putExtra("crop", "true");
//            //indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
//            //indicate output X and Y
//            cropIntent.putExtra("outputX", 256);
//            cropIntent.putExtra("outputY", 256);
//            //retrieve data on return
//            cropIntent.putExtra("return-data", true);
//            //start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, PIC_CROP);
//        }
//        //respond to users whose devices do not support the crop action
//        catch(ActivityNotFoundException anfe){
//            //display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }
            }