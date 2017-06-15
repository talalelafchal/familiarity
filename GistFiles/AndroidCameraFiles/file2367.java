package com.example.photodiary;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.*;
import android.support.v4.app.NavUtils;
import android.util.*;

public class NewEntryActivity extends Activity {
	private static final int CAMERA_REQUEST = 1888; 
	private static final String EXTRA_TITLE = "TITLE";
	private static final String EXTRA_BODY = "BODY";
	
	private DiaryDataSource mDataSource;
	
	private Button mSaveButton;
	private EditText mTitleEdit;
	private EditText mBodyEdit;
	private ImageView mPhotoImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_entry);
		// Show the Up button in the action bar.
		setupActionBar();
		
		mDataSource = new DiaryDataSource(this);
		
		mTitleEdit = (EditText)findViewById(R.id.editTextNewTitle);
		mBodyEdit = (EditText)findViewById(R.id.editTextNewBody);
		
		mSaveButton = (Button)findViewById(R.id.buttonNewSave);
		mSaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				saveEntry();
			}
			
		});
	}
	
	private void saveEntry() {		
		Entry entry = new Entry();
		entry.setTitle(mTitleEdit.getText().toString());
		entry.setBody(mBodyEdit.getText().toString());		
		mDataSource.add(entry);
		
		finish();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_entry, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.snap_photo_action:
			snapPhoto();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void snapPhoto() {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(intent, CAMERA_REQUEST);	    
	}
	
	private void renderPhoto(Intent intent) {
	    Bundle extras = intent.getExtras();
	    if (extras.get("data") != null) {
	    	Bitmap photo = (Bitmap) extras.get("data");
	    	if (photo != null)
	    		mPhotoImage.setImageBitmap(photo);
	    }	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
				if (data != null)
					renderPhoto(data);
				Log.d("DIARY", "Render photo?");
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_TITLE, mTitleEdit.getText().toString());
		outState.putString(EXTRA_BODY, mBodyEdit.getText().toString());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mTitleEdit.setText(savedInstanceState.getString(EXTRA_TITLE));
		mBodyEdit.setText(savedInstanceState.getString(EXTRA_BODY));
	}

}
