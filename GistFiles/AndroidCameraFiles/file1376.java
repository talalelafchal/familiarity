package com.example.administrator.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2014/10/8.
 */
public class MyDialogDemo extends Activity {
    private Button mBT1,mBT2,mBT3,mBT4,mBT5,mBT6,mBT7;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog_demo);

        mBT1 = (Button) findViewById(R.id.myButton1);       // 基本提示
        mBT2 = (Button) findViewById(R.id.myButton2);       // 三個選項
        mBT3 = (Button) findViewById(R.id.myButton3);       // 有輸入框
        mBT4 = (Button) findViewById(R.id.myButton4);       // 單選框
        mBT5 = (Button) findViewById(R.id.myButton5);       // 複選框
        mBT6 = (Button) findViewById(R.id.myButton6);       // 列表框
        mBT7 = (Button) findViewById(R.id.myButton7);       // 自定義布局

        mBT1.setOnClickListener(myButton);
        mBT2.setOnClickListener(myButton);
        mBT3.setOnClickListener(myButton);
        mBT4.setOnClickListener(myButton);
        mBT5.setOnClickListener(myButton);
        mBT6.setOnClickListener(myButton);
        mBT7.setOnClickListener(myButton);
    }

    Button.OnClickListener myButton = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == mBT1.getId()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyDialogDemo.this);
                builder.setMessage("確認退出嗎?");
                builder.setTitle("提示");
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MyDialogDemo.this.finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();

            } else if (view.getId() == mBT2.getId()) {
                new AlertDialog.Builder(MyDialogDemo.this)
                        .setIcon(R.drawable.ic_launcher_camera)
                        .setTitle("喜好調查")
                        .setMessage("喜歡李連結嗎")
                        .setPositiveButton("喜歡", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MyDialogDemo.this,
                                        "很喜歡他的電影",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("不喜歡", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MyDialogDemo.this,
                                        "我不喜歡",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNeutralButton("一般", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MyDialogDemo.this,
                                        "談不上喜不喜歡",
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .create().show();

            } else if (view.getId() == mBT3.getId()) {
                new AlertDialog.Builder(MyDialogDemo.this)
                        .setTitle("請輸入")
                        .setIcon(R.drawable.ssd_ok)
                        .setView(new EditText(MyDialogDemo.this))
                        .setPositiveButton("確定", null)
                        .setNegativeButton("取消",null)
                        .show();

            } else if (view.getId() == mBT4.getId()) {
                new AlertDialog.Builder(MyDialogDemo.this)
                        .setTitle("單選框")
                        .setSingleChoiceItems(new String[] {"item1", "item2"}, 0,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            } else if (view.getId() == mBT5.getId()) {
                new AlertDialog.Builder(MyDialogDemo.this)
                        .setTitle("複選框")
                        .setMultiChoiceItems(new String[] {"item1", "item2", "item3"}, null, null)
                        .setPositiveButton("確定", null)
                        .setNegativeButton("取消", null)
                        .show();

            } else if (view.getId() == mBT6.getId()) {
                new AlertDialog.Builder(MyDialogDemo.this)
                        .setTitle("列表框")
                        .setItems(new String[] {"item1", "item2", "item3"}, null)
                        .setNegativeButton("確定", null)
                        .show();

            } else if (view.getId() == mBT7.getId()) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(
                        R.layout.dialog_layout,
                        (ViewGroup) findViewById(R.id.dialog));

                new AlertDialog.Builder(MyDialogDemo.this)
                        .setTitle("自定義布局")
                        .setView(layout)
                        .setPositiveButton("確定", null)
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    };
}
