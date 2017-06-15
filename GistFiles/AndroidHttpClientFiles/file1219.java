
package com.android.demo.notepad3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class NotesDbAdapter {

    public static final String route_routeID = "routeID";
    public static final String route_agencyID = "agencyID";
    public static final String route_shortName = "shortName";
    public static final String route_longName = "longName";
    public static final String route_routeType = "routeType";
    public static final String route_ROWID = "_id";
     
    public static final String trip_routeID = "routeID";
    public static final String trip_serviceID = "serviceID";
    public static final String trip_tripID = "tripID";
    public static final String trip_directionID = "directionID";
    public static final String trip_blockID = "blockID";
    public static final String trip_shapeID = "shapeID";
    public static final String trip_ROWID = "_id";
    
    public static final String stopTime_tripID = "tripID";
    public static final String stopTime_arrivalTime = "arrivalTime";
    public static final String stopTime_departureTime = "departureTime";
    public static final String stopTime_stopID = "stopID";
    public static final String stopTime_stopSequence = "stopSequence";
    public static final String stopTime_ROWID = "_id";
    
    public static final String stop_stopID = "stopID";
    public static final String stop_stopName = "stopName";
    public static final String stop_ROWID = "_id";
    
    public static final String v_file = "file";
    public static final String v_version = "versionNum";
    public static final String v_ROWID = "_id";
    
    public static final String checkV_file = "file";
    public static final String checkV_version = "versionNum";
    public static final String checkV_ROWID = "_id";
    
    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table Routes (_id integer primary key autoincrement, "
        + "routeID text, agencyID text, shortName text, longName text, routeType text);";
    
    private static final String DATABASE_CREATE2 =
        "create table Trips (_id integer primary key autoincrement, "
        + "routeID text, serviceID text, tripID text, directionID text, blockID text, shapeID text);";
    
    private static final String DATABASE_CREATE3 =
        "create table StopTimes (_id integer primary key autoincrement, "
        + "tripID text, arrivalTime text, departureTime text, stopID text, stopSequence text);";
    
    private static final String DATABASE_CREATE4 =
        "create table Stops (_id integer primary key autoincrement, "
        + "stopID text, stopName text);";
    
    private static final String DATABASE_CREATE5 =
        "create table Versions (_id integer primary key autoincrement, "
        + "file text, versionNum text);";
    
    private static final String DATABASE_CREATE6 =
        "create table CheckVersions (_id integer primary key autoincrement, "
        + "file text, versionNum text);";




    private static final String DATABASE_NAME = "MyData";
    private static final String DATABASE_TABLE_Routes = "Routes";
    private static final String DATABASE_TABLE_Trips = "Trips";
    private static final String DATABASE_TABLE_StopTimes = "StopTimes";
    private static final String DATABASE_TABLE_Stops = "Stops";
    private static final String DATABASE_TABLE_Versions = "Versions";
    private static final String DATABASE_TABLE_CheckVersions = "CheckVersions";
    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
       public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
            db.execSQL(DATABASE_CREATE3);
            db.execSQL(DATABASE_CREATE4);
            db.execSQL(DATABASE_CREATE5);
            db.execSQL(DATABASE_CREATE6);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long insertRoute(String routeID, String agencyID, String shortName, String longName, String routeType) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(route_routeID, routeID);
        initialValues.put(route_agencyID, agencyID);
        initialValues.put(route_shortName, shortName);
        initialValues.put(route_longName, longName);
        initialValues.put(route_routeType,routeType);

        return mDb.insert(DATABASE_TABLE_Routes, null, initialValues);
    }
    
    public long insertTrip(String routeID, String serviceID, String tripID, String directionID, String blockID,String shapeID) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(trip_routeID, routeID);
        initialValues.put(trip_serviceID, serviceID);
        initialValues.put(trip_tripID, tripID);
        initialValues.put(trip_directionID, directionID);
        initialValues.put(trip_blockID, blockID);
        initialValues.put(trip_shapeID, shapeID);

        return mDb.insert(DATABASE_TABLE_Trips, null, initialValues);
    }
    
    public long insertStopTime(String tripID, String arrivalTime, String departureTime,String stopID) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(stopTime_tripID, tripID);
        initialValues.put(stopTime_arrivalTime, arrivalTime);
        initialValues.put(stopTime_departureTime, departureTime);
        initialValues.put(stopTime_stopID, stopID);

        return mDb.insert(DATABASE_TABLE_StopTimes, null, initialValues);
    }
    
    public long insertStop(String stopID, String stopName) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(stop_stopID, stopID);
        initialValues.put(stop_stopName, stopName);

        return mDb.insert(DATABASE_TABLE_Stops, null, initialValues);
    }

    public long insertVersion(String file, String version) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(v_file, file );
        initialValues.put(v_version, version);

        return mDb.insert(DATABASE_TABLE_Versions, null, initialValues);
    }
    
    public long insertCheckVersion(String file, String version) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(checkV_file, file );
        initialValues.put(checkV_version, version);

        return mDb.insert(DATABASE_TABLE_CheckVersions, null, initialValues);
    }
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE_Routes, route_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    //Routes
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE_Routes, new String[] {route_ROWID,
                route_longName, route_shortName}, null, null, null, null, null);
    }
    
    
    
    //Trips based on RouteID and Direction
    public Cursor fetchTrips(Long passedRowId, int passedDir) {

         return mDb.query(DATABASE_TABLE_Trips, new String[] {trip_ROWID,
                trip_tripID}, passedRowId+"="+trip_routeID + " and " + passedDir+"="+trip_directionID,null, null, null, null);
         
    }
    
    
    //Stop times based on trip ID
   public Cursor fetchStopTimes(String passedTripID) {

     return mDb.query(DATABASE_TABLE_StopTimes, new String[] {stopTime_ROWID,
  		   stopTime_arrivalTime, stopTime_stopID}, passedTripID+"="+stopTime_tripID ,null, null, null, null);
     
 }
   //Stop name based on stopID
   public Cursor fetchStopName(String passedStopID) {

	     return mDb.query(DATABASE_TABLE_Stops, new String[] {stop_ROWID,
	    		   stop_stopName}, passedStopID+"="+stop_stopID ,null, null, null, null);
	     
	 }
   //Return currentVersion table
   public Cursor fetchCurrentVersions() {

	     return mDb.query(DATABASE_TABLE_Versions, new String[] {v_ROWID,
	    		   v_file, v_version}, null ,null, null, null, null);
	     
	 }
   // return latest version file read in table
   public Cursor fetchCheckVersions() {

	     return mDb.query(DATABASE_TABLE_CheckVersions, new String[] {checkV_ROWID,
	    		   checkV_file, checkV_version}, null ,null, null, null, null);
	     
	 }

    

    //Deletes then creates a new table specified by string passed
    public void EraseTable(String table){
   mDb.execSQL("DROP TABLE IF EXISTS " + table);
   
   if (table.equals("Routes")){mDb.execSQL(DATABASE_CREATE);}
   else if (table.equals("Trips")){mDb.execSQL(DATABASE_CREATE2);}
   else if (table.equals("StopTimes")){mDb.execSQL(DATABASE_CREATE3);}
   else if (table.equals("Stops")){mDb.execSQL(DATABASE_CREATE4);}
   else if (table.equals("checkVersions")){mDb.execSQL(DATABASE_CREATE6);}
    }
}
