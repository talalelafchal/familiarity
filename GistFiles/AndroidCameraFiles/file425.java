package com.hanhuy.android.vision;

import android.app.Activity;
import android.graphics.*;
import android.media.Image;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;

import static org.bytedeco.javacpp.opencv_video.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

// ----------------------------------------------------------------------

interface ImageListener {
    void onImage(Image image);
}
public class FacePreview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        FrameLayout layout = new FrameLayout(this);
        layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        CameraPreview p = new CameraPreview(this);
        FaceView faceView = new FaceView(this);
        layout.addView(p.textureView());
        layout.addView(faceView);
        p.imageListener_$eq(scala.Option.apply((ImageListener)faceView));
        setContentView(layout);
    }
}

class FaceView extends SurfaceView implements SurfaceHolder.Callback, ImageListener {
    private Surface surface = null;
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        surface = holder.getSurface();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surface = holder.getSurface();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surface = null;
    }

    public void onImage(Image image) {
        processImage(ImageUtil.imageToMat(image), image.getWidth(), image.getHeight());

    }
    public static final int SUBSAMPLING_FACTOR = 4;

    private Mat mask;
    private Mat frame;
    private final BackgroundSubtractor bs = createBackgroundSubtractorMOG2(500, 16, false);
    private Mat fgMask;
    private final ImageUtil.MatConverter matConverter = new ImageUtil.MatConverter();
    private final ImageUtil.MatConverter matConverter2 = new ImageUtil.MatConverter();
    private final ImageUtil.MatConverter matConverter3 = new ImageUtil.MatConverter();
    private final CascadeClassifier cascade;

    public FaceView(FacePreview context) {
        super(context);
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);

        try {
            File classifierFile = Loader.extractResource(getClass(),
                    "/haarcascade_frontalface_alt.xml",
                    context.getCacheDir(), "classifier", ".xml");
            cascade = new CascadeClassifier();
            cascade.load(classifierFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    final Mat upper = new Mat(3, 1, CV_8UC3, new Scalar(36, 174, 255, 255));
    final Mat lower = new Mat(3, 1, CV_8UC3, new Scalar(0,   58,   0,   0));
    final Mat erodeKernel = getStructuringElement(MORPH_ELLIPSE, new opencv_core.Size(4,4));
    final Mat erodeKernel2 = getStructuringElement(MORPH_ELLIPSE, new opencv_core.Size(6,6));
    final opencv_core.Point POINT0 = new opencv_core.Point(0,0);
    private opencv_core.Size reducedSize;
    private void processImage(Mat preview, int width, int height) {
        int f = SUBSAMPLING_FACTOR;
        if (mask == null || mask.cols() != width/f || mask.rows() != height/f) {
            if (mask != null) mask.release();
            mask = new Mat(height/f, width/f, CV_8UC1);
        }
        if (frame == null || frame.cols() != width/f || frame.rows() != height/f) {
            if (frame != null) frame.release();
            frame = new Mat(height/f, width/f, CV_8UC3);
        }
        if (fgMask == null || fgMask.cols() != width/f || fgMask.rows() != height/f) {
            if (fgMask != null) fgMask.release();
            fgMask = new Mat();
        }
        if (reducedSize == null || reducedSize.width() != preview.cols()/f || reducedSize.height() != preview.rows()/f) {
            if (reducedSize != null) reducedSize.deallocate();
            reducedSize = new opencv_core.Size(preview.cols()/f, preview.rows()/f);
        }

        resize(preview, frame, reducedSize, 0, 0, INTER_NEAREST);
        RectVector faces = new RectVector();
        cascade.detectMultiScale(frame, faces);
        preview.release();
        cvtColor(frame, frame, COLOR_YUV2BGR_I420);
        Mat hsv = new Mat();
        cvtColor(frame, hsv, COLOR_BGR2HSV);
        mask.zero();
        inRange(hsv, lower, upper, mask);
        hsv.release();

        Mat blank = new Mat();
        Mat blank2 = new Mat();

        dilate(mask, mask, erodeKernel);
        erode(mask, mask, erodeKernel);
        frame.copyTo(blank, mask);
        fgMask.zero();
        bs.apply(blank, fgMask);
        dilate(fgMask, fgMask, erodeKernel2);
        erode(fgMask, fgMask, erodeKernel2);
        blank.copyTo(blank2, fgMask);
        blank2.copyTo(frame);
        fgMask.copyTo(blank2);
        blank.release();
        MatVector vec = new MatVector();
        findContours(blank2, vec, RETR_EXTERNAL, CHAIN_APPROX_NONE);
        blank2.release();
        MatVector hullVec = new MatVector();
        for (int i = 0; i < vec.size(); i++) {
            Mat m = vec.get(i);
            if (m.total() > 500 && m.total() < 1450) {
                Mat m_ = new Mat();
                drawContours(frame, vec, i, Scalar.MAGENTA, -1, LINE_8, m_, 0, POINT0);
                m_.release();
//                RotatedRect r = minAreaRect(m);
                Mat hull = new Mat();
                convexHull(m, hull);
                hullVec.put(new Mat[] { hull });
            }
            m.release();
        }
        if (hullVec.size() > 0)
            drawContours(frame, hullVec, -1, Scalar.CYAN);
        vec.deallocate();
        drawToSurface(faces);
    }

    final Paint paint;
    {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
    }
    private void drawToSurface(RectVector faces) {
        Surface surf = surface;
        if (surf != null && surf.isValid()) {
            Canvas canvas = surf.lockCanvas(null);
            canvas.scale(-1, 1);
            if (frame.total() > 4) {
                Bitmap b = matConverter.convertToBitmap(frame);
                if (b != null) {
                    canvas.drawBitmap(b, -canvas.getWidth() + 100, 100, null);
                    if (faces != null) {
                        long total = faces.size();
                        for (int i = 0; i < total; i++) {
                            opencv_core.Rect r = faces.get(i);
                            int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                            canvas.drawRect(
                                    -canvas.getWidth() + x + 100, y + 100,
                                    -canvas.getWidth() + (x+w) + 100, (y+h) + 100, paint);
                        }
                    }
                }
                if (mask != null && mask.data() != null) {
                    b = matConverter2.convertToBitmap(mask);
                    if (b != null) {
                        canvas.drawBitmap(b, -canvas.getWidth() + 100, 500, null);
                    }
                }
                if (fgMask != null && fgMask.data() != null) {
                    b = matConverter3.convertToBitmap(fgMask);
                    if (b != null) {
                        canvas.drawBitmap(b, -canvas.getWidth() + 100, 900, null);
                    }
                }
            }
            if (surf.isValid())
                surf.unlockCanvasAndPost(canvas);
        }
    }
}

