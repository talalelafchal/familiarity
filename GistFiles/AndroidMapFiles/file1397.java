package in.co.srishti13;

import java.util.Map;
import java.util.Random;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Settings;
import android.provider.ContactsContract.Contacts;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

  protected EditText textMessage;
	protected String PREFS_NAME = "contactPref";
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	protected Button send,exit;
	protected ImageView add,inst,aboutus;
	protected LinearLayout ll,det,cd;
	protected int contactCount ;
	protected Dialog methodDia,addNewDia;
	protected int newContactID = 1000;
	String showContact = null;
	private static final int CONTACT_PICKER_RESULT = 1001;
	LocationManager locationManager ;
	protected CheckBox cbGPS;
	protected boolean sendGPS = false;
	double longitude;
	double latitude;
	String provider;
	ListView lv;
	ArrayAdapter<String> ad;
	TextView clear;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activityy);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		  settings = getSharedPreferences(PREFS_NAME, 0);
		  editor = settings.edit();
		  editor.commit();
		  
		  lv = new ListView(getApplicationContext());
		  


   //       View cd = new View(getApplicationContext());
     //     LayoutInflater inflater=getLayoutInflater();
       //   cd=inflater.inflate(R.layout.contact_detail, ll, false);

		 contactCount = settings.getInt("contactCount",0);
		 editor.putInt("contactCount", contactCount);
		 sendGPS = settings.getBoolean("sendGPS", false);
		 editor.putBoolean("senGPS", sendGPS);
		 editor.commit();
	//	 newContactID = settings.getInt("newContactID",0);
	//	 editor.putInt("newContactID", newContactID);
	//	 editor.commit();
		 
	//	ll = (LinearLayout) findViewById(R.id.ll);
		 textMessage = (EditText) findViewById(R.id.textMessage);
		det =(LinearLayout) findViewById(R.id.det);
		clear = (TextView) findViewById(R.id.clear);
		lv = (ListView) findViewById(R.id.listView1);
		add = (ImageView) findViewById(R.id.addPhoneNumber);
		send = (Button) findViewById(R.id.buttonSend);
		exit = (Button) findViewById(R.id.buttonExit);
		cbGPS = (CheckBox) findViewById(R.id.checkBoxGPS);
		inst =(ImageView) findViewById(R.id.imageInstructions);
		aboutus =(ImageView) findViewById(R.id.imageAboutUs);
		
		if(settings.contains("message")){
			textMessage.setText(settings.getString("message", ""));
		}
		

	     ad = new ArrayAdapter<String>(getApplicationContext(), R.layout.contact);
	     
		if(contactCount != 0){
			listv();
			lv.setAdapter(ad);
		}
		
        // Getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);        
        
        // Creating an empty criteria object
        Criteria criteria = new Criteria();
        
        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);
        
                
        if(provider!=null && !provider.equals("")){
        	
        	// Get the location from the given provider 
            Location location = locationManager.getLastKnownLocation(provider);
                        
            locationManager.requestLocationUpdates(provider, 1000*60*15, 50, this);
            
            
            if(location!=null)
            	onLocationChanged(location);
            else{
            	Toast.makeText(getBaseContext(), "Location can't be retrieved,Please switch on GPS", Toast.LENGTH_LONG).show();

            }
        }else{
        	Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
		

		cbGPS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked){
					Toast.makeText(getApplicationContext(), "Please enable the GPS from GPS sttings if disabled", Toast.LENGTH_LONG).show();
					reqlup();
					sendGPS = true;
					}
					else{

					Toast.makeText(getApplicationContext(), "No GPS", Toast.LENGTH_SHORT).show();
					locationManager.removeUpdates(MainActivity.this);
					sendGPS = false;
					}
					
					 editor.putBoolean("senGPS", cbGPS.isChecked());
					 editor.commit();
				
				
			}
		});
				
		
