package com.example.picshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class EventPage extends AppCompatActivity {
    public Firebase mRef;
    public ProgressDialog mProgress;
    public String eventName;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public TextView event_name;
    public ImageView img;
    public Button upload_from_memory;
    public Button capture_and_upload;
    public StorageReference mStorage;
    public static final int GALLERY_INTENT = 2;
    public static final int CAMERA_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        mProgress = new ProgressDialog(this);
        upload_from_memory = (Button) findViewById(R.id.upload_from_memory_button);
        capture_and_upload = (Button)findViewById(R.id.capture_and_upload_button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        Firebase.setAndroidContext(this);

        event_name = (TextView)findViewById(R.id.eventTextView);
        eventName = sharedPreferences.getString("event", "").toString();
        event_name.setText(eventName);
        mStorage = FirebaseStorage.getInstance().getReference();

        upload_from_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_INTENT);
            }
        });

        capture_and_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, CAMERA_REQUEST_CODE);
            }
        });

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            final Uri uri = data.getData();
            final StorageReference filepath = mStorage.child(eventName).child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mRef = new Firebase("https://startuppfirebase.firebaseio.com/albums/");
                    mRef.child(eventName).child(uri.getLastPathSegment().toString()).setValue(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(EventPage.this, "upload done", Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){

            final Uri uri = data.getData();
            final StorageReference filepath = mStorage.child(eventName).child(uri.getLastPathSegment());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mRef = new Firebase("https://startuppfirebase.firebaseio.com/albums/");
                    mRef.child(eventName).child(uri.getLastPathSegment().toString()).setValue(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(EventPage.this, "upload done", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
