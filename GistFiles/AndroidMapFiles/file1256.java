package in.co.srishti13;

import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class AboutUs extends Activity {

  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		TextView inst = (TextView) findViewById(R.id.aboutus);
		inst.setMovementMethod(new ScrollingMovementMethod());
		inst.setText("Developer:\n\n" +
				     "Rahul Meena \n CSE 2nd year\n\n" +
				     "Surendra Gadwal \n CSE 2nd year\n\n");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_about_us, menu);
		return true;
	}

}
