package fr.ecosyndic.ecosyndic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class EmailActivity extends Activity {

    static final String[] Service = new String[]{"Comptable", "Gestion", "Location et transaction", "Autre..."};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        final Spinner spin = (Spinner) findViewById(R.id.spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Service);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        final Button valid = (Button) findViewById(R.id.valider);
        valid.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Option();
            }
        });
    }

    public void Option() {

        int t = 0;
        final Button valid = (Button) findViewById(R.id.valider);
        if (valid != null) {
            final Spinner spin = (Spinner) findViewById(R.id.spin);
            final TextView text = (TextView)
                    findViewById(R.id.information);
            final TextView title = (TextView)
                    findViewById(R.id.titre);
            if ((spin != null) && spin.isEnabled()) {
                switch (spin.getSelectedItemPosition()) {
                    case 0:
                        title.setText("Comprable ");
                        text.setText("Responsable - Remy Samir");
                        t = 1;
                        break;
                    case 1:
                        title.setText("Gestion ");
                        text.setText("Responsable - Valérie Elmira");
                        t = 2;
                        break;
                    case 2:
                        title.setText("Location/Transaction ");
                        text.setText("Responsable - Aurore Chopin");
                        t = 3;
                        break;
                    case 3:
                        title.setText("Autre ");
                        text.setText("Accueil");
                        t = 4;
                        break;
                    default:
                        break;
                }
                spin.setEnabled(false);
                valid.setText("Annuler");
                final Button env = (Button) findViewById(R.id.env);


                final int finalT = t;
                env.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String n = null;

                        if (finalT == 1) {
                            n = "comptable@ecosyndic.fr";
                        } else if (finalT == 2) {
                            n = "gestion@ecosyndic.fr";
                        } else if (finalT == 3) {
                            n = "location@ecosyndic.fr";
                        } else if (finalT == 4) {
                            n = "contact@ecosyndic.fr";
                        }

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, n);
                        i.putExtra(Intent.EXTRA_SUBJECT, "objet :");
                        i.putExtra(Intent.EXTRA_TEXT, " ");
                        try {
                            startActivity(Intent.createChooser(i, "Envoyer un mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(EmailActivity.this,
                                    "There are no email clients installed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                });
            } else {
                spin.setEnabled(true);
                valid.setText("Valider");
                text.setText("Aucune sélection");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_email, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        EmailActivity.this.startActivity(new Intent(EmailActivity.this, MainActivity.class));
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



