package com.example.fill;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

// Taken from:
//   http://stackoverflow.com/questions/14421694/taking-pictures-with-camera-android-programmatically
public class T_Camera1 extends Activity {
  Uri outputFileUri;
  Intent cameraIntent;

  public void test() {
    String file = "blabla.jpg";
    File newfile = new File(file);
    try {
      newfile.createNewFile();
    } catch (IOException e) {}
      outputFileUri = Uri.fromFile(newfile);

      cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
      startActivityForResult(cameraIntent, 0);
    }
}