package com.bgood.danny.hockeyliguevirtuelle;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Danny on 2014-10-15.
 */
public class WebContentProvider {
    public static void UpdateContent(Context context) {
        WebContentTask task = new WebContentTask();
        Thread t = new Thread(task);
        t.start();

        while (t.getState() != Thread.State.TERMINATED) {
        }

        try {
            FileOutputStream outputStream = context.openFileOutput("ligue.txt", Context.MODE_PRIVATE);
            outputStream.write(task.getContent().getBytes());
            outputStream.close();
            Toast.makeText(context,"Data file updated.",
                    Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
