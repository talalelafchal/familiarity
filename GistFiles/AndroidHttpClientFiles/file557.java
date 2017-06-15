package com.baidu.demo.graphic;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-7
 * Time: 下午5:42
 * To change this template use File | Settings | File Templates.
 */
public class OpenGLRender implements GLSurfaceView.Renderer {

    private final OpenGLInterface streetscape;

    public OpenGLRender(OpenGLInterface streetscape) {
        this.streetscape = streetscape;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glShadeModel(GL10.GL_SMOOTH);

        gl.glClearDepthf(1.0f);

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glFrontFace(GL10.GL_CCW);

        gl.glEnable(GL10.GL_CULL_FACE);

        gl.glCullFace(GL10.GL_FRONT);


        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        if (streetscape != null) {
            streetscape.initObject(gl);
        }

    }

    public void onDrawFrame(GL10 gl) {
        if (streetscape != null) {
            streetscape.DrawScene(gl);
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Sets the current view port to the new size.
        gl.glViewport(0, 0, width, height);
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();
        // Calculate the aspect ratio of the window
        GLU.gluPerspective(gl, 90f, (float) width / (float) height, 0.1f, 1000.0f);
        // Select the modelview matrix
        gl.glMatrixMode(GL10.GL_MODELVIEW);
//        gl.glTranslatef(0.0f,0.0f,-3.0f);
        // Reset the modelview matrix
        gl.glLoadIdentity();
    }
}
