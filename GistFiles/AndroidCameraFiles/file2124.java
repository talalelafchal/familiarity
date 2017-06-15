package com.baidu.demo.graphic;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-26
 * Time: 上午10:31
 * To change this template use File | Settings | File Templates.
 */
public class OpenGLRender1 implements GLSurfaceView.Renderer {

    private final OpenGLInterface streetscape;

    public OpenGLRender1(OpenGLInterface streetscape) {
        this.streetscape = streetscape;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.f, 0.f, 0.5f);
        Log.i("Demo","create") ;

//        gl.glShadeModel(GL10.GL_SMOOTH);
//
//        gl.glClearDepthf(1.0f);
//
//        gl.glEnable(GL10.GL_DEPTH_TEST);
//        gl.glFrontFace(GL10.GL_CCW);
//
//        gl.glEnable(GL10.GL_CULL_FACE);
//
//        gl.glCullFace(GL10.GL_FRONT);
//
//
//        gl.glDepthFunc(GL10.GL_LEQUAL);
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

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
        gl.glViewport(0, 0, width, height);
        gl.glClearColor(1f, 1f, 1f, 0.5f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        GLU.gluLookAt(gl, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        //设置proj矩阵
        GLU.gluPerspective(gl, 90, width / height, 2.0f, 1000.0f);
        Log.i("Demo","changed") ;
    }
}
