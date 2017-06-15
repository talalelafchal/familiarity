import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.*;

/**
 * Copies selected picture from device's gallery to your app file location. Make sure you have permissions.
 */
public class CopyPictureFromGallery implements Usecase<Uri> {

    private final Picasso picasso;
    private final Application application;
    private final Uri originUri;

    private final int requiredWidth;
    private final int requiredHeight;

    public CopyPictureFromGallery(Application application, Picasso picasso, Uri uriFromGallery, int requiredWidth, int requiredHeight) {
        this.application = application;
        this.picasso = picasso;
        this.originUri = uriFromGallery;
        this.requiredWidth = requiredWidth;
        this.requiredHeight = requiredHeight;
    }

    @Override
    public Subscription execute(Observer<Uri> observer) {
        return copyOriginalFromGallery()
                .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                    @Override
                    public Observable<Bitmap> call(Uri uri) {
                        return transformToResizedBitmap(uri);
                    }
                })
                .flatMap(new Func1<Bitmap, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(Bitmap bitmap) {
                        return saveResized(bitmap);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observable<Uri> copyOriginalFromGallery() {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(Subscriber<? super Uri> subscriber) {
                final Uri destinationUri;
                try {
                    final File file = createTempFile(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp_pic");
                    destinationUri = Uri.fromFile(file);
                    ParcelFileDescriptor descriptor = application.getContentResolver().openFileDescriptor(originUri, "r");
                    assert descriptor != null;
                    FileDescriptor fd = descriptor.getFileDescriptor();
                    FileInputStream input = new FileInputStream(fd);
                    FileOutputStream output = new FileOutputStream(destinationUri.getPath());
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = input.read(buf)) > 0) {
                        output.write(buf, 0, len);
                    }
                    input.close();
                    output.close();
                    subscriber.onNext(destinationUri);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                    subscriber.onCompleted();
                }
            }
        });
    }

    private Observable<Bitmap> transformToResizedBitmap(final Uri uri) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                RequestCreator requestCreator = picasso.load(uri)
                        .resize(requiredWidth, requiredHeight)
                        .centerInside();
                Bitmap bitmap;
                try {
                    bitmap = requestCreator.get();
                    File tmp = new File(uri.getPath());
                    if (tmp.exists()) {
                        tmp.delete();
                    }
                    subscriber.onNext(bitmap);
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private Observable<Uri> saveResized(final Bitmap bitmap) {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(Subscriber<? super Uri> subscriber) {
                FileOutputStream out = null;
                try {
                    final File file = FileUtils.createTempFile(application.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile_pic");
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    subscriber.onNext(Uri.fromFile(file));
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
  
    public static File createTempFile(File directory, String name) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + name;
        return File.createTempFile(imageFileName, ".jpg", directory);
    }
}
