public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        // Test data we're going to insert into the DB to see if it works.
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieEntry.COLUMN_ID, "1011");
        movieValues.put(MovieEntry.COLUMN_TITLE, "Judul Film 1");
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "1 Januari 2017");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "Overview film 1");
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, "path poster");
        movieValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "path backdrop");
        movieValues.put(MovieEntry.COLUMN_POPULARITY, 100);
        movieValues.put(MovieEntry.COLUMN_VOTE_COUNT, 900);
        movieValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 56.9);


        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue(movieRowId != -1);
        // A cursor is your primary interface to the query results.
        Cursor newsCursor = db.query(
                MovieEntry.TABLE_NAME, // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(newsCursor, movieValues);

        dbHelper.close();
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
            Log.v(LOG_TAG,idx+"");
        }
        valueCursor.close();
    }
}