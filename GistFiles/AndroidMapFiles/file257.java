package com.example.androidlab.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

public class UserDA {

    private DMO conn;
    private Activity activity;
   
 	public UserDA(Activity activity){
 		this.activity = activity;
 	}
 	
 	/**
 	 * All CRUD(Create, Read, Update, Delete) Operations
 	 */
  
 	// Adding new 
 	public void add(User obj) {
        conn = new Database(activity, new UserTable(activity));
        
        ContentValues values = new ContentValues();
 		values.put(DBConfig.UserTableConfig.NAME.name(), obj.getName());
 		
        conn.insert(DBConfig.TBL_USER.name(), values);
        conn.close();
 	}
  
 	// Getting single 
 	public User getUser(int id) {
 		conn = new Database(activity, new UserTable(activity));
 		Cursor cursor = conn.selectById(DBConfig.TBL_USER.name(), id);
 		User obj = null;
 		if (cursor != null)
 			if(cursor.moveToFirst())
	 			obj = new User(Integer.parseInt(cursor.getString(0)),
	 				cursor.getString(1));
 		return obj;
 	}
  
 	// Getting All 
 	public List<User> getAll() {
 		List<User> list = new ArrayList<User>();
 		conn = new Database(activity, new UserTable(activity));
 		Cursor cursor = conn.select(DBConfig.TBL_USER.name(), null);
  
 		// looping through all rows and adding to list
 		if (cursor.moveToFirst()) {
 			do {
 				User obj = new User();
 				obj.setID(Integer.parseInt(cursor.getString(0)));
 				obj.setName(cursor.getString(1));
 				// Adding to list
 				list.add(obj);
 			} while (cursor.moveToNext());
 		}
 		return list;
 	}
  
 	public List<Map> getAllByMap() {
 		List<Map> list = new ArrayList<Map>();
 		conn = new Database(activity, new UserTable(activity));
 		Cursor cursor = conn.select(DBConfig.TBL_USER.name(), null);  
 		
 		// looping through all rows and adding to list
 		if (cursor.moveToFirst()) {
 			do {
 				Map map = new HashMap();
 				map.put(DBConfig.UserTableConfig.ID.name(), cursor.getString(0));
 		        map.put(DBConfig.UserTableConfig.NAME.name(), cursor.getString(1));
 				list.add(map);
 			} while (cursor.moveToNext());
 		}
 		return list;
 	}
 	
 	// Updating single 
 	public void update(User obj) {
 		conn = new Database(activity, new UserTable(activity));
 		ContentValues values = new ContentValues();
 		values.put(DBConfig.UserTableConfig.NAME.name(), obj.getName());
 		conn.update(DBConfig.TBL_USER.name(), obj.getID(), values);
 	}
  
 	// Deleting single 
 	public void deleteUser(User obj) {
 		conn = new Database(activity, new UserTable(activity));
 		conn.delete(DBConfig.TBL_USER.name(), obj.getID());
 	}
  
 	public void deleteAll() {
 		conn = new Database(activity, new UserTable(activity));
 		conn.deleteAll(DBConfig.TBL_USER.name());
 	}
 	
 	// Getting Count
 	public int getCount() {
 		conn = new Database(activity, new UserTable(activity));
 		Cursor cursor = conn.selectCount(DBConfig.TBL_USER.name());
 		if(cursor.moveToFirst()){
 			cursor.close();
 			return cursor.getCount();
 		} else
 			return 0;
 	}
} 