// gps location
		
		OnClickListener ocl = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(v == add){

					getContact();
				}
				
				else if(v == send){
					
					sendSMS();
				}
				
				else if(v == exit){
					det.removeAllViews();
					finish();
				}
				
				else if( v == clear){
					editor.clear();
					editor.commit();
					ad.clear();
					lv.setAdapter(ad);
				}
				
				else if(v == inst){
			        Intent intent = new Intent(MainActivity.this, Instructions.class);
			        startActivity(intent);
				}
				else if(v == aboutus){
			        Intent intent = new Intent(MainActivity.this, AboutUs.class);
			        startActivity(intent);
					
				}
				
			}
		};

		add.setOnClickListener(ocl);
		send.setOnClickListener(ocl);
		exit.setOnClickListener(ocl);
		clear.setOnClickListener(ocl);
		inst.setOnClickListener(ocl);
		aboutus.setOnClickListener(ocl);
	}
	
	public void listv(){

		  final Map <String,?> contacts = settings.getAll();
		for(int i =0; i<contactCount;i++){

			if(contacts.get("contactName"+i) != null)
			{
				ad.add(("Name:" + settings.getString("contactName"+i, "no name")+"\n"+
		           "Phone Number:"+ settings.getString("contactPN"+i, "no number")));
			}
		}
		
		ad.notifyDataSetChanged();
		lv.invalidateViews();
	}
	
	public static int getContactIDFromNumber(String contactNumber,Context context)
	{
	    contactNumber = Uri.encode(contactNumber);
	    int phoneContactID = new Random().nextInt();
	    Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,Uri.encode(contactNumber)),new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup._ID}, null, null, null);
	        while(contactLookupCursor.moveToNext()){
	            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(PhoneLookup._ID));
	            }
	        contactLookupCursor.close();

	    return phoneContactID;
	}
	
	public void reqlup(){
		locationManager.requestLocationUpdates(provider, 000*60*15, 50, this);
	}
	
	public void selectContactAddMethod(){
		
		//AlertDialog.Builder adbuilder = new AlertDialog.Builder(getApplicationContext());
		methodDia = new Dialog(MainActivity.this);
		methodDia.setContentView(R.layout.select_method_dialog);
		methodDia.setTitle("From");
		methodDia.setCancelable(true);
		final TextView newContact = (TextView)methodDia.findViewById(R.id.textView1);
		newContact.setText("New Contact");
		final TextView contactList = (TextView)methodDia.findViewById(R.id.textView2);
		contactList.setText("Contact List");
		OnClickListener ocl = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(v == newContact){
					Toast.makeText(getApplicationContext(), "new conatact please", Toast.LENGTH_SHORT).show();
					addNewContact();
					methodDia.dismiss();
				}
				
				else if(v == contactList){

					getContact();
					methodDia.dismiss();
				}

			}
			
			
		};
		
		newContact.setOnClickListener(ocl);
		contactList.setOnClickListener(ocl);
		methodDia.show();
		}
	
	public void addNewContact(){
		
		addNewDia = new Dialog(MainActivity.this);
		addNewDia.setContentView(R.layout.add_new_contact);
		addNewDia.setTitle("New Contact Details");
		addNewDia.setCancelable(true);
		
		final EditText newContactName = (EditText)addNewDia.findViewById(R.id.textContactName);
		final EditText newContactNumber = (EditText)addNewDia.findViewById(R.id.textContactNumber);
		final Button add = (Button)addNewDia.findViewById(R.id.buttonAdd);
		
		OnClickListener ocl = new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String tempName = newContactName.getText().toString();
				String tempNumber = newContactNumber.getText().toString();
				

				  if(settings.contains(tempName)||settings.contains(tempNumber)){

						Toast.makeText(MainActivity.this, "Number is already present in the list", Toast.LENGTH_SHORT)
								.show();
				  }
				  
				  else{
					  
					  
					  if(newContactName.getText().toString() == null && tempNumber != null){

						  editor.putString(tempNumber, tempNumber);
						  editor.putString("contactPN"+contactCount, tempNumber);
						  editor.commit();
						  showContact = "Phone Number:"+ settings.getString("contactPN"+contactCount, "no number");
					  }
					  
					  else if(newContactName.getText().toString() == null && newContactNumber.getText().toString() == null ){
						  Toast.makeText(getApplicationContext(), "Please atleast enter the Contact Number", Toast.LENGTH_SHORT).show();
					  }
					  
					  else{
					  editor.putString(tempName,tempName);
					  editor.putString(tempNumber, tempNumber);
					  editor.putString("contactName"+contactCount, tempName);
					  editor.putString("contactPN"+contactCount, tempNumber);
					  editor.commit();
					  
					  showContact = "Name:" + settings.getString("contactName"+contactCount, "no name")+"\n"+
					           "Phone Number:"+ settings.getString("contactPN"+contactCount, "no number");
					  }

					  if(showContact != null){
						  
					  
			          View cd = new View(getApplicationContext());
			          LayoutInflater inflater=getLayoutInflater();
			          cd=inflater.inflate(R.layout.contact_detail, ll, false);
					  TextView tv = (TextView)cd.findViewById(R.id.textView1);
					  tv.setText(showContact);
					  ImageView iv = (ImageView)cd.findViewById(R.id.imageView1);
					  final int count = contactCount;
					  final String id = ""+newContactID;
					  iv.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							det.removeView(findViewById(count));
							editor.remove(id);
							editor.remove("contactName"+count);
							editor.remove("contactPN"+count);
							editor.commit();
							
						}
					});
					
					  cd.setId(count);
					  det.addView(cd);
					  contactCount++;
					  newContactID++;
					  editor.putInt("contactCount", contactCount);
					  editor.putInt("newContactID", newContactID);
					  editor.commit();
					  }
				  addNewDia.dismiss();
				  }
			}
		};
		
		add.setOnClickListener(ocl);
		addNewDia.show();
	}

	public boolean getContact() {

		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
		return true;
	}
	
	public void sendSMS() {
		
		String tempTextMessage = textMessage.getText().toString();
		
		if(tempTextMessage.length() != 0){
			editor.putString("message", tempTextMessage);
		}
		if(settings.getInt("contactCount", 0) != 0){
		  final Map <String,?> contacts = settings.getAll();
		
		for(int i =0; i<settings.getInt("contactCount", 0);i++){
			
			if(contacts.get("contactName"+i) != null)
			{
				final int tempi = i;
			Log.d("contact size", ""+contacts.size());	
			Log.d("contact details", ""+contacts.get("contactName"+i)+": "+contacts.get("contactPN"+i));
			
// START message status
			
			 String SENT = "SMS_SENT";
		        String DELIVERED = "SMS_DELIVERED";
		 
		        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(SENT), 0);
		 
		        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
		            new Intent(DELIVERED), 0);
		 
		        //---when the SMS has been sent---
		        registerReceiver(new BroadcastReceiver(){
		            @Override
		            public void onReceive(Context arg0, Intent arg1) {
		                switch (getResultCode())
		                {
		                    case Activity.RESULT_OK:
		                        Toast.makeText(getBaseContext(), "SMS sent to "+contacts.get("contactName"+tempi), 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
		                        Toast.makeText(getBaseContext(), "Generic failure", 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                    case SmsManager.RESULT_ERROR_NO_SERVICE:
		                        Toast.makeText(getBaseContext(), "No service", 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                    case SmsManager.RESULT_ERROR_NULL_PDU:
		                        Toast.makeText(getBaseContext(), "Null PDU", 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                    case SmsManager.RESULT_ERROR_RADIO_OFF:
		                        Toast.makeText(getBaseContext(), "Radio off", 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                }
		                
		                MainActivity.this.unregisterReceiver(this); 
		            }
		        }, new IntentFilter(SENT));
		 
		        //---when the SMS has been delivered---
		        registerReceiver(new BroadcastReceiver(){
		            @Override
		            public void onReceive(Context arg0, Intent arg1) {
		                switch (getResultCode())
		                {
		                    case Activity.RESULT_OK:
		                        Toast.makeText(getBaseContext(), "SMS delivered to "+contacts.get("contactName"+tempi), 
		                                Toast.LENGTH_SHORT).show();
		                        break;
		                    case Activity.RESULT_CANCELED:
		                        Toast.makeText(getBaseContext(), "SMS not delivered to "+contacts.get("contactName"+tempi), 
		                                Toast.LENGTH_SHORT).show();
		                        break;                        
		                }

		                MainActivity.this.unregisterReceiver(this);
		            }
		        }, new IntentFilter(DELIVERED));        
			
// END message status	    
			String phoneNumber = ""+contacts.get("contactPN"+i);

	   //   ArrayList<String> parts = smsManager.divideMessage(message); 
			
			String message = "Help me! "+contacts.get("contactName"+i);

			if(tempTextMessage.length() != 0){
				if(sendGPS){
					message = tempTextMessage.toString()+". I am here right now: \n"+"Longitude: "+longitude+"\n"+"Latitude: "+latitude;
				}
				else
					message = tempTextMessage.toString()+"";
			}
			else{
				if(sendGPS){
					message = message+". I am here right now: \n"+"Longitude: "+longitude+"\n"+"Latitude: "+latitude;
					}
			}
			SmsManager smsManager = SmsManager.getDefault();
		    smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
			
			 } // if condition
			
		} // for loop
	
	  } // contactCount != 0
		
		else{
			Toast.makeText(getApplicationContext(), "No contacts in the list", Toast.LENGTH_SHORT).show();
		}
	} // sendSMS()
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String contactName = "unknown";
		String contactID = "unknown";
		String contactPN   = "123456789";
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CONTACT_PICKER_RESULT:

                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData,null,
                    null, null, null);
                if (c.moveToFirst()) {  
                    contactName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));  
                    
                    contactID = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) 
                    {
                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactID,null, null);
                        phones.moveToFirst();
                        contactPN = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                
                }
				break;
			}
		} 
		
		else {
			Toast.makeText(this, "No phone number found", Toast.LENGTH_SHORT)
					.show();
		}
		  if(settings.contains(contactID)){

				Toast.makeText(this, "Number is already present in the list", Toast.LENGTH_SHORT)
						.show();
		  }
		  else if(contactID != "unknown"){
		  editor.putString(contactID, contactID);
		  editor.putString("contactName"+contactCount, contactName);
		  editor.putString("contactPN"+contactCount, contactPN);
		  editor.commit();

		  TextView tv = new TextView(getApplicationContext());
		  tv.setText("Name:" + settings.getString("contactName"+contactCount, "no name")+"\n"+
			           "Phone Number:"+ settings.getString("contactPN"+contactCount, "no number"));
		  tv.setTextSize(15);
		  tv.setTextColor(Color.BLACK);
		  String tmep = "Name:" + settings.getString("contactName"+contactCount, "no name")+"\n"+
		           "Phone Number:"+ settings.getString("contactPN"+contactCount, "no number");
		  ad.add(tmep);
		  lv.setAdapter(ad);
/*
          View cd = new View(getApplicationContext());
          LayoutInflater inflater=getLayoutInflater();
          cd=inflater.inflate(R.layout.contact_detail, ll, false);
		  TextView tv = (TextView)cd.findViewById(R.id.textView1);
		  tv.setText("Name:" + settings.getString("contactName"+contactCount, "no name")+"\n"+
			           "Phone Number:"+ settings.getString("contactPN"+contactCount, "no number"));
		  tv.setTextSize(15);
		  tv.setTextColor(Color.BLACK);
		  ImageView iv = (ImageView)cd.findViewById(R.id.imageView1);
		  final int count = contactCount;
		  final String id = contactID;
		  iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				det.removeView(findViewById(count));
				editor.remove(id);
				editor.remove("contactName"+count);
				editor.remove("contactPN"+count);
				editor.commit();
				
			}
		});
		
		  cd.setId(count);
		  det.addView(cd);
		  
		  */
		  contactCount++;
		  editor.putInt("contactCount", contactCount);
		  editor.commit();
		  }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
		longitude = location.getLongitude();
		latitude = location.getLatitude();
		sendSMS();
		Toast.makeText(getApplicationContext(),"Longitude:" + longitude+"\n"+"Latitude:" + latitude ,Toast.LENGTH_SHORT).show();
	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
