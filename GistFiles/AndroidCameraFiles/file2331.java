package com.baidu.demo.graphic;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created with IntelliJ IDEA.
 * User: wangziji
 * Date: 13-11-7
 * Time: 下午6:07
 * To change this template use File | Settings | File Templates.
 */
public interface OpenGLInterface {
    public void DrawScene(GL10 gl);

    public void initObject(GL10 gl);
}
