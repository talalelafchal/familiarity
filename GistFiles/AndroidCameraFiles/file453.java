public class Shaders {
    public static final String COLOR_VERTEX_SHADER =
        "uniform mat4 uMVPMatrix;\n" +
        "attribute vec4 vPosition;\n" +
        "void main() {\n" +
        "  gl_Position = uMVPMatrix * vPosition;\n" +
        "  gl_PointSize = 10.0;\n" +
        "}";

    public static final String COLOR_FRAGMENT_SHADER =
        "precision mediump float;\n" +
        "uniform vec4 vColor;\n" +
        "void main() {\n" +
        "  gl_FragColor = vColor;\n" +
        "}";

    public static int loadShader(int type, String shaderCode){

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type);

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);

            return shader;
    }
}
