package andrej.jelic.attendance;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "Main activity; ";
    private BluetoothAdapter mBluetoothAdapter;
    private Button startNow;
    private Button startAtTime;
    private Button history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        setContentView(R.layout.activity_main);

        startNow = (Button) findViewById(R.id.button_start_now_main);
        startAtTime = (Button) findViewById(R.id.button_start_at_time_main);
        history = (Button) findViewById(R.id.history_button);
        startNow.setOnClickListener(this);
        startAtTime.setOnClickListener(this);
        history.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    @Override
    public void onClick(View v) {
       int id = v.getId();

        if (id == R.id.button_start_now_main) {
            Intent intent = new Intent(this, Started_now.class);
            this.startActivity(intent);
        }
        else if (id == R.id.button_start_at_time_main) {
            Intent intent = new Intent(this, Start_at_time.class);
            this.startActivity(intent);
        } else if (id == R.id.history_button) {
            Intent intent = new Intent(this, History.class);
            this.startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
