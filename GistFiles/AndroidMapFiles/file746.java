import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class DB {
	// Database fields
	public static final String KEY_ROWID = "id";
	protected String[] COLUMNS = new String[] {};
	protected String TABLE = "";
	protected Context context;
	protected SQLiteDatabase database;
	protected DBHelper dbHelper;

	public DB(Context pContext) {
		this.context = pContext;
	}
	public DB(Context pContext, String pTable, String[] pColumns) {
		this.context = pContext;
		this.COLUMNS = pColumns;
		this.TABLE = pTable;
	}
	
	public DB open() throws SQLException {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Create a new record If the record is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long create(HashMap<String, String> columns) {
		ContentValues initialValues = createContentValues(columns);
		return database.insert(TABLE, null, initialValues);
	}
	
	/**
	 * Update the record
	 */
	public boolean update(int rowId, HashMap<String, String> columns) {
		ContentValues updateValues = createContentValues(columns);
		return database.update(TABLE, updateValues, KEY_ROWID + "=" + rowId, null) > 0;
	}	
	
	/**
	 * Update the record
	 */
	public boolean update(int rowId, ContentValues columns) {
		return database.update(TABLE, columns, KEY_ROWID + "=" + rowId, null) > 0;
	}		
	
	/**
	 * Deletes todo
	 */
	public boolean delete(int rowId) {
		return database.delete(TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}	
	
	/**
	 * Return a Cursor over the list of all todo in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetch() {
		return fetch(null, null);
	}
	public Cursor fetch(String conditions) {
		return fetch(conditions, null);
	}	
	public Cursor fetch(String conditions, String[] values) {
		Cursor mCursor = database.query(TABLE, COLUMNS, conditions, values, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}		
		return mCursor;
	}	

	/**
	 * Return a Cursor positioned at the defined todo
	 */
	public ContentValues fetchOne(String rowId) throws SQLException {
		return fetchOne(KEY_ROWID, rowId);
	}	
	public ContentValues fetchOne(String key, String value) throws SQLException {
		ContentValues values = new ContentValues();
		Cursor mCursor = database.query(true, TABLE, COLUMNS,
				key + "='" + value + "'", null, null, null, null, null);
		if (mCursor != null) {
			if(mCursor.getCount() > 0) {
				mCursor.moveToFirst();
				for(int i=0,len=mCursor.getColumnCount();i<len;i++) {
					values.put(mCursor.getColumnName(i), mCursor.getString(i));
				}
			}
			mCursor.close();
		}
		return values;
	}	
	public ContentValues fetchOne(String conditions, String[] values) throws SQLException {
		ContentValues result = new ContentValues();
		Cursor mCursor = database.query(true, TABLE, COLUMNS,
				conditions, values, null, null, null, null);
		if (mCursor != null) {
			if(mCursor.getCount() > 0) {
				mCursor.moveToFirst();
				for(int i=0,len=mCursor.getColumnCount();i<len;i++) {
					result.put(mCursor.getColumnName(i), mCursor.getString(i));
				}
			}
			mCursor.deactivate();
			mCursor.close();
		}
		return result;
	}		
	
	
	@SuppressWarnings("unchecked")
	protected ContentValues createContentValues(HashMap<String, String> columns) {
		ContentValues values = new ContentValues();
		Iterator it = columns.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        values.put(pairs.getKey().toString(), pairs.getValue().toString());
	    }
		return values;
	}
}