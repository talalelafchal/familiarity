import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import jp.likenote.android.R;

public class ImagePicker {
    public static final String TAG = "ImagePicker";

    private String filePath;
    private int chooserType;
    private ImageChooserManager imageChooserManager;


    private Activity mActivity;
    private ImageChooserListener imageChooserListener;

    public ImagePicker(Activity activity, ImageChooserListener pickerListener) {
        this.mActivity = activity;
        this.imageChooserListener = pickerListener;
    }

    public void openDialog() {
        String str[] = new String[]{mActivity.getResources().getString(R.string.text_open_camera),
                mActivity.getResources().getString(R.string.text_choose_from_library)};

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setItems(str,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performImgPicAction(which);
                    }
                }).show();
    }

    void performImgPicAction(int which) {
        if (which == 1) {
            chooserType = ChooserType.REQUEST_PICK_PICTURE;
        } else {
            chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        }
        initializeImageChooser(chooserType);
        try {

            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "OnActivityResult");
        Log.i(TAG, "File Path : " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        if (resultCode == Activity.RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                initializeImageChooser(chooserType);
                imageChooserManager.reinitialize(filePath);
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    private void initializeImageChooser(int type) {
        imageChooserManager = new ImageChooserManager(mActivity, type, true);
        imageChooserManager.setImageChooserListener(imageChooserListener);
    }
}
