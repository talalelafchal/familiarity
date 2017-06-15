package com.baidu.demo.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import com.baidu.demo.HttpRequst.HttpRequest;
import com.baidu.demo.R;
import com.baidu.demo.util.TestParam;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-25
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
public class BitmapReceived extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitmap_recieved_layout);
        ImageView iv = (ImageView) findViewById(R.id.imageview1);
        InputStream is = HttpRequest.getImage(TestParam.panoID, 2,4 , TestParam.qsdata_udt, 5);
        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        iv.setImageBitmap(bitmapTmp);
    }
}