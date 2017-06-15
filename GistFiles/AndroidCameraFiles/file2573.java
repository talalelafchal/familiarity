package com.example.jenny.myapplication.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.jenny.myapplication.R;
import com.example.jenny.myapplication.data.Photo;
import com.example.jenny.myapplication.service.ImageServiceImpl;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Widget displays each item on the photo list. Show image, title, and a control button.
 */
public class PhotoListItem extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "PhotoListItem";

    private final Context context;
    private final ImageServiceImpl imageService;

    private final TextView titleView;
    private final ImageView imageView;
    private final TextView descView;

    private Photo photo;

    public PhotoListItem(final Context context, ImageServiceImpl imageService) {
        super(context);
        this.context = context;
        this.imageService = imageService;
        View container = inflate(context, R.layout.photo_item, null);
        imageView = (ImageView) container.findViewById(R.id.photo_image);
        titleView = (TextView) container.findViewById(R.id.photo_title);
        descView = (TextView) container.findViewById(R.id.photo_desc);
        addView(container);
    }

    public void setPhotoData(final Photo photo) {
        if (photo == null) {
            return;
        }

        this.photo = photo;
        if (photo.getUrl() != null && !photo.getUrl().equals("")) {
            imageService.downloadPhoto(photo.getThumbUrl(), successListener(), errorListener());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PhotoViewActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("PHOTO", photo);
        intent.putExtra("PHOTO_BUNDLE", bundle);

        context.startActivity(intent);
    }

    private Response.Listener<Bitmap> successListener() {
        return new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
                descView.setText("Picture ID : " + photo.getId());
                titleView.setText(photo.getTitle());
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
