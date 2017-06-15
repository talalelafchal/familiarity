package com.example.xubin.hello;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

public class HelloActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//生命周期方法

        LinearLayout layout=new LinearLayout(this);//定义布局管理器才能配置下面多个组件
//只能配置一个组件

//        TextView text=new TextView(this);//要根据上下文（context）创建组件
//        text.setText(super.getString(R.string.info));//通过string.xml文件设置文字
//        super.setContentView(text);//设置要使用的布局管理器


        //框架布局管理器也叫帧布局管理器
        FrameLayout layout1=new FrameLayout(this);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
        );
        FrameLayout.LayoutParams viewParam=new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ImageView img=new ImageView(this);
        img.setImageResource(R.drawable.ic_launcher);
        EditText edit=new EditText(this);
        edit.setText("请输入");
        Button but=new Button(this);
        but.setText("确定");
        layout.addView(img,viewParam);
        layout.addView(edit,viewParam);
        layout.addView(but,viewParam);
        super.setContentView(layout,layoutParams);

        //定义线性布局管理器
       // LinearLayout layout1=new LinearLayout(this);
//        LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.FILL_PARENT,
//                ViewGroup.LayoutParams.FILL_PARENT
//        );
//        layout.setOrientation(LinearLayout.VERTICAL);//所有组件采用垂直方式摆放
//
//        //定义显示组件的布局管理器，
//        LinearLayout.LayoutParams txtParam=new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.FILL_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT  //定义组件的布局管理器参数
//        );
//        TextView txt=new TextView(this);
//        txt.setLayoutParams(txtParam);//配置文本显示组件的参数
//        txt.setText("徐斌");
//        txt.setTextSize(20);
//        layout.addView(txt,txtParam);//增加组件
//        super.setContentView(layout,param);//增加新的布局管理器


       setContentView(R.layout.activity_hello);//设置要使用的布局管理器
        //这是一种设置文字方法
//        TextView text=(TextView)super.findViewById(R.id.name);//取得TextView组件
//        text.setText(R.string.info);//设置文字
//        Button but=(Button)super.findViewById(R.id.button);
//        but.setText(“确定”);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hello, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
