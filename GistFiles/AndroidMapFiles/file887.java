package com.winnerawan.futsalmatch;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.winnerawan.helper.SQLiteHandler;
import com.winnerawan.helper.SessionManager;

import java.util.HashMap;

import at.markushi.ui.CircleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {
    @InjectView(R.id.tool_bar) Toolbar toolbar;
    @InjectView(R.id.floatingButton) CircleButton fButton;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView txtName,txtEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.CircularFloatingActionButton);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fButton = (CircleButton) findViewById(R.id.floatingButton);
        SubActionButton.Builder itemButton = new SubActionButton.Builder(this);
        ImageView icFButton_0 = new ImageView(this);
        ImageView icFButton_1 = new ImageView(this);
        ImageView icFButton_2 = new ImageView(this);
        icFButton_0.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        icFButton_1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));
        icFButton_2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_light));

        SubActionButton btn_a = itemButton.setContentView(icFButton_0).build();
        SubActionButton btn_b = itemButton.setContentView(icFButton_1).build();
        SubActionButton btn_c = itemButton.setContentView(icFButton_2).build();
        FloatingActionMenu bfMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(btn_a)
                .addSubActionView(btn_b)
                .addSubActionView(btn_c)
                .attachTo(fButton)
                .build();
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
// enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
// enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
// set the transparent color of the status bar, 0% darker
        tintManager.setTintColor(Color.parseColor("#00000000"));
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        txtName.setText(name);
        txtEmail.setText(email);
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(SetProfile.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_profile, menu);
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
