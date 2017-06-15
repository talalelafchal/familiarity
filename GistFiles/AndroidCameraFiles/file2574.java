package com.example.jenny.myapplication.client;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jenny.myapplication.R;
import com.example.jenny.myapplication.data.Photo;
import com.example.jenny.myapplication.service.ImageServiceImpl;
import com.example.jenny.myapplication.util.TouchableImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Widget displays single photo in a larger view.
 */
public class PhotoView extends ScrollView {

    private final Context context;
    private final TouchableImage imageView;
    private final TextView titleView;
    private final TextView farmView;
    private final TextView serverView;
    private final TextView secretView;
    private final Button openButton;
    private final Button saveButton;
    private final TextView photoStatus;
    private Bitmap image;

    public PhotoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.context = context;

        View container = inflate(context, R.layout.photo_view, null);
        imageView = (TouchableImage) container.findViewById(R.id.photo_image);
        titleView = (TextView) container.findViewById(R.id.photo_title);
        farmView = (TextView) container.findViewById(R.id.photo_farm);
        serverView = (TextView) container.findViewById(R.id.photo_server);
        secretView = (TextView) container.findViewById(R.id.photo_secret);
        openButton = (Button) container.findViewById(R.id.open_button);
        saveButton = (Button) container.findViewById(R.id.save_button);
        photoStatus = (TextView) container.findViewById(R.id.photo_status);

        addView(container);
    }

    public PhotoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PhotoView(Context context) {
        this(context, null, 0);
    }

    public void init(ImageServiceImpl imageService, final Photo photo) {
        if (photo == null) {
            return;
        }
        titleView.setText(photo.getTitle());
        farmView.setText(photo.getFarmId());
        serverView.setText(photo.getServerId());
        secretView.setText(photo.getSecret());

        openButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(photo.getUrl());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                context.startActivity(launchBrowser);
            }
        });

        saveButton.setOnClickListener(new OnClickListener() {
              @Override
              public void onClick(View v) {
                  String output = "";
                  File pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(
                          Environment.DIRECTORY_DCIM + File.separator + "Camera").toString());
                  if (pictureDirectory == null || image == null) {
                      return;
                  }
                  if (!pictureDirectory.exists()) {
                      pictureDirectory.mkdir();
                  }
                  try {
                      output = pictureDirectory + File.separator + photo.getId() + ".jpg";
                      FileOutputStream fos = new FileOutputStream(output);
                      image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                      fos.close();
                  } catch (IOException e) {
                      e.printStackTrace();
                      photoStatus.setText("Saving fail !");
                  }

                  final Handler handler = new Handler(context.getMainLooper());
                  MediaScannerConnection.scanFile(context,
                          new String[]{output},
                          null,
                          new MediaScannerConnection.OnScanCompletedListener() {
                              public void onScanCompleted(String path, Uri uri) {
                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          photoStatus.setText("Saving completed !");
                                      }
                                  });
                              }
                              private void runOnUiThread(Runnable r) {
                                  handler.post(r);
                              }
                          });
              }
                                      }

        );
        if (photo.getUrl() != null && !photo.getUrl().equals("")) {
            if (photo.getUrl() != null && !photo.getUrl().equals("")) {
                imageService.downloadPhoto(photo.getUrl(), successListener(), errorListener());
            }
        }
    }

    private Response.Listener<Bitmap> successListener() {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                image = response;
                imageView.setImageBitmap(response);

                ObjectAnimator.ofFloat(imageView, View.ALPHA, 0.2f, 1.0f).setDuration(1000).start();

                openButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
            }
        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }
}
