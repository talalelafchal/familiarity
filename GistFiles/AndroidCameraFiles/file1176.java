package com.cornershopapp.shopper.android.ui.main;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cornershopapp.shopper.android.R;
import com.cornershopapp.shopper.android.entity.responses.SimpleResponse;
import com.cornershopapp.shopper.android.enums.BarcodeTypes;
import com.cornershopapp.shopper.android.enums.PendingRequestTypes;
import com.cornershopapp.shopper.android.network.RestApi;
import com.cornershopapp.shopper.android.services.InsertPendingRequestService;
import com.cornershopapp.shopper.android.sql.Order;
import com.cornershopapp.shopper.android.ui.BaseActivity;
import com.cornershopapp.shopper.android.utils.CameraPreview;
import com.cornershopapp.shopper.android.utils.Utils;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by romantolmachev on 11/11/15.
 */
public class TakePhotoActivity extends BaseActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.step_one_of_four_layout)
    RelativeLayout stepOneOfFourLayout;

    @InjectView(R.id.camera_preview)
    FrameLayout cameraPreviewLayout;

    @InjectView(R.id.take_photo_button)
    FrameLayout takePhotoButton;

    @InjectView(R.id.retake_photo_button)
    TextView retakePhotoButton;

    Camera camera;
    CameraPreview cameraPreview;
    File photoFile;
    boolean photoTaken;

    Camera.PictureCallback picture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(final byte[] originalImageData, Camera camera) {

            photoTaken = true;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    byte[] resizedImageData = resizeImage(originalImageData);
                    photoFile = Utils.createPublicImageFile();
                    if (photoFile == null) {
                        System.out.println("Error creating media file, check storage permissions: ");
                        return;
                    }

                    try {
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(resizedImageData);
                        fos.close();

                        //TODO switch to Retrofit 2.0 and cancel uploading previous screenshot
                        uploadPhotoRequest();
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Error accessing file: " + e.getMessage());
                    }

                }
            }).start();

        }
    };

    final int PHOTO_WIDTH = 1200;
    final int PHOTO_HEIGHT = 1200;

    BarcodeTypes barcodeType;
    float total;

    public static final int CHECKOUT = 102;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CHECKOUT) {

            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_photo);
        setUpToolbarAndTitleAndHome(getString(R.string.finish_picking), R.menu.menu_accept_order);
        ButterKnife.inject(this);

        stepOneOfFourLayout.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        retakePhotoButton.setOnClickListener(this);

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.step_one_of_four_layout:

                if(photoTaken) {

                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.key_barcode), Parcels.wrap(barcodeType));
                    args.putFloat(getString(R.string.key_order_total), total);

                    startActivityForResult(new Intent(this, ScanReceiptBarcodeActivity.class).putExtras(args), CHECKOUT);
                }

                break;
            case R.id.take_photo_button:
                camera.takePicture(null, null, picture);
                takePhotoButton.setVisibility(View.GONE);
                retakePhotoButton.setVisibility(View.VISIBLE);
                break;
            case R.id.retake_photo_button:
                retakePhotoButton.setVisibility(View.GONE);
                takePhotoButton.setVisibility(View.VISIBLE);
                camera.startPreview();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_call:
                showWhomToCallDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        camera = getCameraInstance();
        if(camera != null) {
            setupCamera();
        } else {

            /**
             * Delay is necessary for cases when you go back from the ScanReceiptBarcodeActivity
             * and need to wait a bit before Camera is released
             */
            cameraPreviewLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    camera = getCameraInstance();
                    if(camera != null) {
                        setupCamera();
                    }
                }
            }, 300);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            cameraPreview.getHolder().removeCallback(cameraPreview);
            camera.release();
            camera = null;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    byte[] resizeImage(byte[] input) {
        Bitmap original = BitmapFactory.decodeByteArray(input, 0, input.length);
        Bitmap resized = Bitmap.createScaledBitmap(original, PHOTO_WIDTH, PHOTO_HEIGHT, true);

        ByteArrayOutputStream blob = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

        return blob.toByteArray();
    }

    void setupCamera() {
        cameraPreview = new CameraPreview(TakePhotoActivity.this, camera);
        cameraPreviewLayout.removeAllViews();
        cameraPreviewLayout.addView(cameraPreview);
        retakePhotoButton.setVisibility(View.GONE);
        takePhotoButton.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {Order._ID, Order.TOTAL, Order.SCAN_TYPE};
        return new CursorLoader(this, ContentUris.withAppendedId(Order.CONTENT_URI, orderId), projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            //if scan receipt is enabled
            if (cursor.getString(cursor.getColumnIndex(Order.SCAN_TYPE)) != null) {
                barcodeType = BarcodeTypes.valueOf(cursor.getString(cursor.getColumnIndex(Order.SCAN_TYPE)));
                total = cursor.getFloat(cursor.getColumnIndex(Order.TOTAL));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * ================================= API REQUESTS ==============================================
     */

    private void uploadPhotoRequest() {

            RestApi.uploadReceipt(orderId, photoFile, new Callback<SimpleResponse>() {
                @Override
                public void success(SimpleResponse simpleResponse, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {
                    handleErrors(error);
                }
            });
        
    }

}
