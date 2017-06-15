package com.gpi.ratethis;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Rating extends Activity {
    static final int TAKE_PICTURE_REQUEST_CODE = 1010;

    Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        
        setupViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case TAKE_PICTURE_REQUEST_CODE:
                handleTakePictureResult(resultCode, intent);
                break;
        }
    }

    private void handleTakePictureResult(int resultCode, Intent resultIntent) {
        if(resultCode == RESULT_OK) {
            String photoPathName = mPhotoUri.getPath();


        }
        else {
            Toast.makeText(this, "User Canceled", Toast.LENGTH_LONG).show();
        }
    }

    private void setupViews() {
        Button takePicture = (Button) findViewById(R.id.takePictureButton);
        
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakePictureClick((Button)view);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    void onTakePictureClick(Button view) {
        Toast.makeText(this, "Picture Taken", Toast.LENGTH_LONG).show();

        mPhotoUri = PhotoHelper.generateTimeStampPhotoFileUri();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.rating, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
