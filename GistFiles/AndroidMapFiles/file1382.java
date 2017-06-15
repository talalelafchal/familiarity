import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

/**
 * Created by prashanth on 23/2/17.
 */

public class Utils {
  
    public Bitmap getPhotoBitmap(ContentResolver contentResolver, String photoUri) throws IOException {
        Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photoUri));
        return photo;
    }

    public Bitmap getPhotoBitmap(ContentResolver contentResolver, Uri photoUri) throws IOException {
        Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver, photoUri);
        return photo;
    }
}
