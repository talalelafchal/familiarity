public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // mengosongkan semua data di database
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // deleteAllRecords dipanggil pada setiap test (agar terpenuhi kondisi clean state)
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    // test menambahkan data dan membacanya dengan content provider
    public void testInsertReadProvider() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        dbHelper.getWritableDatabase();

        ContentValues movieValues1 = new ContentValues();
        movieValues1.put(MovieEntry.COLUMN_ID, "1013");
        movieValues1.put(MovieEntry.COLUMN_TITLE, "Judul Film 3");
        movieValues1.put(MovieEntry.COLUMN_RELEASE_DATE, "3 Januari 2017");
        movieValues1.put(MovieEntry.COLUMN_OVERVIEW, "Overview film 3");
        movieValues1.put(MovieEntry.COLUMN_POSTER_PATH, "path poster");
        movieValues1.put(MovieEntry.COLUMN_BACKDROP_PATH, "path backdrop");
        movieValues1.put(MovieEntry.COLUMN_POPULARITY, 66);
        movieValues1.put(MovieEntry.COLUMN_VOTE_COUNT, 900);
        movieValues1.put(MovieEntry.COLUMN_VOTE_AVERAGE, 49.9);

        Uri movieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues1);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        assertTrue(movieInsertUri != null);


        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI, // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(movieCursor, movieValues1);


        // Now see if we can successfully query if we include the row id
        movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieUri(movieRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(movieCursor, movieValues1);

        dbHelper.close();
    }

    public void testGetType() {
        // content://id.co.lazystudio.popularmovies/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/id.co.lazystudio.popularmovies/movie
        assertEquals(MovieEntry.CONTENT_TYPE, type);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    long newsRowId;

    static ContentValues createMovieLoganValues(){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_ID, "1014");
        movieValues.put(MovieEntry.COLUMN_TITLE, "Logan");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "12 Februari 2017");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "Menceritakan tentang Wolverine");
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "path poster");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "path backdrop");
        movieValues.put(MovieEntry.COLUMN_POPULARITY, 77);
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, 900);
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 87.9);

        return  movieValues;

    }

    // Inserts movie data for the logan data set.
    public void insertLoganData() {

        ContentValues loganMovieValues = createMovieLoganValues();
        Uri newsInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, loganMovieValues);
        assertTrue(newsInsertUri != null);
    }

    // melakukan test update dan read movie
    public void testUpdateAndReadMovie() {
        insertLoganData();
        String newTitle = "Logan - Wolverine 2017";

        // Make an update to one value.
        ContentValues loganTitleUpdate = new ContentValues();
        loganTitleUpdate.put(MovieEntry.COLUMN_TITLE, newTitle);

        mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, loganTitleUpdate, null, null);

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues loganAltered = createMovieLoganValues();
        loganAltered.put(MovieEntry.COLUMN_TITLE, newTitle);

        TestDb.validateCursor(movieCursor, loganAltered);
    }

    // melakukan test penghapusan overview dan read movie
    public void testRemoveOverviewAndReadMovie() {
        insertLoganData();

        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,
                MovieEntry.COLUMN_OVERVIEW, null);

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues loganAltered = createMovieLoganValues();
        loganAltered.remove(MovieEntry.COLUMN_OVERVIEW);

        TestDb.validateCursor(movieCursor, loganAltered);
        int idx = movieCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW);
        assertEquals(4, idx);
        Log.v(LOG_TAG, "index: "+ idx);
    }

}