package com.example.xubin.clickproject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class MyActivity extends Activity {

    private EditText edit=null;
    private Button btn=null;
    private TextView txt=null;


    private EditText num1=null;
    private EditText num2=null;
    private TextView end=null;
    private TextView tv=null;
    private Button add=null;
    private Button min=null;
    private Button sub=null;
    private Button div=null;

    private int editnum1=0;
    private int editnum2=0;

    private Button change=null;
    private ImageView img=null;

    private EditText password=null;
    private CheckBox check=null;

    private TextView show=null;
    private RadioGroup sex=null;
    private RadioButton male=null;
    private RadioButton female=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        this.edit=(EditText)super.findViewById(R.id.myedit);
        this.btn=(Button)super.findViewById(R.id.btn);
        this.txt=(TextView)super.findViewById(R.id.txt);


        this.num1=(EditText)super.findViewById(R.id.num1);
        this.num2=(EditText)super.findViewById(R.id.num2);
        this.tv=(TextView)super.findViewById(R.id.tv);
        this.end=(TextView)super.findViewById(R.id.end);
        this.add=(Button)super.findViewById(R.id.add);
        this.min=(Button)super.findViewById(R.id.min);
        this.sub=(Button)super.findViewById(R.id.sub);
        this.div=(Button)super.findViewById(R.id.div);
        this.btn.setOnClickListener(new ShowListener()
//        {
//            public void onClick(View v){
//                String info=MyActivity.this.edit.getText().toString();//取得输入的内容
//                MyActivity.this.txt.setText("输入的内容是："+info);//更新文本显示组件内容
//            }
//        } 匿名内部类
         );//设置事件

        this.num1.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                MyActivity.this.num1.setText("");
            }
        });
        this.num2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MyActivity.this.num2.setText("");
            }
        });
        this.add.setOnClickListener(new AddListener());
        this.min.setOnClickListener(new MinListener());
        this.sub.setOnClickListener(new SubListener());
        this.div.setOnClickListener(new DivListener());



        this.change=(Button)super.findViewById(R.id.change);
        this.img=(ImageView)super.findViewById(R.id.img);
        this.change.setOnClickListener(new MyChangeListener());

        this.password=(EditText)super.findViewById(R.id.password);
        this.check=(CheckBox)super.findViewById(R.id.check);
        this.check.setOnClickListener(new OnClickListenerImp());


        this.show=(TextView)super.findViewById(R.id.show);
        this.sex=(RadioGroup)super.findViewById(R.id.sex);
        this.male=(RadioButton)super.findViewById(R.id.male);
        this.female=(RadioButton)super.findViewById(R.id.female);
        this.sex.setOnCheckedChangeListener(new OnCheckChangeListenerImp());
    }

    private class OnCheckChangeListenerImp implements OnCheckedChangeListener{
        public void onCheckedChanged(RadioGroup group,int checkedId){
            String temp=null;
            if(MyActivity.this.male.getId()==checkedId){
                temp=MyActivity.this.male.getText().toString();
            }
            if(MyActivity.this.female.getId()==checkedId){
                temp=MyActivity.this.female.getText().toString();
            }
            MyActivity.this.show.setText("性别是："+temp);
        }
    }

    private class ShowListener implements OnClickListener{

        public void onClick(View v){
            String info=MyActivity.this.edit.getText().toString();//取得输入的内容
            MyActivity.this.txt.setText("输入的内容是："+info);//更新文本显示组件内容
        }
    }
    private class AddListener implements OnClickListener{
        public void onClick(View v){
            MyActivity.this.editnum1=Integer.parseInt(MyActivity.this.num1.getText().toString());
            MyActivity.this.editnum2=Integer.parseInt(MyActivity.this.num2.getText().toString());
            MyActivity.this.tv.setText("+");
            MyActivity.this.end.setText(String.valueOf(editnum1+editnum2));

        }
    }
    private class MinListener implements OnClickListener{
        public void onClick(View v){
            MyActivity.this.editnum1=Integer.parseInt(MyActivity.this.num1.getText().toString());
            MyActivity.this.editnum2=Integer.parseInt(MyActivity.this.num2.getText().toString());
            MyActivity.this.tv.setText("-");
            MyActivity.this.end.setText(String.valueOf(editnum1-editnum2));

        }
    }
    private class SubListener implements OnClickListener{
        public void onClick(View v){
            MyActivity.this.editnum1=Integer.parseInt(MyActivity.this.num1.getText().toString());
            MyActivity.this.editnum2= Integer.parseInt(MyActivity.this.num2.getText().toString());
            MyActivity.this.tv.setText("*");
            MyActivity.this.end.setText(String.valueOf(editnum1*editnum2));

        }
    }
    private class DivListener implements OnClickListener{
        public void onClick(View v){
            MyActivity.this.editnum1=Integer.parseInt(MyActivity.this.num1.getText().toString());
            MyActivity.this.editnum2=Integer.parseInt(MyActivity.this.num2.getText().toString());
            MyActivity.this.tv.setText("/");
            MyActivity.this.end.setText(String.valueOf(editnum1/editnum2));
        }
    }

    private class OnClickListenerImp implements OnClickListener{
        public void onClick(View v){
            if(MyActivity.this.check.isChecked()){
                MyActivity.this.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//明文显示
            }else{
                MyActivity.this.password.setTransformationMethod(PasswordTransformationMethod.getInstance());//密文显示
            }
        }
    }

    private class MyChangeListener implements OnClickListener{
        public void onClick(View v){
            if(MyActivity.this.getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED){
                MyActivity.this.change.setText("错误：无法改变屏幕方向");
            }else{
                if(MyActivity.this.getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){//现在为竖屏
                    MyActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//改变为横屏
                }else if (MyActivity.this.getRequestedOrientation()==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                    MyActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        }
    }
    public void onConfigurationChanged(Configuration newConfig){//表示系统修改是触发
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            MyActivity.this.change.setText("改变屏幕方向为竖屏显示");
            MyActivity.this.img.setImageResource(R.drawable.ic_launcher);//显示横屏图片
        }else if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            MyActivity.this.change.setText("改变屏幕方向为横屏显示");
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
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
