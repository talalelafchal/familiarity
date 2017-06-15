package andrej.jelic.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by Korisnik on 25.6.2015..
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static SQLiteDatabase database;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "studentManager";

    // Table Names
    private static final String TABLE_ACTIVE = "CREATE TABLE " + DatabasesContract.FeedActiveDatabase.TABLE_NAME +
            " (" + DatabasesContract.FeedActiveDatabase.COLUMN_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabasesContract.FeedActiveDatabase.COLUMN_STUDENT + TEXT_TYPE + COMMA_SEP +
            DatabasesContract.FeedActiveDatabase.COLUMN_ATTEND_TIME + TEXT_TYPE + " )";

    private static final String TABLE_FINISHED = "CREATE TABLE " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME +
            "( " + DatabasesContract.FeedFinishedDatabase.COLUMN_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT + TEXT_TYPE + COMMA_SEP +
            DatabasesContract.FeedFinishedDatabase.COLUMN_ATTEND_TIME + TEXT_TYPE + COMMA_SEP +
            DatabasesContract.FeedFinishedDatabase.COLUMN_LEAVE_TIME + TEXT_TYPE + " )";

    private static final String TABLE_HISTORY = "CREATE TABLE " + DatabasesContract.HistoryDatabase.TABLE_NAME + "( " +
            DatabasesContract.HistoryDatabase.COLUMN_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabasesContract.HistoryDatabase.COLUMN_TABLENAME + TEXT_TYPE + " )";


    private static final String SQL_DELETE_ENTRIES_ACTIVE =
            "DROP TABLE IF EXISTS " + DatabasesContract.FeedActiveDatabase.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_FINISHED =
            "DROP TABLE IF EXISTS " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;

    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + DatabasesContract.HistoryDatabase.TABLE_NAME;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        this.database = db;

        db.execSQL(TABLE_ACTIVE);
        db.execSQL(TABLE_FINISHED);
        db.execSQL(TABLE_HISTORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_ACTIVE);
        db.execSQL(SQL_DELETE_ENTRIES_FINISHED);
        db.execSQL(SQL_DELETE_HISTORY);

        onCreate(db);
    }

    public void addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT, student.getStudent());

        values.put(DatabasesContract.FeedFinishedDatabase.COLUMN_ATTEND_TIME, student.getAttendTime());

        values.put(DatabasesContract.FeedFinishedDatabase.COLUMN_LEAVE_TIME, student.getLeaveTime());

        db.insert(DatabasesContract.FeedFinishedDatabase.TABLE_NAME, null, values);
    }

    public void addStudent(ActiveStudent activeStudent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabasesContract.FeedActiveDatabase.COLUMN_STUDENT, activeStudent.getStudent());

        values.put(DatabasesContract.FeedActiveDatabase.COLUMN_ATTEND_TIME, activeStudent.getAttendTime());

        db.insert(DatabasesContract.FeedActiveDatabase.TABLE_NAME, null, values);
    }

    public List<ActiveStudent> getActiveStudents() {
        List<ActiveStudent> activeStudentList = new ArrayList<ActiveStudent>();

        String selectQuery = "SELECT * FROM " + DatabasesContract.FeedActiveDatabase.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ActiveStudent activeStudent = new ActiveStudent();
                activeStudent.setID(Integer.parseInt(cursor.getString(0)));
                activeStudent.setStudent(cursor.getString(1));
                activeStudent.setAttendTime(cursor.getString(2));
                activeStudentList.add(activeStudent);
            } while (cursor.moveToLast());
        }

        return activeStudentList;
    }

    public Cursor getActiveCursor() {

        String selectQuery = "SELECT " + DatabasesContract.FeedActiveDatabase.COLUMN_STUDENT + " AS " + DatabasesContract.FeedActiveDatabase._ID + ", * FROM " + DatabasesContract.FeedActiveDatabase.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    public Cursor getCursor() {
        String selectQuery = "SELECT " + DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT + " AS " + DatabasesContract.FeedFinishedDatabase._ID + ", * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;

        Log.e(TAG, "select Query: " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    public Cursor getCursor(String tableName) {
        String selectQuery = "SELECT " + DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT
                + " AS " + DatabasesContract.FeedFinishedDatabase._ID + ", * FROM " + tableName;

        Log.e(TAG, "select Query: " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }


    public int getActiveStudentsCount() {
        String countQuery = " SELECT * FROM " + DatabasesContract.FeedActiveDatabase.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<Student>();

        String selectQuery = "SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setID(Integer.parseInt(cursor.getString(0)));
                student.setStudent(cursor.getString(1));
                student.setAttendTime(cursor.getString(2));
                student.setLeaveTime(cursor.getString(3));
                studentList.add(student);
            } while (cursor.moveToLast());
        }
        return studentList;
    }

    public int getAllStudentsCount() {
        String countQuery = " SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int updateStudent(Student student) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabasesContract.FeedFinishedDatabase.COLUMN_LEAVE_TIME, student.getLeaveTime());

        return db.update(DatabasesContract.FeedFinishedDatabase.TABLE_NAME, values,
                DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT + " = ?",
                new String[]{String.valueOf(student.getStudent())});
    }

    public void deleteStudent(ActiveStudent activeStudent) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabasesContract.FeedActiveDatabase.TABLE_NAME,
                DatabasesContract.FeedActiveDatabase.COLUMN_STUDENT + " = ?",
                new String[]{String.valueOf(activeStudent.getStudent())});
    }

    public void clearActiveStudents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabasesContract.FeedActiveDatabase.TABLE_NAME, null, null);
    }

    public void clearAllStudents() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabasesContract.FeedFinishedDatabase.TABLE_NAME, null, null);
    }

    public boolean checkStudent(Student student) {

        String compare = student.getStudent();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME + " WHERE "
                + DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT + " = '" + compare + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
           // cursor.close();
            Log.e(TAG, "Nema studenta u tablici");
            return true;
        } else {
            //cursor.close();
            Log.e(TAG, "Student je vec u tablici");
            return false;
        }
    }

    public boolean checkActiveStudent(ActiveStudent activeStudent) {

        String compareActive = activeStudent.getStudent();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM " + DatabasesContract.FeedActiveDatabase.TABLE_NAME + " WHERE "
                + DatabasesContract.FeedActiveDatabase.COLUMN_STUDENT + " = '" + compareActive + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
             cursor.close();
            Log.e(TAG, "Nema studenta u tablici");
            return true;
        } else {
            cursor.close();
            Log.e(TAG, "Student je vec u tablici");
            return false;
        }
    }

    public String composeJSONfromSQLite() {

        ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("student", cursor.getString(1));
                map.put("attend_time", cursor.getString(2));
                map.put("leave_time", cursor.getString(3));
                studentList.add(map);
            } while (cursor.moveToNext());
            db.close();
            cursor.close();
        }
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(studentList);

    }

    public String composeJSONfromSQLite(String tableName) {

        ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + tableName;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("student", cursor.getString(1));
                map.put("attend_time", cursor.getString(2));
                map.put("leave_time", cursor.getString(3));
                studentList.add(map);
            } while (cursor.moveToNext());
            db.close();
            cursor.close();
        }
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(studentList);

    }

    public Boolean createTable(String tableName) {

        SQLiteDatabase db = this.getReadableDatabase();

        String NEW_TABLE = "CREATE TABLE " + tableName +
                " (" + DatabasesContract.FeedFinishedDatabase.COLUMN_KEY_ID + " INTEGER PRIMARY KEY," +
                DatabasesContract.FeedFinishedDatabase.COLUMN_STUDENT + TEXT_TYPE + COMMA_SEP +
                DatabasesContract.FeedFinishedDatabase.COLUMN_ATTEND_TIME + TEXT_TYPE + COMMA_SEP +
                DatabasesContract.FeedFinishedDatabase.COLUMN_LEAVE_TIME + TEXT_TYPE + " )";

        Log.e(TAG, " NEW TABLE : " + NEW_TABLE);

        db.execSQL(NEW_TABLE);

        String query = " SELECT COUNT(*) FROM sqlite_master WHERE TYPE = 'table' AND name = '" + tableName + "'" ;
        Cursor cursor = db.rawQuery(query, null);

        return (cursor.getCount() > 0);

    }

    public boolean checkTable(Tables tables) {

        String compare = tables.getTableName();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = " SELECT * FROM " + DatabasesContract.HistoryDatabase.TABLE_NAME + " WHERE "
                + DatabasesContract.HistoryDatabase.COLUMN_TABLENAME + " = '" + compare + "'";
       // String query = " SELECT COUNT(*) FROM sqlite_master WHERE TYPE = 'table' AND name = '" + compare + "'" ;

        Cursor cursor = db.rawQuery(query, null);

        return (cursor.getCount() <= 0);
    }

    public void addTableName(Tables tables) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabasesContract.HistoryDatabase.COLUMN_TABLENAME, tables.getTableName());

        db.insert(DatabasesContract.HistoryDatabase.TABLE_NAME, null, values);
    }

    public boolean save(Tables tables) {

        SQLiteDatabase db = this.getWritableDatabase();

        String COPY = "INSERT INTO " + tables.getTableName() + " SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;
        db.execSQL(COPY);
        String countInFinished = " SELECT * FROM " + DatabasesContract.FeedFinishedDatabase.TABLE_NAME;
        String countInNew = " SELECT * FROM " + tables.getTableName();
        Cursor cursor = db.rawQuery(countInFinished, null);
        Cursor cursorNew = db.rawQuery(countInNew, null);

        Log.e(TAG, " Cursor: " + cursor.getCount());
        Log.e(TAG, " Cursor new: " + cursorNew.getCount());

        Log.e(TAG, "Compare cursors: " + (cursor.getCount() == cursorNew.getCount()));

        return (cursor.getCount() == cursorNew.getCount());

    }

    public Cursor getTables() {
        String selectQuery = "SELECT " + DatabasesContract.HistoryDatabase.COLUMN_TABLENAME
                + " AS " + DatabasesContract.FeedFinishedDatabase._ID + ", * FROM "
                + DatabasesContract.HistoryDatabase.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }
}
