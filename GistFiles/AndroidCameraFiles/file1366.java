/**
 * 图片工具类
 *
 * @author chenchong
 *         2016/10/26
 *         上午11:37
 */
public class PictureUtil {
  /** 拍照,该方法提供了权限检查 */
  public static void takePhoto(final ActivityHelper helper, final TakePhotoListener listener) {
    if (!Dexter.isRequestOngoing()) {
      Dexter.checkPermissions(new MultiplePermissionsListener() {
                                @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                                  if (report.areAllPermissionsGranted()) {
                                    // 授予了所有权限
                                    try {
                                      // 首先,创建要写入图片的临时文件
                                      File file = FileUtil.createTmpFile(helper.getContext());
                                      Context context = MyApplication.getContext();
                                      Uri uri;

                                      if (OSVersionUtils.hasN()) {
                                        // 在Android N及以上需要使用FileProvider来生成content://...,否则会报出FileUriExposedException错误
                                        uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                                            file);
                                      } else {
                                        // 而在之下的版本,则需要从File生成uri,否则写入文件的时候会出现错误
                                        uri = Uri.fromFile(file);
                                      }
                                      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                      ComponentName componentName =
                                          NavigationUtil.resolveActivity(helper.getContext(), intent);
                                      // 然后,检查是否存在相机应用
                                      if (componentName == null) {// 如果不存在,则进行错误回调
                                        listener.onTakePhotoFailure(NO_AVAILABLE_CAMERA);
                                      } else {
                                        // 如果存在,则打开相机并给出正确回调
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        helper.startActivityForResult(intent, Constants.TAKE_PHOTO_REQUEST_CODE);
                                        listener.onCameraSuccess(file);
                                      }
                                    } catch (IOException e) {
                                      e.printStackTrace();
                                      // 创建临时文件失败
                                      listener.onTakePhotoFailure(UNKNOW_ERROR);
                                    }
                                  } else {
                                    // 有权限被拒绝
                                    listener.onTakePhotoFailure(PERMISSION_DENIED);
                                  }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                    PermissionToken token) {
                                  token.continuePermissionRequest();
                                }
                              }, OSVersionUtils.READ_EXTERNAL_STORAGE(), Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.CAMERA);
    }
  }

  /** 处理拍照结果 */
  public static void takePhotoResult(int requestCode, int resultCode, Intent data, File file,
      TakePhotoListener listener) {
    if (requestCode == Constants.TAKE_PHOTO_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        // 拍照成功
        listener.onTakePhotoSuccess(file.getPath());
        MyApplication.getContext()
            .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        if (BuildConfig.DEBUG) Log.d(TAG, "拍照成功,data : " + data);
      } else {
        // 拍照失败或者取消,则删除临时文件
        if (file != null && file.exists()) {
          boolean result = file.delete();
          listener.onTakePhotoFailure(UNKNOW_ERROR);
          if (BuildConfig.DEBUG) {
            Log.d(TAG, "拍照失败/取消,删除临时文件;path : " + file.getAbsolutePath() + ",结果 : " + result);
          }
        } else if (BuildConfig.DEBUG) {
          Log.d(TAG, "删除临时文件失败,文件为空或者不存在");
        }
      }
    }
  }

  /** 默认的拍照错误提示处理 */
  public static void defaultTakePhotoError(CoordinatorLayout cl, @Error int error) {
    if (cl != null) {
      if (error == PERMISSION_DENIED) {
        UIUtils.showFailSnackBar(cl,
            MyApplication.getContext().getString(R.string.text_take_photo_request_permission));
      } else if (error == NO_AVAILABLE_CAMERA) {
        UIUtils.showFailSnackBar(cl,
            MyApplication.getContext().getString(R.string.text_no_available_camera));
      } else {
        UIUtils.showFailSnackBar(cl,
            MyApplication.getContext().getString(R.string.text_take_photo_failure));
      }
    }
  }
  
  public interface Listener {
    void onSuccess();

    void onFailure(@Error int error);
  }

  public interface TakePhotoListener {
    /**
     * 成功打开相机
     *
     * @param file 创建的临时文件,用于拍照成功后写入数据
     */
    void onCameraSuccess(File file);

    void onTakePhotoSuccess(String path);

    void onTakePhotoFailure(@Error int error);
  }

  @Retention(RetentionPolicy.SOURCE) @IntDef({
      PERMISSION_DENIED, EXTERNAL_STORAGE_NOT_WRITABLE, NO_FREE_SPACE, UNKNOW_ERROR,
      NO_AVAILABLE_CAMERA
  }) public @interface Error {
  }
}