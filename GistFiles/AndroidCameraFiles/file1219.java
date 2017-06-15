import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    
    private Uri currentImageUri;
    
    ...

    public void tomarFoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                currentImageUri = createImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                startActivityForResult(takePictureIntent, IMAGE_FROM_CAMERA);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Uri createImageFile() throws IOException {
        String imageFileName = "mobileDay_" + System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/MobileDay/");
        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (!created) {
                throw new IOException("Directory not created");
            }
        }
        File tempFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return Uri.fromFile(tempFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_FROM_CAMERA && resultCode == RESULT_OK) {
            //Notificar a la galería
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, currentImageUri));
            try {
                //Obtener imagen full size
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), currentImageUri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
