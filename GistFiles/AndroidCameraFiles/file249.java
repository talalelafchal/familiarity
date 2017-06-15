public class CameraMicPermissionHelper extends Fragment {
  private static final int REQUEST_CAMERA_MIC_PERMISSIONS = 10;
  public static final String TAG = "CamMicPerm";

  private CameraMicPermissionCallback mCallback;
  private static boolean sCameraMicPermissionDenied;

  public static CameraMicPermissionHelper newInstance() {
    return new CameraMicPermissionHelper();
  }

  public CameraMicPermissionHelper() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof CameraMicPermissionCallback) {
      mCallback = (CameraMicPermissionCallback) activity;
    } else {
      throw new IllegalArgumentException("activity must extend BaseActivity and implement LocationHelper.LocationCallback");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mCallback = null;
  }

  public void checkCameraMicPermissions() {
    if (PermissionUtil.hasSelfPermission(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})) {
      mCallback.onCameraMicPermissionGranted();
    } else {
      // UNCOMMENT TO SUPPORT ANDROID M RUNTIME PERMISSIONS
      if (!sCameraMicPermissionDenied) {
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA_MIC_PERMISSIONS);
      }
    }
  }

  public void setCameraMicPermissionDenied(boolean cameraMicPermissionDenied) {
    this.sCameraMicPermissionDenied = cameraMicPermissionDenied;
  }

  public static boolean isCameraMicPermissionDenied() {
    return sCameraMicPermissionDenied;
  }

  /**
   * Callback received when a permissions request has been completed.
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                         int[] grantResults) {

    if (requestCode == REQUEST_CAMERA_MIC_PERMISSIONS) {
      if (PermissionUtil.verifyPermissions(grantResults)) {
        mCallback.onCameraMicPermissionGranted();
      } else {
        Log.i("BaseActivity", "LOCATION permission was NOT granted.");
        mCallback.onCameraMicPermissionDenied();
      }

    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }


  public interface CameraMicPermissionCallback {
    void onCameraMicPermissionGranted();
    void onCameraMicPermissionDenied();
  }

}