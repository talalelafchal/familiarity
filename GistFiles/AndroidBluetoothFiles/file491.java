package com.sigtuple.aadi.autoscan.blood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.sigtuple.aadi.asynctasks.SaveToFile;
import com.sigtuple.aadi.autoscan.ASConstants;
import com.sigtuple.aadi.autoscan.BaseScanner;
import com.sigtuple.aadi.bluetooth.BLECommand;
import com.sigtuple.aadi.bluetooth.BLEConstants;
import com.sigtuple.aadi.bluetooth.BluetoothLeService;
import com.sigtuple.aadi.bluetooth.BluetoothLeServiceAdapter;
import com.sigtuple.aadi.events.OnBLETaskCompleted;
import com.sigtuple.aadi.events.RequestImageCapture;
import com.sigtuple.aadi.events.RequestNextScan;
import com.sigtuple.aadi.interfaces.ScannerController;
import com.sigtuple.aadi.models.QIFactors;
import com.sigtuple.aadi.models.Slide;
import com.sigtuple.aadi.processing.image.ImageUtils;
import com.sigtuple.aadi.utils.LogWrapper;
import com.sigtuple.aadi.utils.SharedPrefUtils;
import com.sigtuple.aadi.utils.URLUtils;
import com.sigtuple.aadi.views.CustomAlertDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.HashMap;

/**
 * Created by prateek on 12/8/16.
 */
public class BloodScanner extends BaseScanner {

    private BloodScanPlan mPlan;
    private HashMap<Integer, JSONObject> sequenceObjects;
    private ScannerController controller;

    public static final String ACTION_TAKE_PICTURE = "action_picture";
    public static final String ACTION_AUTO_SCAN  = "action_auto_scan";

    int autoScanState;
    int prevAutoScanState;
    int autoScanStateArg;
    int vertical = 0;
    int horizontal = 0;
    int numImages;
    Mat autoScanStateObj;
    Bitmap bitmap;
    boolean isScanning = false;
    int x;
    int y;

    Slide slide;

    Handler mhandler = new Handler();
    Context context;

    public static final String TAG = "BloodScanner";

    public BloodScanner(BluetoothLeServiceAdapter adapter) {
        super(adapter);
        mPlan = new BloodScanPlan();
        x = 0;
        y = 0;
        sequenceObjects = new HashMap<>();


    }

    @Override
    public void start(Context context) {
        mPlan = new BloodScanPlan();
        // prepareNextCommand(new RequestNextScan());
        this.context = context;
        autoScan();
    }

    @Override
    public void subscribe(ScannerController controller) {
        this.controller = controller;
    }

