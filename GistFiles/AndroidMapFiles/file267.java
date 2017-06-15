package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
	public ImageButton imgbtn,imbtnadd,imbtndel,imbtnupdata;
	public AutoCompleteTextView autoComplete;
	public ArrayList<HashMap<String, String>> contactsArrayList;
	public String[] contactsName ;
	private ProgressDialog dialog;
	private EditText edtname,edtbirthday,edttel,edtemail,edtadd,edttag,edtnote;
	public int size = 0;
	public boolean start = true;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v =inflater.inflate(R.layout.people_layout, container, false); 
		listView = (ListView) v.findViewById(R.id.lvPEOPLE);
		imgbtn  = (ImageButton) v.findViewById(R.id.btnimport);
		imbtnadd = (ImageButton) v.findViewById(R.id.imgbtnadd);
	    autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autoComplete);
	    Parse.initialize(getActivity(), "8mNYYPLOR08iJAkCt535lP8BfOcNo1ouO2bTbdte", "5Jsm0reTBpRnhope1dRrmXMgpCZjXCO40jlAYBdC");
	    
	    getParseDate("");
	    
	    imbtnadd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				people_add  ppadd = new people_add();
				ppadd.setMode("add");
				Fragment  fg =  ppadd;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fg).commit();
			}});
	    
	 
		imgbtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setParseData();
				Toast.makeText(getActivity(), "Import Successful",Toast.LENGTH_SHORT).show();
			
				
			}});
	    
		
		
	    
		
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	
	public void setAutoCompleteTextView(){
		//Log.v("", ""+contactsName.length);
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
				if(autoComplete.getText().toString().length() == 0 && listView.getCount()<size){
					listView.setAdapter(null);
					getParseDate("");
					Log.v("score", "121: " + contactsArrayList.size()+"=="+listView.getCount() + "==" + size);
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
				final String Oid =  contactsArrayList.get(position).get("ID");
				final String name = contactsArrayList.get(position).get("NAME");
    	        final String number = contactsArrayList.get(position).get("NUMBER");
    	        new AlertDialog.Builder(getActivity())
    	            .setTitle(number)
    	            .setItems(new String[]{"Detail","Call"}, new DialogInterface.OnClickListener() {
    	                 @Override
    	                 public void onClick(DialogInterface dialog, int which) {
    	                     switch(which){
    	                     case 0:
    	                    	people_add  ppadd = new people_add();
    	         				ppadd.setMode("edit");
    	         				ppadd.setID(Oid);
    	         				Fragment  fg =  ppadd;
    	         				FragmentManager fragmentManager = getFragmentManager();
    	         				fragmentManager.beginTransaction()
    	         				.replace(R.id.content_frame, fg).commit();
    	                    	 
    	                    	 
    	                         break;
    	                         
    	                     case 1:
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
	public void getParseDate(final String name){
		contactsArrayList =  new ArrayList<HashMap<String, String>>();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
	    //query.whereEqualTo("ID", ParseUser.getCurrentUser().getObjectId());
	    
	    if(!name.equals("")){
	    	query.whereEqualTo("name", name);
	    }
	    
	    query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
	                //Log.v("score", "Retrieved " + objects.size() + " scores");
	                contactsName = new String[objects.size()];
	                if(name.equals("")){
	                	size = objects.size();
	                }
	                
	                for(int i = 0 ; i<objects.size() ; i++){
	                	HashMap<String, String> hm = new HashMap<String, String>();
	                	hm.put("ID", objects.get(i).getObjectId().toString());
	                	hm.put("NAME", objects.get(i).get("name").toString());
	                	hm.put("NUMBER", objects.get(i).get("tel").toString());
	                	contactsArrayList.add(hm);
	                	contactsName[i] = objects.get(i).get("name").toString();
	                }

	                //Log.v("score", "111: " + objects.size());
	                
	                setListView();
	                
	                if(start){
	                	setAutoCompleteTextView();
	                	start = false;
	                }
	                
	            } else {
	                Log.v("score", "Error: " + e.getMessage());
	            }
			}
	    });
	    query.clearCachedResult();
	    
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
                System.out.println( "Name=" +value); 
                testObject.put("name", value);
            }  else  if (mimetype.contains( "/email" )) {  
                System.out.println( "Email=" +value);  
                testObject.put("email", value);
            }  else  if (mimetype.contains( "/phone" )) {  
                System.out.println( "Tel=" +value);  
                testObject.put("tel", value);
            }  else  if (mimetype.contains( "/postal" )) {  
                System.out.println( "Address=" +value);  
                testObject.put("add", value);
            }  else  if (mimetype.contains( "/birthday" )) {  
                System.out.println( "birthday=" +value);  
                testObject.put("birthday", value);
            }
            
            
            //testObject.put("ID", ParseUser.getCurrentUser().getObjectId());
            testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
            Log.v("", ""+mimetype);
            
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
	
	
	
	
	private void setContentView(int peopleAdd) {
		// TODO Auto-generated method stub
		
	}
}
