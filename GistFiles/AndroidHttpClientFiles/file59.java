package com.example.earhustlefour;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.example.earhustlefour.R;

import android.speech.tts.TextToSpeech;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
  private static TextToSpeech tts;
	private TextView t;
	private static final String ns = null;
    private ListView lv;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        t=new TextView(this);
        t=(TextView)findViewById(R.id.mainText);
        
        try {
			String response=new Story().execute("http://loganfrederick.com/test/wapo_article_one.txt").get();
	        t.setText(response);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}        
        tts = new TextToSpeech(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	// SPEECH CODE
	
	public static void speak(CharSequence charSequence) {
		String text=(String) charSequence;
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);		
	}

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
            	// TURNED ON onInitSpeech
                speak(t.getText());
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
        
    }

}
