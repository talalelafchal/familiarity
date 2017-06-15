package fr.ecosyndic.ecosyndic;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
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


@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    Button bouton5 = null;
    Button bouton6 = null;
    Button bouton7 = null;
    Button quitter = null;
    Button ml=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bouton5 = (Button) findViewById(R.id.button5);
        bouton5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, ContactsActivity.class));

            }
        });
        bouton6 = (Button) findViewById(R.id.button6);
        bouton6.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, TelephoneActivity.class));
            }
        });
        bouton7 = (Button) findViewById(R.id.button7);
        // bouton7.setTypeface(face);
        bouton7.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ecosyndic.crypto-extranet.com/extranet/connexion/login"));
                startActivity(i);
            }
        });
        quitter = (Button) findViewById(R.id.quitter);
        quitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(3);
            }
        });
        ml = (Button) findViewById(R.id.ml);
        ml.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MentionsActivity.class));

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
    public void onDestroy() {
        super.onDestroy();


        System.exit(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            // actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return true;
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