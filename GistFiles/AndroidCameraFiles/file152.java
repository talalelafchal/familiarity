package net.vvakame.polkodotter;

import roboguice.activity.GuiceActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends GuiceActivity {

	@SuppressWarnings("unused")
	private final Activity self = this;
	private final int REQUEST_PICK_CONTACT = 0;

	@InjectView(R.id.gallery)
	ImageButton mButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main); // Injection now!!

		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_PICK_CONTACT);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_PICK_CONTACT) {
			data.setClass(this, DotActivity.class);
			Uri pictUri = data.getData();
			if (pictUri != null) {
				data.putExtra("mediaImageUri", pictUri);
				startActivity(data);
			}
		}
	}
}