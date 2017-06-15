public class MovieDbHelper extends SQLiteOpenHelper {
    // Jika merubah skema database, versi database harus ditingkatkan.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Membuat tabel untuk menyimpan data movie
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // ID unik sebagai primary key
                MovieEntry.COLUMN_ID + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " FLOAT NOT NULL," +
                MovieEntry.COLUMN_VOTE_COUNT + " INT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " FLOAT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Digunakan untuk meng-upgrade database
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}