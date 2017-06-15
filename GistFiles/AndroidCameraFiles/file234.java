package com.kazim.ud;

/**
 * Created by Tasneem on 21/08/15.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.moodstocks.android.AutoScannerSession;
import com.moodstocks.android.MoodstocksError;
import com.moodstocks.android.Result;
import com.moodstocks.android.Scanner;

public class ScanActivity extends Activity implements AutoScannerSession.Listener {

    ImageButton imageButton,imgM1,imgM2,imgM3,imgM4,imgM5;
    LinearLayout menu;
    private AutoScannerSession session = null;
    private static final int TYPES = Result.Type.IMAGE | Result.Type.QRCODE | Result.Type.EAN13;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imageButton=(ImageButton)findViewById(R.id.imgMenu);
        imgM1=(ImageButton)findViewById(R.id.imgM1);
        imgM2=(ImageButton)findViewById(R.id.imgM2);
        imgM3=(ImageButton)findViewById(R.id.imgM3);
        imgM4=(ImageButton)findViewById(R.id.imgM4);
        imgM5=(ImageButton)findViewById(R.id.imgM5);
        menu=(LinearLayout)findViewById(R.id.menu);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ScanActivity.this, imageButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(ScanActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                menu.setVisibility(View.VISIBLE);
                //popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method

        SurfaceView preview = (SurfaceView)findViewById(R.id.preview);

        try {
            session = new AutoScannerSession(this, Scanner.get(), this, preview);
            session.setResultTypes(TYPES);
        } catch (MoodstocksError e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(menu.getVisibility()==View.VISIBLE)
            menu.setVisibility(View.INVISIBLE);
        else {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onResult(Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.resume();
            }
        });
        builder.setTitle(result.getType() == Result.Type.IMAGE ? "Image:" : "Barcode:");
        builder.setMessage(result.getValue());
        builder.show();
    }

    @Override
    public void onCameraOpenFailed(Exception e) {
        // Implemented in a few steps
    }

    @Override
    public void onWarning(String debugMessage) {
        // Implemented in a few steps
    }

    @Override
    protected void onResume() {
        super.onResume();
        session.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        session.stop();
    }
}