package com.example.administrator.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Created by Administrator on 2014/10/10.
 */
public class GetPictureDemo extends Activity {
    private Button mButton;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getpicture);

        mButton = (Button) findViewById(R.id.myButton);
        mImageView = (ImageView) findViewById(R.id.myImage);

        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 使用Intent調用其他服務幫忙拍照 */
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    /* 取得相片後返回的監聽 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Bundle extras = data.getExtras();       //取出拍照後, 回傳

                    /* 將資料轉換為Bitmap格式 */
                    Bitmap bmp = (Bitmap) extras.get("data");

                    /* 如果有資料, 就丟doCropPhoto進行裁剪 */
                    if (bmp != null) {
                        doCropPhoto(bmp);
                    }
                    break;
                case 1:
                    Bitmap photo = data.getParcelableExtra("data");
                    mImageView.setImageBitmap(photo);
                    break;
                default:
                    break;
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 進行照片裁剪 */
    protected void doCropPhoto(Bitmap data) {
        Intent intent = getCropImageIntent(data);
        startActivityForResult(intent, 1);
    }

    /* 照片裁剪的規則 */
    public static Intent getCropImageIntent(Bitmap data) {
        Intent intent = new Intent("com.android.camera.action.CROP");   //這句目前還不知道要幹嗎的
        intent.setType("image/*");
        intent.putExtra("data", data);
        intent.putExtra("crop", "true");        //crop = true 有這句, 才能叫出裁剪

        /* 裁剪框的比例,X:Y = 1:1 */
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        /* 回傳照片的比例 */
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        intent.putExtra("return-data", true);

        return intent;
    }
}
