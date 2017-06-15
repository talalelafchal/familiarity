package com.team.customui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 *自定义时间组件
 */
public class MyTimeView extends LinearLayout {


        static SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:ss");
        static SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy年MM月dd日");
        static Calendar cal = Calendar.getInstance();
        private TextView textViewTime, textViewDate;


        public MyTimeView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // 使用layoutinflater把布局加载到本ViewGroup
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.sample_my_time_view, this);

            textViewTime = (TextView) findViewById(R.id.textViewTime);
            textViewDate = (TextView) findViewById(R.id.textViewDate);


            startThread();

        }

        public static String getCurrentTime(Date date) {

            sdf_time.format(date);
            return sdf_time.format(date);
        }

        public static String getCurrentDate(Date date) {

            sdf_date.format(date);
            return sdf_date.format(date);
        }

        public static String getCurrentWeekDay(Date dt) {
            String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
            cal.setTime(dt);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;

            return weekDays[w];
        }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Toast.makeText(getContext(), "我是时间控件", 1000).show();

                break;
            default:
                break;
        }
        return true;
    }

        private void startThread() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        handler.sendEmptyMessage(12);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }

                }
            }).start();
        }

        Handler handler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 12) {
                    Date date = new Date();
                    textViewTime.setText(getCurrentTime(date));
                    textViewDate.setText(getCurrentDate(date)+getCurrentWeekDay(date));

                }

            }
        };

    }
