package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.abc.model.R;


public class People extends Fragment{

	public People(){} 
	public ListView listView;
	//public EditText et;
	//public ImageButton bt;
	public View v;
	public AutoCompleteTextView autoComplete;
	public ArrayList<HashMap<String, String>> contactsArrayList = new ArrayList<HashMap<String, String>>();
	public String[] contactsName ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	    v =inflater.inflate(R.layout.people_layout, container, false); 
	    //et = (EditText) v.findViewById(R.id.edit_query);
	    //bt = (ImageButton) v.findViewById(R.id.imageButton1);
	    listView = (ListView) v.findViewById(R.id.lvPEOPLE);
	    autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autoComplete);
	    getPhoneBookData();
	    //setButton();
	    setListView();
	    
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,contactsName);
        autoComplete.setAdapter(adapter);
        
        autoComplete.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				getAssignedPhoneBookData(autoComplete.getText().toString());
				listView.setAdapter(null);
				SimpleAdapter adapter = new SimpleAdapter(getActivity(),contactsArrayList,R.layout.people_contact_entry,new String[] { "NAME","NUMBER" }, 
						new int[] { R.id.txtNAMEPHONE,R.id.txtDATAPHONE });
				listView.setAdapter(adapter);
			}});
        
        autoComplete.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.v("","--"+autoComplete.getText().toString().length());
				if(autoComplete.getText().toString().length() == 0){
					listView.setAdapter(null);
					getPhoneBookData();
					SimpleAdapter adapter = new SimpleAdapter(getActivity(),contactsArrayList,R.layout.people_contact_entry,new String[] { "NAME","NUMBER" }, 
							new int[] { R.id.txtNAMEPHONE,R.id.txtDATAPHONE });
					listView.setAdapter(adapter);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}});
		return v;
	}
	
	/*public void setButton(){
		bt.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});
	
	}*/
	
	public void setListView(){
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),contactsArrayList,R.layout.people_contact_entry,new String[] { "NAME","NUMBER" }, 
				new int[] { R.id.txtNAMEPHONE,R.id.txtDATAPHONE });
		listView.setAdapter(adapter);
		
		
		
		listView.setOnItemClickListener(new OnItemClickListener(){
    		
		    
    		

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				final String name = contactsArrayList.get(position).get("NAME");
    	        final String number = contactsArrayList.get(position).get("NUMBER");
    	        new AlertDialog.Builder(getActivity())
    	            .setTitle(number)
    	            .setItems(new String[]{"Call"}, new DialogInterface.OnClickListener() {
    	                 @Override
    	                 public void onClick(DialogInterface dialog, int which) {
    	                     switch(which){
    	                     case 0:
    	                       Intent call = new Intent(
    	                             Intent.ACTION_CALL, Uri.parse("tel:" + number));
    	                         startActivity(call);
    	                         break;
    	                     }
    	                 }
    	            })
    	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
    	                 @Override
    	                 public void onClick(DialogInterface dialog, int which) {
    	                     // TODO Auto-generated method stub
    	                 }    
    	            })
    	            .show();   
			}
			
    	});
	}
	
	
	public void getPhoneBookData(){
		contactsArrayList.clear();
		Cursor contacts_name = getActivity().getContentResolver().query(
	        ContactsContract.Contacts.CONTENT_URI,
	        null,
	        null,
	        null,
	        null);
	    contactsName = new String[contacts_name.getCount()];
	    int i = 0;
	    while (contacts_name.moveToNext()) {
	      HashMap<String, String> contactsMap = new HashMap<String, String>();
	        String phoneNumber = "";
	        long id = contacts_name.getLong(
	            contacts_name.getColumnIndex(ContactsContract.Contacts._ID));
	        Cursor contacts_number = getActivity().getContentResolver().query(
	            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	            null,
	            ContactsContract.CommonDataKinds.Phone.CONTACT_ID 
	            + "=" + Long.toString(id),
	            null,
	            null);
	            
	        while (contacts_number.moveToNext()) {
	            phoneNumber = contacts_number
	                .getString(contacts_number.getColumnIndex(                                                
	                ContactsContract.CommonDataKinds.Phone.NUMBER));
	        }
	        contacts_number.close();
	        String name = contacts_name.getString(contacts_name
	            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	        contactsMap.put("NAME", name);
	        contactsName[i] = name;
	        contactsMap.put("NUMBER", phoneNumber);
			contactsArrayList.add(contactsMap);
			i++;
			Log.v("",name);
	    }
	    
	}
	
	public void getAssignedPhoneBookData(String NAME){
		contactsArrayList.clear();
		Cursor contacts_name = getActivity().getContentResolver().query(
	        ContactsContract.Contacts.CONTENT_URI,
	        null,
	        null,
	        null,
	        null);
	 
	    while (contacts_name.moveToNext()) {
	      if(contacts_name.getString(contacts_name.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)).equals(NAME)){
	    	  HashMap<String, String> contactsMap = new HashMap<String, String>();
		        String phoneNumber = "";
		        long id = contacts_name.getLong(
		            contacts_name.getColumnIndex(ContactsContract.Contacts._ID));
		        Cursor contacts_number = getActivity().getContentResolver().query(
		            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		            null,
		            ContactsContract.CommonDataKinds.Phone.CONTACT_ID 
		            + "=" + Long.toString(id),
		            null,
		            null);
		            
		        while (contacts_number.moveToNext()) {
		            phoneNumber = contacts_number
		                .getString(contacts_number.getColumnIndex(                                                
		                ContactsContract.CommonDataKinds.Phone.NUMBER));
		        }
		        contacts_number.close();
		        String name = contacts_name.getString(contacts_name
		            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		        contactsMap.put("NAME", name);
		        contactsMap.put("NUMBER", phoneNumber);
				contactsArrayList.add(contactsMap);
	      }
	    }
	    
	}
	

}
