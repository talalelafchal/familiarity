public class GLCameraOptions {
    public float eyeX;
    public float eyeY;
    public float eyeZ;
    public float centerX;
    public float centerY;
    public float centerZ;
    public float upX;
    public float upY;
    public float upZ;
    public float near;
    public float far;

    public GLCameraOptions setEye(float x, float y, float z) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
        return this;
    }

    public GLCameraOptions setCenter(float x, float y, float z) {
        centerX = x;
        centerY = y;
        centerZ = z;
        return this;
    }

    public GLCameraOptions setUp(float x, float y, float z) {
        upX = x;
        upY = y;
        upZ = z;
        return this;
    }

    public GLCameraOptions setDepthRange(float near, float far) {
        this.near = near;
        this.far = far;
        return this;
    }
}