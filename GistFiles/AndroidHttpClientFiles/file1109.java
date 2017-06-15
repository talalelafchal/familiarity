package edu.ualr.cpsc7398.updatechecker.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.view.fragment.ConclusionFragment;
import edu.ualr.cpsc7398.updatechecker.view.fragment.InformationFragment;
import edu.ualr.cpsc7398.updatechecker.view.fragment.StatisticsFragment;
import edu.ualr.cpsc7398.updatechecker.view.fragment.WhatsGoingOnFragment;

public class MainActivity extends AppCompatActivity { //note, view click listener

    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setNavigationIcon(R.drawable.icon);
        ((TextView) findViewById(R.id.main_toolbar_title)).setText("Dynamic-Resource Update Checker!");

        //start fragments
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); //common transaction object

            InformationFragment fragment1 = new InformationFragment(); //Note: creating the instance of Fragment, a dynamic object
            //instantiating fragment via default constructor
            transaction.replace(R.id.sample_content_fragment1, fragment1);

            StatisticsFragment fragment2 = new StatisticsFragment();
            transaction.replace(R.id.sample_content_fragment2, fragment2);

            WhatsGoingOnFragment fragment3 = new WhatsGoingOnFragment();
            transaction.replace(R.id.sample_content_fragment3, fragment3);

            ConclusionFragment fragment4 = new ConclusionFragment();
            transaction.replace(R.id.sample_content_fragment4, fragment4);

            transaction.commit(); //common commit

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_checker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.GlossaryID:
                Intent intent1 = new Intent(this, GlossaryActivity.class);
                startActivity(intent1);
                return true;
            case R.id.AboutID:
                Intent intent2 = new Intent(this, AboutActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
