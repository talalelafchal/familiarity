public abstract class Model3D {

    private Integer mProgram;
    private int mColorHandle;

    private float[] mColor;
    private String mVertexShader;
    private String mFragmentShader;

    private void setupShaders() {
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram,
                GLHelper.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShader));
        GLES20.glAttachShader(mProgram,
                GLHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShader));
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {
        if(mProgram == null) {
            if(mVertexShader == null || mFragmentShader == null) {
                return;
            };
            setupShaders();
        }

        GLES20.glUseProgram(mProgram);
        GLHelper.checkGlError("glUseProgram");

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLHelper.checkGlError("glGetAttribLocation");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLHelper.checkGlError("glEnableVertexAttribArray");
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, getVertexBuffer());
        GLHelper.checkGlError("glVertexAttribPointer");

        if(mColor != null) {
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            GLHelper.checkGlError("glGetUniformLocation");
            GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);
            GLHelper.checkGlError("glUniform4fv");
        }

        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLHelper.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0);
        GLHelper.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, getIndexBuffer().capacity(), GLES20.GL_UNSIGNED_SHORT, getIndexBuffer());
        GLHelper.checkGlError("glDrawElements");

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLHelper.checkGlError("glDisableVertexAttribArray");
    }

    protected abstract FloatBuffer getVertexBuffer();

    protected abstract ShortBuffer getIndexBuffer();

    public void setColor(int color) {
        mColor = new float[] {GLColor.r(color), GLColor.g(color), GLColor.b(color), GLColor.a(color)};
        if(mVertexShader == null || mFragmentShader == null) {
            setShaders(Shaders.COLOR_VERTEX_SHADER, Shaders.COLOR_FRAGMENT_SHADER);
        }
    }

    private void setShaders(String vertexShader, String fragmentShader) {
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        mProgram = null;
    }
}