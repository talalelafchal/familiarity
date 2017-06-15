

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by TalErez on 26/07/2016.
 */
public class DBcommend
{
    Context context;

    public DBcommend(Context context) {
        this.context = context;
    }

    public  void AddMovies (PopMovies movie){

        DBHelper helper=new DBHelper(context);

        ContentValues contentValues=new ContentValues();
        contentValues.put(DBcontains.SBUJECT,movie.getName());
        contentValues.put(DBcontains.BODY,movie.getViewmovie());
        contentValues.put(DBcontains.URL,movie.getURL());
        contentValues.put(DBcontains.Seen,movie.getSeen());
        contentValues.put(DBcontains.Rating,movie.getRating());
        contentValues.put(DBcontains.IMDBID,movie.getIdmovie());
        contentValues.put(DBcontains.YotubeCose,movie.getYoutubecode());


        helper.getWritableDatabase().insert(DBcontains.TABLE_NAME,null,contentValues);


    }

    public Cursor GetMovies(){

        DBHelper helpes=new DBHelper(context);

        Cursor tempTable=helpes.getReadableDatabase().rawQuery("SELECT * FROM movies",null);
        return tempTable;
    }

    public void deliteMovieList( ){

        DBHelper delet=new DBHelper(context);
        delet.getWritableDatabase().delete(DBcontains.TABLE_NAME,null,null);

    }

    public void update(PopMovies movie,String id)
    {
        DBHelper helper=new DBHelper(context);
        ContentValues contentValues=new ContentValues();
        contentValues.put(DBcontains.SBUJECT,movie.getName());
        contentValues.put(DBcontains.BODY,movie.getViewmovie());
        contentValues.put(DBcontains.URL,movie.getURL());
        contentValues.put(DBcontains.Seen,movie.getSeen());
        contentValues.put(DBcontains.Rating,movie.getRating());
        contentValues.put(DBcontains.IMDBID,movie.getIdmovie());
        contentValues.put(DBcontains.YotubeCose,movie.getYoutubecode());

        helper.getWritableDatabase().update(DBcontains.TABLE_NAME, contentValues, "_id=" + id, null);

    }

    public void deleteItem(String id){

        DBHelper help=new DBHelper(context);
        help.getWritableDatabase().delete(DBcontains.TABLE_NAME, "_id=" + id, null);
    }

}
