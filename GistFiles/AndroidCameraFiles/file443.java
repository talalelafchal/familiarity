package com.example.jenny.myapplication.client;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import com.example.jenny.myapplication.BaseActivity;
import com.example.jenny.myapplication.R;
import com.example.jenny.myapplication.data.Photo;
import com.example.jenny.myapplication.service.ImageServiceImpl;

import javax.inject.Inject;

/**
 *  Activity to show full view of an image.
 *
 */
public class PhotoViewActivity extends BaseActivity {

    @Inject
    ImageServiceImpl imageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        setContentView(R.layout.activity_photo_view);

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("PHOTO_BUNDLE");
            Photo photo = (Photo) bundle.getSerializable("PHOTO");
            photoView.init(imageService, photo);
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
