package com.wang.tim.takephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends ActionBarActivity {
    private static final int TAKE_PHOTO =1;
    private static final int CROP_PHOTO =2;
    private Button button;
    private ImageView imageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //主页上一个拍照的按钮，一个imageView来显示最后的照片
        button = (Button)findViewById(R.id.takePhoto);
        imageView = (ImageView)findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建要储存相片的文件
                File imageFile = new File(Environment.getExternalStorageDirectory(),"image.jpg");
                try{
                    if(imageFile.exists()){
                        imageFile.delete();
                    }else{
                        imageFile.createNewFile();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                //解析这个相片文件为uri
                imageUri = Uri.fromFile(imageFile);
                //启动相关照相程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent,TAKE_PHOTO);//照相，请求代码设置为TAKE_PHOTO
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){//根据请求代码处理相关的逻辑
                    //照好相好启动相关的裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri,"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent,CROP_PHOTO);//启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if(resultCode == RESULT_OK){//处理裁剪
                    try{//获得图片并设置
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }
}
