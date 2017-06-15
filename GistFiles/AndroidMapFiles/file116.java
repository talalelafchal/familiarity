package jose.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity {

    Button firstButton;
    Button secondButton;
    Button thirdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstButton = (Button) findViewById(R.id.first_button);
        secondButton = (Button) findViewById(R.id.second_button);
        thirdButton = (Button) findViewById(R.id.third_button);

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(170, 80);
        layoutParams.setMargins(150, 130, 0, 0);
        firstButton.setLayoutParams(layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(180, 80);
        layoutParams.setMargins(650, 50, 0, 0);
        secondButton.setLayoutParams(layoutParams);

        layoutParams = new RelativeLayout.LayoutParams(220, 80);
        layoutParams.setMargins(220, 420, 0, 0);
        thirdButton.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}