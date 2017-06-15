package com.example.ratanak.bottompopup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view to the screen
        setContentView(R.layout.activity_main);

        //find btn from xml
        ImageView btn = (ImageView) findViewById(R.id.btn);

        //set click listener when user click on button
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                Toast.makeText(this, "Open Camera", Toast.LENGTH_SHORT).show();
                //Call our Dialogmenu
                DialogMenu dialogMenu = new DialogMenu(this);
                dialogMenu.setListener(new DialogMenu.OnDialogMenuListener() {
                    @Override
                    public void onPicturePress() {
                        Toast.makeText(MainActivity.this, "Camera", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGalleryPress() {
                        Toast.makeText(MainActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
