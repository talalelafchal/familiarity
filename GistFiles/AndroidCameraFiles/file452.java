package com.example.xubin.tablelayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MyActivity extends Activity {

    private String titleData[][]=new String[][]{
        {"id","name","email"},
        {"1","xubin","xubin@gmail.com"},
        {"2","xuin","xubin@gmail.com"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        TableLayout layout=new TableLayout(this);
        TableLayout.LayoutParams layoutParams=new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
        );
        layout.setBackgroundResource(R.drawable.ic_launcher);
        for (int x=0;x<this.titleData.length;x++){
            TableRow row=new TableRow(this);
            for (int y=0;y<this.titleData.length;y++){
                TextView text=new TextView(this);
                text.setText(this.titleData[x][y]);
                row.addView(text,y);
            }
            layout.addView(row);
        }
        setContentView(layout,layoutParams);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
