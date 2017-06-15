import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * DatabaseUtil class makes All Database Related Operation i.e.
 * insert,update,delete,connect ,etc...
 * 
 * @author shashank.acharya
 * @Date October 8, 2014
 * @Version 1.0
 */
public class DatabaseUtil extends SQLiteOpenHelper {

	private SQLiteDatabase databaseConn = null;
	private final Context mContext;

	public DatabaseUtil(Context context, String dbName) {
		super(context, dbName, null, 1);
		this.mContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {
		File dbFile = new File(Constants.DB_PATH + Constants.DATABASE_NAME);
		return dbFile.exists();
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transferring bytestream.
	 */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream inputStream = mContext.getAssets().open(
				Constants.DATABASE_NAME + ".db");

		// Path to the just created empty db
		String outFileName = Constants.DB_PATH + Constants.DATABASE_NAME;

		// Open the empty db as the output stream
		OutputStream outputStream = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		// Close the streams
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}

	/**
	 * Insert MapData into the table
	 * 
	 * @param tableName
	 * @param mapData
	 * @return insert rows count
	 */
	public int insert(String tableName, HashMap<String, String> mapData) {
		databaseConn.insertOrThrow(tableName, null,
				createContentValues(mapData));
		return 1;
	}

	/**
	 * Delete Data From Table According to WhereCondition
	 * 
	 * @param tableName
	 * @param mapData
	 * @return deleted rows count
	 */
	public int delete(String tableName, String whereConditionString,
			String[] whereArgs) {
		return databaseConn.delete(tableName, whereConditionString, whereArgs);
	}

	/**
	 * Get Data From from Database in List of HashMap according to condition
	 * 
	 * @param tableName
	 * @param fields
	 * @param mapData
	 * @return List of HashMap Containing Data
	 */
	public ArrayList<HashMap<String, String>> select(boolean isDistinct,
			String tableName, String[] fields, String whereConditionPart,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {

		Cursor cursor = null;
		ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();

		try {
			cursor = databaseConn.query(isDistinct, tableName, fields,
					whereConditionPart, selectionArgs, groupBy, having,
					orderBy, limit);

			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				HashMap<String, String> map = new HashMap<String, String>();
				for (int j = 0; j < fields.length; j++) {
					map.put(fields[j], cursor.getString(j));
				}
				mapList.add(map);
			}
		} finally {
			cursor.close();
		}

		return mapList;
	}

	/**
	 * Used to get the number of records inside any table.Mainly the query makes
	 * it easy to get the values.
	 * 
	 * @param selectionArgs
	 * @param query
	 * @return {@link Integer}
	 */
	public int rowCount(String[] selectionArgs, String query) {
		int rowCount = (int) DatabaseUtils.longForQuery(databaseConn, query,
				selectionArgs);
		return rowCount;
	}

	/**
	 * Make Connection with Database
	 * 
	 * @throws IOException
	 */
	public void connect(Context context, String database)
			throws SQLiteException, IOException {
		databaseConn = context.openOrCreateDatabase(database,
				Context.MODE_PRIVATE, null);
	}

	/**
	 * DisConnect the Connection With Database
	 * 
	 * @return status
	 */
	public boolean disConnect() throws SQLException {

		if (databaseConn != null) {
			databaseConn.close();
			databaseConn = null;
		}
		return true;
	}

	/**
	 * It Begins the New Transaction
	 */
	public void beginTransaction() {
		databaseConn.beginTransaction();
	}

	/**
	 * It Commits All Database Related Changes
	 * 
	 * @return status
	 */
	public boolean commit() throws IllegalStateException {
		databaseConn.setTransactionSuccessful();
		return true;
	}

	/**
	 * Ends The Transaction
	 * 
	 * @return status
	 */
	public boolean endTransaction() {
		databaseConn.endTransaction();
		return true;
	}

	/**
	 * To Update the Data in Table From Where Condition
	 * 
	 * @param table
	 * @param mapData
	 * @param whereConditionPart
	 * @param whereArgs
	 * @return count of updated rows
	 */
	public int update(String table, HashMap<String, String> mapData,
			String whereConditionPart, String[] whereArgs) {
		return databaseConn.update(table, createContentValues(mapData),
				whereConditionPart, whereArgs);
	}

	/**
	 * Execute any Raw Query and return Result in Cursor
	 * 
	 * @param query
	 * @param selectionArgs
	 * @return Cursor of respective data 
	 */
	public Cursor executeRawQuery(String query, String[] selectionArgs) {
   	      
   	      return cursor = databaseConn.rawQuery(query, selectionArgs);
	}

	/**
	 * Convert HashMap to ContentValues
	 * 
	 * @param column
	 *            HashMap
	 * @return ContentValues
	 */
	protected ContentValues createContentValues(HashMap<String, String> columns) {
		ContentValues values = new ContentValues();
		Iterator<Map.Entry<String, String>> it = columns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) it
					.next();
			values.put(pairs.getKey().toString(), pairs.getValue().toString());
		}
		return values;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}