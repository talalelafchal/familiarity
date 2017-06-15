package gloird.fr.testspinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    static Spinner spinner = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<classe> list = new ArrayList<classe>();

        list.add(new classe(1,"test1",20));
        list.add(new classe(2,"test2",30));
        list.add(new classe(3,"test3",40));
        list.add(new classe(4,"test4",50));

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,list);

        spinnerArrayAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void toastState( String prefix, classe st )
    {
        if ( st != null )
        {
            String desc = "Event: " + prefix + "\nnom: " + st.getNom();
            desc += "\nNB: " + st.getNb() + "\nid: " + String.valueOf( st.getId() );
            Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        // Get the currently selected State object from the spinner
        classe st = (classe)spinner.getSelectedItem();

        // Show it via a toast
        toastState( "onItemSelected", st );
    }

    public void onNothingSelected(AdapterView<?> parent )
    {
    }

    // This click routine is simply here to show you how to get the current spinner item outside of
    // a spinner generated event.  You'll notice that it is identical to the code use in the
    // onItemSelected event.
    public void onClick(View v )
    {
        // Get the currently selected State object from the spinner
        classe st = (classe)spinner.getSelectedItem();

        // Show it via a toast
        toastState( "onClick", st );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
