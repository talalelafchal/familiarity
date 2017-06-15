/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.airhockey.copy;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.copy.util.LoggerConfig;
import com.airhockey.copy.util.ShaderHelper;
import com.airhockey.copy.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class AirHockeyRenderer implements Renderer {

    private static final String A_POSITION = "a_Position";//用于获取vertexShader中的位置变量
    private static final String A_COLOR = "a_Color";//用于获取vertexShader中的颜色变量
    private static final int COLOR_COMPONENT_COUNT = 3;//RGB三个分量
    private static final int POSITION_COMPONENT_COUNT = 2;//记录顶点的两个分量
    private static final int BYTES_PER_FLOAT = 4;//一个float类型占用4个字节
    private static final int STRIDE = (POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)
            *BYTES_PER_FLOAT;//设置一个跨距
    private final FloatBuffer vertexData;//用于在本地存储数据
    private final Context context;//定义上下文
    private int program;//新建程序对象，用于调用已经链接在一起的着色器
    private int aPositionLocation;
    private int aColorLocation;



    public AirHockeyRenderer() {
        // This constructor shouldn't be called -- only kept for showing
        // evolution of the code in the chapter.
        context = null;
        vertexData = null;
    }

    public AirHockeyRenderer(Context context) {
        this.context = context;
        

        float[] tableVerticesWithTriangles = {

           //Triangle Fan(X,Y,R,G,B)
                0f,    0f,   1f,   1f,   1f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                //Line
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,
                //Mallets
                0f, -0.25f, 0f, 0f, 1f,
                0f,  0.25f, 1f, 0f, 0f
        };

        /**
         * 分配内存函数
         */
        vertexData = ByteBuffer
                //使用allocate进行分配内存
            .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                //声明字节序组织方式，按照本地字节序进行组织
            .order(ByteOrder.nativeOrder())
                //以浮点数而非单独字节的方式操作数据
            .asFloatBuffer();
        //将数据从Dalvik的内存复制到本地内存
        vertexData.put(tableVerticesWithTriangles);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {


        //设置清屏颜色及透明度
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //通过调用TextResourceReader中的readTextFileFromResource函数
        // 对vertex_shader及fragment_shader进行读取
        String vertexShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        //编译vertexShader及fragmentShader
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

              //调用已经链接的着色器
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        //验证程序是否有效，是否可以运行
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        //通过新建的程序在屏幕进行绘制
        glUseProgram(program);

        //获取vertexShader中的a_Position
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        //获取vertexShader中的a_COLOR
        aColorLocation = glGetAttribLocation(program,A_COLOR);

        
        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        //关联到vertexShader中的Position数据
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        //把顶点数据与着色器中的a_Color关联一起
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);
    }


    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface./设置视口尺寸
        glViewport(0, 0, width, height);
    }


    /**
     * 绘制函数。所有的绘制操作函数均在这里执行
     */
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface./调用glClear清空屏幕，并使用
        //glClearColor设定的颜色进行填充屏幕
        //glClear(GL_COLOR_BUFFER_BIT);

       glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        glDrawArrays(GL_LINES, 6, 2);
        glDrawArrays(GL_POINTS,8,1);
        glDrawArrays(GL_POINTS,9,1);
    }
}
