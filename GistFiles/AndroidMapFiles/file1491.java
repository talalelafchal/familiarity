package fr.ecosyndic.ecosyndic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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


public class TelephoneActivity extends Activity {

    static final String[] Service = new String[]{"Comptable", "Gestion Locative", "Syndic"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephone);
        //Typeface face= Typeface.createFromAsset(getAssets(), "polices/Gabrielle.ttf");
        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        //TextView yourTextView = (TextView) findViewById(titleId);
        // yourTextView.setTypeface(face);
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
        String Newline = System.getProperty("line.separator");
        int t = 0;
        final Button valid = (Button) findViewById(R.id.valider);
        if (valid != null) {
            final Spinner spin = (Spinner) findViewById(R.id.spin);
            final TextView text = (TextView)
                    findViewById(R.id.information);
            if ((spin != null) && spin.isEnabled()) {
                switch (spin.getSelectedItemPosition()) {
                    case 0:
                        text.setText("Comprable " + Newline + "Responsable - Remy Samir " + Newline + "Tel : 01 55 33 12 83" + Newline + "Mail:comptable@ecosyndic.fr");
                        t = 1;
                        break;
                    case 1:
                        text.setText("Gestion Locative " + Newline + "Responsable - Valérie Elmira " + Newline + "Tel : 01 55 33 12 81" + Newline + "Mail: gestion@ecosyndic.fr");
                        t = 2;
                        break;
                    case 2:
                        text.setText("Syndic" + Newline + "Responsable - Aurore Chopin" + Newline + "Tel : 01 55 33 12 84" + Newline + "Mail:location@ecosyndic.fr");
                        t = 3;
                        break;

                    default:
                        break;
                }
                spin.setEnabled(false);
                valid.setText("Annuler");
                final Button appel = (Button) findViewById(R.id.appel);
                final Button env = (Button) findViewById(R.id.env);

                final int finalT = t;
                appel.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String n = null;

                        if (finalT == 1) {
                            n = "tel:0155331283";
                        } else if (finalT == 2) {
                            n = "tel:0155331281";
                        } else if (finalT == 3) {
                            n = "tel:0155331284";
                        }


                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(n.toString()));
                        startActivity(callIntent);
                    }
                });


                env.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String mail = null;

                        if (finalT == 1) {
                            mail = "comptable@ecosyndic.fr";
                        } else if (finalT == 2) {
                            mail = "gestion@ecosyndic.fr";
                        } else if (finalT == 3) {
                            mail = "location@ecosyndic.fr";
                        }


                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, mail);
                        i.putExtra(Intent.EXTRA_SUBJECT, "objet :");
                        i.putExtra(Intent.EXTRA_TEXT, " ");
                        try {
                            startActivity(Intent.createChooser(i, "Envoyer un mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(TelephoneActivity.this,
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
        getMenuInflater().inflate(R.menu.menu_telephone, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        TelephoneActivity.this.startActivity(new Intent(TelephoneActivity.this, MainActivity.class));
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



