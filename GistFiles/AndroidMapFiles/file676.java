/**
 * Author : James Elsey Date : 26/Feb/2011 Title : TextToSpeechDemo URL :
 * Http://www.JamesElsey.co.uk
 * 
 * Adapted by Richard Le Mesurier from original code by James Elsey at
 * https://github.com/jameselsey/TextToSpeechDemo.
 * 
 * As referenced on Stack Overflow answer by Richard Le Mesurier at
 * http://stackoverflow.com/a/23792562/383414
 * 
 * Requires the `lib.ui.widget.Boast` class by Richard Le Mesurier at
 * https://gist.github.com/mobiRic/9786993
 */
package mobiric.tts.subtitles;

import java.util.HashMap;

import com.jameselsey.demo.texttospeechdemo.R;

import lib.ui.widget.Boast;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.Toast;

/**
 * This class demonstrates checking for a TTS engine, and if one is available it will spit out some
 * speak.
 * 
 * Additionally it will display subtitles as the text is spoken.
 */
public class SubtitleTextToSpeechDemo extends Activity implements TextToSpeech.OnInitListener
{
	private TextToSpeech tts;
	// This code can be any value you want, its just a checksum.
	private static final int TTS_REQUEST_CODE = 1234;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// check to TTS
		Intent ttsInstallCheck = new Intent();
		ttsInstallCheck.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(ttsInstallCheck, TTS_REQUEST_CODE);
	}

	/**
	 * TextToSpeech.OnInitListener callback is called when the TTS engine has initialised.
	 */
	public void onInit(int i)
	{
		doSpeak("Hello Milton");
		doSpeak("Here is the answer");
		doSpeak("each utterance triggers the listener");
		doSpeak("so you can Boast about it");
	}

	private void doSpeak(String text)
	{
		HashMap<String, String> params = new HashMap<String, String>();

		// use the actual text as the key to ID the utterance
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
		tts.speak(text, TextToSpeech.QUEUE_ADD, params);
	}


	/**
	 * This is the callback from the TTS engine check, if a TTS is installed we create a new TTS
	 * instance (which in turn calls onInit), if not then we will create an intent to go off and
	 * install a TTS engine
	 * 
	 * @param requestCode
	 *            int Request code returned from the check for TTS engine.
	 * @param resultCode
	 *            int Result code returned from the check for TTS engine.
	 * @param data
	 *            Intent Intent returned from the TTS check.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (TTS_REQUEST_CODE == requestCode)
		{
			if (TextToSpeech.Engine.CHECK_VOICE_DATA_PASS == resultCode)
			{
				tts = new TextToSpeech(this, this);
				tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
				{

					@Override
					public void onStart(final String utteranceId)
					{
						SubtitleTextToSpeechDemo.this.runOnUiThread(new Runnable()
						{
							public void run()
							{
								Boast.showText(SubtitleTextToSpeechDemo.this, utteranceId, Toast.LENGTH_LONG);
							}
						});
						Log.d("mobiRic", "start: " + utteranceId);
					}

					@Override
					public void onError(String utteranceId)
					{
					}

					@Override
					public void onDone(String utteranceId)
					{
					}
				});
			}
			else
			{
				// not installed
			}
		}
	}

	/**
	 * Be kind, once you've finished with the TTS engine, shut it down so other applications can use
	 * it without us interfering with it :)
	 */
	@Override
	public void onDestroy()
	{
		// Don't forget to shutdown!
		if (tts != null)
		{
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
}
