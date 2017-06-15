package com.baidu.demo.graphic;

import javax.microedition.khronos.opengles.GL10;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import com.baidu.demo.HttpRequst.HttpRequest;
import com.baidu.demo.util.TestParam;
import com.baidu.demo.util.opengl.*;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-18
 * Time: 下午4:03
 * To change this template use File | Settings | File Templates.
 */
public class Sphere {
    public final float PI = 3.14159f;
    private float angle, angleX, angleZ, radius;
    private int vertexCount, row, column, tranCount, texRCount, texCCount;
    private Vector<texNode> texManager = new Vector<texNode>();
    private Vector<Vertex> vecPoint = new Vector<Vertex>();
    private Vector<Mesh> vecMesh = new Vector<Mesh>();
    private String panoID;

    public Sphere(int row, int column, float radius) {
        this.row = row;
        this.column = column;
        this.radius = radius;

        panoID = "01002200001308291352525005D";
        angle = 0.f;
        vertexCount = (row + 1) * (column + 1);

        //构造球模型
        float rUnit = 360.05f / row;
        float cUnit = 180.f / column;
        final float DEGREE_TO_RADIUS = PI / 180;
        int iLen = column + 1;
        int jLen = row + 1;
        float radiusAroundZ;
        float radiusAroundY;
        float tempLen;
        for (int i = 0; i < iLen; i++) {
            radiusAroundZ = i * cUnit * DEGREE_TO_RADIUS;
            for (int j = 0; j < jLen; j++) {
                radiusAroundY = -1 * j * rUnit * DEGREE_TO_RADIUS;
                Vertex v = new Vertex();
                v.y = radius * (float) Math.cos(PI - radiusAroundZ);

                if (i == 0 || i == (iLen - 1)) {
                    v.x = v.z = 0f;
                } else {
                    tempLen = (float) Math.sqrt(radius * radius - v.y * v.y);
                    v.x = tempLen * (float) Math.cos(radiusAroundY);
                    v.z = tempLen * (float) Math.sin(radiusAroundY);
                }

                v.y += radius / 30f;
                v.y = -v.y;
                v.z = -v.z;
                if (i == 0 || i == (iLen - 1)) {
                    v.u = (j + 0.5f) * 1 / row;
                } else {
                    v.u = (float) j / row;
                }

                v.u = (float) j / row;
                v.v = (float) i / column;

                v.uCull = v.u;
                v.vCull = v.v;

                float fLen = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
                v.nx = v.x / fLen;
                v.ny = v.y / fLen;
                v.nz = v.z / fLen;
                v.rowIdx = v.colIdx = -1;

                vecPoint.add(v);
            }
        }

        iLen = row;
        for (int i = 0; i < iLen; i++) {
            Mesh m = new Mesh();
            m.index0 = i;
            m.index1 = (row + 1) + i;
            m.index2 = (row + 1) + i + 1;
            m.visual = true;
            vecMesh.add(m);
        }

        iLen = column - 1;
        jLen = row;
        for (int i = 1; i < iLen; i++) {
            for (int j = 0; j < jLen; j++) {
                Mesh m = new Mesh();
                m.index0 = i * (row + 1) + j;
                m.index1 = (i + 1) * (row + 1) + j;
                m.index2 = (i + 1) * (row + 1) + j + 1;
                vecMesh.add(m);

                m = new Mesh();
                m.index0 = i * (row + 1) + j;
                m.index1 = (i + 1) * (row + 1) + j + 1;
                m.index2 = i * (row + 1) + j + 1;
                m.visual = true;
                vecMesh.add(m);
            }

        }

        iLen = (row + 1) * column - 1;
        for (int i = (row + 1) * (column - 1); i < iLen; i++) {
            Mesh m = new Mesh();
            m.index0 = i;
            m.index1 = i + row + 1;
            m.index2 = i + 1;
            m.visual = true;
            vecMesh.add(m);
        }
        tranCount = vecMesh.size();


    }

