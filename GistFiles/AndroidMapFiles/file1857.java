package de.databasetemplate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class manages database creation and updates.
 * 
 * Based on "Android SQLite Database and ContentProvider - Tutorial" by Lars
 * Vogel. ( http://www.vogella.com/articles/AndroidSQLite/article.html )
 * 
 * @author Andreas Bender
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "my_database.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_IDENTITIES = "create table "
	  + IdentitiesDb.TABLE_NAME + "(" +
      IdentitiesDb.COL_ID + " integer primary key autoincrement, " +
	  IdentitiesDb.COL_USER + " text " +
      IdentitiesDb.COL_PW + " text " +
      ");";
	
	  public SQLiteHelper(Context context) {
	      super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
	      database.execSQL(CREATE_IDENTITIES);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	      Log.w(SQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	        + newVersion + ", which will destroy all old data");
	      db.execSQL("DROP TABLE IF EXISTS " + IdentitiesDb.TABLE_NAME);
	      onCreate(db);
	  }

} 