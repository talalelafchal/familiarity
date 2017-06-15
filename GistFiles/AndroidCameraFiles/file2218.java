package dm.com.imagepixelator;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class PixelatedImageActivity extends AppCompatActivity {
    DPixelImageView img_pixelated;
    TextView save_picture;
    ProgressDialog pd;
    LinearLayout lin_pixelated_image_show_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pixelated_image_show);

        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.setTitle("Please Wait");
        pd.setMessage("Saving...");

        img_pixelated = (DPixelImageView) findViewById(R.id.img_pixelated);
        img_pixelated.setImageBitmap(Global.takenOrSelectedImage);
        img_pixelated.pixelate(Global.selectedPixelationLevel);
        lin_pixelated_image_show_back = (LinearLayout) findViewById(R.id.lin_pixelated_image_show_back);
        lin_pixelated_image_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_picture = (TextView) findViewById(R.id.save_picture);
        save_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new saveIt().execute();
            }
        });
    }

    public class saveIt extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            FileOutputStream out = null;
            try {
                File file = new File(Global.galleryDir, new Random().nextInt(99999 - 10000) + 10000 + ".png");
                out = new FileOutputStream(file);
                Global.imageCache.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            Toast.makeText(PixelatedImageActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
            lin_pixelated_image_show_back.performClick();
        }
    }

}
