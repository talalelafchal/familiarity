package com.projet.consulting.lttd.m3appli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Clients extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.clients, menu);
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
