package com.example.fm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by MKSoft01 on 10/17/13.
 */
public class Tracking extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        TextView currentDate=(TextView)findViewById(R.id.tracTvCurDate);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

// textView is the TextView view that should display it
      currentDate.setText(currentDateTimeString);
    }
}