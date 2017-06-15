package fr.ecosyndic.ecosyndic;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class ContactsActivity extends Activity implements View.OnTouchListener,
        View.OnClickListener {
    TextView title = null;
    TextView h1 = null;
    TextView h2 = null;
    TextView h3 = null;
    TextView adresse = null;
    TextView telemail = null;
    TextView horaires = null;
    Button env = null;
    Button appel = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        //Typeface face= Typeface.createFromAsset(getAssets(), "polices/Gabrielle.ttf");
        //int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        //TextView yourTextView = (TextView) findViewById(titleId);
        // yourTextView.setTypeface(face);
        title = (TextView) findViewById(R.id.title);
        h1 = (TextView) findViewById(R.id.h1);
        h2 = (TextView) findViewById(R.id.h2);
        h3 = (TextView) findViewById(R.id.h3);
        telemail = (TextView) findViewById(R.id.telemail);
        adresse = (TextView) findViewById(R.id.adresse);
        horaires = (TextView) findViewById(R.id.horaires);
        env = (Button) findViewById(R.id.env);
        env.setText("Envoyer un mail");
        env.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, "contact@ecosyndic.fr");
                i.putExtra(Intent.EXTRA_SUBJECT, "objet :");
                i.putExtra(Intent.EXTRA_TEXT, " ");
                try {
                    startActivity(Intent.createChooser(i, "Envoyer un mail..."));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(ContactsActivity.this,
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        appel = (Button) findViewById(R.id.appeler);
        appel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0155331280"));
                startActivity(callIntent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        ContactsActivity.this.startActivity(new Intent(ContactsActivity.this, MainActivity.class));
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
