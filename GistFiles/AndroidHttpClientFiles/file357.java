package com.matpompili.settle;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.matpompili.settle.R;

import org.w3c.dom.Text;

public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView filippica = (TextView) findViewById(R.id.welcome_text);
        filippica.setText("Settle è una applicazione, ancora in forte sviluppo " +
                "(quindi piena di bug), per trovare un'aula libera nella città universitaria.\n" +
                "È basata sulle segnalazioni degli utenti che, utilizzando Settle, " +
                "possono inviare aggiornamenti sullo stato delle diverse aule.\n" +
                "La valutazione sullo stato di un'aula è data dalla media delle segnalazioni " +
                "degli utenti nell'ultima ora, dando più peso a quelle più recenti.\n" +
                "Non tutte le facoltà e gli edifici sono per ora supportati, ma verrano aggiunti " +
                "con l'aumentare degli utenti.\n" +
                "Verranno rilasciati aggiornamenti periodici e l'applicazione all'avvio segnalerà " +
                "se state usando l'ultima versione disponibile.");
        Button gotIt = (Button) findViewById(R.id.button);
        gotIt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
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
