package com.ti.nfcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.ti.nfcdemo.R;


public class MainActivity extends Activity {

	Button button_rw_operations;
    Button button_iso;
    Button button_nfc_peer_to_peer;
    Button button_ti_com;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		button_rw_operations = (Button) findViewById(R.id.button_nfc_read_write);
        button_iso = (Button) findViewById(R.id.button_iso);
        button_nfc_peer_to_peer =  (Button) findViewById(R.id.button_nfc_peer_to_peer);
        button_ti_com = (Button) findViewById(R.id.button_nfc_ti_com);


		final Intent newintent = new Intent(getApplicationContext(),rw_operations_activity.class);

		button_rw_operations.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(newintent);

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.main_layout, menu);
		return true;
	}

}
