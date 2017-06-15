package com.example.contactlist;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class deil extends Activity implements OnCheckedChangeListener,
OnClickListener
{
	Bitmap bmp;
	 Uri selectedImageUri;
	  String  selectedPath;
	  ImageView ph;
	  Button camera, gallary, save;
	  
	  ToggleButton tb;
		SharedPreferences prefs;
		Editor edit;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_list);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		edit = prefs.edit();
		
		
		tb = (ToggleButton) findViewById(R.id.toggleButton1);
		tb.setOnCheckedChangeListener(this);
		
		
		 ph=(ImageView)findViewById(R.id.photo);
		camera=(Button)findViewById(R.id.camera);
		gallary=(Button)findViewById(R.id.galary);
		save=(Button)findViewById(R.id.save);
		
		
		
// set contact Photo in Imageview
		byte[] byteArray = getIntent().getByteArrayExtra("image");		
		if(byteArray!=null){
			 bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		}		
		
		if(bmp!=null)
		{
			Bitmap bmpimg = Bitmap.createScaledBitmap( bmp, 130,150, true);
			ph.setImageBitmap(bmpimg);
		}
		else
		{
			ph.setImageResource(R.drawable.upload_photo);
		}
// set Camera Photo in Image view		
		camera.setOnClickListener(new OnClickListener() {
		    
			   @Override
			   public void onClick(View v) {
			    // TODO Auto-generated method stub
			    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			                startActivityForResult(cameraIntent, 100); 
			   }
			  });
		
		
// set Gallery Photo In Image view			   
			   
			  gallary.setOnClickListener(new OnClickListener() {
			   
			   public void onClick(View v) {
			    // TODO Auto-generated method stub
			     openGallery(10);
			   }
			  });
			 
			 
// Save Contact Image			  
			  save.setOnClickListener(new OnClickListener() {
				  ByteArrayOutputStream stream = new ByteArrayOutputStream();
				   public void onClick(View v) {
				    // TODO Auto-generated method stub
					   
					   
					  
					   if (bmp != null)
						{ // If an image is selected successfully
							// Bitmap bmp = BitmapFactory.decodeResource(getResources(),
							// R.drawable.ic_launcher);
							bmp.compress(Bitmap.CompressFormat.PNG, 0, stream);
							//String id = Constants.id;
							//String id =getIntent().getStringExtra("id");
							//String datas=getIntent().getStringExtra("id");
							//long datas=getIntent().getLongExtra("pos", 0);
							//int value = getIntent().getExtras().getInt("pos");
							// ImageAdapter adapter = new ImageAdapter(imgPrevActivity.this, "image prev", null);
							Intent i=getIntent();
							int hitesh=getIntent().getIntExtra("pos", 0);
							setContactPhoto(stream.toByteArray(), hitesh);
							Log.d("bmp","hello"+bmp);
					   
							deil.this.finish();
							Intent in= new Intent(deil.this,MainActivity.class);
							startActivity(in);
						}
					   
				   }
				  });
				
		
}
	


	
	@SuppressWarnings("deprecation")
	void setContactPhoto(byte[] byteArray, long parseLong) {
		// TODO Auto-generated method stub
		Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, parseLong);
		android.provider.Contacts.People.setPhotoData(
				deil.this.getContentResolver(), uri, byteArray);
		Log.d("main", "id: " + parseLong + " Uri: " + uri + " bytearray: "
				+ byteArray.length);

	}
	
	
	void setContactPhoto(ContentResolver c, byte[] bytes, Long datas) {
		ContentValues values = new ContentValues();
		int photoRow = -1;
		// personId = 8863;
		String where = ContactsContract.Data._ID + " = " + datas + " AND "
				+ ContactsContract.Data.MIMETYPE + "=='"
				+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
				+ "'";
		Cursor cursor = c.query(ContactsContract.Data.CONTENT_URI, null, where,
				null, null);
		int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
		if (cursor.moveToFirst()) {
			photoRow = cursor.getInt(idIdx);
		}
		cursor.close();

		values.put(ContactsContract.Data.RAW_CONTACT_ID, datas);
		values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes);
		values.put(ContactsContract.Data.MIMETYPE,
				ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

		if (photoRow >= 0) {
			c.update(ContactsContract.Data.CONTENT_URI, values,
					ContactsContract.Data._ID + " = " + photoRow, null);
			Toast.makeText(deil.this, "updated " + datas, 50000).show();
		} else {
			c.insert(ContactsContract.Data.CONTENT_URI, values);
			Toast.makeText(deil.this, "inserted " + datas, 50000).show();
		}
	}
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1) {
			edit.putBoolean("main_state", arg1);
			// startService(new Intent(MainActivity.this, Unlock_hud.class));

		} else {
			edit.putBoolean("main_state", arg1);
		}
		edit.commit();
	}











// Method for Gallery image	
	 public void openGallery(int req_code){
		 
		 Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		 startActivityForResult(i, 10);
		
	   }
	 
// OnActivity For get the image	 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
	 
	 
	        if (resultCode == RESULT_OK) {
	         if(data.getData() != null){
	           selectedImageUri = data.getData();
	         }else{
	          Log.d("selectedPath1 : ","Came here its null !");
	          Toast.makeText(getApplicationContext(), "failed to get Image!", 500).show();
	         }
	          
	         if (requestCode == 100 && resultCode == RESULT_OK) {  
	        	 super.onActivityResult(requestCode, resultCode, data);
	             Bitmap bp = (Bitmap) data.getExtras().get("data");
	             
	             ph.setImageBitmap(bp);
	 
	            } 
	          
	            if (requestCode == 10)
	 
	            {
	 
	            	 Uri selectedImage = data.getData();
	                 String[] filePathColumn = { MediaStore.Images.Media.DATA };
	                 Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
	                 cursor.moveToFirst();
	                 int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	                 Log.d("picturePath","picturepath");
	                 String picturePath = cursor.getString(columnIndex);
	                 cursor.close();
	                 
	                 ph.setImageBitmap(getScaledBitmap(picturePath, 400, 400));
	 
	            }
	 
	        }
	 
	    }
	 
// Method for Gallery Image Resize in Bitmap	 
	private Bitmap getScaledBitmap(String picturePath, int width, int height) {
	    BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
	    sizeOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(picturePath, sizeOptions);

	    int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

	    sizeOptions.inJustDecodeBounds = false;
	    sizeOptions.inSampleSize = inSampleSize;

	    return BitmapFactory.decodeFile(picturePath, sizeOptions);
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and
	        // width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will
	        // guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}



	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
	 
	        String[] projection = { MediaStore.Images.Media.DATA };
	 
	        Cursor cursor = managedQuery(uri, projection, null, null, null);
	 
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	 
	        cursor.moveToFirst();
	 
	        return cursor.getString(column_index);
	 
	    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed(){
	    super.onBackPressed();
	    Intent in=new Intent(deil.this,MainActivity.class);
	    startActivity(in);
	}
	  
}


