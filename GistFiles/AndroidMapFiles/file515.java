package com.example.les1_2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 07.09.13
 * Time: 4:10
 * To change this template use File | Settings | File Templates.
 */
class MyView extends View {

    public static int[] red = {255,0,255,255,255,0,0,0,100,200,100,255,255,200,200,255};
    public static int[] gre = {255,255,0,255,0,255,0,0,100,200,255,255,100,200,255,200};
    public static int[] blu = {255,255,255,0,0,0,255,0,100,200,255,100,255,255,200,200};
    public static int kolCol = 16;
    public Random random = new Random();
    boolean createTable = false;
    public static boolean cheange = false;
    public static int w = 240;
    public static int h = 320;

    static public Integer[][] table = new Integer[w][h];
    static public Integer[][] table2 = new Integer[w][h];
    public static Canvas myCanvas;
    Bitmap sceen = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
    public static boolean[] end = {true,true,true,true};

    private long lastFpsCalcUptime;
    private long frameCounter;

    private long fps;

    private static final long FPS_CALC_INTERVAL = 1000L;

    public MyView(Context context){
        super(context);
        int[] rand = new int[w * h];
        for (i = 0;i < w * h;i++){
            rand[i] = random.nextInt(923421);
        }
        kol=0;
        for (i = 0;i < w;i++){
            for (j = 0;j < h;j++){
                table2[i][j] = rand[kol] % kolCol;
                kol++;
                table[i][j] = table2[i][j];
                //sceen.setPixel(i,j,color.rgb(red[table[i][j]], gre[table[i][j]], blu[table[i][j]]));
            }
        }
        for (int i = 0;i<h;i++){
            for (int j = 0;j<w;j++){
                MyView.table[j][i] = MyView.table2[j][i];
                MyView.pix[i*w+j]=color.rgb(MyView.red[MyView.table2[j][i]], MyView.gre[MyView.table2[j][i]], MyView.blu[MyView.table2[j][i]]);
            }
        }
    }

    Paint p = new Paint();
    Color color = new Color();
    int i,j;

    ThreadDraw t1 = new ThreadDraw();
    ThreadDraw t2 = new ThreadDraw();
    ThreadDraw t3 = new ThreadDraw();
    ThreadDraw t4 = new ThreadDraw();

    public static int[] pix = new int[w*h];
    int kol;
    boolean flag = true;

    @Override
    public void onDraw(Canvas canvas){
        measureFps();
        /*t1.t.run();
        t2.t.run();
        t3.t.run();
        t4.t.run(); */
        for (int i = 0;i<w;i++){
            for (int j = 0;j<h;j++){
                flag = true;
                for (int x = -1;x <= 1 && flag;x++)
                    for (int y = -1;y <= 1 && flag;y++)
                        if (((table[i][j] + 1) % kolCol) == table[(i + x + w) % w][(j + y + h) % h]){
                            table2[i][j] = ((table[i][j] + 1) % kolCol);
                            flag = false;
                        }
                sceen.setPixel(i,j,color.rgb(red[table2[i][j]], gre[table2[i][j]], blu[table2[i][j]]));
            }
        }

        for (int i = 0;i<h;i++){
            for (int j = 0;j<w;j++){
                table[j][i] = table2[j][i];
                pix[i*w+j]=color.rgb(red[table2[j][i]], gre[table2[j][i]], blu[table2[j][i]]);
            }
        }
        //sceen.setPixels(pix,0,w,0,0,w,h);
        canvas.drawBitmap(pix,0,w,0,0,w,h,false,null);//(sceen,0,0,null);
        p.setColor(Color.BLACK);
        p.setTextSize(20);
        canvas.drawText("fps=" + fps, 5, 30, p);
        postInvalidate();
    }

    private void measureFps() {
        frameCounter++;
        long now = SystemClock.uptimeMillis();
        long delta = now - lastFpsCalcUptime;
        if (delta > FPS_CALC_INTERVAL) {
            fps = frameCounter * FPS_CALC_INTERVAL / delta;
            frameCounter = 0;
            lastFpsCalcUptime = now;
        }
    }

    class ThreadDraw{
        Thread t = new Thread(){
            public void run(){
                SectionSceen sectionSceen = new SectionSceen();
            }
        };
    }
}
