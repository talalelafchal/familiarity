public class DatabaseConnection extends SQLiteOpenHelper {

    // Database Information.
    private static final String DATABASE_NAME = "BlogPost";
    private static final int DATABASE_VERSION = 1;
    // Table Name.
    private static final String TABLE_BLOGPOSTS = "blogposts";
    // Blog Post Table Columns, these map to the public fields on the BlogPost class.
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    // Singleton Instance.
    private static DatabaseConnection ourInstance;
    // The ID of the next row number, used for Insertions.
    private static long NEXT_ROW_ID_NUMBER;

    /**
     * Simple Constructor for Database Connection
     */
    private DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setupNextRowIdNumber();
    }

    /**
     * Allows retrieval of the Singleton Instance,
     * in the app we'll call DatabaseConnection.getInstance(getApplicationContext()) to get the
     * instance.
     */
    public static synchronized DatabaseConnection getInstance(Context context) {
        // Initialise the instance if it's null.
        if (ourInstance == null) ourInstance = new DatabaseConnection(context);

        return ourInstance;
    }

    /**
     * Used by the Android System to Create the Database Initially.
     *
     * @param database The Database Instance from Android System.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {

        // SQL Query for creating the table.
        final String CREATE_BLOGPOSTS_TABLE = "CREATE TABLE " + TABLE_BLOGPOSTS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_TITLE + " TEXT)";
        database.execSQL(CREATE_BLOGPOSTS_TABLE);
    }

    /**
     * Used by the Android System to Upgrade the database version.
     *
     * @param database   The Database Instance from Android System.
     * @param oldVersion The Old version of the Database.
     * @param newVersion The New version of the Database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // Drop the older table, if there's a newer table, and create the newer table.
        if (newVersion > oldVersion) {
            // SQL Query for Deleting (dropping) the table.
            final String DROP_BLOGPOSTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_BLOGPOSTS;
            database.execSQL(DROP_BLOGPOSTS_TABLE);

            onCreate(database);
        }
    }
