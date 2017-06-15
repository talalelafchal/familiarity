package com.ozateck.db;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * [データベース]アクセスオブジェクト
 */

public class SQLiteDAO{
	
	private static final String TAG = "SQLiteDAO";
	
	// main
	public static final String TABLE_NAME  = "main";
	public static final String COL_uid     = "uid";
	public static final String COL_mName   = "mName";
	public static final String COL_mLat    = "mLat";
	public static final String COL_mLon    = "mLon";
	public static final String COL_mStatus = "mStatus";
	
	/*
	 * テーブル情報の取得
	 */
	
	// 全てのデータを取得
	public static ArrayList<HashMap<String, String>>
								getAllList(SQLiteDatabase db){
		String sql = "SELECT * FROM " + TABLE_NAME;
		return getArrayList(db, sql);
	}
	
	/*
	 *  DB,SQLからArrayListを取得
	 */
	private static ArrayList<HashMap<String, String>>
						getArrayList(SQLiteDatabase db, String sql){

		ArrayList<HashMap<String, String>> list = 
				new ArrayList<HashMap<String, String>>();
		
		Cursor c = db.rawQuery(sql, null);
		int total = c.getCount();
		c.moveToFirst();
		for(int i=0; i<total; i++){
			String[] strs = c.getColumnNames();// key
			HashMap<String, String> map = new HashMap<String, String>();
			for(int s=0; s<strs.length; s++){
				Log.i(TAG, "key:" + strs[s] + "_" + c.getString(s));
				map.put(strs[s], c.getString(s));
			}
			list.add(map);
			c.moveToNext();
		}
		c.close();
		
		return list;
	}
	
	// Insert
	public static void insert(SQLiteDatabase db){
		ContentValues cValues = new ContentValues();
		cValues.put(COL_mName,   "Yabacho");
		cValues.put(COL_mLat, 100.0d);
		cValues.put(COL_mLon, 200.0d);
		cValues.put(COL_mStatus, "WIFI");
		long result = db.insert(TABLE_NAME, null, cValues);
		Log.d(TAG, "result:" + result);
	}
	
	// Delete
	public static void delete(SQLiteDatabase db, int uid){
		int result = db.delete(TABLE_NAME, COL_uid + " = ?", new String[]{String.valueOf(uid)});
		Log.d(TAG, "result:" + result);
	}
	
	// Count
	public static int count(SQLiteDatabase db){
		Cursor c = db.rawQuery("SELECT count(*) as cnt FROM " + TABLE_NAME, null);
		c.moveToFirst();
		int count = c.getInt(c.getColumnIndex("cnt"));
		c.close();
		return count;
	}
	
	/*
	 * データ確認用
	 */
	// 配列データの確認(List)
	public static void checkList(List<HashMap<String, String>> list){
		Log.i(TAG, "-checkList-");
		for(int s=0; s<list.size(); s++){
			Log.d(TAG, "-[" + s + "]-");
			HashMap<String, String> map = list.get(s);
			Set<String> keySet = map.keySet();
			for(Iterator<String> it = keySet.iterator(); it.hasNext();){
				String key = it.next();
				String value = map.get(key);
				Log.d(TAG, "key:" + key + "_value:" + value);
			}
		}
	}
	
	// 配列データの確認(Array)
	public static void checkArray(int[] array){
		Log.i(TAG, "-checkArray-");
		for(int i=0; i<array.length; i++){
			Log.d(TAG, "array:" + array[i]);
		}
	}
}
