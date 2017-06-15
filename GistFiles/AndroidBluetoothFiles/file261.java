package net.iseteki.example.konashiRAZRSample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity {

    private CheckBox mCheckBox;
    private KonashiManager konashiManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCheckBox = (CheckBox) findViewById(R.id.checkBox);

        konashiManager = new KonashiManager();
        konashiManager.setContext(this);
        konashiManager.startDiscovery();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, "Connected.", Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(KonashiManager.ACTION_CONNECT_COMPLETED));
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getStringExtra(KonashiManager.EXTRA_VALUE).equals("01")) {
                    mCheckBox.setChecked(true);
                }
                else {
                    mCheckBox.setChecked(false);
                }
            }
        }, new IntentFilter(KonashiManager.ACTION_VALUE_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        konashiManager.disconnect();;
    }
}
