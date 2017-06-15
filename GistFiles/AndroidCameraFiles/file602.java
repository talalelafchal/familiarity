package dm.com.imagepixelator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import dm.com.dmlib.dimageview.DZoomableImageView;

public class ImageShowActivity extends AppCompatActivity {

    DZoomableImageView img_taken_picture;
    ImageView sample_level1, sample_level2, sample_level3;
    LinearLayout lin_image_show_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        img_taken_picture = (DZoomableImageView) findViewById(R.id.img_taken_picture);
        img_taken_picture.setImageBitmap(Global.takenOrSelectedImage);

//        new MyClass().loadImageFromFileUri(this,Global.galleryDir+"/13378.png",img_taken_picture);

        sample_level1 = (ImageView) findViewById(R.id.sample_level1);
        sample_level2 = (ImageView) findViewById(R.id.sample_level2);
        sample_level3 = (ImageView) findViewById(R.id.sample_level3);

        lin_image_show_back = (LinearLayout) findViewById(R.id.lin_image_show_back);
        lin_image_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sample_level1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.selectedPixelationLevel = 85;
                startActivity(new Intent(ImageShowActivity.this, PixelatedImageActivity.class));
            }
        });

        sample_level2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.selectedPixelationLevel = 50;
                startActivity(new Intent(ImageShowActivity.this, PixelatedImageActivity.class));
            }
        });

        sample_level3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.selectedPixelationLevel = 35;
                startActivity(new Intent(ImageShowActivity.this, PixelatedImageActivity.class));
            }
        });

    }
}
