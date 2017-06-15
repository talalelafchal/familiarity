package in.co.srishti13;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class Instructions extends Activity {

  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
		
		TextView inst = (TextView) findViewById(R.id.inst);
		inst.setMovementMethod(new ScrollingMovementMethod());
		inst.setText("Help Me!\n\n"+
					 "Help Me App lets user send message(with their current location coordinates) instanlty to the specified Phone numbers in case of any emergency.\n\n" +
					 "The user needs to add the numbers he/she wants to send the message to.\n\n" +
					 "The user can also select the option \"Send GPS location\" to send his/her current location.\n" +
					 "\n" +
					 "In case of emergency the user needs to press the \"Send\" button and should not close the app (by pressing \"Save and Exit\" button) otherwise it would prevent the app from sending his/her location coordinates at a change of 50 metres from current position. ");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_instructions, menu);
		return true;
	}

}
