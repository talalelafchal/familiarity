package com.lonict.android.tonoradio;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.lonict.android.tonoradio.RadioStation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private MediaPlayer mediaplayer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS));
        //Settings.NameValueTable.getUriFor()
        //Settings.System.getString()
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }*/
    public void startStreaming()
    {
        try
        {
            initMediaPlayer();
            String url = "http://kcrw.ic.llnwd.net/stream/kcrw_music"; // your URL here

            RadioStation rs = new RadioStation(this.getResources().openRawResource(R.raw.radiostations)) ;
            for (RadioStationPOJO radiostation : rs.getRadioStations() )
            {
                System.out.println(radiostation.getImageUri()) ;
                System.out.println(radiostation.getRadioURL()) ;
                System.out.println(radiostation.getRadioName()) ;
                System.out.println(radiostation.getRadiodesc()) ;

                Log.e("ERROR", radiostation.getImageUri());
                Log.e("ERROR", radiostation.getRadioName());
                Log.e("ERROR", radiostation.getRadioURL());
                Log.e("ERROR", radiostation.getRadiodesc());
                Log.e("ERROR", "-----------");
            }

            mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaplayer.setDataSource(url);
            mediaplayer.prepare(); // might take long! (for buffering, etc)
            mediaplayer.start();
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString()) ;
        }
    }
    public void playRadio (View view)
    {
        startStreaming();
    }
    public void stopRadio(View view)
    {
        if (mediaplayer!=null)
        {
            mediaplayer.stop();
            mediaplayer.release();
        }
    }
    private void initMediaPlayer()
    {
        mediaplayer = new MediaPlayer();
    }
}
