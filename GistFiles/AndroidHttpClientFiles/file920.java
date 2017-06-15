package com.baidu.demo.Activities;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import com.baidu.demo.graphic.OpenGLInterface;
import com.baidu.demo.graphic.OpenGLRender;
import com.baidu.demo.graphic.OpenGLRender1;
import com.baidu.demo.graphic.Sphere;
import com.baidu.demo.util.TestParam;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-26
 * Time: 上午10:21
 * To change this template use File | Settings | File Templates.
 */
public class StreetScape extends Activity implements OpenGLInterface {

    private Sphere sphere;
    private GLSurfaceView surfaceView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surfaceView = new GLSurfaceView(this);
        surfaceView.setRenderer(new OpenGLRender1(this));
        setContentView(surfaceView);
    }

    @Override
    public void DrawScene(GL10 gl) {
        sphere.drawByPipeLine(TestParam.panoID,10,10,10,10,10,10,gl);
    }

    @Override
    public void initObject(GL10 gl) {
        sphere = new Sphere(36,36,10000);
    }
}