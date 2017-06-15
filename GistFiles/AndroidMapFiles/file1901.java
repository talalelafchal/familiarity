import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {

	private static String TAG = DB.class.getSimpleName();
	private static SQLiteDatabase mInstance = null;
	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "database_name";

	public static final String CREATE_TABLE_SCRIPT = "CREATE TABLE bla bla bla";

	public synchronized static SQLiteDatabase instance(Context ctx) {
		if (mInstance == null) {
			mInstance = new DB(ctx.getApplicationContext()).getWritableDatabase();
		}

		return mInstance;
	}

	private DB(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SCRIPT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS tablename");
		onCreate(db);
	}

	public static ArrayList<ContentValues> selectRows(Context ctx, String sql, String[] params) {
		Cursor c = DB.instance(ctx).rawQuery(sql, params);

		ArrayList<ContentValues> retVal = new ArrayList<ContentValues>();
		ContentValues map;
		if(c.moveToFirst()) {
			do {
				map = new ContentValues();
				DatabaseUtils.cursorRowToContentValues(c, map);
				retVal.add(map);
			} while(c.moveToNext());
		}
		c.close();
		return retVal;
	}

	public static void executeSQL(Context ctx, String sql, String[] params) {
		DB.instance(ctx).execSQL(sql, params);
	}

	public static long lastId(Context ctx, String tabela) {
		ArrayList<ContentValues> rows = DB.selectRows(ctx, "SELECT max(_id) as seq FROM " + tabela, null);
		return rows.get(0).getAsLong("seq");
	}



}