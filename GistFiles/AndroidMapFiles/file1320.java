package fr.ecosyndic.ecosyndic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MentionsActivity extends Activity {
    String Newline = System.getProperty("line.separator");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentions);
       TextView text= (TextView) findViewById(R.id.textView);
        TextView info= (TextView) findViewById(R.id.informations);
        info.setText("Conformément aux dispositions des articles 6–III et 19 de la Loi n° 2004-575 du 21 juin 2004"+Newline +
                 " pour la Confiance dans l’économie numérique, dite L.C.E.N., nous portons à la connaissance "+Newline+
                    "des utilisateurs et visiteurs de l'application d'Ecosyndic les informations suivantes :"+Newline
               );
        TextView mt= (TextView) findViewById(R.id.mention);
        mt.setText("Statut du propriétaire : societe"+Newline +
                "Préfixe : SARL"+Newline +
                "Nom de la Société : Ecosyndic"+Newline+
                "Responsables: Aurore CHAOPIN - Allyson MOREAU"+Newline+
                "Adresse: 39 Rue du Faubourg Poissonnière, 75009 Paris"+Newline+
                 "Téléphone:01 55 33 12 80"+Newline+
                  "Capital: 7000€"+Newline+
                   "SIRET:515 294 767 00030"+Newline+
                   "Adresse de courrier électronique: contact(at)ecosyndic.fr"+Newline+ Newline+
                     "Créatrice de l'application: Fanny BATCHO");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_telephone, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        MentionsActivity.this.startActivity(new Intent(MentionsActivity.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

}
