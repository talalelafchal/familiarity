package com.cronline.haito.projectbeggining.activities.camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cronline.haito.projectbeggining.R;
import com.cronline.haito.projectbeggining.util.CamPreview;
import com.cronline.haito.projectbeggining.util.helpers.AlertHelper;
import com.cronline.haito.projectbeggining.view.alerts.CameraUtilsBuilder;
import com.cronline.haito.projectbeggining.util.helpers.DateHelper;
import com.cronline.haito.projectbeggining.util.helpers.ToastHelper;
import com.cronline.haito.projectbeggining.view.cameraRelated.CircleDrawView;
import com.cronline.haito.projectbeggining.view.cameraRelated.images.ImageFragment;
import com.cronline.haito.projectbeggining.view.cameraRelated.images.ImagePagerViewAdapter;
import com.cronline.haito.projectbeggining.view.cameraRelated.images.ImagePreviewView;
import com.cronline.haito.projectbeggining.view.cameraRelated.images.ImageSnapshotView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends FragmentActivity {

    private Camera camera;
    private int cameraId = -1;

    private CamPreview camPreview;
    private FrameLayout frameLayout;
    private Camera.Parameters camParameters;
    private LinearLayout horizontalMenu;

    private ViewPager viewPager;

    private CircleDrawView circleDrawView;
    private ArrayList<ImageSnapshotView> imageBuffor;

    private ImagePagerViewAdapter imagePagerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageBuffor = new ArrayList<>();
        setContentView(R.layout.activity_camera);

        camera = initCamera();

        camPreview = new CamPreview(CameraActivity.this, camera);
        frameLayout = (FrameLayout) findViewById(R.id.cameraHolderFrameLayout);
        frameLayout.addView(camPreview, 0);

        circleDrawView = createCircle();
        frameLayout.addView(circleDrawView);

        horizontalMenu = (LinearLayout) findViewById(R.id.horizontalCameraMenu);

        ImageView cameraShot = (ImageView) findViewById(R.id.takeShot);
        cameraShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            ToastHelper.showShort(CameraActivity.this, getString(R.string.fototaken));
                            constructSnapshot(data);
                            camera.startPreview();
                            shuffleSnapshots();
                        }
                    });
                } catch (RuntimeException e) {
                    ToastHelper.showShort(CameraActivity.this, "Picture take failed");
                }
            }
        });

        ImageView saveButton = (ImageView) findViewById(R.id.savePhoto);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllPhotos();
            }
        });

        ImageView discardImage = (ImageView) findViewById(R.id.deletePhoto);
        discardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllPhotos();
                ToastHelper.showShort(CameraActivity.this, getString(R.string.fotodiscarded));
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imagePagerViewAdapter = new ImagePagerViewAdapter(getSupportFragmentManager(), imageBuffor, frameLayout.getWidth(), frameLayout.getHeight());
        viewPager.setAdapter(imagePagerViewAdapter);

        checkParams();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getVisibility() == View.VISIBLE)
            viewPager.setVisibility(View.INVISIBLE);
        else
            super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camPreview.getCamera() == null) {
            camera = initCamera();
            camPreview.setCamera(camera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        camPreview.setCamera(null);
        camera.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.release();
        camPreview.setCamera(null);
    }

    public Camera initCamera() {
        boolean cam = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);

        if (!cam) {
            ToastHelper.showShort(CameraActivity.this, getString(R.string.nocamera));
            finish();
            return null;
        } else {
            // wykorzystanie danych zwroconych przez kolejna funkcje getCameraId
            cameraId = getCameraId();
            // jest jakas kamera!
            try {
                if (cameraId < 0) {
                    return null;
                } else if (cameraId >= 0) {
                    return Camera.open(cameraId);
                } else {
                    return Camera.open();
                }
            } catch (RuntimeException e) {
                ToastHelper.showShort(CameraActivity.this, "Cannot connect to camera");
                finish();
                return null;
            }
        }
    }

    private int getCameraId() {
        int camerasCount = Camera.getNumberOfCameras(); // pobranie referencji do kamer

        for (int i = 0; i < camerasCount; i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);

            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK || cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }

        return -1;
    }

    private void checkParams() {
        camParameters = camera.getParameters();

        attachExposureCompesation();

        if (camParameters.getSupportedWhiteBalance() != null)
            attachWhiteBalance();

        if (camParameters.getSupportedPictureSizes() != null) {
            attachSize();
        }
    }

    private void attachExposureCompesation() {
        ImageView imageView = new ImageView(CameraActivity.this);
        imageView.setImageResource(R.drawable.galery);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int max = camParameters.getMaxExposureCompensation();
                final int min = camParameters.getMinExposureCompensation();
                final int range = Math.abs(min) + Math.abs(max);

                String[] tablica = new String[range];
                for (int i = 0; i < range; i++) {
                    tablica[i] = (min + i) + "";
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(CameraActivity.this);
                alert.setTitle(getString(R.string.exposureCompensation));
                alert.setItems(tablica, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        camParameters.setExposureCompensation(min + which);
                        camera.setParameters(camParameters);
                        Log.d("camparams", "compensation " + camera.getParameters().getExposureCompensation());
                    }
                });

                alert.show();
            }
        });

        horizontalMenu.addView(imageView);
    }

    private void attachWhiteBalance() {
        CameraUtilsBuilder builder = new CameraUtilsBuilder(CameraActivity.this, camera) {
            protected void onClickCallback(int which) {
                camParameters.setWhiteBalance(camParameters.getSupportedWhiteBalance().get(which));
                camera.setParameters(camParameters);
            }

            public String[] data() {
                final List<String> supportedWhiteBalance = camParameters.getSupportedWhiteBalance();
                String[] tablica = supportedWhiteBalance.toArray(new String[supportedWhiteBalance.size()]);

                for (String s : supportedWhiteBalance) {
                    Log.d("camparams", "params" + s);
                }

                return tablica;
            }
        };

        builder.appendChildTo(horizontalMenu);
    }

    private void attachSize() {
        ImageView imageView = new ImageView(CameraActivity.this);
        imageView.setImageResource(R.drawable.galery);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<Camera.Size> supportedPictureSizes = camParameters.getSupportedPictureSizes();
                String[] params = new String[supportedPictureSizes.size()];

                for (int i = 0; i < supportedPictureSizes.size(); i++) {
                    Camera.Size current = supportedPictureSizes.get(i);
                    params[i] = current.width + "x" + current.height;
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(CameraActivity.this);
                alert.setTitle("Uwaga!");
                alert.setItems(params, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        camParameters.setPictureSize(supportedPictureSizes.get(which).width, supportedPictureSizes.get(which).height);
                        camera.setParameters(camParameters);

                        Log.d("camparams", camera.getParameters().getPictureSize().width + "");
                    }
                });

                alert.show();
            }
        });

        horizontalMenu.addView(imageView);
    }

    private CircleDrawView createCircle() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        return new CircleDrawView(this, width / 100 * 35, width / 2, height / 2, 5);
    }

    private ImageSnapshotView constructSnapshot(byte[] image) {
        try {
            ImageSnapshotView imageSnapshotView = new ImageSnapshotView(CameraActivity.this,
                    image,
                    100);

            imageSnapshotView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v instanceof ImageSnapshotView) {
                        constructAlert((ImageSnapshotView) v);
                    }

                    return true;
                }
            });

            imageBuffor.add(imageSnapshotView);
            frameLayout.addView(imageSnapshotView);

            return imageSnapshotView;
        } catch (OutOfMemoryError e) {
            AlertHelper.makeAlert(CameraActivity.this, "Limit pamieci");
            return null;
        }
    }

    private void saveSinglePhoto(final ImageSnapshotView imageSnapshotView) {
        saveSinglePhoto(imageSnapshotView, "");
    }

    private void saveSinglePhoto(final ImageSnapshotView imageSnapshotView, String add) {
        frameLayout.removeView(imageSnapshotView);
        imageBuffor.remove(imageSnapshotView);
        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DateHelper.getCurrentDateForImageCapture() + " - " + add + ".png");
        FileOutputStream fs;
        try {
            fs = new FileOutputStream(output);
            fs.write(imageSnapshotView.getInitialImage());
            fs.close();
            Log.d("fileLocation", output.getAbsolutePath());
            ToastHelper.showShort(CameraActivity.this, getString(R.string.fotosaved));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAllPhotos() {
        if (!imageBuffor.isEmpty()) {
            for (int i = 0; i < imageBuffor.size(); i++)
                saveSinglePhoto(imageBuffor.get(i), String.valueOf(i));
        } else {
            ToastHelper.showShort(CameraActivity.this, getString(R.string.nophoto));
        }
    }

    private void deletePhotoAtIndex(ImageSnapshotView imageSnapshotView) {
        imageBuffor.remove(imageSnapshotView);
        frameLayout.removeView(imageSnapshotView);
        shuffleSnapshots();
    }

    private void deleteAllPhotos() {
        for (ImageSnapshotView inArrayList : imageBuffor) {
            frameLayout.removeView(inArrayList);
        }
        imageBuffor.clear();
    }

    private void constructAlert(final ImageSnapshotView imageSnapshotView) {
        AlertDialog.Builder alert = new AlertDialog.Builder(CameraActivity.this);
        alert.setTitle("Uwaga!");
        alert.setItems(new String[]{"Zapisz to", "Zapisz wszystkie", "Usun to", "Usun wszystkie", "Podglad"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        saveSinglePhoto(imageSnapshotView);
                        break;
                    case 1:
                        saveAllPhotos();
                        break;
                    case 2:
                        deletePhotoAtIndex(imageSnapshotView);
                        break;
                    case 3:
                        deleteAllPhotos();
                        break;
                    case 4:
                        showPreview(imageSnapshotView);
                        break;
                }
            }
        });

        alert.show();
    }

    private void showPreview(final ImageSnapshotView imageSnapshotView) {
        viewPager.setVisibility(View.VISIBLE);
        imagePagerViewAdapter.setHeight(frameLayout.getHeight());
        imagePagerViewAdapter.setWidth(frameLayout.getWidth());
        imagePagerViewAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(imageBuffor.indexOf(imageSnapshotView));
    }

    private void shuffleSnapshots() {
        if (imageBuffor.size() > 0) {
            float angle = 360 / imageBuffor.size();
            int radius = circleDrawView.getRadius();
            int centerX = circleDrawView.getXCircle();
            int centerY = circleDrawView.getYCircle();

            for (int i = 0; i < imageBuffor.size(); i++) {
                ImageSnapshotView currentProcessed = imageBuffor.get(i);
                float xPos = radius * (float) Math.cos(Math.toRadians(angle * i)) + centerX - currentProcessed.getSize() / 2;
                float yPos = radius * (float) Math.sin(Math.toRadians(angle * i)) + centerY - currentProcessed.getSize() / 2;
                currentProcessed.move(xPos, yPos);
            }
        }
    }

}
