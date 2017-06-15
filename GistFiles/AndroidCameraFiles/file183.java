package sagarpreet97.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sagarpreet chadha on 21-07-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
//    // Database Version
//    private static final int DATABASE_VERSION = 1;
//
//    // Database Name
//    private static final String DATABASE_NAME = "database_name";
//
//    // Table Names
//     static final String DB_TABLE = "table_image";
//
//    // column names
//     static final String KEY_TITLE = "image_title";
//     static final String KEY_IMAGE = "image_data";
//     static final String KEY_DESC = "image_desc";
//
//    // Table create statement
//    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
//            KEY_TITLE + " TEXT," +
//            KEY_IMAGE + " BLOB , " +
//            KEY_DESC + "TEXT);";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//        // creating table
//        db.execSQL(CREATE_TABLE_IMAGE);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // on upgrade drop older tables
//        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
//
//        // create new table
//        onCreate(db);
//    }
public static final String DB_TABLE = "movies";
    public static final String KEY_TITLE = "image_title";
     public static final String KEY_IMAGE = "image_data";
     public  static final String KEY_DESC = "image_desc";

    public DatabaseHelper(Context context) {
        super(context, "CN",  null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " +  DB_TABLE + " ( " + KEY_TITLE + " TEXT ," +
                KEY_DESC  + " TEXT, " + KEY_IMAGE + " BLOB);");
        // db.execSQL("create table students ( _ID INTEGER PRIMARY KEY ," +
        //          " batch_id INTEGER, name TEXT, college TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
