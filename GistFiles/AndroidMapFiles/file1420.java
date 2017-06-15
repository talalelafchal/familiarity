package com.example.androidlab;
 
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class UserDA {
	// Helper
	private SQLiteOpenHelper sqlHelper;
	
	// Table name
	private static final String TABLE_NAME = "user";
 
	// Table Columns names
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
 
	public UserDA(SQLiteOpenHelper helper) {
		sqlHelper = helper;
	}
	
	public void onCreate(SQLiteDatabase db) {		
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
		Log.d(TABLE_NAME, "DB created!");
	}
 
	public void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
		Log.d(TABLE_NAME, "DB upgraded!");
	}
	
	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */
 
	// Adding new 
	public void add(User obj) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
 
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, obj.getName()); 
 
		db.insert(TABLE_NAME, null, values);
		db.close(); 
	}
 
	// Getting single 
	public User get(int id) {
		SQLiteDatabase db = sqlHelper.getReadableDatabase();
 
		Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID, KEY_NAME },
				KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
				null, null);
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
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
 
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
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
 
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
 
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Map map = new HashMap();
				map.put(KEY_ID, cursor.getString(0));
		        map.put(KEY_NAME, cursor.getString(1));
				list.add(map);
			} while (cursor.moveToNext());
		}
 
		return list;
	}
	
	// Updating single 
	public int update(User obj) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
 
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, obj.getName());
 
		// updating row
		return db.update(TABLE_NAME, values, KEY_ID + " = ?",
				new String[] { String.valueOf(obj.getID()) });
	}
 
	// Deleting single 
	public void delete(User obj) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?",
				new String[] { String.valueOf(obj.getID()) });
		db.close();
	}
 
	public void deleteAll() {
		SQLiteDatabase db= sqlHelper.getWritableDatabase();
	    db.delete(TABLE_NAME, null, null);
	}
	
	// Getting Count
	public int getCount() {
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor.moveToFirst()){
			cursor.close();
			return cursor.getCount();
		} else
			return 0;
	}
}