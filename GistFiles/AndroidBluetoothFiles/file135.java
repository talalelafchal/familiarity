package linz.jku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import service.ShakeContactService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.QuickContactBadge;

public class Main extends Activity {
	
	/*
	 * FINAL VARIABLES
	 */
	private static final String TAG = "Main";
	private static final int PICK_CONTACT_REQUEST = 0;
	private static final int EDIT_CONTACT_REQUEST = 1;
	
	private String URIEncodedString;
	
	private boolean serviceRunning;
	private CheckBox cbEnableService;
	private SharedPreferences mPrefs;
	private QuickContactBadge badge;
	
	// This is a contact which will be sent to the request
	private static Uri defaultContact;
	private static byte[] byteContact; // store a contact in bytes


	/* Called when the activity is first created. */

	/**
	 * All Activities have a method called onCreate(). This is the first method
	 * that is called always when an activity is started. 
	 * ShakeContact/res/layout/main.xml
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			// Declaration of widgets
			cbEnableService = (CheckBox) findViewById(R.id.cbServiceEnable);
			Button bExit = ((Button) findViewById(R.id.bExit));
			Button bShare = ((Button) findViewById(R.id.bShare));
			Button bConfig = ((Button) findViewById(R.id.bConfig));
			
			this.badge = (QuickContactBadge) findViewById(R.id.quickContactBadge1);

			bShare.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(Main.this, Shaking.class);
					startActivity(intent);
				}
			});

			serviceRunning = serviceRunning();

			// Checks enableService checkbox an running state in order to start
			// or not the service.
			if (cbEnableService.isChecked() && !serviceRunning) {
				startService(new Intent(this, ShakeContactService.class));
			}
			cbEnableService.setChecked(serviceRunning);

			// Setting up listeners
			cbEnableService.setOnClickListener(cbEnableServiceListener);
			bExit.setOnClickListener(bExitListener);			
			
			// Get the shared preferences and the Uri encoded String from memory
			mPrefs = getSharedPreferences("DefaultContactFile", MODE_WORLD_WRITEABLE);
			URIEncodedString = mPrefs.getString("URI", null);
			
			// Parse the encoded String to URI and get the default Contact
			if(URIEncodedString != null) {	
				defaultContact = Uri.parse(URIEncodedString);
				contactToBytes();
			}			
			bConfig.setOnClickListener(bConfiguration);
			badge.setImageResource(R.drawable.droid);  
			badge.setOnClickListener(cbBadgeListener);

		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	
	/**
	 * Method executed when the application is resumed.
	 */
	@Override
	public void onResume(){
		if(!serviceRunning()){
			cbEnableService.setChecked(false);
		}
		super.onResume();
	}
	
	/**
	 * Get a File which contains the Contact object, get the Stream associated to the File
	 * and transform the Stream to a byte array
	 * 
	 */
	private void contactToBytes () {	

		String vfile = "POContactsRestore.vcf";
		byteContact = null;

		// Get the default contact from the content resolver
		Cursor phones = getContentResolver().query(defaultContact, null, null,
				null, null);
		phones.moveToFirst();

		// Get the lookupKey from the contact
		String lookupKey = phones.getString(phones
				.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		// Get the VCard_Uri using the lookupKey of the contact
		Uri uri = Uri.withAppendedPath(
				ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
		AssetFileDescriptor fd;
		
		try {
			// Open a file with the Contact info
			fd = this.getContentResolver().openAssetFileDescriptor(uri, "r");
			FileInputStream fis = fd.createInputStream();
			// Get the contact in bytes
			byteContact = new byte[(int) fd.getDeclaredLength()];
			fis.read(byteContact);
			
			String VCard = new String(byteContact);
			String path = Environment.getExternalStorageDirectory().toString()
					+ File.separator + vfile;
			FileOutputStream mFileOutputStream = new FileOutputStream(path,false);
			mFileOutputStream.write(VCard.toString().getBytes());
			
			Log.d("Vcard", VCard.toString());			
		} catch (Exception e1) {
			Log.d("vCard", e1.toString());
		}
        
	}
	
	/**
	 * Return the default contact established.
	 * @return
	 */
	public static byte[] getDefaultContact() {
		return byteContact;
	}

	/**
	 * Check if the service is running.
	 * @return
	 */
	private boolean serviceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (service.service.getClassName().contains("ShakeContactService")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Set the action when the checkbox is enable to activate the service.
	 */
	private OnClickListener cbEnableServiceListener = new OnClickListener() {
		public void onClick(View v) {
			if (((CheckBox) v.findViewById(R.id.cbServiceEnable)).isChecked()) {
				v.getContext().startService(
						new Intent(v.getContext(), ShakeContactService.class));
			} else {
				v.getContext().stopService(
						new Intent(v.getContext(), ShakeContactService.class));
			}
		}
	};

	/**
	 * Set the action when the exit button is pressed.
	 */
	private OnClickListener bExitListener = new OnClickListener() {
		public void onClick(View v) {
			((Activity) v.getContext()).finish();
		}
	};
		
	/**
	 * Set the action when the config button is pressed.
	 */
	private OnClickListener bConfiguration = new OnClickListener() {
		public void onClick(View v) {	
			Log.d(TAG, "CONFIGURATION PRESSED");
			// If there's not defaultContact, launch an intent and the user have
			// to create a new one
			// If the defaultContact is already created, it's showed to the user
			// and can be edited
			if (defaultContact == null) {
				Intent createContact = new Intent(
						ContactsContract.Intents.Insert.ACTION,
						Contacts.CONTENT_URI);				
				startActivityForResult(createContact, PICK_CONTACT_REQUEST);
				
			} else {
				startActivityForResult(new Intent(Intent.ACTION_VIEW, defaultContact), EDIT_CONTACT_REQUEST);

			}
		}
	};
	
	/**
	 * Set the action when the picture in main activity is pressed	 
	 */
	private OnClickListener cbBadgeListener = new OnClickListener() {
		public void onClick(View v) {
			if (defaultContact != null) {
				startActivityForResult(new Intent(Intent.ACTION_VIEW, defaultContact), EDIT_CONTACT_REQUEST);
			}
		}
		
	};
	
	/**
	 * Method used when we define the contact.
	 */
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		if ((reqCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK)
				|| reqCode == EDIT_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
			Log.d(TAG, "ON_ACTIVITY_RESULT");

			// A contact was picked. Here we will just display it
			// to the user.
			defaultContact = data.getData();
			String dataURI = data.getDataString();
			
			// Persist the URI information as encoded String
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString("URI", dataURI);
			editor.commit();
			
			// transform the default contact to bytes
			contactToBytes();
			
			badge.assignContactUri(defaultContact);

		}
	}
}

