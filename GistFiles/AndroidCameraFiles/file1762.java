import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.util.IOUtils;
import com.github.yoojia.fast.view.AutoView;
import com.github.yoojia.fast.view.ViewFinder;
import com.parkingwang.app.Const;
import com.parkingwang.app.R;
import com.parkingwang.app.support.BugReport;
import com.parkingwang.app.support.FileUtils;
import com.parkingwang.app.support.MessageProxy;
import com.parkingwang.widget.BaseActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 拍摄行驶证的界面
 *
 * @author 浩码农 (msdx.android@qq.com)
 * @version 2015-12-28
 * @since 2015-12-28
 */
public class TakePhotoActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    @AutoView(viewId = R.id.surface_view)
    private SurfaceView mSurfaceView;
    @AutoView(viewId = R.id.viewfinder)
    private View mViewfinder;
    @AutoView(viewId = R.id.cancel)
    private View mCancel;
    @AutoView(viewId = R.id.take_photo)
    private View mTakePhoto;

    private boolean mWaitForTakePhoto;
    private boolean mIsSurfaceReady;

    private Camera.Size mBestPictureSize;
    private Camera.Size mBestPreviewSize;

    @Nullable
    private Camera mCamera;

    private String mOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        ViewFinder.inject(this);

        mViewfinder.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mTakePhoto.setOnClickListener(this);

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);

        mOutput = PhotoActionHelper.getOutputPath(getIntent());
    }

    private void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (RuntimeException e) {
                if ("Fail to connect to camera service".equals(e.getMessage())) {
                    MessageProxy.showError(this, R.string.msg_camera_invalid_permission_denied);
                } else if ("Camera initialization failed".equals(e.getMessage())) {
                    MessageProxy.showError(this, R.string.msg_camera_invalid_initial_failed);
                } else {
                    MessageProxy.showError(this, R.string.msg_camera_invalid_unknown_error);
                }
                finish();
                return;
            }
        }

        final Camera.Parameters cameraParams = mCamera.getParameters();
        cameraParams.setPictureFormat(ImageFormat.JPEG);
        cameraParams.setRotation(90);
        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(cameraParams);

        // 短边比长边
        final float ratio = (float) mSurfaceView.getWidth() / mSurfaceView.getHeight();

        // 设置pictureSize
        List<Camera.Size> pictureSizes = cameraParams.getSupportedPictureSizes();
        if (mBestPictureSize == null) {
            mBestPictureSize =findBestPictureSize(pictureSizes, cameraParams.getPictureSize(), ratio);
        }
        cameraParams.setPictureSize(mBestPictureSize.width, mBestPictureSize.height);

        // 设置previewSize
        List<Camera.Size> previewSizes = cameraParams.getSupportedPreviewSizes();
        if (mBestPreviewSize == null) {
            mBestPreviewSize = findBestPreviewSize(previewSizes, cameraParams.getPreviewSize(),
                    mBestPictureSize, ratio);
        }
        cameraParams.setPreviewSize(mBestPreviewSize.width, mBestPreviewSize.height);

        setSurfaceViewSize(mBestPreviewSize);

        try {
            mCamera.setParameters(cameraParams);
        } catch (RuntimeException e) {
            reportBug(cameraParams, e);
        }

        if (mIsSurfaceReady) {
            startPreview();
        }
    }

    private void reportBug(Camera.Parameters cameraParams, RuntimeException e) {
        final List<Camera.Size> pictureSizes = cameraParams.getSupportedPictureSizes();
        final List<Camera.Size> previewSizes = cameraParams.getSupportedPreviewSizes();
        final StringBuilder sb = new StringBuilder();
        sb.append("surface[").append(mSurfaceView.getWidth()).append(",").append(mSurfaceView.getHeight()).append("]\n");
        buildSizesLog("picture", pictureSizes, sb);
        buildSizesLog("preview", previewSizes, sb);
        BugReport.report(new Exception("无法匹配合适大小:" + sb.toString(), e));
    }

    private void buildSizesLog(String tag, List<Camera.Size> sizes, StringBuilder sb) {
        sb.append(tag).append("{");
        for(Camera.Size size : sizes) {
            sb.append("[").append(size.width).append(",").append(size.height).append("],");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}\n");
    }

    private void setSurfaceViewSize(Camera.Size size) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        params.height = mSurfaceView.getWidth() * size.width / size.height;
        mSurfaceView.setLayoutParams(params);
    }

    /**
     * 找到短边比长边大于于所接受的最小比例的最大尺寸
     *
     * @param sizes       支持的尺寸列表
     * @param defaultSize 默认大小
     * @param minRatio    相机图片短边比长边所接受的最小比例
     * @return 返回计算之后的尺寸
     */
    private Camera.Size findBestPictureSize(List<Camera.Size> sizes, Camera.Size defaultSize, float minRatio) {
        final int MIN_PIXELS = 320 * 480;

        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            //移除不满足比例的尺寸
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }
            //移除太小的尺寸
            if (size.width * size.height < MIN_PIXELS) {
                it.remove();
            }
        }

        // 返回符合条件中最大尺寸的一个
        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }
        // 没得选，默认吧
        return defaultSize;
    }

    /**
     * @param sizes
     * @param defaultSize
     * @param pictureSize 图片的大小
     * @param minRatio preview短边比长边所接受的最小比例
     * @return
     */
    private Camera.Size findBestPreviewSize(List<Camera.Size> sizes, Camera.Size defaultSize,
                                            Camera.Size pictureSize, float minRatio) {
        final int pictureWidth = pictureSize.width;
        final int pictureHeight = pictureSize.height;
        boolean isBestSize = (pictureHeight / (float)pictureWidth) > minRatio;
        sortSizes(sizes);

        Iterator<Camera.Size> it = sizes.iterator();
        while (it.hasNext()) {
            Camera.Size size = it.next();
            if ((float) size.height / size.width <= minRatio) {
                it.remove();
                continue;
            }

            // 找到同样的比例，直接返回
            if (isBestSize && size.width * pictureHeight == size.height * pictureWidth) {
                return size;
            }
        }

        // 未找到同样的比例的，返回尺寸最大的
        if (!sizes.isEmpty()) {
            return sizes.get(0);
        }

        // 没得选，默认吧
        return defaultSize;
    }

    private static void sortSizes(List<Camera.Size> sizes) {
        Collections.sort(sizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size a, Camera.Size b) {
                return b.height * b.width - a.height * a.width;
            }
        });
    }

    private void startPreview() {
        if (mCamera == null) {
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
            BugReport.report(e);
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    private void closeCamera() {
        if (mCamera == null) {
            return;
        }
        mCamera.cancelAutoFocus();
        stopPreview();
        mCamera.release();
        mCamera = null;
    }

    /**
     * 请求自动对焦
     */
    private void requestFocus() {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        mCamera.autoFocus(null);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (mCamera == null || mWaitForTakePhoto) {
            return;
        }
        mWaitForTakePhoto = true;
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                onTakePhoto(data);
                mWaitForTakePhoto = false;
            }
        });
    }

    private void onTakePhoto(byte[] data) {
        final String tempPath = mOutput + "_";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempPath);
            fos.write(data);
            fos.flush();
            PhotoActionHelper.clipImage(this)
                    .extra(getIntent())
                    .output(mOutput).input(tempPath)
                    .requestCode(Const.Request.CLIP_IMAGE).start();
        } catch (Exception e) {
            BugReport.report(e);
        } finally {
            IOUtils.close(fos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                openCamera();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsSurfaceReady = true;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsSurfaceReady = false;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.viewfinder:
                requestFocus();
                break;
            case R.id.cancel:
                cancelAndExit();
                break;
            case R.id.take_photo:
                takePhoto();
                break;
            default:// do nothing
        }
    }

    @Override
    public void onBackPressed() {
        cancelAndExit();
    }

    private void cancelAndExit() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.Request.CLIP_IMAGE) {
            String tempPath = PhotoActionHelper.getInputPath(data);
            if (tempPath != null) {
                FileUtils.delete(tempPath);
            }
            if (resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
