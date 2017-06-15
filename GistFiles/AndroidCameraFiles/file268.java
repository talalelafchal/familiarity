package rish.crearo.imagestablization.camera;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by rish on 20/4/17.
 */

public class Shader {

    private static final String TAG = Shader.class.getSimpleName();
    private int mProgram = 0, mVertexShader = 0, mFragmentShader = 0;
    private String mVertexSource, mFragmentSource;

    private final HashMap<String, Integer> mShaderHandleMap = new HashMap<String, Integer>();

    public Shader() {
    }

    public void setProgram(Context context, int vertexRaw, int fragmentRaw) throws Exception {
        mVertexSource = loadRawString(vertexRaw, context);
        mFragmentSource = loadRawString(fragmentRaw, context);

        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexSource);
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentSource);

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, mVertexShader);
            GLES20.glAttachShader(program, mFragmentShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            if (linkStatus[0] != 0) {
                String error = GLES20.glGetProgramInfoLog(program);
                deleteProgram();
                throw new Exception(error);
            }
        }
        mProgram = program;
        mShaderHandleMap.clear();
    }

    private int loadShader(int shaderType, String source) throws Exception {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int compiled[] = new int[1];

            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String error = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                throw new Exception(error);
            }
        }
        return shader;
    }

    public int getHandle(String name) {
        if (mShaderHandleMap.containsKey(name)) {
            return mShaderHandleMap.get(name);
        }

        int handle = GLES20.glGetAttribLocation(mProgram, name);
        if (handle == -1) {
            handle = GLES20.glGetUniformLocation(mProgram, name);
        }
        if (handle == -1) {
            Log.d(TAG, "Could not get attrib location for " + name);
        } else {
            mShaderHandleMap.put(name, handle);
        }

        return handle;
    }

    public void useProgram() {
        GLES20.glUseProgram(mProgram);
    }

    public void deleteProgram() {
        GLES20.glDeleteShader(mFragmentShader);
        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteProgram(mProgram);
        mProgram = mFragmentShader = mVertexShader = 0;
    }

    private String loadRawString(int rawId, Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(rawId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return baos.toString();
    }

}
