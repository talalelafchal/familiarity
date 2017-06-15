package andrej.jelic.attendance;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Calendar;


public class Start_at_time extends Activity implements View.OnClickListener{

    private static final int REQUEST_CODE_START = 1;
    public static final String PREFS_NAME = "PrefsFile";
    private static final String TAG = "Start at time; ";
    private boolean connected = false;

    private static long ALARM_TIME;
    private TimePicker startPicker;
    private TimePicker endPicker;
    private int hour;
    private int min;
    private Button start_at_time;
    private AlarmManager mAlarmManager;
    private Intent mStartAtTimeIntent;
    private PendingIntent mStartAtTimePendingIntent;
    Resources system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_at_time);
        setLayout();

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mStartAtTimeIntent = new Intent(this, AlarmReceiver.class);
        mStartAtTimeIntent.putExtra("id", REQUEST_CODE_START);
        mStartAtTimePendingIntent = PendingIntent.getBroadcast(this, 0, mStartAtTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void setLayout() {
        startPicker = (TimePicker) findViewById(R.id.start_time_Picker);
        endPicker = (TimePicker) findViewById(R.id.end_time_Picker);
        start_at_time = (Button) findViewById(R.id.button_start);
        start_at_time.setOnClickListener(this);

        set_time_picker_number_colour(startPicker);
        set_time_picker_number_colour(endPicker);

        startPicker.setIs24HourView(true);
        endPicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        startPicker.setCurrentHour(hour);
        startPicker.setCurrentMinute(min);

        endPicker.setCurrentHour(hour + 1);
        endPicker.setCurrentMinute(min);
    }

    @Override
    public void onClick(View v) {
        writeToPrefs();

        ALARM_TIME = time_to_millis(hour, min);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, ALARM_TIME, mStartAtTimePendingIntent);

        Log.e(TAG, "Alarm starting at: " + hour + " : " + min);

        Toast.makeText(getApplicationContext(), "Starting at: " + pad(hour) + " : " +  pad(min),
                Toast.LENGTH_LONG).show();

        finish();
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else return "0" + String.valueOf(c);
    }

    private void writeToPrefs() {
        int end_hour;
        int end_min;
        hour = startPicker.getCurrentHour();
        min = startPicker.getCurrentMinute();
        end_hour = endPicker.getCurrentHour();
        end_min = endPicker.getCurrentMinute();

        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("Hour", hour);
        editor.putInt("Minutes", min);
        editor.putInt("End hour", end_hour);
        editor.putInt("End minutes", end_min);
        editor.apply();
    }

    //Pretvaranje postavljenog vremena u milisekunde zbog alarma
    public long time_to_millis(int hour, int min)
    {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);

        c.set(year, month, day, hour, min);
        return c.getTimeInMillis();
    }


    // Postavlja se boja brojki na Time pickeru
    private void set_time_picker_number_colour(TimePicker timePicker) {
        system = Resources.getSystem();
        final int hour_number_id = system.getIdentifier("hour", "id", "android");
        int minute_number_id = system.getIdentifier("minute", "id", "android");

        NumberPicker hour_numberpicker = (NumberPicker) timePicker.findViewById(hour_number_id);
        NumberPicker minute_numberpicker = (NumberPicker) timePicker.findViewById(minute_number_id);

        set_numberpicker_text_colour(hour_numberpicker);
        set_numberpicker_text_colour(minute_numberpicker);

    }

    public void set_numberpicker_text_colour(NumberPicker number_picker) {
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.numbers_color);

        int i;
        for (i = 0; i < count; i++) {
            View child = number_picker.getChildAt(i);

            try {
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);
                ((Paint) wheelpaint_field.get(number_picker)).setColor(color);
                number_picker.invalidate();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_start_at_time, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
