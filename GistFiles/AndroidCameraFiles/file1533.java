import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * CustomCameraHost overrides the methods of SimpleCameraHost.
 * To see those methods:
 * http://bit.ly/1wqRcEb
 */
public class CustomCameraHost extends SimpleCameraHost {

  protected static File latestVideo;
  private static final String TAG = "CustomCameraHost";
  private static final int DEFAULT_VIDEO_WIDTH = 720;
  private static final int DEFAULT_VIDEO_HEIGHT = 480;
  private static final int DEFAULT_VIDEO_FRAMERATE = 30;
  CamcorderProfile profile = null;

  public CustomCameraHost(Context ctxt) {
    super(ctxt);
  }

  public CamcorderProfile getOptimalVideoSize(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
    }
    if (profile == null) {
      profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
    }
    if (profile == null) {
      Log.e(TAG, "No CamcorderProfile available.");
    }
    return profile;
  }

  /////////////////////////////////////////////
  // Preview functions

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public Camera.Size getPreferredPreviewSizeForVideo(int displayOrientation,
                                                     int width,
                                                     int height,
                                                     Camera.Parameters parameters,
                                                     Camera.Size deviceHint) {
    Camera.Size camSize = deviceHint;
    String man = Build.MANUFACTURER;
    if (man.equals("samsung")) {
      List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
      boolean fit = false;
      getOptimalVideoSize();
      for (Camera.Size range : previewSizes) {
        // Gotta make sure that it's in range.
        if (range.width == profile.videoFrameWidth
                && range.height == profile.videoFrameHeight) {
          camSize = range;
          fit = true;
          break;
        }
      }
      if (!fit) {
        Log.e(TAG, "Preview size does not fit camcorder size...");
      }
    } else if (deviceHint != null) {
      camSize = deviceHint;
    }
    return camSize;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @Override
  public void configureRecorderProfile(int cameraId,
                                       MediaRecorder recorder) {
    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    getOptimalVideoSize();
    if (profile == null) {
      recorder.setVideoSize(DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT);
      recorder.setVideoFrameRate(DEFAULT_VIDEO_FRAMERATE);
    } else {
      recorder.setVideoSize(profile.videoFrameWidth,
                            profile.videoFrameHeight);
      recorder.setVideoFrameRate(profile.videoFrameRate);
    }
    recorder.setVideoEncodingBitRate(3000000);
    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
  }

}
