package com.example.alvin.blogdua;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Dua extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dua);
        //menerapkan tool bar sesuai id toolbar | ToolBarAtas adalah variabel buatan sndiri
        Toolbar ToolBarAtas = (Toolbar)findViewById(R.id.toolbar_dua);
        setSupportActionBar(ToolBarAtas);
        ToolBarAtas.setLogo(R.mipmap.ic_launcher);
        ToolBarAtas.setLogoDescription(getResources().getString(R.string.app_name));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menerapkan menu terpilih di menu-> empat.xml
        getMenuInflater().inflate(R.menu.menu_dua, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_file) {
            Toast.makeText(Dua.this, "Ini menu Setting", Toast.LENGTH_LONG).show();
            return true;
        }
        else if(id == R.id.action_camera) {
            Toast.makeText(Dua.this, "Take Picture", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.action_save){
            Toast.makeText(Dua.this, "Save Data", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
