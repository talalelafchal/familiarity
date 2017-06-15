package com.projet.consulting.lttd.m3appli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class Articles extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        DatabaseHandler DB = new DatabaseHandler(this);
        ListView listView = (ListView)findViewById(R.id.listViewArticles);
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Table", "Inserting ...");
        /*DB.addArticle(new Article("Item1", 1.1, "APACE", new Date(), null));
        DB.addArticle(new Article("Item2", 2.2, "APACE", new Date(), null));
        DB.addArticle(new Article("Item3", 3.3, "APACE", new Date(), null));
        DB.addArticle(new Article("Item4", 4.4, "APACE", new Date(), null));
        */
        // Reading all contacts
        Log.d("Reading", "Reading all articles...");
        List<Article> articles = DB.getAllArticles();
        ArrayAdapter<Article> adapterArticles = new ArrayAdapter<Article>(this, android.R.layout.simple_list_item_1, articles);
        listView.setAdapter(adapterArticles);
        /*for (Article ac : articles) {
            String log = "Id: "+ac.getId()+" ,Name: " + ac.getName() + " ,User: " + ac.getUser()+ " ,Price: " + ac.getPrice()+ " ,Create: " + ac.getCreate()+ " ,Update: " + ac.getUpdate();
            // Writing Contacts to log
            Log.d("Article", log);
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.articles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.Home:
                Intent Menu = new Intent(this,Home.class);
                this.startActivity(Menu);
                return true;
            case R.id.articles:
                Intent Articles = new Intent(this,Articles.class);
                this.startActivity(Articles);
                return true;
            case R.id.clients:
                Intent Clients = new Intent(this,Clients.class);
                this.startActivity(Clients);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
