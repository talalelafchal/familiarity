package yusheng.seekbartest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;


public class MyActivity extends Activity {

    private SeekBar seekBar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        seekBar = (SeekBar)findViewById(R.id.firstSeekBar);
        seekBar.setOnSeekBarChangeListener(new seekBarListener());
    }

    class seekBarListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            System.out.println("progress:"+ progress + "; fromUser"+ fromUser);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            System.out.println("onstart");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            System.out.println("stopstart");
        }
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
