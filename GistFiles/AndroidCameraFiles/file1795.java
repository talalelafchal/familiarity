package <yourPackage>;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by kpob on 12/31/15.
 */
public class CameraManager {

    private static final int GALLERY_PHOTO_REQUEST_CODE = 2425;
    private static final int NEW_GALLERY_PHOTO_REQUEST_CODE = 2125;
    private static final int TAKE_PICTURE_REQUEST_CODE = 2555;

    private static Uri imageUri;
    private String filePath;

    private final Activity activity;
    private final BitmapFactory.Options options;

    private Subscription subscription;

    @Inject
    public CameraManager(Activity activity, BitmapFactory.Options options) {
        this.activity = activity;
        this.options = options;
    }

    public void selectFromGallery() {
        if (Build.VERSION.SDK_INT < 19){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.set_image)), GALLERY_PHOTO_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(intent, NEW_GALLERY_PHOTO_REQUEST_CODE);
        }
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE);
    }

    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, Intent data, Subscriber<Bitmap> subscriber) {
        if (null == data) {
            return;
        }

        Observable<Bitmap> observable;
        switch (requestCode) {
            case GALLERY_PHOTO_REQUEST_CODE:
                imageUri = data.getData();
                observable = Observable.just(imageUri).map(this::preKitKatGetBitmap);
                break;
            case NEW_GALLERY_PHOTO_REQUEST_CODE:
                imageUri = data.getData();
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                //noinspection ResourceType
                activity.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                observable = Observable.just(imageUri).map(this::getBitmap);
                break;
            case TAKE_PICTURE_REQUEST_CODE:
                filePath = imageUri.toString();
                imageUri = Uri.parse(imageUri.toString().replace("file://", ""));

                observable = Observable.just(imageUri).map(uri -> BitmapFactory.decodeFile(uri.toString(), options));
                break;
            default:
                return;
        }

        subscription = observable.subscribe(subscriber);
    }

    public void pause() {
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private Bitmap preKitKatGetBitmap(Uri uri) {
        List<String> uriPath = uri.getPathSegments();
        long imageId = Long.parseLong(uriPath.get(uriPath.size() - 1));
        return MediaStore.Images.Thumbnails.getThumbnail(activity.getContentResolver(), imageId,
                MediaStore.Images.Thumbnails.MINI_KIND, options);
    }

    @SuppressLint("NewApi")
    private Bitmap getBitmap(Uri uri) {
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = activity.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Uri getImageUri() {
        return imageUri;
    }

    public String getFilePath() {
        return filePath;
    }

    public void cancel() {
        filePath = null;
        imageUri = null;
    }
}
