package com.example.les1_2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 07.09.13
 * Time: 4:11
 * To change this template use File | Settings | File Templates.
 */
public class SectionSceen {
    int id = MyActivity.getMyId();
    int halfw = MyView.w/2;
    int halfh = MyView.h/2;
    int w = MyView.w;
    int h = MyView.h;
    Bitmap bitmap;
    Color color = new Color();
    Paint p = new Paint();
    int[] pixels = new int[halfh*halfw];
    int kol=0;

    public SectionSceen(){
        onDraw();
    }

    void onDraw(){
        bitmap = Bitmap.createBitmap(halfw, halfh, Bitmap.Config.RGB_565);
        kol=0;
        for (int i = (id/2)*halfw;i<(id/2)*halfw+halfw;i++){
            for (int j = (id%2)*halfh;j<(id%2)*halfh+halfh;j++){
                for (int x = -1;x <= 1;x++)
                    for (int y = -1;y <= 1;y++)
                        if (((MyView.table[i][j] + 1) % MyView.kolCol) == MyView.table[(i + x + w) % w][(j + y + h) % h]){
                            MyView.table2[i][j] = ((MyView.table[i][j] + 1) % MyView.kolCol);
                            MyView.cheange = true;
                        }
            }
        }
        for (int i = (id%2)*halfh;i<(id%2)*halfh+halfh;i++){
            for (int j = (id/2)*halfw;j<(id/2)*halfw+halfw;j++){
                MyView.table[j][i] = MyView.table2[j][i];
                MyView.pix[i*w+j]=color.rgb(MyView.red[MyView.table2[j][i]], MyView.gre[MyView.table2[j][i]], MyView.blu[MyView.table2[j][i]]);
            }
        }
        //bitmap.setPixels(pixels,0,halfw,0,0,halfw,halfh);
        //canvas.drawBitmap(bitmap,(id/2)*halfw,(id%2)*halfh,p);
        MyView.end[id] = true;
    }
}
