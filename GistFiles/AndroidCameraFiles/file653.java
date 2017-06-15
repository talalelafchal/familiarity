package com.baidu.demo.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.baidu.demo.HttpRequst.HttpRequest;
import com.baidu.demo.R;
import com.baidu.demo.tut.Ball;
import com.baidu.demo.tut.IOpenGLDemo;
import com.baidu.demo.tut.OpenGLESActivity;
import com.baidu.demo.util.TestParam;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-25
 * Time: 上午11:34
 * To change this template use File | Settings | File Templates.
 */
public class OpenGLDemo extends OpenGLESActivity implements IOpenGLDemo {

    float eyeX = 0f;
    float eyeY = 0f;
    float eyeZ = 4f;



    Ball ball = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView.setOnTouchListener(onTouchListener);
    }

    @Override
    public void initObject(GL10 gl) {
        ball = new Ball(5,10);
        ball.loadTexture(TestParam.panoID,3,gl);
//        ball = new Ball(5, initTexture(gl, R.drawable.test));
    }

    @Override
    public void initLight(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHTING);
        initWhiteLight(gl, GL10.GL_LIGHT0, 0.5f, 0.5f, 0.5f);
    }

    @Override
    public void DrawScene(GL10 gl) {
        super.DrawScene(gl);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glPushMatrix();
        ball.drawSelf(gl);
        gl.glPopMatrix();

    }

    public int loadImageTexture(String panoID, int colIdx, int rowIdx, int levelTemp, GL10 gl) {
        Bitmap bitmap = null;
        InputStream is = HttpRequest.getImage(panoID, colIdx, rowIdx, TestParam.qsdata_udt, levelTemp);
        int texture;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            int[] textures = new int[1];
            // 指定生成N个纹理（第一个参数指定生成1个纹理），
            // textures数组将负责存储所有纹理的代号。
            gl.glGenTextures(1, textures, 0);
            // 获取textures纹理数组中的第一个纹理
            texture = textures[0];
            // 通知OpenGL将texture纹理绑定到GL10.GL_TEXTURE_2D目标中
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
            // 设置纹理被缩小（距离视点很远时被缩小）时候的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            // 设置纹理被放大（距离视点很近时被方法）时候的滤波方式
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            // 设置在横向、纵向上都是平铺纹理
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                    GL10.GL_REPEAT);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                    GL10.GL_REPEAT);
            // 加载位图生成纹理
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
            Log.i("Demo", "tid =" + texture);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 生成纹理之后，回收位图
            if (bitmap != null)
                bitmap.recycle();
        }

        return texture;
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        float lastX
                ,
                lastY;

        private int mode = 0;

        float oldDist = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    mode += 1;

                    oldDist = caluDist(event);

                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;

                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode >= 2) {
                        float newDist = caluDist(event);
                        if (Math.abs(newDist - oldDist) > 2f) {
                            zoom(newDist, oldDist);
                        }
                    } else {
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;

                        float a = 180.0f / 320;
                        ball.mAngleX += dx * a;
                        ball.mAngleY += dy * a;
                    }
                    break;
            }

            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            return true;
        }
    };

    public void zoom(float newDist, float oldDist) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float px = displayMetrics.widthPixels;
        float py = displayMetrics.heightPixels;

        ball.zoom += (newDist - oldDist) * (ball.maxZoom - ball.minZoom) / FloatMath.sqrt(px * px + py * py) / 4;

//        if (ball.zoom > ball.maxZoom) {
//            ball.zoom = ball.maxZoom;
//        } else if (ball.zoom < ball.minZoom) {
//            ball.zoom = ball.minZoom;
//        }
    }

    public float caluDist(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(dx * dx + dy * dy);
    }
}
