import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class EditChaoshiInfoActivity extends FragmentActivity {

    private static final int REQUEST_SELECT_IMG = 0;
    private static final int REQUEST_CROP = 1;

    private ImageView
            mIvChaoshiImg,                  // 超市图片
            mIvSelectImage;                 // 选择图片箭头

    private File avatar;
    {
         File parent = new File(Enviroment.GetExternalStorageDirectory(), "com.erdaren");
         parent.mkdrs();
         avatar = new File(parent, "avatar.png");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chaoshi_info);

        //  初始化控件
        mIvChaoshiImg = (ImageView) findViewById(R.id.iv_chaoshi);
        mIvSelectImage = (ImageView) findViewById(R.id.iv_select_chaoshi_tupian);

        mIvSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_SELECT_IMG);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SELECT_IMG:
                if(resultCode == RESULT_OK){
                    cropSquareImg(data.getData());
                }
                break;
            case REQUEST_CROP:
                if(data != null){
                    Bundle bundle = data.getExtras();
                    if(bundle != null){
                        Bitmap bitmap = BitmapFactory.decodeFile(avatar.getAbsolutePath());
                        mIvChaoshiImg.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    //  裁剪图片为正方形
    private void cropSquareImg(Uri uri, int size){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputX", size);
        intent.purExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar))
        startActivityForResult(intent, REQUEST_CROP);
    }

}
