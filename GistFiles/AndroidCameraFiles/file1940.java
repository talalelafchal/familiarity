import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Tyler on 10/21/2014.
 */
public class CameraStateManager {

    private static MediaRecorder recorder;
    private static String mediaRecorderState;

    public CameraStateManager(MediaRecorder recorder){
        CameraStateManager.recorder=recorder;
    }

    private void prepareRecorder(){
        try{
            Log.i(this.toString(), "Putting media recorder in PREPARED state from " + mediaRecorderState + " state");
            if(mediaRecorderState.equals("DATASOURCECONFIGURED")) {
                recorder.prepare();
                mediaRecorderState = "PREPARED";
            }else{
                Log.d(this.toString(),"Putting media recorder in PREPARED state from "+mediaRecorderState+" state FAILED");
            }
        }catch(IOException ex){
            Log.d("pfaff","FUCK"+ex.toString());
        }
    }

    private void stopRecorder(){
        Log.i(this.toString(),"Putting recorder in STOPPED state from "+mediaRecorderState+" state");
        if(mediaRecorderState.equals("RECORDING")) {
            try {
                //if the recorder is stopped to quickly after being started, an exception will be thrown
                recorder.stop();
            }catch(RuntimeException stoppedTooSoonException){
                Log.d(this.toString(),"Recorder stopped before it fully started "+stoppedTooSoonException.toString());
                //delete the output file, because it has nothing in it
               // this.getActivity().deleteFile(path);
            }
            mediaRecorderState="INITIAL";
        }else{
            Log.d(this.toString(),"Putting recorder in STOPPED state FAILED");
        }
    }

    private void resetRecorder(){
        //Recorder can be reset from any state
        Log.i(this.toString(),"Putting recorder in INITIAL state from "+mediaRecorderState+" state");
        if(mediaRecorderState.equals("RELEASED")) {
            recorder.reset();
            mediaRecorderState="INITIAL";
        }else{
            Log.d(this.toString(),"Putting recorder in INITIAL state failed, must not be in a RELEASED state");
        }
    }

    private void releaseRecorder(){
        Log.i(this.toString(),"Putting recorder in RELEASED state from "+mediaRecorderState+" state");
        if(mediaRecorderState.equals("INITIAL")) {
            recorder.release();
            mediaRecorderState="RELEASED";
        }else{
            Log.d(this.toString(),"Putting recorder in RELEASED state failed, must be in INITIAL state");
        }
    }

    private void startRecording() {

        Log.i(TAG,"Putting recorder in RECORDING state from "+mediaRecorderState+" state");

        if(mediaRecorderState.equals("PREPARED")) {
            recorder.start();
            mediaRecorderState = "RECORDING";
        }else{
            Log.i(TAG,"Putting recorder in RECORDING state failed from "+mediaRecorderState+" state");
        }
    }

    private void initRecorder() {


        /*final PackageManager pm = activity.getPackageManager();

        boolean opened=false;
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Log.d("pfaff", "has front facing camera");
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Log.d("pfaff", "has back facing camera");
                opened = safeCameraOpen(1); //this  sets the previewsizes for use in setvideosize
            } else {
                opened = safeCameraOpen(0);//still the front camera.... but it's camera 0 if theres only one camera
            }

        }

        mPreview.getHolder().setKeepScreenOn(true);
        if(opened){*/
//            try {
//                mCamera.setPreviewDisplay(mPreview.getHolder());
//            }catch (Exception e){
//                Log.e("PFAFF","FAILED TO SET PREIVEW DISPLAY ON CAMERA");
//                Log.e(getString(R.string.app_name),e.toString());
//            }
//            mCamera.startPreview();
//            mCamera.unlock();
//            if (recorder == null) {
//                recorder = new MediaRecorder();
//            }
//            //  recorder.reset();
//            recorder.setCamera(mCamera);
//            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/VideoAlarm";
//            recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//            recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorderState="INITIALIZED";
//            // recorder.setMaxDuration(50000); // 50 seconds
//            //  recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
//            recorder.setPreviewDisplay(mPreviewSurfaceHolder.getSurface());
//            path=path + "/" + fileDate + ".3gp";
//            if(!outputFileSet) {
//                recorder.setOutputFile(path);
//                this.outputFileSet = true;
//            }
//            recorder.setOrientationHint(270);
//            recorder.setVideoSize(previewSizes.get(indexOfMaxSize).width, previewSizes.get(indexOfMaxSize).height);

            mediaRecorderState="DATASOURCECONFIGURED";

        }
}
