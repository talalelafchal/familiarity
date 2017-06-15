package th.co.fingertip.eventproFP.ui;

import java.util.ArrayList;
import java.util.HashMap;

import th.co.fingertip.eventproFP.EventproFPEnum;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

public class EventMapActivity extends MapActivity implements View.OnClickListener {

  /*
	 * BraodcastReceiver instance lets you catch the message that is broadcasted somewhere.
	 * When the message is received, the onReceive() method of BoardcastReceiver instance
	 * will be trigger if it's registered to the activity. On activity may have more than
	 * one receiver. The example of boardcast receiver is shown below.
	 *
	 * Steps to add BoardcardReceiver:
	 * 1. declare a variable as BoardcastReceiver type using anonymous class declarion pattern.
	 * 2. within the anonymous class scope, override onReceive()
	 * 3. implement what you want to do (after the message is received) in onReceive() method.
	 * 4. register the receiver instance to the activity in onResume() method.
	 * 5. unregister the receiver instance from the activity in onPause() method.
	 *
	 */
	public BroadcastReceiver event_detail_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				// do something after receiving the message
			}
			catch(Exception e){

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.event_map_layout);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(event_detail_receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/* register event_detail_receiver to the activity.
		 * the second parameter is the message (intent) that we want to catch
		 * with this receiver instance.
         */
		registerReceiver(event_detail_receiver, new IntentFilter(EventproFPEnum.Action.VIEW_EVENT));
	}

}