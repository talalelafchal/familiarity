

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.banner.Banner;
import com.startapp.android.publish.banner.BannerListener;

public class editFormBrowser extends AppCompatActivity {


    ListView list;
    MyCustomeCursorAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppSDK.init(.this,"00000",true);
        setContentView(R.layout.activity_edit_form_browser);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.edit_toolbar2);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("");

        TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title2);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Milkshake.ttf");
        mTitle.setTypeface(custom_font);

        mTitle.setTextSize(25);

        list=(ListView)findViewById(R.id.listView);


        DBHelper helps = new DBHelper(this);
        DBcommend db1 = new DBcommend(this);
        Cursor c = db1.GetMovies();

        adapter1=new MyCustomeCursorAdapter(this,c);
        list.setAdapter(adapter1);

        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // String selecedItem=(String)((TextView)view).getText();

                //Toast.makeText(MainActivity.this,"hello"+selecedItem , Toast.LENGTH_SHORT).show();

                Cursor c= (Cursor)adapter1.getItem(position);
                String Ssub=c.getString(c.getColumnIndex(DBcontains.SBUJECT));
                String Bbody=c.getString(c.getColumnIndex(DBcontains.BODY));
                String url=c.getString(c.getColumnIndex(DBcontains.URL));
                String youtu=c.getString(c.getColumnIndex(DBcontains.YotubeCose));
                String imdb=c.getString(c.getColumnIndex(DBcontains.IMDBID));
                String rat=c.getString(c.getColumnIndex(DBcontains.Rating));
                String Id=c.getString(c.getColumnIndex("_id"));


                Intent intent=new Intent(editFormBrowser.this,editmovie.class);
                intent.putExtra("subjectt",Ssub);
                intent.putExtra("body",Bbody);
                intent.putExtra("url",url);
                intent.putExtra("youtube",youtu);
                intent.putExtra("imdb",imdb);
                intent.putExtra("rat",rat);
                startActivity(intent);

            }
        });

        Banner ban=(Banner)findViewById(R.id.startAppBannerBro);
        ban.setBannerListener(new BannerListener() {
            @Override
            public void onReceiveAd(View view) {

            }

            @Override
            public void onFailedToReceiveAd(View view) {

            }

            @Override
            public void onClick(View view) {

            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorits, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.searchagain)
        {
            Intent inter=new Intent(editFormBrowser.this,SearchMovie.class);
            startActivity(inter);
        }

        else if(item.getItemId()==R.id.delete)
        {
            DBcommend db1 = new DBcommend(this);
            db1.deliteMovieList();
            adapter1.notifyDataSetChanged();
            this.onResume();
        }

        else if(item.getItemId()==R.id.exit){
            finish();

        }

        else if(item.getItemId()==R.id.home){
            Intent in=new Intent(editFormBrowser.this,Popular.class);
            startActivity(in);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.moviemenu, menu);

    }

    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo inf=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        Cursor c= (Cursor)adapter1.getItem(inf.position);
        final String Ssub=c.getString(c.getColumnIndex(DBcontains.SBUJECT));
        final String Bbody=c.getString(c.getColumnIndex(DBcontains.BODY));
        final String url=c.getString(c.getColumnIndex(DBcontains.URL));
        final String Id=c.getString(c.getColumnIndex("_id"));
        final int seen=c.getInt(c.getColumnIndex(DBcontains.Seen));
        final String Rating=c.getString(c.getColumnIndex(DBcontains.Rating));
        final String youtibecode=c.getString(c.getColumnIndex(DBcontains.YotubeCose));
        final String imdbid=c.getString(c.getColumnIndex(DBcontains.IMDBID));

         if(item.getItemId()==R.id.deliete)
        {
            DBHelper help=new DBHelper(this);
            help.getWritableDatabase().delete(DBcontains.TABLE_NAME, "_id=" + Id, null);
            this.onResume();
        }

         else if(item.getItemId()==R.id.seeneitem){

             PopMovies m=new PopMovies(Ssub,url);
             m.setViewmovie(Bbody);
             m.setSeen("1");
             m.setRating(Rating);
             m.setIdmovie(imdbid);
             m.setYoutubecode(youtibecode);
             String id=c.getString(c.getColumnIndex("_id"));



             DBHelper help=new DBHelper(this);
             DBcommend db1=new DBcommend(this);
             db1.update(m,id);
             adapter1.notifyDataSetChanged();
             this.onResume();

             //   help.getWritableDatabase().delete(DBcontains.TABLE_NAME, "_id=" + Id, null);


         }


        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {

        DBcommend db1=new DBcommend(this);

        Cursor c = db1.GetMovies();
        adapter1=new MyCustomeCursorAdapter(this,c);
        list.setAdapter(adapter1);

        super.onResume();
    }
}
