package com.baidu.demo.Activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.Log;
import com.baidu.demo.HttpRequst.HttpRequest;
import com.baidu.demo.graphic.OpenGLInterface;
import com.baidu.demo.graphic.OpenGLRender;
import com.baidu.demo.util.TestParam;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-25
 * Time: 下午3:01
 * To change this template use File | Settings | File Templates.
 */
public class CubeDemo extends Activity implements OpenGLInterface {

    int[] textureID;
    private GLSurfaceView surfaceView;
    private int texture;
    private float[] cubeVertices = {-0.6f, -0.6f, -0.6f, -0.6f, 0.6f,
            -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, -0.6f, -0.6f,
            -0.6f, -0.6f, -0.6f, -0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f, -0.6f, -0.6f,
            0.6f, -0.6f, -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, 0.6f, -0.6f, 0.6f,
            0.6f, -0.6f, 0.6f, -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, -0.6f, 0.6f,
            -0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f, 0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, -0.6f, 0.6f, 0.6f, -0.6f, -0.6f, 0.6f, 0.6f, -0.6f,
            -0.6f, 0.6f, -0.6f, -0.6f, 0.6f, 0.6f, -0.6f, 0.6f, 0.6f, 0.6f,
            0.6f, 0.6f, 0.6f, 0.6f, -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, -0.6f,
            -0.6f, -0.6f, -0.6f, 0.6f, -0.6f, -0.6f, 0.6f, -0.6f, 0.6f, 0.6f,
            -0.6f, 0.6f, -0.6f,};
    // 定义立方体所需要的6个面（一共是12个三角形所需的顶点）
    private byte[] cubeFacets = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35,};
    // 定义纹理贴图的座标数据
    private float[] cubeTextures = {1.0000f, 1.0000f, 1.0000f, 0.0000f,
            0.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f, 1.0000f,
            1.0000f, 0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f,
            1.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f, 0.0000f,
            1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f, 1.0000f, 0.0000f,
            0.0000f, 0.0000f, 0.0000f, 1.0000f, 0.0000f, 1.0000f, 1.0000f,
            1.0000f, 1.0000f, 0.0000f, 1.0000f, 0.0000f, 0.0000f, 0.0000f,
            0.0000f, 1.0000f, 0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f,
            0.0000f, 1.0000f, 0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f,
            0.0000f, 1.0000f, 1.0000f, 1.0000f, 1.0000f, 0.0000f, 1.0000f,
            0.0000f, 0.0000f, 0.0000f, 0.0000f, 1.0000f};
    private FloatBuffer cubeVerticesBuffer;
    private ByteBuffer cubeFacetsBuffer;
    private FloatBuffer cubeTexturesBuffer;
    private int colCount, rowCount, picCount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new GLSurfaceView(this);
        surfaceView.setRenderer(new OpenGLRender(this));
        setContentView(surfaceView);
    }

    @Override
    public void DrawScene(GL10 gl) {
        // 清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        // 启用顶点座标数据
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 启用贴图座标数组数据
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // 设置当前矩阵模式为模型视图。
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        // --------------------绘制第一个图形---------------------
        gl.glLoadIdentity();
        // 把绘图中心移入屏幕2个单位
        gl.glTranslatef(0f, 0.0f, -2.0f);
        // 旋转图形
        gl.glRotatef(30f, 0, 1, 0);
        gl.glRotatef(30f, 1, 0, 0);
        // 设置顶点的位置数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVerticesBuffer);
        // 设置贴图的的座标数据
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTexturesBuffer);
        // 执行纹理贴图
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[1]);
        // 按cubeFacetsBuffer指定的面绘制三角形
//        gl.glDrawElements(GL10.GL_TRIANGLES, cubeFacetsBuffer.remaining(),
//                GL10.GL_UNSIGNED_BYTE, cubeFacetsBuffer);
//        // 绘制结束
//        gl.glFinish();

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, cubeVertices.length / 3);
        // 禁用顶点、纹理座标数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private void setImgRowCol(int level) {
        switch (level) {
            case 1: {
                colCount = 1;
                rowCount = 1;
            }
            break;
            case 2: {
                colCount = 2;
                rowCount = 1;
            }
            break;
            case 3: {
                colCount = 4;
                rowCount = 2;
            }
            break;
            case 4: {
                colCount = 8;
                rowCount = 4;
            }
            break;
            case 5: {
                colCount = 16;
                rowCount = 8;
            }
            break;
        }
        picCount = rowCount * colCount;
    }

    public void loadTexture(String panoID, int level, GL10 gl) {
        setImgRowCol(level);
        IntBuffer textureBuffer = IntBuffer.allocate(picCount);
        gl.glGenTextures(picCount, textureBuffer);
        textureID = textureBuffer.array();
        int row = 0;
        int col = 0;
        for (int i = 0; i < picCount; i++) {
            Bitmap bitmap = null;
            if (col == colCount) {
                col = 0;
                row++;
            }
            Log.i("Demo","c:"+col+"row:"+row);
            InputStream is = HttpRequest.getImage(panoID, row, col, TestParam.qsdata_udt, level);
            col++;
            try {
                bitmap = BitmapFactory.decodeStream(is);
                // 通知OpenGL将texture纹理绑定到GL10.GL_TEXTURE_2D目标中
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[i]);
                // 设置纹理被缩小（距离视点很远时被缩小）时候的滤波方式
                gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                        GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
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
        }
        Log.i("Demo", "loading complete");
//        textureMapping();
    }

    @Override
    public void initObject(GL10 gl) {
        gl.glDisable(GL10.GL_DITHER);
        // 设置系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glClearColor(0, 0, 0, 0);
        // 设置阴影平滑模式
        gl.glShadeModel(GL10.GL_SMOOTH);
        // 启用深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST);
        // 设置深度测试的类型
        gl.glDepthFunc(GL10.GL_LEQUAL);
        // 启用2D纹理贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 装载纹理
//        loadTexture(gl);
        loadTexture(TestParam.panoID,3,gl);
        ByteBuffer vbb = ByteBuffer.allocateDirect(cubeVertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        cubeVerticesBuffer = vbb.asFloatBuffer();
        cubeVerticesBuffer.put(cubeVertices);
        cubeVerticesBuffer.position(0);

        //cubeVerticesBuffer = FloatBuffer.wrap(cubeVertices);
        // 将立方体的6个面（12个三角形）的数组包装成ByteBuffer
//        ByteBuffer fbb = ByteBuffer.allocate(cubeFacets.length*4);
//        fbb.order(ByteOrder.nativeOrder());
//        cubeFacetsBuffer = fbb.asFloatBuffer()
        cubeFacetsBuffer = ByteBuffer.allocateDirect(cubeFacets.length * 4);
        cubeFacetsBuffer.order(ByteOrder.nativeOrder());
        cubeFacetsBuffer.put(cubeFacets);
        cubeFacetsBuffer.position(0);
//        cubeFacetsBuffer = ByteBuffer.wrap(cubeFacets);
        // 将立方体的纹理贴图的座标数据包装成FloatBuffer
        ByteBuffer tbb = ByteBuffer.allocateDirect(cubeTextures.length * 4);
        tbb.order(ByteOrder.nativeOrder());

        cubeTexturesBuffer = tbb.asFloatBuffer();
        cubeTexturesBuffer.put(cubeTextures);
        cubeTexturesBuffer.position(0);
    }

    private void loadTexture(GL10 gl) {
        Bitmap bitmap = null;
        InputStream is = HttpRequest.getImage(TestParam.panoID, 2, 4, TestParam.qsdata_udt, 5);
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
    }
}