    public boolean drawByPipeLine(String panoID, //int width, int height, int fovy,
                                  float northDir, float pitch, float roll, float yawCamera, float pitchCamera, float rollCamera, GL10 gl) {
//        Log.i("Demo","start drawing");
//        gl.glViewport(0, 0, width, height);
//        gl.glClearColor(1f, 0f, 0f, 0f);
//        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//        gl.glShadeModel(GL10.GL_FLAT);
//        gl.glEnable(GL10.GL_TEXTURE_2D);
//        gl.glDisable(GL10.GL_DEPTH_TEST);
//        gl.glLoadIdentity();
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        GLU.gluLookAt(gl, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);

        //沿Z轴旋转
        gl.glRotatef(roll, 0.0f, 0.0f, 1.0f);

        //沿X轴旋转
        gl.glRotatef(pitch + pitchCamera, 1.0f, 0.0f, 0.0f);

        //沿Y轴旋转
        gl.glRotatef((northDir + 90.f + yawCamera), 0.0f, 1.0f, 0.0f);

//        gl.glMatrixMode(GL10.GL_PROJECTION);
//        gl.glLoadIdentity();
//        //设置proj矩阵
//        GLU.gluPerspective(gl, fovy, width / height, 2.0f, 1000.0f);
        calcTexture(4);

        cullMesh(gl);

        float[] verArray = new float[3 * 3];
        float[] norArray = new float[3 * 3];
        float[] texArray = new float[2 * 3];

        for (int i = 0; i < tranCount; i++) {
//            Log.i("Demo","loading texture");
            Mesh pMesh = vecMesh.get(i);
//            if (pMesh.visual) {
                int rowIdx = vecPoint.get(pMesh.index0).rowIdx;
                int colIdx = vecPoint.get(pMesh.index0).colIdx;
                int levelTemp = 4;
                String strPath = panoID + colIdx + "_" + rowIdx + levelTemp;
                int textureID = findTex(strPath);
                if (textureID == -1) {
                    Log.i("Demo","c:"+colIdx+"r:"+rowIdx);
                    textureID = loadImageTexture(panoID, colIdx, rowIdx, levelTemp, gl);
                    texNode node = new texNode();
                    node.textureID = textureID;
                    node.strPath = strPath;
                    texManager.add(node);
                    //gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT,1);
                }
//                Log.i("Demo","tid:"+textureID);
//                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
//                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
//                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
//                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);//线性过滤
//                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);//线性过滤
//                gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

                verArray[0] = vecPoint.get(pMesh.index0).x;
                verArray[1] = vecPoint.get(pMesh.index0).y;
                verArray[2] = vecPoint.get(pMesh.index0).z;
                verArray[3] = vecPoint.get(pMesh.index1).x;
                verArray[4] = vecPoint.get(pMesh.index1).y;
                verArray[5] = vecPoint.get(pMesh.index1).z;
                verArray[6] = vecPoint.get(pMesh.index2).x;
                verArray[7] = vecPoint.get(pMesh.index2).y;
                verArray[8] = vecPoint.get(pMesh.index2).z;

                norArray[0] = vecPoint.get(pMesh.index0).nx;
                norArray[1] = vecPoint.get(pMesh.index0).ny;
                norArray[2] = vecPoint.get(pMesh.index0).nz;
                norArray[3] = vecPoint.get(pMesh.index1).nx;
                norArray[4] = vecPoint.get(pMesh.index1).ny;
                norArray[5] = vecPoint.get(pMesh.index1).nz;
                norArray[6] = vecPoint.get(pMesh.index2).nx;
                norArray[7] = vecPoint.get(pMesh.index2).ny;
                norArray[8] = vecPoint.get(pMesh.index2).nz;

                texArray[0] = vecPoint.get(pMesh.index0).uCull;
                texArray[1] = vecPoint.get(pMesh.index0).vCull;
                texArray[2] = vecPoint.get(pMesh.index1).uCull;
                texArray[3] = vecPoint.get(pMesh.index1).vCull;
                texArray[4] = vecPoint.get(pMesh.index2).uCull;
                texArray[5] = vecPoint.get(pMesh.index2).vCull;

                if (Math.abs(texArray[0] - texArray[2]) < 1.0e-5f) {
                    if (texArray[0] > texArray[4]) {
                        texArray[4] = 1f;
                    }
                    if(texArray[2]>texArray[4]){
                        texArray[4]=1f;
                    }
                }

                if (Math.abs(texArray[2] - texArray[4]) < 1.0e-5f) {
                    if (texArray[0] > texArray[2]) {
                        texArray[2] = 1f;
                    }
                    if(texArray[0]>texArray[4]){
                        texArray[4]=1f;
                    }
                }

                if (Math.abs(texArray[1] - texArray[5]) < 1.0e-5f) {
                    if (texArray[5] > texArray[3]) {
                        texArray[3] = 1f;
                    }
                    if(texArray[1]>texArray[3]){
                        texArray[3]=1f;
                    }
                }
                if (Math.abs(texArray[3] - texArray[5]) < 1.0e-5f) {
                    if (texArray[1] > texArray[3]) {
                        texArray[3] = 1f;
                    }
                    if(texArray[1]>texArray[5]){
                        texArray[5]=1f;
                    }
                }


                ByteBuffer vbb = ByteBuffer.allocateDirect(verArray.length * 4);
                vbb.order(ByteOrder.nativeOrder());
                FloatBuffer mVertexBuffer = vbb.asFloatBuffer();
                mVertexBuffer.put(verArray);
                mVertexBuffer.position(0);


                ByteBuffer nbb = ByteBuffer.allocateDirect(norArray.length * 4);
                nbb.order(ByteOrder.nativeOrder());
                FloatBuffer mNormalBuffer = vbb.asFloatBuffer();
                mNormalBuffer.put(norArray);
                mNormalBuffer.position(0);


                ByteBuffer cbb = ByteBuffer.allocateDirect(texArray.length * 4);
                cbb.order(ByteOrder.nativeOrder());
                FloatBuffer mTextureBuffer = cbb.asFloatBuffer();
                mTextureBuffer.put(texArray);
                mTextureBuffer.position(0);

                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                gl.glVertexPointer(3, GL10.GL_FIXED, 0, mVertexBuffer);


                gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
                gl.glNormalPointer(GL10.GL_FIXED, 0, mNormalBuffer);


                gl.glEnable(GL10.GL_TEXTURE_2D);
//                gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
//                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
//                gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
                  gl.glColor4x(1,0,0,0);
                gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 1);
//            }
        }
        return true;
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
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
//                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
//            // 设置纹理被放大（距离视点很近时被方法）时候的滤波方式
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D,
//                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
//            // 设置在横向、纵向上都是平铺纹理
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
//                    GL10.GL_REPEAT);
//            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
//                    GL10.GL_REPEAT);

            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);//线性过滤
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);//线性过滤
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
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

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngleX() {
        return angleX;
    }

    public void setAngleX(float angleX) {
        this.angleX = angleX;
    }

    public float getAngleZ() {
        return angleZ;
    }

    public void setAngleZ(float angleZ) {
        this.angleZ = angleZ;
    }

    public String getPanoID() {
        return panoID;
    }

    public void setPanoID(String panoID) {
        this.panoID = panoID;
    }

    private void setImgRowCol(int level) {
        switch (level) {
            case 1: {
                texRCount = 1;
                texCCount = 1;
            }
            break;
            case 2: {
                texRCount = 2;
                texCCount = 1;
            }
            break;
            case 3: {
                texRCount = 4;
                texCCount = 2;
            }
            break;
            case 4: {
                texRCount = 8;
                texCCount = 4;
            }
            break;
            case 5: {
                texRCount = 16;
                texCCount = 8;
            }
            break;
        }
    }

    private void calcTexture(int level) {
//        Log.i("Demo","start calcText");
        setImgRowCol(level);
        int iLen = vecPoint.size();
        Vector<Integer> tempR = new Vector<Integer>();
        Vector<Integer> tempC = new Vector<Integer>();
        for (int i = 0; i < iLen; i++) {
            Vertex v = vecPoint.get(i);
            v.rowIdx = (int) Math.floor(v.u * texRCount);
            v.colIdx = (int) Math.floor(v.v * texCCount);
            v.uCull = v.u * texRCount - v.rowIdx;
            v.vCull = v.v * texCCount - v.colIdx;

            tempR.add(v.rowIdx);
            tempC.add(v.colIdx);
        }

        //求最小值
        int minRow = -100;
        int minCol = -100;
        int rowCount = tempR.size();
        for (int i = 0; i < rowCount; i++) {
            if (minRow < tempR.get(i)) {
                minRow = tempR.get(i);
            }
            if (minCol < tempC.get(i)) {
                minCol = tempC.get(i);
            }
        }

        for (int i = 0; i < iLen; i++) {
            Vertex v = vecPoint.get(i);
            if (v.rowIdx > minRow) {
                v.uCull = 1f;
            }
            if (v.colIdx > minCol) {
                v.vCull = 1f;
            }
        }
    }

    private boolean isVisible(Vertex pos, GL10 gl) {
        MatrixGrabber mg = new MatrixGrabber();
        float[] matView = mg.mModelView;
        float[] matPro = mg.mProjection;

        VectorGL vView = new VectorGL();
        vView.x = pos.x * matView[0] + pos.y * matView[4] + pos.z * matView[8] + matView[12];
        vView.y = pos.x * matView[1] + pos.y * matView[5] + pos.z * matView[9] + matView[13];
        vView.z = pos.x * matView[2] + pos.y * matView[6] + pos.z * matView[10] + matView[14];
        vView.w = pos.x * matView[3] + pos.y * matView[7] + pos.z * matView[11] + matView[15];

        VectorGL vProj = new VectorGL();
        vProj.x = vView.x * matPro[0] + vView.y * matPro[4] + vView.z * matPro[8] + matPro[12];
        vProj.y = vView.x * matPro[1] + vView.y * matPro[5] + vView.z * matPro[9] + matPro[13];
        vProj.z = vView.x * matPro[2] + vView.y * matPro[6] + vView.z * matPro[10] + matPro[14];
        vProj.w = vView.x * matPro[3] + vView.y * matPro[7] + vView.z * matPro[11] + matPro[15];

        vProj.x /= vProj.w;
        vProj.y /= vProj.w;
        vProj.z /= vProj.w;
        vProj.w /= vProj.w;

        return vProj.x >= -1.0 && vProj.x <= 1.0
                && vProj.y >= -1.0 && vProj.y <= 1.0
                && vProj.z >= 0.0 && vProj.z <= 1.0;
    }

    private void cullMesh(GL10 gl) {
//        Log.i("Demo","cullMesh");
        Mesh mesh = new Mesh();
        Vertex[] vertex = new Vertex[3];
        for (int i = 0; i < tranCount; i++) {
            mesh = vecMesh.get(i);
            vertex[0] = vecPoint.get(mesh.index0);
            vertex[1] = vecPoint.get(mesh.index1);
            vertex[2] = vecPoint.get(mesh.index2);
            //对三角形做裁切
            boolean[] bVisible = new boolean[3];

            for (int j = 0; j < 3; j++) {
                Vertex vTemp = vertex[j];
                bVisible[j] = isVisible(vTemp, gl);
            }
            if (bVisible[0] || bVisible[1] || bVisible[2]) {
                mesh.visual = true;
            } else {
                mesh.visual = false;
            }

        }

    }

    private int findTex(String texPath) {
        int texCount = texManager.size();
        for (int i = 0; i < texCount; i++) {
            texNode temp = texManager.get(i);
            if (texPath.equals(temp.strPath)) {
                return temp.textureID;
            }
        }
        return -1;
    }

    private class texNode {
        protected String strPath;
        protected int textureID = -1;
    }

    private class VectorGL {
        float x, y, z, w;
    }

}
