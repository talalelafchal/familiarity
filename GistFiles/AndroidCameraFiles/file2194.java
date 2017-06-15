package com.example.contactlist;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends Activity {
	ListView userList;
	UserCustomAdapter userAdapter;
	ArrayList<User> userArray = new ArrayList<User>();
	 Bitmap bit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		initList();

	}

	@Override
	protected void onResume() {
		super.onResume();
		userArray.clear();
		initList();
	}

	void initList() {
		
		
		 ContentResolver cr = getContentResolver();
		 
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
	        
	        if (cur.getCount() > 0)
	        {
	        	while (cur.moveToNext()) {
		    	// get Contact Id
		        String id = cur.getString(
	                        cur.getColumnIndex(ContactsContract.Contacts._ID));
		        
		        // get Contact Name
		        String name = cur.getString(
	                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		       
			
			   // Get Contact Number
		        String number = null;
		        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
	 
	                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
	                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
	                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
	 
	                new String[]{id},
	                null);
	 
		        if (cursorPhone.moveToFirst()) {
	            number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	        }
	 
	        cursorPhone.close();
	 
			
			// Get Contact Photo 
			String uri=cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
			InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
			        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
 
			if (inputStream != null) {
			    bit = BitmapFactory.decodeStream(inputStream);
       
//	                ImageView imageView = (ImageView) findViewById(R.id.img_contact);
//	                imageView.setImageBitmap(photo);
			}
			else {
				  bit=null;
			}
			userArray.add((new User(id,name,number,uri,bit)));
//	 		if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//	 		    //Query phone here.  Covered next
//	 	        }
	            }
	 	}

		
							
						


			userAdapter = new UserCustomAdapter(MainActivity.this, R.layout.row,
					userArray);
			userList = (ListView) findViewById(R.id.lst_contacts);
			userList.setItemsCanFocus(false);
			userList.setTextFilterEnabled(true);
			userList.setAdapter(userAdapter);
			userList.setDividerHeight(0);
			userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		        @Override
		        public void onItemClick(AdapterView<?> parent, View view,
		                final int arg2, long arg3) {
		        	User u=userArray.get((int)arg3);
				    Log.d("main","ok"+(int)arg3);
				    
				    
                     
					//Constants.id = Constants.userArray.get(arg2);
					
		        	Intent backIntent = new Intent(MainActivity.this,deil.class);
		        	if(u.getbi()!= null)
		        	{
		        		
		        		//Constants.position = arg2;
						//Constants.id = Constants.userArray.get(arg2);
		        		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		        		u.getbi().compress(Bitmap.CompressFormat.PNG, 100, stream);
		        		byte[] byteArray = stream.toByteArray();
		        		backIntent.putExtra("image",byteArray);
		        	
		        	}		        	
		        	
		        	backIntent.putExtra("pos",arg2);
		        	
		    		startActivity(backIntent);
		    		MainActivity.this.finish();
		        }

		    });

		
//		 EditText myFilter = (EditText) findViewById(R.id.editSearch);
//		  myFilter.addTextChangedListener(new TextWatcher() {
//
//		  public void afterTextChanged(Editable s) {
//		  }
//
//		  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//		  }
//
//		  public void onTextChanged(CharSequence s, int start, int before, int count) {
//			  userAdapter.getFilter().filter(s.toString());
//		  }
//		  });
//		userAdapter.notifyDataSetChanged();
	}




	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
}