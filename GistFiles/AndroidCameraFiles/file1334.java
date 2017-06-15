package com.example.wenfahu.simplecam;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.*;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "OCV-lane";
    private ImageView driving_status;

    public CWorker[] workers = new CWorker [3];
    public Bundle[] bundles = new Bundle[3];

    public int JOB_LANE = 0, JOB_PERSON = 1, JOB_CAR = 2;

    TextView laneLabel, carLabel, peopleLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        // mOpenCvCameraView.setMaxFrameSize(320, 240);
        mOpenCvCameraView.setCvCameraViewListener(this);
        driving_status = (ImageView) findViewById(R.id.imageView);
        driving_status.setImageResource(R.drawable.rgba);
        laneLabel = (TextView)findViewById(R.id.laneLabel);
        carLabel = (TextView)findViewById(R.id.carLabel);
        peopleLabel = (TextView)findViewById(R.id.peopleLabel);

        JobHandler handler;

        int index = -1;
        handler = new JobHandler(++index);
        workers[index] = new CWorker(handler, this.getApplicationContext(), null) {
            @Override
            public int work(Mat input, Bundle data) {
                int[] result = JniTools.lanePlus(input.getNativeObjAddr());
                data.putIntArray("lines", result);
                return 0;
            }

            @Override
            public void postData(Mat output, Bundle data) {
                int[] lines = data.getIntArray("lines");
                if (lines == null) { return; }
                for (int i = 0; i < lines.length; i += 4) {
                    Imgproc.line(output, new Point(lines[i], lines[i + 1]),
                        new Point(lines[i + 2], lines[i + 3]),
                        new Scalar(255, 0, 0), 3);
                }
            }

            @Override
            public void updateUI(Bundle data) {
                int[] lines = data.getIntArray("lines");
                if (lines != null) {
                    laneLabel.setText(String.valueOf(lines.length / 4) + " lines");
                }
            }
        };

        handler = new JobHandler(++index);
        workers[index] = new CWorker(handler, this.getApplicationContext(), null) {
            @Override
            public int work(Mat input, Bundle data) {
                float[] result = JniTools.detCars(input.getNativeObjAddr());
                data.putFloatArray("cars", result);
                return 0;
            }

            @Override
            public void postData(Mat output, Bundle data) {
                float[] lines = data.getFloatArray("cars");
                if (lines == null) { return; }
                for (int i = 0; i < lines.length; i += 5) {
                    Imgproc.rectangle(output, new Point(lines[i], lines[i + 1]),
                            new Point(lines[i + 2], lines[i + 3]),
                            new Scalar(0, 0, 255), 3);
                }
            }

            @Override
            public void updateUI(Bundle data) {
                float[] lines = data.getFloatArray("cars");
                if (lines != null) {
                    carLabel.setText(String.valueOf(lines.length / 5) + " cars");
                }
            }
        }; //*/
        handler = new JobHandler(++index);
        workers[index] = /**/ null; /*/ new CWorker(handler, this.getApplicationContext(), null) {
            @Override
            public int work(Mat input, Bundle data) {
                float[] result = JniTools.detPeople(input.getNativeObjAddr());
                data.putFloatArray("people", result);
                return 0;
            }

            @Override
            public void postData(Mat output, Bundle data) {
                float[] lines = data.getFloatArray("people");
                if (lines == null) { return; }
                for (int i = 0; i < lines.length; i += 5) {
                    Imgproc.rectangle(output, new Point(lines[i], lines[i + 1]),
                            new Point(lines[i + 2], lines[i + 3]),
                            new Scalar(255, 0, 255), 3);
                }
            }

            @Override
            public void updateUI(Bundle data) {
                float[] lines = data.getFloatArray("people");
                if (lines != null) {
                    peopleLabel.setText(String.valueOf(lines.length / 5) + " people");
                }
            }
        }; //*/
    }

    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onResume(){
        super.onResume();
        mOpenCvCameraView.enableView();

    }

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        // if (frame != null && !frame.empty()) return frame;
        boolean needRerun = false;
        for (CWorker worker: workers) {
            if (worker != null && !worker.running) {
                needRerun = true;
                break;
            }
        }
        if (needRerun) {
            Mat jobInput = frame.clone();
            for (CWorker worker: workers) {
                if (worker != null && !worker.running) {
                    worker.running = true;
                    worker.input = jobInput;
                    new Thread(worker).start();
                    break;
                }
            }
        }
        for (int i = 0; i < workers.length; i++) {
            if (bundles[i] != null) {
                workers[i].postData(frame, bundles[i]);
            }
        }
        return frame;
    }

    public class JobHandler extends Handler {
        private final int index;

        public JobHandler(int index) {
            this.index = index;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            bundles[index] = data;
            workers[index].running = false;
            workers[index].updateUI(data);
            Log.i("job handler", "recv: " + String.valueOf(index));
        }
    }

}
