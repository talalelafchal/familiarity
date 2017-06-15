package edu.liu.shapefileexample;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by kiichi on 7/21/13.
 */

//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
public class Installer {
    // context = activity
    // assetName = "shapefile.zip"
    //
    public static String unzip(Context context, String assetName, String destPath, boolean overwrite){



        // Create a directory in the SDCard to store the files
        File file = new File(destPath);
        if (!file.exists()){
            file.mkdirs();
        }
        else{
            file.delete();
        }


        try{
            // Open the ZipInputStream
            ZipInputStream inputStream = new ZipInputStream(context.getAssets().open(assetName));

            // from the first entry (copressed first file), until you get null, keep getting next file
            for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream.getNextEntry()){



                String innerFileName = destPath + File.separator + entry.getName();

                File innerFile = new File(innerFileName);
                //if overwrite option is on and file already exists, delete
                if (innerFile.exists()) {
                    if (overwrite == false ){
                        Log.v("myapp","Skipping... "+ innerFileName);
                        continue;
                    }
                    else {
                        innerFile.delete();
                    }
                }
                Log.v("myapp","Extracting: " + entry.getName() + "...");

                // Check if it is a folder
                if (entry.isDirectory()){
                    // Its a folder, create that folder
                    innerFile.mkdirs();
                }
                else{
                    // Create a file output stream
                    FileOutputStream outputStream = new FileOutputStream(innerFileName);
                    final int BUFFER = 2048;

                    // Buffer the ouput to the file
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream,BUFFER);

                    // Write the contents
                    int count = 0;
                    byte[] data = new byte[BUFFER];
                    while ((count = inputStream.read(data, 0, BUFFER)) != -1){
                        bufferedOutputStream.write(data, 0, count);
                    }

                    // Flush and close the buffers
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
                Log.v("myapp","DONE");

                // Close the current entry
                inputStream.closeEntry();
            }

            inputStream.close();

        }
        catch (IOException e){
            Log.v("myapp","error = " + e);
            e.printStackTrace();
        }

        return destPath + File.separator + assetName.replace(".zip","");
    }
}
