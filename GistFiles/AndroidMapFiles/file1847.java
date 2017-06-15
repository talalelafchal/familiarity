package jp.kaki.sazae_san;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		TextView textView = (TextView) findViewById(R.id.textView);
		Bundle bundle = getIntent().getExtras();
		int num = bundle.getInt("msg");
//		String msg = bundle.getString("msg").toString();
		textView.setText(num);
	}
}