package com.takwolf.android.cropimg;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class CropImageActivity extends Activity {

    private File tempFile;
    private ImageView imgResult;
    private int crop = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        imgResult = (ImageView) findViewById(R.id.img_result);
        String filePath = getExternalCacheDir().getAbsolutePath();
        tempFile = new File(filePath, "tmp.jpg");
        System.out.println(tempFile.getAbsolutePath());
    }

    public void onBtnFromLibrary(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra("output", Uri.fromFile(tempFile));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", crop);// 输出图片大小
        intent.putExtra("outputY", crop);
        startActivityForResult(intent, 100);
    }

    public void onBtnFromCamera(View view) {
        /* 
         * 在某些4.x的手机上，这不工作，会报错
         * 解决方案是使用这个库：
         * https://github.com/biokys/cropimage 
         */
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("output", Uri.fromFile(tempFile));
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", crop);// 输出图片大小
        intent.putExtra("outputY", crop);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
            imgResult.setImageBitmap(bmp);
        }
    }

    public static Intent getCropImageIntent(Bitmap data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.putExtra("data", data);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        return intent;
    }

}
