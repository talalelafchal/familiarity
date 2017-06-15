package org.xkit.android.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {
	private Context context;
	private SQLiteDatabase sqlite;

	private MyDatabaseHelper helper;

	public DBAdapter(Context c) {
		this.context = c;
	}

	public void open() {
		helper = new MyDatabaseHelper(context, 10);
		sqlite = helper.getWritableDatabase();
	}

	public void execSQL(String sql) {
		Log.i("sql execute", sql);
		sqlite.execSQL(sql);
	}

	public Cursor getResultSet(String tableName, String condition,
			Object[] fields) {
		StringBuffer sb = new StringBuffer();
		String allFields = new String();
		if (fields == null) {
			allFields = "*";
		} else {
			for (int i = 0; i < fields.length; i++) {
				allFields += fields[i].toString() + ",";
			}
			allFields = allFields.substring(0, allFields.length() - 1);
		}
		sb.append("select ").append(allFields).append(" from ").append(
				tableName).append(" where ").append(condition);
		Log.i("sqlquery", sb.toString());
		return sqlite.rawQuery(sb.toString(), null);
	}

	public Cursor getResultSet(String sql) {
		Log.i("sql query", sql);
		return sqlite.rawQuery(sql, null);
	}

	public List<Map<String, String>> getResultSet(String sql, int pageSize) {
		Log.i("sql query", sql);
		Cursor cursor = sqlite.rawQuery(sql, null);
		int count = cursor.getCount();
		int columnCount = cursor.getColumnCount();
		Log.d("Column Count", "" + columnCount);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>(
				count);
		Map<String, String> entity = new HashMap<String, String>(columnCount);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			// 找出一共有多少列
			for (int i = 0; i < columnCount; i++) {
				Log.d("Column Found", cursor.getColumnName(i) + " : "
						+ cursor.getString(i));
				entity.put(cursor.getColumnName(i), cursor.getString(i));
			}
			list.add(entity);
		}
		cursor.close();
		entity = null;
		return list;
	}

	public void close() {
		// 关闭我们打开的数据库
		throw new RuntimeException("Only for Stub!");
	}
}
