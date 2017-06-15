import android.util.Log;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gordon on 5/14/2017.
 */

public class FaceFeatureTracker {
    private static final String TAG = "OCVTest::Tracker";

    public static final Size SIZE_480P = new Size(640,480);
    public static final Size SIZE_720P = new Size(1280,720);
    public static final Size SIZE_1080P = new Size(1920,1080);

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);
    private static final Scalar EYE_RECT_COLOR = new Scalar(255, 0, 0, 255);
    private static final Scalar EYE_CORNERS_COLOR = new Scalar(255, 155, 0, 255);
    private static final Scalar EYE_CENTER_COLOR = new Scalar(120, 250, 0, 255);
    private static final Scalar NOSE_TIP_COLOR = new Scalar(120, 250, 0, 255);
    private static final Scalar MOUTH_CORNERS_COLOR = new Scalar(0, 120, 200, 255);
    private static final int POINT_DRAW_RADIUS = 2;

    private static final int EYE_LEFT_INNER = 0;
    private static final int EYE_LEFT_OUTER = 4;
    private static final int EYE_RIGHT_INNER = 1;
    private static final int EYE_RIGHT_OUTER = 5;
    private static final int NOSE_TIP = 6;
    private static final int MOUTH_LEFT = 2;
    private static final int MOUTH_RIGHT = 3;

    private static final Point3 EYE_LEFT_OUTER_3D = new Point3(-225.0, 170.0, -135.0);
    private static final Point3 EYE_RIGHT_OUTER_3D = new Point3(225.0, 170.0, -135.0);
    private static final Point3 NOSE_TIP_3D = new Point3(0.0, 0.0, 0.0);
    private static final Point3 MOUTH_LEFT_3D = new Point3(-150.0, -150.0, -125.0);
    private static final Point3 MOUTH_RIGHT_3D = new Point3(150.0, -150.0, -125.0);

    private FaceDetector faceDetector;
    private EyeDetector eyeDetector;
    private EyeCenterDetector eyeCenterDetector;
    private FlandmarkDetector flandmarkDetector;

    private Rect eyesDetectROI;
    private double eyesROITopBound = 0.2; //search for eyes starting from top 0.2 of face
    private double eyesROIBotBound = 0.5; //search for eyes ending at top 0.5 of face

    private Rect faceRect;
    private MatOfRect eyesMatOfRect;
    private Rect eyeRect;

    private Point eyeCenter;
    private Point noseTipEnd;

    private List<Point> faceFeatures;
    private List<Point> faceFeatures2D;
    private List<Point3> faceFeatures3D;
    private double[] pitchYawRoll;

    public Rect debugRect;
    public Mat debugMat;

    public enum Detector{
        FACE,
        EYE,
    }

    FaceFeatureTracker(File faceCascade, File eyeCascade, File flandmarkModel){
        faceDetector = new FaceDetector(faceCascade);
        eyeDetector = new EyeDetector(eyeCascade);
        eyeCenterDetector = new EyeCenterDetector();
        flandmarkDetector = new FlandmarkDetector(flandmarkModel.getAbsolutePath());
        faceFeatures3D = new ArrayList<>();
        faceFeatures3D.add(EYE_LEFT_OUTER_3D);
        faceFeatures3D.add(EYE_RIGHT_OUTER_3D);
        faceFeatures3D.add(NOSE_TIP_3D);
        faceFeatures3D.add(MOUTH_LEFT_3D);
        faceFeatures3D.add(MOUTH_RIGHT_3D);

        if(faceDetector.ready() && eyeDetector.ready() && flandmarkDetector.ready()){
            Log.i(TAG, "Tracker ready");
        }else {
            Log.e(TAG, "Tracker load failed!");
        }
    }
    public Point getEyeCenter(){
        return eyeCenter;
    }
    public Point[] getEyeCorners(){
        if(eyeCenter.x < faceRect.x+faceRect.width/2){
            return new Point[]{faceFeatures.get(EYE_LEFT_OUTER),faceFeatures.get(EYE_LEFT_INNER)};
        }else{
            return new Point[]{faceFeatures.get(EYE_RIGHT_INNER), faceFeatures.get(EYE_RIGHT_OUTER)};
        }
    }
    public double getPitch(){
        return pitchYawRoll[0];
    }

    public void setDetectorParameters(Detector type, double minSize){
        switch(type){
            case FACE:
                faceDetector.setDetectorParameters(minSize);
                break;
            case EYE:
                eyeDetector.setDetectorParameters(minSize);
                break;
        }
    }
    public void setDetectorParameters(Detector type, double scale, int neighbors){
        switch(type){
            case FACE:
                faceDetector.setDetectorParameters(scale, neighbors);
                break;
            case EYE:
                eyeDetector.setDetectorParameters(scale, neighbors);
                break;
        }
    }
    public void setDetectorParameters(Detector type, double scale, int neighbors, double minSize){
        switch(type){
            case FACE:
                faceDetector.setDetectorParameters(scale, neighbors, minSize);
                break;
            case EYE:
                eyeDetector.setDetectorParameters(scale, neighbors, minSize);
                break;
        }
    }

    public void detect(Mat img){
        clearAll();
        debugRect = new Rect(0,0,img.width(),img.height());

        List<Rect> faces = faceDetector.detect(img).toList();
        if(faces.size() > 0){

            faceRect = new Rect();
            for(Rect r : faces){
                if(r.area() > faceRect.area()){ faceRect = r; }
            }

            processEyes(img);

            faceFeatures = flandmarkDetector.detect(img,faceRect);
            if(faceFeatures!=null){
                estimateHeadPose(img);
            }
        }
    }

    public void draw(Mat img){
        if(faceRect != null){

            Imgproc.rectangle(img, faceRect.tl(), faceRect.br(), FACE_RECT_COLOR, 2); // draw face

            //draw eyes
            if(eyesMatOfRect != null) {
                for (Rect eye : eyesMatOfRect.toArray()) {
                    Imgproc.rectangle(img, Utilities.sum(eye.tl(), eyesDetectROI.tl()),
                            Utilities.sum(eye.br(), eyesDetectROI.tl()), EYE_RECT_COLOR, 2);
                }

                if(eyeCenter != null){
                    Point p = eyeCenter;
                    Imgproc.circle(img, p, POINT_DRAW_RADIUS, EYE_CENTER_COLOR,5);
                }

            }

            if(faceFeatures!=null){

                for(int i=0;i<faceFeatures.size();i++){
                    Point p = faceFeatures.get(i);
                    if(i==EYE_LEFT_INNER || i==EYE_LEFT_OUTER || i==EYE_RIGHT_INNER || i==EYE_RIGHT_OUTER){
                        Imgproc.circle(img,p, POINT_DRAW_RADIUS, EYE_CORNERS_COLOR, 5);
                    }else if(i==NOSE_TIP){
                        Imgproc.circle(img,p, POINT_DRAW_RADIUS, NOSE_TIP_COLOR, 5);
                    }else if(i==MOUTH_LEFT || i==MOUTH_RIGHT){
                        Imgproc.circle(img,p, POINT_DRAW_RADIUS, MOUTH_CORNERS_COLOR, 5);
                    }
                }

                if(noseTipEnd!=null){
                    Imgproc.line(img,faceFeatures.get(NOSE_TIP),noseTipEnd, EYE_RECT_COLOR,5);
                    Imgproc.putText(img,"pitch: "+pitchYawRoll[0],new Point(img.cols()-700,img.rows()-100), Core.FONT_HERSHEY_COMPLEX,1.3,new Scalar(0,255,255), 5);
                }
            }
        }
    }
    private void estimateHeadPose(Mat img){
        if(faceFeatures != null){
            faceFeatures2D = new ArrayList<>();
            faceFeatures2D.add(faceFeatures.get(EYE_LEFT_OUTER));
            faceFeatures2D.add(faceFeatures.get(EYE_RIGHT_OUTER));
            faceFeatures2D.add(faceFeatures.get(NOSE_TIP));
            faceFeatures2D.add(faceFeatures.get(MOUTH_LEFT));
            faceFeatures2D.add(faceFeatures.get(MOUTH_RIGHT));

            MatOfPoint2f features2D = new MatOfPoint2f();
            MatOfPoint3f features3D = new MatOfPoint3f();
            features2D.fromList(faceFeatures2D);
            features3D.fromList(faceFeatures3D);

            double focalLength = img.cols();
            Point center = new Point(img.cols()/2, img.rows()/2);
            Mat cameraMatrix = new Mat(3,3,CvType.CV_64FC1);
            cameraMatrix.put(0,0,focalLength,0,center.x,0,focalLength,center.y,0,0,1);
            MatOfDouble distCoeffs = new MatOfDouble(0.0, 0.0, 0.0, 0.0);

            Mat rotationVector = new Mat();
            Mat translationVector = new Mat();

            Calib3d.solvePnP(features3D, features2D,cameraMatrix,distCoeffs,rotationVector,translationVector);

            //getEulerAngles(rotationVector);
            for(int i=0;i<3;i++){
                pitchYawRoll[i] = rotationVector.get(i,0)[0];
            }

            MatOfPoint3f noseEndPoint3D = new MatOfPoint3f();
            MatOfPoint2f noseEndPoint2D = new MatOfPoint2f();
            List<Point3> tmp = new ArrayList<>();
            tmp.add(new Point3(0,0,1000.0));
            noseEndPoint3D.fromList(tmp);

            Calib3d.projectPoints(noseEndPoint3D,rotationVector,translationVector,cameraMatrix,distCoeffs,noseEndPoint2D);
            if(!noseEndPoint2D.empty()){
                noseTipEnd = noseEndPoint2D.toArray()[0];
            }

            features2D.release();
            features3D.release();
            cameraMatrix.release();
            distCoeffs.release();
            rotationVector.release();
            translationVector.release();
            noseEndPoint2D.release();
            noseEndPoint3D.release();
        }
    }
    private void getEulerAngles(Mat rotationVector){
        Mat rotationMatrix = new Mat();
        Calib3d.Rodrigues(rotationVector,rotationMatrix);

        Mat cameraMatrix = new Mat();
        Mat rotMatrix = new Mat();
        Mat transVector = new Mat();
        Mat rotMatrixX = new Mat();
        Mat rotMatrixY = new Mat();
        Mat rotMatrixZ = new Mat();
        Mat eulerAngles = new Mat();
        Mat projMatrix = new Mat(3,4,CvType.CV_64FC1);

        //contents of rotation matrix
        double[] r = new double[(int)(rotationMatrix.total()*rotationMatrix.channels())];
        rotationMatrix.get(0,0,r);
        projMatrix.put(0,0,
                r[0],r[1],r[2],0,
                r[3],r[4],r[5],0,
                r[6],r[7],r[8],0);

        Calib3d.decomposeProjectionMatrix(projMatrix,cameraMatrix,rotMatrix,transVector,rotMatrixX,rotMatrixY,rotMatrixZ,eulerAngles);
        eulerAngles.get(0,0,pitchYawRoll);

        cameraMatrix.release();
        rotMatrix.release();
        transVector.release();
        rotMatrixX.release();
        rotMatrixY.release();
        rotMatrixZ.release();
        eulerAngles.release();
        projMatrix.release();
    }

    private void processEyes(Mat img){
        Point eyesROITopLeft = new Point(faceRect.tl().x, faceRect.tl().y + faceRect.height*eyesROITopBound);
        Point eyesROIBotRight = new Point(faceRect.br().x, faceRect.tl().y + faceRect.height*eyesROIBotBound);
        eyesDetectROI = new Rect(eyesROITopLeft, eyesROIBotRight);
        eyesMatOfRect = eyeDetector.detect(img.submat(eyesDetectROI));
        if(!eyesMatOfRect.empty()){
            eyeRect = eyesMatOfRect.toArray()[0];
            eyeRect.x += eyesDetectROI.x;
            eyeRect.y += eyesDetectROI.y;

            detectEyeCenter(img, eyeRect);
        }
    }
    private void detectEyeCenter(Mat img, Rect eye){
        double fastResizeWidth = 18;

        Mat ROI = img.submat(eye).clone();
        resizeImageToMax(ROI, new Size(fastResizeWidth, fastResizeWidth*ROI.rows()/ROI.cols()));

        eyeCenter = eyeCenterDetector.detectEyeCenter(ROI);
        eyeCenter = unscalePoint(eyeCenter,ROI.width(),eye.width);
        eyeCenter.x += eye.x;
        eyeCenter.y += eye.y;

        Log.i("OCVTest::foreground",Thread.currentThread().toString());
    }

    private void clearAll(){
        faceRect = null;
        eyesMatOfRect = null;
        eyeRect = null;
        eyeCenter = null;
        noseTipEnd = null;
        faceFeatures = null;
        pitchYawRoll = new double[3];
        debugMat = null;
    }

    public void resizeImageToMax(Mat img, Size maxSize){
        if(Utilities.compare(img.size(), maxSize) == Utilities.COMP_GREATER){
            Imgproc.resize(img, img, maxSize);
        }
    }
    private Point unscalePoint(Point p, int resized, int original){
        float ratio = (float)resized/ original;
        int x = (int)Math.round(p.x / ratio);
        int y = (int)Math.round(p.y / ratio);
        return new Point(x,y);
    }

    public void debugTo(Mat img){
        debugMat = new Mat(img.size(),img.type(), new Scalar(255,255,255));
        img.submat(debugRect).copyTo(debugMat.submat(debugRect));
    }
}