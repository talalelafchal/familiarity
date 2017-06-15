

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TalErez on 26/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
    super(context,DBcontains.DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String command = "CREATE TABLE " + DBcontains.TABLE_NAME +
                "(" +DBcontains.MOVIE_ID + " INTEGER PRIMARY KEY autoincrement," + DBcontains.SBUJECT + " TEXT,"
                + DBcontains.BODY + " TEXT,"+DBcontains.IMDBID + " TEXT,"+ DBcontains.URL + " TEXT,"+DBcontains.YotubeCose + " TEXT,"+ DBcontains.Rating + " TEXT," +DBcontains.Seen + " TEXT )";

        try {
            db.execSQL(command);
        }
        catch (SQLiteException ex) {

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



    }
}