    public void ackReceivedFromBluetooth(Intent intent) {
        if (!ASConstants.AUTO_SCAN)
            return;

        String status = intent.getStringExtra("status");
        int seqId = intent.getIntExtra("seq", 0);
        JSONObject seqObj = getJsonForSeq(seqId);
        String opType = "";

        try {
            if (seqObj != null) {
                opType = seqObj.getString("optype");
                setCoordinates(seqObj.getString("opcode").charAt(0), seqObj.getInt("oparg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (status != null && !status.equals(URLUtils.STATUS_OK)) {
            if (autoScanState == BLEConstants.AUTOSCAN_STATE_CONN_RETRY || bleAdapter.isConnected()) {
                //handle error
                CustomAlertDialog alertDialog = new CustomAlertDialog(context, "BLE Communication Error", "Stopping Auto Scan");
                alertDialog.showAlertDialog();
                return;
            } else {
                //try reconnecting
                bleAdapter.reconnect();
                prevAutoScanState = autoScanState;
                autoScanState = BLEConstants.AUTOSCAN_STATE_CONN_RETRY;

            }
        } else if (autoScanState == BLEConstants.AUTOSCAN_STATE_CONN_RETRY) {
            autoScanState = prevAutoScanState;
        }

        int origState = autoScanState;


        switch (autoScanState) {
            case BLEConstants.AUTOSCAN_STATE_START:
                autoScanState = BLEConstants.AUTOSCAN_STATE_SET_SPEED;
                autoScanStateArg = 0;
                setSpeed(ASConstants.AUTO_SCAN_SPEED, true);
                break;
            case BLEConstants.AUTOSCAN_STATE_SET_SPEED:
                autoScanState = BLEConstants.AUTOSCAN_STATE_HOME_X;
                autoScanStateArg = 0;
                homeX(true);
                break;
            case BLEConstants.AUTOSCAN_STATE_HOME_X:
                autoScanState = BLEConstants.AUTOSCAN_STATE_HOME_Y;
                autoScanStateArg = 0;
                homeY(true);
                break;
            case BLEConstants.AUTOSCAN_STATE_HOME_Y:
                autoScanState = BLEConstants.AUTOSCAN_STATE_HOME_OFFSET_X;
                autoScanStateArg = 0;
                if (ASConstants.AUTOSCAN_HOME_OFFSET_X > 0) {
                    if (ASConstants.AUTOSCAN_HOME_OFFSET_X > ASConstants.AUTOSCAN_MAX_SINGLE_STEP) {
                        autoScanStateArg += ASConstants.AUTOSCAN_MAX_SINGLE_STEP;
                    }
                    moveX(Math.min(ASConstants.AUTOSCAN_HOME_OFFSET_X, ASConstants.AUTOSCAN_MAX_SINGLE_STEP), true);
                    break;
                }

            case BLEConstants.AUTOSCAN_STATE_HOME_OFFSET_X:
                if (ASConstants.AUTOSCAN_HOME_OFFSET_X > ASConstants.AUTOSCAN_MAX_SINGLE_STEP &&
                        autoScanStateArg < ASConstants.AUTOSCAN_HOME_OFFSET_X) {
                    LogWrapper.e(TAG, "Inside move x");
                    int toMove = Math.min(ASConstants.AUTOSCAN_MAX_SINGLE_STEP, (ASConstants.AUTOSCAN_HOME_OFFSET_X - autoScanStateArg));
                    autoScanStateArg += toMove;
                    moveX(toMove, true);
                    break;
                } else {
                    LogWrapper.e(TAG, "Inside else move y");
                    autoScanState = BLEConstants.AUTOSCAN_STATE_HOME_OFFSET_Y;
                    autoScanStateArg = 0;
                    LogWrapper.e(TAG, "Home offset Y : " + ASConstants.AUTOSCAN_HOME_OFFSET_Y + "Max single step is : " + ASConstants.AUTOSCAN_MAX_SINGLE_STEP);

                    if (ASConstants.AUTOSCAN_HOME_OFFSET_Y > 0) {
                        if (ASConstants.AUTOSCAN_HOME_OFFSET_Y > ASConstants.AUTOSCAN_MAX_SINGLE_STEP) {
                            autoScanStateArg += ASConstants.AUTOSCAN_MAX_SINGLE_STEP;
                        }
                        LogWrapper.e(TAG, "Moving Y now");
                        moveY(Math.min(ASConstants.AUTOSCAN_MAX_SINGLE_STEP, ASConstants.AUTOSCAN_HOME_OFFSET_Y), true);
                        break;
                    }
                }

            case BLEConstants.AUTOSCAN_STATE_HOME_OFFSET_Y:
                if (ASConstants.AUTOSCAN_HOME_OFFSET_Y > ASConstants.AUTOSCAN_MAX_SINGLE_STEP &&
                        autoScanStateArg < ASConstants.AUTOSCAN_HOME_OFFSET_Y) {
                    int toMove = Math.min(ASConstants.AUTOSCAN_MAX_SINGLE_STEP, (ASConstants.AUTOSCAN_HOME_OFFSET_Y - autoScanStateArg));
                    autoScanStateArg += toMove;
                    moveY(toMove, true);
                    break;
                } else {
                    LogWrapper.e(TAG, "State is now find state smear");

                    autoScanState = BLEConstants.AUTOSCAN_STATE_FIND_SMEAR;
                    ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS = 0;

                }


            case BLEConstants.AUTOSCAN_STATE_FIND_SMEAR:
                if (ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS == -1) {
                    LogWrapper.e(TAG, "start scanning");
                    //start scanning
                    autoScanState = BLEConstants.AUTOSCAN_STATE_SCAN;
                    ASConstants.reset();
                    //set vertical count since we will be starting at AUTOSCAN_HOME_OFFSET_X
                    vertical = Math.abs((ASConstants.AUTOSCAN_HOME_OFFSET_X - ASConstants.AUTOSCAN_HOME_SLIDE_START_X)) / Math.abs(ASConstants.AUTOSCAN_STEP_X);
                    autoScan();
                    //broadcast(ACTION_AUTO_SCAN);
                } else {
                    int findAttempts = ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS;
                    if (findAttempts >= (ASConstants.AUTOSCAN_SLIDE_LENGTH / Math.abs(ASConstants.AUTOSCAN_STEP_Y))) {
                        //TODO: end autoscan
                        // stopScan();

                    } else if (findAttempts == 0) {
                        ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS = findAttempts + 1;
                        moveY(ASConstants.AUTOSCAN_STEP_Y, true);
                    } else if (opType.equals("move")) {
                        // Todo
                        LogWrapper.e(TAG, "Resetting focus here");
                        ASConstants.reset();
                        controller.takePicture();
                        //broadcast(ACTION_TAKE_PICTURE);


                    } else {
                        if (ASConstants.FOCUS_ATTEMPTS >= 0) {
                            LogWrapper.e(TAG, "Taking picture now");
                             controller.takePicture();
                            //broadcast(ACTION_TAKE_PICTURE);
                        } else {
                            LogWrapper.e(TAG, "Moving now");
                            ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS = findAttempts + 1;
                            moveY(ASConstants.AUTOSCAN_STEP_Y, true);
                        }

                    }
                }
                break;
            case BLEConstants.AUTOSCAN_STATE_SCAN:
                if (opType.equals("move")) {
//                        try {
                            //Thread.sleep(ASConstants.AUTOSCAN_CAPTURE_DELAY);

                    controller.takePicture();

                   // broadcast(ACTION_TAKE_PICTURE);

                } else if (opType.equals("home") ||
                        opType.equals("speed") ||
                        opType.equals("reconn")) {

                    autoScan();
                   // broadcast(ACTION_AUTO_SCAN);


                } else {
                    //default and focus have the same handler
                    //[NSThread sleepForTimeInterval:1];
                    if (ASConstants.FOCUS_ATTEMPTS >= 0) {
                        controller.takePicture();
                        // broadcast(ACTION_TAKE_PICTURE);
                    } else {
                        autoScan();
                       // broadcast(ACTION_AUTO_SCAN);
                    }
                }
                break;

            default:
                break;
        }
        if (autoScanState != origState) {
            LogWrapper.e(TAG, "Auto scan state has been changed");
        }

    }
    public void setContext(Context context){
        this.context = context;
    }

    public void initiateScan() {
        Intent intent = new Intent(BluetoothLeService.BLUETOOTH_INFO);
        intent.putExtra("status", URLUtils.STATUS_OK);
        ackReceivedFromBluetooth(intent);
    }

    public void initiateReconnection() {
        Intent intent = new Intent(BluetoothLeService.BLUETOOTH_INFO);
        intent.putExtra("status", URLUtils.STATUS_ERR);
        ackReceivedFromBluetooth(intent);
    }


    public void autoScan() {

        if (!ASConstants.AUTO_SCAN) {
            vertical = 0;
            horizontal = 0;
            if (!bleAdapter.isConnected()) {
                CustomAlertDialog alertDialog = new CustomAlertDialog(context, "BLE Not Found", "Unable to connect to the BLE Device. Please try again after some time");
                alertDialog.showAlertDialog();
                return;
            }

            autoScanState = BLEConstants.AUTOSCAN_STATE_START;
            autoScanStateArg = 0;
            ASConstants.AUTO_SCAN = true;
            ASConstants.AUTO_SCAN_IMAGE_COUNT = 0;
            initiateScan();
            return;
        }
        if (horizontal >= (ASConstants.AUTOSCAN_SLIDE_LENGTH / Math.abs(ASConstants.AUTOSCAN_STEP_Y)) || ASConstants.AUTO_SCAN_IMAGE_COUNT >= ASConstants.AUTOSCAN_NUM_IMAGES) {
            // stop scan
            LogWrapper.e(TAG, "Stopping scan.");
            stopScan();
        }

        if (vertical > (ASConstants.AUTOSCAN_SLIDE_WIDTH / Math.abs(ASConstants.AUTOSCAN_STEP_X))) {
            vertical = 0;
            horizontal++;
            moveY(ASConstants.AUTOSCAN_STEP_Y, true);
            return;
        }
        //move
        if (horizontal % 2 == 0) {
            moveX(ASConstants.AUTOSCAN_STEP_X, true);
            vertical++;
        } else {
            moveX((-1 * ASConstants.AUTOSCAN_STEP_X), true);
            vertical++;
        }

    }

    public void setCoordinates(char opcode, int oparg) {
        if (opcode == 'h' && oparg == 1) {
            x = 0;
        } else if (opcode == 'h' && oparg == 2) {
            y = 0;
        } else if (opcode == 'h' && oparg == 3) {
            x = 0;
            y = 0;
        } else if (opcode == 'x') {
            x += oparg;
        } else if (opcode == 'y') {
            y += oparg;
        }
    }

    public void stopScan() {
        LogWrapper.e(TAG, "Stopping scan now");
        ASConstants.AUTO_SCAN = false;
        ASConstants.AUTO_SCAN_IMAGE_COUNT = 0;
        ASConstants.reset();
        numImages = 0;
        bleAdapter.disconnect();
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomAlertDialog dialog = new CustomAlertDialog(context, "Scan Stopped", "Stopped Auto scan");
                dialog.showAlertDialog();

            }
        });
    }


