/**
 * Copyright 2016 yayandroid
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package com.yayandroid.utility;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.yayandroid.utility.ImageLoader;

/**
 * Created by Yahya Bayramoglu on 06/05/16.
 * <p/>
 * TODO: Future! Fix if there is more elegant way to handle this issue!
 * There is an issue on getting image from camera
 * http://stackoverflow.com/q/35103658/1171484
 */

public class PickedImageLoader extends AsyncTask<Void, Void, Bitmap> {

    private static final String TAG = PickedImageLoader.class.getSimpleName();
    
    private String imageFilePath;
    private ImageLoaderListener listener;
    private int requiredWidth = ImageLoader.UNKNOWN_SIZE;

    private int retryCount = 0;

    private final int RETRY_LIMIT = 5;
    private final int RETRY_PERIOD = 200;

    public interface ImageLoaderListener {
        void onBitmapReady(Bitmap requiredBitmap);

        void onBitmapLoadingFailed();
    }

    public PickedImageLoader(String imageFilePath, ImageLoaderListener listener) {
        this.imageFilePath = imageFilePath;
        this.listener = listener;
    }

    public PickedImageLoader(String imageFilePath, int requiredWidth, ImageLoaderListener listener) {
        this.imageFilePath = imageFilePath;
        this.requiredWidth = requiredWidth;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap requiredBitmap = null;
        try {
            requiredBitmap = getBitmapFromFile();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return requiredBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
            listener.onBitmapLoadingFailed();
        } else {
            listener.onBitmapReady(bitmap);
        }
    }

    private Bitmap getBitmapFromFile() throws InterruptedException {
        // Don't bother to load bitmap on full size if there is a max limit
        Bitmap requiredBitmap = new ImageLoader.Builder(imageFilePath)
                .setRequiredSize(ImageLoader.WIDTH, requiredWidth)
                .setRequirementType(ImageLoader.MAX)
                .getBitmap();

        if (requiredBitmap == null) {
            retryCount++;
            Log.e(TAG, "Bitmap is null, retrying... " + retryCount);

            if (retryCount <= RETRY_LIMIT) {
                if (isCancelled()) {
                    return null;
                } else {
                    Thread.sleep(RETRY_PERIOD);
                    return getBitmapFromFile();
                }
            }
        }

        return requiredBitmap;
    }

}