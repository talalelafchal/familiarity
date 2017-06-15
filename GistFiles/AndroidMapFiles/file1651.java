package cn.ifengge.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Map;

import cn.ifengge.passport.BuildConfig;
import cn.ifengge.passport.MainApplication;

public class SQLHelper extends SQLiteOpenHelper {
    public static final String TABLE = "table";
    public static final String FIELD = "field";
    public static String DATABASE_NAME;
    public static ArrayList<Map<String, String[]>> TABLES_NAME;
    private static String FIELD_id = "_id";


    /**
     * @param tables;
     **/
    public SQLHelper(Context context, String dbname, ArrayList<Map<String, String[]>> tables) {
        this(context, dbname, tables, 1);
    }

    public SQLHelper(Context context, String dbname, ArrayList<Map<String, String[]>> tables, int version) {
        super(context, dbname, null, version);
        DATABASE_NAME = dbname;
        TABLES_NAME = tables;
        for (int i = 0; i < TABLES_NAME.size(); i++) {
            String creatsql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME.get(i).get(TABLE)[0] + " (" + FIELD_id +
                    " INTEGER primary key autoincrement"
                    + handleField(TABLES_NAME.get(i).get(FIELD)) + " )";
            getWritableDatabase().execSQL(creatsql);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    private String handleField(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (String s : fields) {
            sb.append(", ");
            sb.append(s);
            sb.append(" text");
        }
        return sb.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor select(String table) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(table, null, null, null, null, null, null);
        return cursor;
    }

    public long insert(ContentValues cv, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        /* 将添加的值放入ContentValues */
        long row = db.insert(table, null, cv);

        needTime();
        return row;
    }

    private void needTime() {
        try {
            if(getDatabaseName().equals(MainDBHelper.DB_NAME)){
                MainApplication.mh.updateDBtime();
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    public void delete(int id, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_id + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(table, where, whereValue);
        needTime();
    }

    public void update(int id, ContentValues text, String table) {
        update(id,text,table,true);
    }

    void update(int id, ContentValues text, String table, boolean needtime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_id + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.update(table, text, where, whereValue);
        if(needtime) needTime();
    }

}
