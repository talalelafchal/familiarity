package com.baidu.demo.tut;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import com.baidu.demo.HttpRequst.HttpRequest;
import com.baidu.demo.util.TestParam;

public class Ball {

    public final float maxZoom = -2f;
    public final float minZoom = -4f;
    public float zoom = -0f;
    public float mAngleX = 0;// 沿x轴旋转角度
    public float mAngleY = 0;// 沿y轴旋转角度
    public float mAngleZ = 0;// 沿z轴旋转角度
    int vCount, angleSpan;// 顶点数量
    private IntBuffer mVertexBuffer;// 顶点坐标数据缓冲
    private FloatBuffer mTextureBuffer;// 顶点纹理数据缓冲
    private int colCount, rowCount, picCount;
    private int[] textureID;
    private ArrayList<Integer> alVertix = new ArrayList<Integer>();// 存放顶点坐标的ArrayList
    ArrayList<Float> alTexture = new ArrayList<Float>();// 纹理

    public Ball(int scale, int angleSpan) {
        final int R = 10000 * scale;

        // 实际顶点坐标数据的初始化================begin============================


        this.angleSpan = angleSpan;// 将球进行单位切分的角度

        for (int rowAngle = 90; rowAngle >= -90; rowAngle -= angleSpan) {
            for (int colAngleAngle = 360; colAngleAngle > 0; colAngleAngle -= angleSpan) {
                double xozLength = R * Math.cos(Math.toRadians(rowAngle));
                int x = (int) (xozLength * Math.cos(Math.toRadians(colAngleAngle)));
                int z = (int) (xozLength * Math.sin(Math.toRadians(colAngleAngle)));
                int y = (int) (R * Math.sin(Math.toRadians(rowAngle)));
                alVertix.add(x);
                alVertix.add(y);
                alVertix.add(z);
            }
        }


    }

    public void drawSelf(GL10 gl) {

        gl.glTranslatef(0f, 0f, zoom);

//        gl.glPopMatrix();
        float[] modelview = new float[16];
        ((GL11) gl).glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview, 0); // 获取当前矩阵
        float[] x_axis = {1, 0, 0, 0};
        float[] y_axis = {0, 1, 0, 0};
        Matrix.invertM(modelview, 0, modelview, 0); // 求逆矩阵
        Matrix.multiplyMV(x_axis, 0, modelview, 0, x_axis, 0); // 获取世界x轴在模型坐标系里的指向（w轴）
        Matrix.multiplyMV(y_axis, 0, modelview, 0, y_axis, 0);

        gl.glRotatef(mAngleX, y_axis[0], y_axis[1], y_axis[2]);
        gl.glRotatef(mAngleZ, 0, 0, 1);// 沿Z轴旋转

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 为画笔指定顶点坐标数据
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);


        // 开启纹理
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 允许使用纹理ST坐标缓冲
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // 为画笔指定纹理ST坐标缓冲
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        // 绑定当前纹理

        int temp = vCount/picCount;
        for(int i=0;i<picCount;i++){
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[i]);
            gl.glDrawArrays(GL10.GL_TRIANGLES, i*temp, temp);
        }
//        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
////
////        // 绘制图形
//        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, temp);
//        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[1]);
//
//        // 绘制图形
//        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vCount);

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
            InputStream is = HttpRequest.getImage(panoID, row, col, TestParam.qsdata_udt, level);
            col++;
            try {
                bitmap = BitmapFactory.decodeStream(is);
                // 通知OpenGL将texture纹理绑定到GL10.GL_TEXTURE_2D目标中
                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[i]);
                // 设置纹理被缩小（距离视点很远时被缩小）时候的滤波方式
                gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                        GL10.GL_TEXTURE_MIN_FILTER,  GL10.GL_LINEAR);
                // 设置纹理被放大（距离视点很近时被方法）时候的滤波方式
                gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                        GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
                // 设置在横向、纵向上都是平铺纹理
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                        GL10.GL_REPEAT);
                gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                        GL10.GL_REPEAT);
                gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
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
        textureMapping();
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

    private void textureMapping() {
        vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        // 将alVertix中的坐标值转存到一个int数组中
        int vertices[] = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }
        alVertix.clear();


        int row = (180 / angleSpan) + 1;// 球面切分的行数
        int col = 360 / angleSpan;// 球面切分的列数

        float splitRow = row/rowCount;
        float splitCol = col/colCount;
