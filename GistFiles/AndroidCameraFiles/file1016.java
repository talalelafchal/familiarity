package com.oneunit.drunkcalls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.oneunit.drunkcalls.FeedReaderContract.FeedEntry;

public class MyPhoneStateListener extends PhoneStateListener {
	   Context context; 
	   FeedReaderDbHelper mdDbHelper;
	 
	   public MyPhoneStateListener(Context context) { 
	   super();
		this.context = context; 
	   } 
	 
	   @Override
	   public void onCallStateChanged(int state, String callingNumber) 
	   { 
	     super.onCallStateChanged(state, callingNumber); 
	     switch (state) { 
	       case TelephonyManager.CALL_STATE_IDLE: 
	       break; 
	 
	       case TelephonyManager.CALL_STATE_OFFHOOK: 
	       //handle out going call
	       endCallIfBlocked(callingNumber); 
	       break; 
	 
	       case TelephonyManager.CALL_STATE_RINGING: 
	       //handle in coming call
	       endCallIfBlocked(callingNumber); 
	       break; 
	       
	       default: 
	       break; 
	     }  
	   } 
	 
	   private void endCallIfBlocked(String callingNumber) { 
		   Log.d("PhoneState", "We are in the function");
		   this.mdDbHelper = new FeedReaderDbHelper(context);
	        final SQLiteDatabase db = this.mdDbHelper.getWritableDatabase();
		   Cursor cursor = db.rawQuery("SELECT " + FeedEntry.COLUMN_NAME_ENTRY_ID + " FROM " + FeedEntry.TABLE_NAME + " WHERE " + FeedEntry.COLUMN_NAME_ENTRY_ID + "= '"+ callingNumber +"'", null);
		 if(cursor.moveToFirst()){  
			 TelephonyManager tm = (TelephonyManager) context
		            .getSystemService(Context.TELEPHONY_SERVICE);
		Class c;
		try {
			c = Class.forName(tm.getClass().getName());
		
		Method m = c.getDeclaredMethod("getITelephony");
		m.setAccessible(true);
		Object telephonyService = m.invoke(tm); // Get the internal ITelephony object
		c = Class.forName(telephonyService.getClass().getName()); // Get its class
		m = c.getDeclaredMethod("endCall"); // Get the "endCall()" method
		m.setAccessible(true); // Make it accessible
		m.invoke(telephonyService); // invoke endCall()
            Toast.makeText(this.context, "This number is in black list", Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		 }
	   }
	   
}