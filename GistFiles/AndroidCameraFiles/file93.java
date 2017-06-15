import android.opengl.Matrix;
import android.util.Log;

/**
 * 矩阵变换工具类
 * 
 * @author tinker <sunting.bcwl@gmail.com>
 */
public class TSMatrixState {
    private static final int MATRIX_F4V_SIZE = 16;

    /**
     * 光源位置
     */
    public float[] mLightPosition = new float[]{0, 0, 0};
    /**
     * 光源方向
     */
    public float[] mLightDirection = new float[]{0, 0, 0};
    /**
     * 模型矩阵
     */
    private float[] mModelMatrix = new float[MATRIX_F4V_SIZE];
    /**
     * 视角矩阵(Camera)
     */
    private float[] mViewMatrix = new float[MATRIX_F4V_SIZE];
    /**
     * 模型视角矩阵
     */
    private float[] mViewModelMatrix = new float[MATRIX_F4V_SIZE];
    /**
     * 投影矩阵
     */
    private float[] mProjectionMatrix = new float[MATRIX_F4V_SIZE];
    /**
     * 经过模型-视角-投影变换后传入shader中的最终矩阵
     */
    private float[] mMVPMatrix = new float[MATRIX_F4V_SIZE];

    public TSMatrixState() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
    }

    public static void logMatrix(float[] matrix) {
        String m = "";
        for (int i = 0; i < matrix.length; i++) {
            m = m + matrix[i] + " ";
        }
        Log.d("Matrix", m);
    }

    public void translate(float x, float y, float z) {
        Matrix.translateM(mModelMatrix, 0, x, y, z);
    }

    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
    }

    public void scale(float x, float y, float z) {
        Matrix.scaleM(mModelMatrix, 0, x, y, z);
    }

    public void setLookAtM(float cx, float cy, float cz, float tx, float ty, float tz,
                           float upx, float upy, float upz) {
        Matrix.setLookAtM(mViewMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
    }

    public void frustumM(float left, float right, float bottom, float top, float near, float far) {
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void orthoM(float left, float right, float bottom, float top, float near, float far) {
        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }

    public float[] getViewModelMatrix() {
        Matrix.multiplyMM(mViewModelMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        return mViewModelMatrix;
    }

    public float[] getMVPMatrix() {
        Matrix.multiplyMM(mViewModelMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewModelMatrix, 0);
        return mMVPMatrix;
    }

    public void setLightLocation(float x, float y, float z) {
        mLightPosition[0] = x;
        mLightPosition[1] = y;
        mLightPosition[2] = z;
    }

    public void setLightDirection(float x, float y, float z) {
        mLightDirection[0] = x;
        mLightDirection[1] = y;
        mLightDirection[2] = z;
    }
}