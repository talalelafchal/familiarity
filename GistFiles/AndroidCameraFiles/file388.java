package com.example.eng_mohamedbakr.connectingwithfirebase;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    private StorageReference mStorage;
    private Button mSelectImage;
    private static final int GALLARY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectImage = (Button) findViewById(R.id.selectimage);
        mStorage = FirebaseStorage.getInstance().getReference();
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLARY_INTENT);


            }
        });
    }

    @Override

    protected  void  onActivityResult(int requestCode,int resultcode,Intent data){

        super.onActivityResult(requestCode,resultcode,data);

        if (requestCode == GALLARY_INTENT || requestCode == RESULT_OK){

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(MainActivity.this,"Upload Done",Toast.LENGTH_LONG).show();

                }
            });

        }
    }

