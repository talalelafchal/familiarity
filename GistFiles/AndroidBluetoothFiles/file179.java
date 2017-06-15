package andrej.jelic.attendance;

import android.provider.BaseColumns;

/**
 * Created by Korisnik on 25.6.2015..
 */
public final class DatabasesContract {

    public DatabasesContract() {}

    public static abstract class FeedActiveDatabase implements BaseColumns {

        public static final String TABLE_NAME = "active_database";
        public static final String COLUMN_KEY_ID = "entry_ID";
        public static final String COLUMN_STUDENT = "student";
        public static final String COLUMN_ATTEND_TIME = "attendTime";
    }

    public static abstract class FeedFinishedDatabase implements BaseColumns {

        public static final String TABLE_NAME = "finished_database";
        public static final String COLUMN_KEY_ID = "entry_ID";
        public static final String COLUMN_STUDENT = "student";
        public static final String COLUMN_ATTEND_TIME = "attendTime";
        public static final String COLUMN_LEAVE_TIME = "leaveTime";
    }

    public static abstract class HistoryDatabase implements BaseColumns {

        public static final String TABLE_NAME = "history";
        public static final String COLUMN_KEY_ID = "entry_ID";
        public static final String COLUMN_TABLENAME = "tableName";
    }
}
