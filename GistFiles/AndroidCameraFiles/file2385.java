import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.android.unideal.R;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.soundcloud.android.crop.Crop;
import java.io.File;

public class ImagePicker implements ImageChooserListener {
  public static final String TAG = "ImagePicker";
  public final String KEY_FILE_PATH = "filePath";
  public final String KEY_CHOOSER_TYPE = "chooserType";
  public final String KEY_PICK_REQUEST_CODE = "imageReq";
  private String filePath;
  private int chooserType;
  private int imageReq = -1;
  private ImageChooserManager imageChooserManager;
  private Activity mActivity;
  private ImageListener imageChooserListener;
  private Fragment mFragment;
  private Uri outputUri;
  private Context mContext;
  private boolean isCrop;

  public ImagePicker(Activity activity, ImageListener pickerListener, boolean isCrop) {
    this.mActivity = activity;
    this.mContext = mActivity;
    this.imageChooserListener = pickerListener;
    this.isCrop = isCrop;
    Log.d(TAG, "ImagePicker: Init ");
  }

  public ImagePicker(Fragment fragment, ImageListener pickerListener) {
    this.mFragment = fragment;
    this.mContext = mFragment.getActivity();
    this.imageChooserListener = pickerListener;
    this.isCrop = true;
    Log.d(TAG, "ImagePicker: Init ");
  }

  public ImagePicker(Fragment fragment, ImageListener pickerListener, boolean isCrop) {
    this.mFragment = fragment;
    this.mContext = mFragment.getActivity();
    this.imageChooserListener = pickerListener;
    this.isCrop = isCrop;
    Log.d(TAG, "ImagePicker: Init ");
  }

  public void onSaveInstanceState(Bundle outState) {
    if (outState != null) {
      outState.putInt(KEY_PICK_REQUEST_CODE, imageReq);
      outState.putInt(KEY_CHOOSER_TYPE, chooserType);
      outState.putString(KEY_FILE_PATH, filePath);
      Log.d(TAG, "onSaveInstanceState() called with: " + "outState = [" + outState + "]");
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      imageReq = savedInstanceState.getInt(KEY_PICK_REQUEST_CODE);
      chooserType = savedInstanceState.getInt(KEY_CHOOSER_TYPE);
      filePath = savedInstanceState.getString(KEY_FILE_PATH);
    }
  }

  public void openDialog(final int reqCode) {
    String str[] = new String[] {
        getContext().getResources().getString(R.string.text_open_camera),
        getContext().getResources().getString(R.string.text_choose_from_library)
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setItems(str, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        performImgPicAction(reqCode, which);
      }
    }).show();
  }

  private Context getContext() {
    return mContext;
  }

  void performImgPicAction(int reqCode, int which) {
    imageReq = reqCode;
    if (which == 1) {
      chooserType = ChooserType.REQUEST_PICK_PICTURE;
    } else {
      chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
    }
    initializeImageChooser(chooserType);
    try {
      filePath = imageChooserManager.choose();
    } catch (Exception e) {
      imageChooserListener.onError(e.getMessage());
      e.printStackTrace();
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i(TAG, "OnActivityResult");
    Log.i(TAG, "File Path : " + filePath);
    Log.i(TAG, "Chooser Type: " + chooserType);
    Log.d(TAG, "onActivityResult() called with: "
        + "requestCode = ["
        + requestCode
        + "], resultCode = ["
        + resultCode
        + "], data = ["
        + data
        + "]");
    if (resultCode == Activity.RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE
        || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
      if (imageReq == -1) {
        if (imageChooserListener != null) {
          imageChooserListener.onError("Request code is diff");
        }
        return;
      }

      if (imageChooserManager == null) {
        initializeImageChooser(chooserType);
        imageChooserManager.reinitialize(filePath);
      }
      imageChooserManager.submit(requestCode, data);
    }
    if (requestCode == Crop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
      if (imageChooserListener != null) {
        imageChooserListener.onImagePick(imageReq, outputUri.getPath());
      }

      imageReq = -1;
    }
  }

  private void initializeImageChooser(int type) {
    if (mActivity != null) {
      imageChooserManager = new ImageChooserManager(mActivity, type, true);
    } else {
      imageChooserManager = new ImageChooserManager(mFragment, type, true);
    }
    imageChooserManager.setImageChooserListener(this);
  }

  @Override
  public void onImageChosen(ChosenImage chosenImage) {
    Uri inputUri = Uri.fromFile(new File(chosenImage.getFilePathOriginal()));
    if (isCrop) {
      // default image URI
      outputUri = StorageManager.getDefaultProfileImageURI();
      if (mActivity == null) {
        Crop.of(inputUri, outputUri).asSquare().start(mFragment.getActivity(), mFragment);
      } else {
        Crop.of(inputUri, outputUri).asSquare().start(mActivity);
      }
    } else {
      new ImageCompressTask(chosenImage.getFilePathOriginal()) {
        @Override
        protected void onPreExecute() {
          super.onPreExecute();
          //StorageManager.deleteImageDirectory();
          StorageManager.verifyAppRootDir();
          StorageManager.verifyImagePath();
        }

        @Override
        protected void onPostExecute(String s) {
          super.onPostExecute(s);
          if (imageChooserListener != null) imageChooserListener.onImagePick(imageReq, s);
        }
      }.execute();
    }
  }

  @Override
  public void onError(String s) {
    imageReq = -1;
    if (imageChooserListener != null) {
      imageChooserListener.onError(s);
    }
  }

  public interface ImageListener {
    void onImagePick(int reqCode, String path);

    void onError(String s);
  }
}