    @Subscribe
    public void onBLETaskDone(OnBLETaskCompleted event) {
        // capture image and fire next command
        LogWrapper.i(EventBus.TAG, "Handle OnBLETaskCompleted event");
        EventBus.getDefault().post(new RequestImageCapture());
    }

    public void moveX(int offset, boolean withAsync) {
        byte[] message = BLECommand.prepareMessage(BLECommand.MOVE_X, offset);
        addSequenceInfo(message, "x", offset);
        bleAdapter.sendMessage(message);
    }

    public void moveY(int offset, boolean withAsync) {
        byte[] message = BLECommand.prepareMessage(BLECommand.MOVE_Y, offset);
        addSequenceInfo(message, "y", offset);
        bleAdapter.sendMessage(message);
    }

    public void homeX(boolean withAsync) {
        byte[] message = BLECommand.prepareMessage(BLECommand.HOME, 1);
        addSequenceInfo(message, "h", 1);
        bleAdapter.sendMessage(message);
    }

    public void homeY(boolean withAsync) {
        byte[] message = BLECommand.prepareMessage(BLECommand.HOME, 2);
        addSequenceInfo(message, "h", 2);
        bleAdapter.sendMessage(message);
    }

    public void setSpeed(int speed, boolean withAsync) {
        byte[] message = BLECommand.prepareMessage(BLECommand.SETSPEED, speed);
        addSequenceInfo(message, "s", speed);
        bleAdapter.sendMessage(message);
    }

