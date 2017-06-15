package com.example.nicholasliu.ftc2016_17cv;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

//TODO: Find a way to calculate translational movement needed to center onto line
//TODO: See if there is a way to rotate the view of the JavaCamera
//TODO: Add Dropdown Menu for viewing different filters(HSV, Mask, etc.)

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "FTC_CV::Activity";

    private JavaCameraView mOpenCvCameraView;

    private Scalar mLowerBound = new Scalar(0, 0, 200);
    private Scalar mUpperBound = new Scalar(255, 80, 255);

    private Mat mPyrDownMat, mHsvMat, mMask, mDilatedMask, mHierarchy;

    private List<MatOfPoint> contours = new ArrayList<>();
    private List<MatOfPoint> mContours = new ArrayList<>();

    RotatedRect rectCont;
    private Point[] vertices = new Point[4];

    int contourIndex = -1;
    double maxArea = 0, area;
    double lineLength, maxLength, deltaX, deltaY, longestX, longestY, lineIndex;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mPyrDownMat = new Mat();
                    mHsvMat = new Mat();
                    mMask = new Mat();
                    mDilatedMask = new Mat();
                    mHierarchy = new Mat();
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.preview);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //cDetector.process(inputFrame.rgba());
        return process(inputFrame.rgba());
        //return inputFrame.rgba();
    }
    public Mat process (Mat rgbaFrame){
        //TODO: Should size down frame for faster processing...
        //TODO: Need to figure out how to scale up contours and lines
        /*
        Imgproc.pyrDown(rgbaFrame, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        */

        //Convert to HSV
        Imgproc.cvtColor(rgbaFrame, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        //Filter by HSV Values
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        //TODO: Figure out what this
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        //clear variables for search
        maxArea = 0;
        contours.clear();
        mContours.clear();
        contourIndex = -1;

        //find Contours
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        //LOOP THROUGH ALL CONTOURS
        //filter out small contours & record largest
        for (int i = 0; i < contours.size(); i++){
            area = Imgproc.contourArea(contours.get(i));
            if (area > 400){
                if (area > maxArea) {
                    maxArea = area;
                    contourIndex = i;
                }
                mContours.add(contours.get(i));
            }
        }

        //check if an acceptable contour was found
        if (contourIndex > -1) {
            rectCont = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(contourIndex).toArray()));

            rectCont.points(vertices);
            Imgproc.putText(rgbaFrame, "Angle: " + String.valueOf(findAngle(vertices)), new Point(0,45), 0, 0.5, new Scalar(255, 0, 0));

            for (int j = 0; j < 4; j++) {
                Imgproc.putText(rgbaFrame, "Line" + j + ": " + getLineLength(vertices[j],vertices[(j + 1) % 4] ), new Point(0,60 + (j*15)), 0, 0.5, new Scalar(255, 0, 0));
                Imgproc.putText(rgbaFrame, Integer.toString(j) ,vertices[j], 0, 3, new Scalar(255, 255, 255));
                if (j == lineIndex)
                    Imgproc.line(rgbaFrame, vertices[j], vertices[(j + 1) % 4], new Scalar(0, 0, 255), 10);
                else
                    Imgproc.line(rgbaFrame, vertices[j], vertices[(j + 1) % 4], new Scalar(255, 0, 0), 5);
            }
        }

        //draw all large enough contours
        Imgproc.drawContours (rgbaFrame, mContours, -1, new Scalar(120,255,100), 5);

        //print out area of largest contour for finding size filter
        Imgproc.putText(rgbaFrame, "Area: " + String.valueOf(maxArea), new Point(0,30), 0, 0.5, new Scalar(255, 0, 0));

        return rgbaFrame;

    }

    public double findAngle(Point [] corners){
        maxLength = 0;
        for (int i = 0; i < 4; i++){
            //TODO: reimplement after completing getLineLength
            deltaX = (corners[i].x - corners[(i+1) % 4].x);
            deltaY = (corners[i].y - corners[(i+1) % 4].y);
            lineLength = Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
            if (lineLength > maxLength){ //goes by longest line

            //if (getLineLength(corners[i],corners[(i+1) % 4] ) > maxLength){ //goes by longest line
                maxLength = lineLength;
                //maxLength = getLineLength(corners[i],corners[(i+1) % 4]);
                longestX = deltaX;
                longestY = deltaY;
                lineIndex = i;
            }
        }
        return  Math.atan(longestX/longestY)*180/Math.PI;
    }
    //TODO: temp method for printing out line length
    // Go back to commented in findAngle to continue calculating angle value
    public double getLineLength (Point pt1, Point pt2){
        deltaX = Math.abs(pt1.x - pt2.x);
        deltaY = Math.abs(pt1.y - pt2.y);
        lineLength = Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
        return lineLength;
    }


}