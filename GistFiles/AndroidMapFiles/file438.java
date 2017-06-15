package com.dev.kedzoh.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by romantolmachev on 10/2/15.
 */
public class VideoThumbnailLoader extends AsyncTask<Uri, Void, Bitmap> {

    private Context mContext;
    private ImageView mImageView;
    private int mThumbnailType;

    private String mPath;

    private static Map<String, Bitmap> mCachedBitmaps;

    public VideoThumbnailLoader(Context context, ImageView imageView, int type) {
        mContext = context;
        mImageView = imageView;
        mThumbnailType = type;

        if(mCachedBitmaps == null) {
            mCachedBitmaps = new HashMap<>();
        }
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        mPath = Utils.getPath(mContext, params[0]);

        //if was loaded previously, return cached bitmap
        if(mCachedBitmaps.containsKey(mPath)) {
            return mCachedBitmaps.get(mPath);
        } else {
            return ThumbnailUtils.createVideoThumbnail(mPath, mThumbnailType);
        }

    }

    @Override
    protected void onPostExecute(Bitmap result) {
        mImageView.setImageBitmap(result);
        mCachedBitmaps.put(mPath, result);
    }
}