//        float splitRow = row;
//        float splitCol = col;

        for (int i = 0,a = 0; i < row; i++,a++)// 对每一行循环
        {
            if (i > 0 && i < row - 1) {// 中间行
                for (int j = 0, b= 0; j < col; j++, b++) {// 中间行的两个相邻点与下一行的对应点构成三角形
                    if(a==splitRow){
                        a=0;
                    }
                    if(b==splitCol){
                        b=0;
                    }
                    int k = i * col + j;
                    // 第1个三角形顶点
                    alVertix.add(vertices[(k + col) * 3]);
                    alVertix.add(vertices[(k + col) * 3 + 1]);
                    alVertix.add(vertices[(k + col) * 3 + 2]);

                    // 纹理坐标
                    alTexture.add(b / splitCol);
                    alTexture.add((a + 1) / splitRow);

                    // 第2个三角形顶点
                    int tmp = k + 1;
                    if (j == col - 1) {
                        tmp = (i) * col;
                    }
                    alVertix.add(vertices[(tmp) * 3]);
                    alVertix.add(vertices[(tmp) * 3 + 1]);
                    alVertix.add(vertices[(tmp) * 3 + 2]);

                    // 纹理坐标
                    alTexture.add((b + 1) / splitCol);
                    alTexture.add(a / splitRow);

                    // 第3个三角形顶点
                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);

                    // 纹理坐标
                    alTexture.add(b / splitCol);
                    alTexture.add(a / splitRow);

                    alVertix.add(vertices[(k - col) * 3]);
                    alVertix.add(vertices[(k - col) * 3 + 1]);
                    alVertix.add(vertices[(k - col) * 3 + 2]);
                    alTexture.add(b / splitCol);
                    alTexture.add((a - 1) / splitRow);

                    int tmp1 = k - 1;
                    if (j == 0) {
                        tmp1 = i * col + col - 1;
                    }
                    // 第2个三角形顶点
                    alVertix.add(vertices[(tmp1) * 3]);
                    alVertix.add(vertices[(tmp1) * 3 + 1]);
                    alVertix.add(vertices[(tmp1) * 3 + 2]);
                    alTexture.add((b - 1) / splitCol);
                    alTexture.add(a / splitRow);

                    // 第3个三角形顶点
                    alVertix.add(vertices[k * 3]);
                    alVertix.add(vertices[k * 3 + 1]);
                    alVertix.add(vertices[k * 3 + 2]);
                    alTexture.add(b / splitCol);
                    alTexture.add(a / splitRow);


                }

            }
        }

        vCount = alVertix.size() / 3;// 顶点的数量为坐标值数量的1/3，因为一个顶点有3个坐标

        // 将alVertix中的坐标值转存到一个int数组中
        vertices = new int[vCount * 3];
        for (int i = 0; i < alVertix.size(); i++) {
            vertices[i] = alVertix.get(i);
        }

        // 创建绘制顶点数据缓冲
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mVertexBuffer = vbb.asIntBuffer();// 转换为int型缓冲
        mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);// 设置缓冲区起始位置


        // 创建纹理坐标缓冲
        float textureCoors[] = new float[alTexture.size()];// 顶点纹理值数组
        for (int i = 0; i < alTexture.size(); i++) {
            textureCoors[i] = alTexture.get(i);
        }

        ByteBuffer cbb = ByteBuffer.allocateDirect(textureCoors.length * 4);
        cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
        mTextureBuffer = cbb.asFloatBuffer();// 转换为int型缓冲
        mTextureBuffer.put(textureCoors);// 向缓冲区中放入顶点着色数据
        mTextureBuffer.position(0);// 设置缓冲区起始位置

        // 三角形构造顶点、纹理、法向量数据初始化==========end==============================
    }

    public void cutSpeed() {

        float speed = 5f;
        mAngleX -= speed;
        mAngleY -= speed;
        mAngleZ -= speed;
        if (mAngleX < 0)
            mAngleX = 0;
        if (mAngleY < 0)
            mAngleY = 0;
        if (mAngleZ < 0)
            mAngleZ = 0;
    }

    private class Vertex{
        protected int x,y,z;
        protected float s,t;
    }

    private class VertexList{
        protected ArrayList<Vertex> list = new ArrayList<Vertex>();
    }
}
