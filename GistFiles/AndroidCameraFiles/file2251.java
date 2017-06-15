package com.example.xubin.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity {


    private EditText edit=null;
    private CheckBox check=null;

    //下拉列表
    private Spinner spinner=null;
    private ArrayAdapter<CharSequence> adapter=null;

    private Spinner spedu=null;
    private ArrayAdapter<CharSequence> adedu=null;
    private List<CharSequence> dateEdu=null;//定义一个集合数据


    //时间选择器
    private TimePicker mytp=null;

    //日期选择器
    private DatePicker mydp=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        this.check=(CheckBox)super.findViewById(R.id.check);
        this.check.setChecked(true);
        this.edit=(EditText)super.findViewById(R.id.edit);//取得组件
        //this.edit.setEnabled(false);//不可编辑


        //下拉列表
        this.spinner=(Spinner)super.findViewById(R.id.mycolor);
        this.spinner.setPrompt("请选择喜欢的颜色");
        this.adapter=ArrayAdapter.createFromResource(this,
                R.array.color_labels,android.R.layout.simple_dropdown_item_1line);
        this.adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.spinner.setAdapter(this.adapter);

        //配置list集合包装的下拉框内容
        this.dateEdu=new ArrayList<CharSequence>();
        this.dateEdu.add("大学");
        this.dateEdu.add("高中");
        this.dateEdu.add("初中");
        this.spedu=(Spinner)super.findViewById(R.id.myedu);
        this.spedu.setPrompt("请选择学历");
        this.adedu=new ArrayAdapter<CharSequence>(this,android.R.layout.simple_dropdown_item_1line,this.dateEdu);
        this.spedu.setAdapter(this.adedu);


        this.mytp=(TimePicker)super.findViewById(R.id.tp);
        this.mytp.setIs24HourView(true);//设置为24小时制
        this.mytp.setCurrentHour(20);//设置时
        this.mytp.setCurrentMinute(59);//设置分

        this.mydp=(DatePicker)super.findViewById(R.id.dp);
        this.mydp.updateDate(2000,1,1);//更新日期

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
