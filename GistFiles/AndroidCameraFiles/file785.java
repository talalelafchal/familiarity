
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    static {
        OpenCVLoader.initDebug();
    }

    static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    JavaCameraView view;
    CascadeClassifier classifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new JavaCameraView(this, 0);
        view.setCvCameraViewListener(this);
        setContentView(view);

        try {
            final File lol = new File(getFilesDir(), "lol");

            BufferedSource in = Okio.buffer(Okio.source(getResources().openRawResource(R.raw.cascade)));
            BufferedSink out = Okio.buffer(Okio.sink(lol));

            out.writeAll(in);

            out.close();
            in.close();

            classifier = new CascadeClassifier(lol.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.enableView();
    }

    @Override
    protected void onPause() {
        view.disableView();
        super.onPause();
    }

    @Nullable
    Size size;

    final Size maxSize = new Size();
    final MatOfRect faces = new MatOfRect();

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat gray = inputFrame.gray();

        if (size == null) {
            final int faceSize = Math.round(gray.rows() * 0.2F);
            size = new Size(faceSize, faceSize);
        }

        classifier.detectMultiScale(gray, faces, 1.1, 2, 2, size, maxSize);

        final Mat rgba = inputFrame.rgba();

        for (Rect aFacesArray : faces.toArray())
            Imgproc.rectangle(rgba, aFacesArray.tl(), aFacesArray.br(), FACE_RECT_COLOR, 3);

        return rgba;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }
}
