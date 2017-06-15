package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.abc.model.R;
import com.abc.drawer_fragment.People;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class People extends Fragment{

	public People(){}
	
	public ListView listView;
	public View v;
	public ImageButton imgbtn;
	public AutoCompleteTextView autoComplete;
	public ArrayList<HashMap<String, String>> contactsArrayList = new ArrayList<HashMap<String, String>>();
	public String[] contactsName ;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.people_layout, container, false); 
		listView = (ListView) v.findViewById(R.id.lvPEOPLE);
		imgbtn  = (ImageButton) v.findViewById(R.id.btnimport);
	    autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autoComplete);
	    Parse.initialize(getActivity(), "8mNYYPLOR08iJAkCt535lP8BfOcNo1ouO2bTbdte", "5Jsm0reTBpRnhope1dRrmXMgpCZjXCO40jlAYBdC");
	    
	    getParseDate("");
	    
	    
		imgbtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setParseData();
				Toast.makeText(getActivity(), "Successful",
						Toast.LENGTH_SHORT).show();
				
			}});
	    
	    
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	
	public void setAutoCompleteTextView(){
		Log.v("", ""+contactsName.length);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,contactsName);
        autoComplete.setAdapter(adapter);
        
        autoComplete.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//getAssignedPhoneBookData(autoComplete.getText().toString());
				listView.setAdapter(null);
				getParseDate(autoComplete.getText().toString());
				
				/*SimpleAdapter adapter = new SimpleAdapter(getActivity(),contactsArrayList,R.layout.people_contact_entry,new String[] { "NAME","NUMBER" }, 
						new int[] { R.id.txtNAMEPHONE,R.id.txtDATAPHONE });
				listView.setAdapter(adapter);*/
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
					getParseDate("");
					/*SimpleAdapter adapter = new SimpleAdapter(getActivity(),contactsArrayList,R.layout.people_contact_entry,new String[] { "NAME","NUMBER" }, 
							new int[] { R.id.txtNAMEPHONE,R.id.txtDATAPHONE });
					listView.setAdapter(adapter);*/
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}});
	}
	
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
    	                       /*Intent call = new Intent(
    	                             Intent.ACTION_CALL, Uri.parse("tel:" + number));
    	                         startActivity(call);*/
    	                    	 
    	                    	 
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
	public void getParseDate(final String name){
		contactsArrayList.clear();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
	    query.whereEqualTo("ID", ParseUser.getCurrentUser().getObjectId());
	    
	    if(!name.equals("")){
	    	query.whereEqualTo("name", name);
	    }
	    
	    query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
	                Log.v("score", "Retrieved " + objects.size() + " scores");
	                contactsName = new String[objects.size()];
	                for(int i = 0 ; i<objects.size() ; i++){
	                	HashMap<String, String> hm = new HashMap<String, String>();
	                	hm.put("NAME", objects.get(i).get("name").toString());
	                	hm.put("NUMBER", objects.get(i).get("tel").toString());
	                	contactsArrayList.add(hm);
	                	contactsName[i] = objects.get(i).get("name").toString();
	                }
	                Log.v("score", "111: " + contactsName.length);
	                setListView();
	                if(name.equals("")){
	                	setAutoCompleteTextView();
	                }
	                
	            } else {
	                Log.v("score", "Error: " + e.getMessage());
	            }
			}
	    });
	}
	
	public  void  setParseData() {  
    String id;  
    String mimetype;  
    
    
    ContentResolver contentResolver =  getActivity().getContentResolver();  
    //只需要從Contacts中獲取ID，其他的都可以不要，通過查看上面編譯後的SQL語句，可以看出將第二個參數  
    //設置成null，默認返回的列非常多，是一種資源浪費。  
    Cursor cursor = contentResolver.query(android.provider.ContactsContract.Contacts.CONTENT_URI,  
            new  String[]{android.provider.ContactsContract.Contacts._ID},  null ,  null ,  null );  
    while (cursor.moveToNext()) {  
    	ParseObject testObject = new ParseObject("Client");
        id=cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.Contacts._ID));  
          
        //從一個Cursor獲取所有的信息  
        Cursor contactInfoCursor = contentResolver.query(  
                android.provider.ContactsContract.Data.CONTENT_URI,  
                new  String[]{android.provider.ContactsContract.Data.CONTACT_ID,  
                        android.provider.ContactsContract.Data.MIMETYPE,
                        android.provider.ContactsContract.Data.DATA1  
                        },   
                android.provider.ContactsContract.Data.CONTACT_ID+ "=" +id,  null ,  null );  
        while (contactInfoCursor.moveToNext()) {  
            mimetype = contactInfoCursor.getString(  
                    contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));  
            String value = contactInfoCursor.getString(  
                    contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));  
            if (mimetype.contains( "/name" )){  
                System.out.println( "姓名=" +value); 
                testObject.put("name", value);
            }  else  if (mimetype.contains( "/email" )) {  
                System.out.println( "郵箱=" +value);  
                testObject.put("email", value);
            }  else  if (mimetype.contains( "/phone" )) {  
                System.out.println( "電話=" +value);  
                testObject.put("tel", value);
            }  else  if (mimetype.contains( "/postal" )) {  
                System.out.println( "郵編=" +value);  
                testObject.put("add", value);
            }   
            testObject.put("ID", ParseUser.getCurrentUser().getObjectId());
            testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
            
        }  
        testObject.saveInBackground(new SaveCallback () {
			@Override
			//彈跳視窗
			public void done(ParseException ex) {
				// TODO Auto-generated method stub
				if (ex == null) {
		            Toast.makeText(getActivity(), "存檔成功", Toast.LENGTH_LONG).show();
		        } else {
		        	Toast.makeText(getActivity(), "存檔失敗:"+ex.getMessage().toString(), Toast.LENGTH_LONG).show();
		        }
			}});
        
        System.out.println( "*********" ); 
        contactInfoCursor.close();  
    }  
    cursor.close();  
}  
	
	
}