    public void focus(int offset, boolean withAsync) {
        LogWrapper.e(TAG, "Focus offset is : " + offset);
        byte[] message = BLECommand.prepareMessage(BLECommand.FOCUS, offset);
        addSequenceInfo(message, "f", offset);
        bleAdapter.sendMessage(message);
    }


    public void addSequenceInfo(byte[] data, String opCode, int oparg) {
        int seqID = data[BLECommand.SEQ_NUMBER];
        LogWrapper.e(TAG, "opcode is :  " + opCode);
        String opType = BLECommand.getOpType(opCode);
        JSONObject seqObj = new JSONObject();
        try {
            seqObj.put("opcode", opCode);
            seqObj.put("optype", opType);
            seqObj.put("seq", seqID);
            seqObj.put("oparg", oparg);
            sequenceObjects.put(seqID, seqObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean isGoodImage(final QIFactors factors) {

        double sharpnessThreshold = ASConstants.IMAGE_BLUR_THRESHOLD;


        //Add normalised sharpness threshoold
        double cappedDensity = 0.0;
        if(factors.density>=ASConstants.IMAGE_NORM_CAPPED_DENSITY_THRESHOLD){
            cappedDensity = ASConstants.IMAGE_NORM_CAPPED_DENSITY_THRESHOLD;
        }
        else{
            cappedDensity = factors.density;
        }
        double normalisedSharpness = factors.sharpness * (1 - cappedDensity);
        double normalisedCount = factors.cell_count / (factors.density + 0.001);
        factors.setNormalised_count(normalisedCount);
        factors.setNormalised_sharpness(normalisedSharpness);

        LogWrapper.e(TAG, "Cell count : " + factors.cell_count + ", sharpness : " + factors.sharpness + ", Density is : " + factors.density);

        if (autoScanState == BLEConstants.AUTOSCAN_STATE_FIND_SMEAR) {
            sharpnessThreshold = 0.8 * sharpnessThreshold;
        } else {
            // Unused code
            if (factors.density >= 2.5 * ASConstants.SCAN_DENSITY_MIN_THRESHOLD) {
                sharpnessThreshold = 1.1 * ASConstants.IMAGE_BLUR_THRESHOLD;
            }

        }

        if (factors.density >= ASConstants.IMAGE_DENSITY_MIN_THRESHOLD && factors.density <= ASConstants.IMAGE_DENSITY_MAX_THRESHOLD &&
                factors.cell_count >= ASConstants.SCAN_CELL_COUNT_THRESHOLD
                ) {

            if (autoScanState == BLEConstants.AUTOSCAN_STATE_FIND_SMEAR && factors.sharpness >= sharpnessThreshold) {
                //
                //  showToast("It's a good image");
                LogWrapper.e(TAG, "Checking for good image");
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "It is a good image", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            } else if (autoScanState != BLEConstants.AUTOSCAN_STATE_FIND_SMEAR && factors.sharpness >= ASConstants.IMAGE_BLUR_THRESHOLD) /**&&
                    factors.normalised_sharpness >= ASConstants.IMAGE_NORM_SHARPNESS_THRESHOLD &&
                    factors.normalised_count >= ASConstants.IMAGE_NORM_CELL_COUNT_THRESHOLD)**/ {
                //          showToast("It's a good image");
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "It is a good image", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
        }
        final double finalSharpnessThreshold = sharpnessThreshold;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogWrapper.e(TAG, "Not a good image . Cell count : " + factors.cell_count + ", sharpness : " + factors.sharpness + ", Density is : " + factors.density + ", Sharpness threshold is : " + finalSharpnessThreshold + ", brightness_index") ;
                Toast.makeText(context, "It is not a good image", Toast.LENGTH_SHORT).show();
            }
        });
        return false;


    }




    public void checkForRefocus(QIFactors factors, int bestDirection, Mat matrix, int fstep, int focusBestOffset) {
        LogWrapper.e(TAG, "Checking for refocus here");
        LogWrapper.e(TAG, "Best sharpness is : " + ASConstants.FOCUS_BEST_SHARPNESS + "sharpness is : " + factors.sharpness);
        int prevDirection = ASConstants.FOCUS_PREV_DIRECTION;
        int focusStep = fstep;
        int focusOffset = ASConstants.FOCUS_OFFSET;

        float roundedSharpness = (float)(Math.round(factors.sharpness * 100.0) / 100.0);
        float roundedBestSharpness = (float)Math.round(ASConstants.FOCUS_BEST_SHARPNESS);
        LogWrapper.e(TAG, "Cell count : " + factors.cell_count + ", sharpness : " + factors.sharpness + ", Density is : " + factors.density);
        if (roundedSharpness > roundedBestSharpness && factors.density > 0
                )  {
            ASConstants.FOCUS_BEST_OFFSET = (-1 * focusStep * prevDirection);

            ASConstants.FOCUS_OFFSET += (-1 * focusStep * prevDirection);
            ASConstants.FOCUS_DIRECTION_STEPS += 1;
            ASConstants.setFocusQIFactors(factors);

            if (bestDirection != prevDirection)
                ASConstants.FOCUS_BEST_DIRECTION = prevDirection;

            if (autoScanStateObj != null) {
                autoScanStateObj.release();

            }

            autoScanStateObj = new Mat(matrix.rows(), matrix.cols(), CvType.CV_8UC4);
            if(autoScanStateObj != null)
                matrix.copyTo(autoScanStateObj);


            focus(prevDirection * focusStep, true);
            return;

        } else {
            tuneInDifferentDirection(focusBestOffset);
        }

    }

    public void tuneInDifferentDirection(int focusBestOffset) {
        LogWrapper.e(TAG, "Tuning in different direction");
        // LogWrapper.e(TAG, "Size of autoscan obj is : " + autoScanStateObj.size());
        if (ASConstants.FOCUS_DIRECTION_CHANGES > 0 || (ASConstants.FOCUS_DIRECTION_STEPS > 1 && autoScanStateObj != null)) {

            ASConstants.reset();
            doImageQualityCheck(focusBestOffset);
        } else {
            int prevDirection = ASConstants.FOCUS_PREV_DIRECTION;
            int focusOffset = ASConstants.FOCUS_OFFSET;
            double initialStep = ASConstants.FOCUS_INITIAL_STEP;


            ASConstants.FOCUS_PREV_DIRECTION = (-1 * prevDirection);
            ASConstants.FOCUS_DIRECTION_CHANGES++;
            ASConstants.FOCUS_DIRECTION_STEPS = 1;
            ASConstants.FOCUS_BEST_OFFSET += (int) (-1 * focusOffset + prevDirection * initialStep);
            ASConstants.FOCUS_OFFSET = (int) (prevDirection * initialStep);
            LogWrapper.i(TAG, "Tuning in different direction FocusOffset : " + focusOffset + ", PrevDirection :" + prevDirection + ", Initial Step : " + initialStep);

            focus((int) (focusOffset - 1 * prevDirection * initialStep), true);

        }

    }

    public void doImageQualityCheck(int bestOffset) {
//        autoScanStateObj = new Mat();
//        Utils.bitmapToMat(bitmap, autoScanStateObj);
        float imageQualitySharpness = ImageUtils.calculateSharpness(autoScanStateObj, ASConstants.IMAGE_QUALITY_PATCH_SIZE);
        QIFactors bestfactors = ImageUtils.calculateImageProperties(autoScanStateObj, ASConstants.IMAGE_QUALITY_PATCH_SIZE, ASConstants.SCAN_CELL_COUNT_MIN_AREA, ASConstants.SCAN_CELL_COUNT_MAX_AREA);
        if (bestfactors == null)
            return;

        bestfactors.setSharpness(imageQualitySharpness);

        LogWrapper.e(TAG, "brightness : " + bestfactors.brightness_index + "cell_count : " + bestfactors.cell_count);

        if (isGoodImage(bestfactors)) {
            if (autoScanState == BLEConstants.AUTOSCAN_STATE_FIND_SMEAR) {
                ASConstants.AUTO_SCAN_FIND_SMEAR_ATTEMPTS = -1;
                LogWrapper.e(TAG, "Focus best offset is : " + ASConstants.FOCUS_BEST_OFFSET);
                focus(bestOffset, true);
                autoScanStateObj.release();
                autoScanStateObj = null;

            } else {

                bestfactors.setCoord_x(x);
                bestfactors.setCoord_y(y);
                bitmap = ImageUtils.convertBGRToBitmap(bitmap, autoScanStateObj);
                SaveToFile saveToFile = new SaveToFile(slide, bestfactors, bitmap);
                saveToFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                controller.incrementPictureCount();
                ASConstants.AUTO_SCAN_IMAGE_COUNT = ++numImages;
                focus(bestOffset, true);
                autoScanStateObj.release();
                autoScanStateObj = null;


            }
        } else {
            LogWrapper.e(TAG, "Focusing now again");
            focus(bestOffset, true);

            autoScanStateObj.release();
            autoScanStateObj = null;


        }
        autoScanStateObj = null;
    }


    public JSONObject getJsonForSeq(int seqId) {
        return sequenceObjects.get(seqId);
    }

    @Override
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void prepareNextCommand(RequestNextScan event) {
        if (mPlan.hasNext()) {
            // ask plan object to prepare the command
            // and internally revise the next step
            mPlan.count++;
            byte[] message = BLECommand.prepareMessage(BLECommand.MOVE_X, 20);
            bleAdapter.sendMessage(message);
        }
    }


    @Override
    public void analyseImage(Mat matrix) {

        int patchSize = ASConstants.SCAN_QUALITY_PATCH_SIZE;




        int minArea = ASConstants.SCAN_CELL_COUNT_MIN_AREA;
        int maxArea = ASConstants.SCAN_CELL_COUNT_MAX_AREA;

        QIFactors factors = ImageUtils.calculateImageProperties(matrix, patchSize, minArea, maxArea);
        float sharpness = ImageUtils.calculateSharpness(matrix, patchSize);

        double imageNormDensityThreshold = ASConstants.IMAGE_NORM_CAPPED_DENSITY_THRESHOLD;
        double cappedDensity = 0.0;
        if (factors.density >= imageNormDensityThreshold) {
            cappedDensity = imageNormDensityThreshold;
        } else {
            cappedDensity = factors.density;
        }

        factors.setSharpness(sharpness);
        final double normalisedSharpness = factors.sharpness * (1 - cappedDensity);
        double normalisedCount = factors
                .cell_count / (factors.density + 0.001);
        factors.setNormalised_count(normalisedCount);
        factors.setNormalised_sharpness(normalisedSharpness);
        LogWrapper.e(TAG, "Cell count : " + factors.cell_count + ", sharpness : " + factors.sharpness + ", Density is : " + factors.density);
        LogWrapper.e(TAG, "Focus attempts is : " + ASConstants.FOCUS_ATTEMPTS);

        int focusStep = Integer.valueOf(SharedPrefUtils.getValueForKey(ASConstants.AUTOSCANFOCUSSTEP));
        int bestDirection = ASConstants.FOCUS_BEST_DIRECTION;

        if (bestDirection == 0) {
            bestDirection = -1;
        }

        LogWrapper.e(TAG, "Best direction is : " + bestDirection);
        LogWrapper.e(TAG, "Focus  step is : " + focusStep);


        if ((sharpness <= 0.125 * ASConstants.SCAN_BLUR_THRESHOLD || (factors.density < 0.125 * ASConstants.SCAN_DENSITY_MIN_THRESHOLD)) ) {
            focusStep *= 4;
        } else if ((sharpness <= 0.25 * ASConstants.SCAN_BLUR_THRESHOLD || (factors.density < 0.5 * ASConstants.SCAN_DENSITY_MIN_THRESHOLD)) ) {
            focusStep *= 2;
        } else if (sharpness >= 0.85 * ASConstants.SCAN_BLUR_THRESHOLD && factors.density >= ASConstants.SCAN_DENSITY_MIN_THRESHOLD) {
            focusStep *= 0.25;

        } else if (sharpness >= 0.75 * ASConstants.SCAN_BLUR_THRESHOLD && factors.density >= ASConstants.SCAN_DENSITY_MIN_THRESHOLD) {
            focusStep *= 0.5;
        }

        ASConstants.FOCUS_STEP = focusStep;


        if (ASConstants.FOCUS_ATTEMPTS == -1) {
            if (factors.density < ASConstants.SCAN_DENSITY_MIN_THRESHOLD / 8 && autoScanState == BLEConstants.AUTOSCAN_STATE_FIND_SMEAR) {
                ASConstants.reset();
                matrix.release();
                focus(0, true);
                return;
            }

            ASConstants.intialise(focusStep, bestDirection, factors.sharpness, factors.density, factors.cell_count, factors.brightness_index, factors.color_index);
            autoScanStateObj = new Mat(matrix.rows(), matrix.cols(), CvType.CV_8UC4);
            matrix.copyTo(autoScanStateObj);
            focus((bestDirection * focusStep), true);
            return;
        } else {
            // Check for focus attempt value and if it's within the range
            int focusBestOffset = ASConstants.FOCUS_BEST_OFFSET;
            if (ASConstants.FOCUS_ATTEMPTS < ASConstants.AUTOSCAN_MAX_FOCUS_ATTEMPTS) {
                ASConstants.FOCUS_ATTEMPTS += 1;
                LogWrapper.e(TAG, "Focus attempts is : " + ASConstants.FOCUS_ATTEMPTS);
                checkForRefocus(factors, bestDirection, matrix, focusStep, focusBestOffset);

            } else {
                ASConstants.reset();
                doImageQualityCheck(focusBestOffset);
            }
        }


    }

   public void broadcast(String action){
       Intent intent = new Intent(action);
       context.sendBroadcast(intent);
   }

    @Override
    public void onSlideLoaded(Slide slide) {
        this.slide = slide;
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }
}
