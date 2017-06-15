package com.example.miguel.hechos_curiosos;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


public class HechosCuriosos extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hechos_curiosos);
        // Declare our View variables
        // Declare our View variables and assign the Views from the layout file
        final TextView factLabel;
        factLabel = (TextView) findViewById(R.id.HechoCuriosoTextView);
        final String hechos[] = getResources().getStringArray(R.array.frases);
        Button showFactButton = (Button) findViewById(R.id.button);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //When the button was clicked, the text going to update
                String fact = "";
                //Random message
                Random randomGenerator = new Random();
                int randomNumber = randomGenerator.nextInt(10);
                fact = Integer.toString(randomNumber);
                factLabel.setText(hechos[Integer.parseInt(fact)]);
            }
        };
        showFactButton.setOnClickListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hechos_curiosos, menu);
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
