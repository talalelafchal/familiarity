package id.co.lazystudio.popularmovies.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Eko on 11/4/2016.
 */

public class MovieContract {

    // Content authority digunakan untuk membuat URI yang digunakan aplikasi
    // untuk menghubungi content provider
    public static final String CONTENT_AUTHORITY = "id.co.lazystudio.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    /* Kelas inner yang mendefinisikan isi tabel dari tabel movie */
    public static final class MovieEntry implements BaseColumns {

        private static final String LOG_TAG = MovieEntry.class.getSimpleName();

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = "id_movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        // Uri untuk insert data
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Uri base untuk list movie
        public static Uri buildMovie() {
            return CONTENT_URI.buildUpon().build();
        }

        public static String getMoviesFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
