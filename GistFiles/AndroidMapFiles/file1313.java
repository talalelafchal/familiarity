package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;

import com.abc.model.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


public class people_add extends Fragment {
	private ImageButton imbtnadd,imbtndel,imbtnupdata;
	private EditText edtname,edtbirthday,edttel,edtemail,edtadd,edtnote;
	protected List<ParseObject> tag;
	public ArrayList<String> TagArrayList;
    Spinner sptag;
    public String mode = "";
    public String ID = "" ;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.people_add, container, false);
		
		imbtnadd = (ImageButton)v.findViewById(R.id.imbtnsave);
		imbtndel = (ImageButton)v.findViewById(R.id.imbtndel);

		edtname = (EditText) v.findViewById(R.id.edtname);
		edtbirthday = (EditText) v.findViewById(R.id.edtbirthday);
		edttel = (EditText) v.findViewById(R.id.edttel);
		edtemail = (EditText) v.findViewById(R.id.edtemail);
		edtadd = (EditText) v.findViewById(R.id.edtaddress);
		sptag = (Spinner) v.findViewById(R.id.sptag);
		edtnote = (EditText) v.findViewById(R.id.edtnote);
		Parse.initialize(getActivity(), "8mNYYPLOR08iJAkCt535lP8BfOcNo1ouO2bTbdte", "5Jsm0reTBpRnhope1dRrmXMgpCZjXCO40jlAYBdC");
		
		if(mode.equals("add")){
			imbtndel.setVisibility(8);
		}
				
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tag"); // get
		// Parse
		// table:ClientNote
		query.findInBackground(new FindCallback<ParseObject>() {
			

			@Override
			public void done(List<ParseObject> objects,com.parse.ParseException e) {
				TagArrayList = new ArrayList<String>();
				if (e == null) { // put resule into a variable:clientNotes
					tag = objects;
					if (tag != null) {
						for (ParseObject purposeObject : tag) {
							TagArrayList.add(purposeObject.getString("name"));
							Log.d("TagArrayList",
									TagArrayList.toString());

						}
					}
					ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_spinner_item,
							TagArrayList);
					purposeAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_item);
					sptag.setAdapter(purposeAdapter);
					
					
					
					if(mode.equals("edit")){
						
						
						ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
					    
						query.getInBackground(ID, new GetCallback<ParseObject>() {//GUERY OBJECT_ID ,利用OBJECT_ID UPDATE DATE
							  public void done(ParseObject gameScore, ParseException e) {
							    if (e == null) {
							    	edtname.setText(gameScore.get("name")==null?"":gameScore.get("name").toString());
							    	edtbirthday.setText(gameScore.get("birthday")==null?"":gameScore.get("birthday").toString());
							    	edttel.setText(gameScore.get("tel")==null?"":gameScore.get("tel").toString());
							    	edtemail.setText(gameScore.get("email")==null?"":gameScore.get("email").toString());
							    	edtadd.setText(gameScore.get("add")==null?"":gameScore.get("add").toString());
							    	for(int i=0;i<TagArrayList.size();i++){
							    		if(TagArrayList.get(i).toString().equals(gameScore.get("tag")==null?"":gameScore.get("tag").toString())){
							    			sptag.setSelection(i);
							    		}
							    	}
							    	
							    }
							  }
						});
						query.clearCachedResult();
					}
					
					
				}
			}
		});
		query.clearCachedResult();
		
		
		imbtnadd.setOnClickListener(new OnClickListener(){
		
			//ADD
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mode.equals("add")){
					btnadd();
				}
				if(mode.equals("edit")){
					btnedit();
				}
			}});
		
		imbtndel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btndel();
			}});
		
	
		
		return v;
		
		
	}
	
	public void btnadd(){
		
			ParseObject testObject = new ParseObject("Client");//DATABASE_TABLE_NAME
    		testObject.put("name",edtname.getText().toString());//ADD_欄位
    		testObject.put("birthday",edtbirthday.getText().toString());
    		testObject.put("tel",edttel.getText().toString());
    		testObject.put("email",edtemail.getText().toString());
    		testObject.put("add",edtadd.getText().toString());
    		String tag = sptag.getSelectedItem().toString();
    		testObject.put("tag",tag);
    		testObject.put("note",edtnote.getText().toString());
    		
    		if(edtadd.getText().toString().equals("")){
    			
    			Toast.makeText(getActivity(), "請填上地址", Toast.LENGTH_LONG).show();
    			
    		}
    		
    		testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
    		
    		testObject.saveInBackground(new SaveCallback () {
    			
				@Override
				//彈跳視窗
				public void done(ParseException ex) {
					// TODO Auto-generated method stub
					if (ex == null) {
			            Toast.makeText(getActivity(), "存檔成功", Toast.LENGTH_LONG).show();
			            FragmentManager fragmentManager = getFragmentManager();
         				fragmentManager.beginTransaction()
         				.replace(R.id.content_frame, new People()).commit();
			        } else {
			        	Toast.makeText(getActivity(),  "存檔失敗:"+ex.getMessage().toString(), Toast.LENGTH_LONG).show();
			        }
				}});
	}
	
	public void btnedit(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("","mode="+mode);
		Log.v("","ID="+ID);
		
		query.getInBackground(ID, new GetCallback<ParseObject>() {//GUERY OBJECT_ID ,利用OBJECT_ID UPDATE DATE
		  public void done(ParseObject gameScore, ParseException e) {
		    if (e == null) {
		      // Now let's update it with some new data. In this case, only cheatMode and score
		      // will get sent to the Parse Cloud. playerName hasn't changed.
		    	gameScore.put("name",edtname.getText().toString());//ADD_欄位
		    	gameScore.put("birthday",edtbirthday.getText().toString());
		    	gameScore.put("tel",edttel.getText().toString());
		    	gameScore.put("email",edtemail.getText().toString());
	    		gameScore.put("add",edtadd.getText().toString());
	    		String tag = sptag.getSelectedItem().toString();
	    		gameScore.put("tag",tag);
	    		gameScore.put("note",edtnote.getText().toString());
		      
		      
		      gameScore.saveInBackground(new SaveCallback () {
        			
					@Override
					//彈跳視窗
					public void done(ParseException ex) {
						// TODO Auto-generated method stub
						if (ex == null) {
				            Toast.makeText(getActivity(), "存檔成功", Toast.LENGTH_LONG).show();
				            FragmentManager fragmentManager = getFragmentManager();
	         				fragmentManager.beginTransaction()
	         				.replace(R.id.content_frame, new People()).commit();
				        } else {
				        	Toast.makeText(getActivity(),  "存檔失敗:"+ex.getMessage().toString(), Toast.LENGTH_LONG).show();
				        }
					}});
		      
		      
		    }
		  }
		});
	}
	
	public void btndel(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("","mode="+mode);
		Log.v("","ID="+ID);
		
		query.getInBackground(ID, new GetCallback<ParseObject>() {//GUERY OBJECT_ID ,利用OBJECT_ID UPDATE DATE
		  public void done(ParseObject gameScore, ParseException e) {
		    if (e == null) {
		    	gameScore.deleteInBackground(new DeleteCallback(){

					@Override
					public void done(ParseException ex) {
						// TODO Auto-generated method stub
						if (ex == null) {
				            Toast.makeText(getActivity(), "刪除成功", Toast.LENGTH_LONG).show();
				            FragmentManager fragmentManager = getFragmentManager();
	         				fragmentManager.beginTransaction()
	         				.replace(R.id.content_frame, new People()).commit();
				        } else {
				        	Toast.makeText(getActivity(),  "刪除失敗:"+ex.getMessage().toString(), Toast.LENGTH_LONG).show();
				        }
					}});
		    }
		  }
		});
	}
	
	public void setMode (String setmode){
		this.mode = setmode;
	}
	public void setID (String setid){
		this.ID = setid;
	}
}
