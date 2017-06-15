//
// AndroidでBluetoothをON/OFF制御するサンプルコード
//
// BluetoothのON/OFFを制御する場合は、AndroidManifestに以下のPermissionが必要
//    <uses-permission android:name="android.permission.BLUETOOTH" />
//    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
//
// license:
//     Copyright (c) 2016 yoggy <yoggy0@gmail.com>
//     Released under the MIT license
//     http://opensource.org/licenses/mit-license.php;
//
package net.sabamiso.android.sample;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BluetoothOnOffActivity extends AppCompatActivity {

    Handler h = new Handler();
    BluetoothAdapter mBluetoothAdapter;

    TextView textViewBluetoothAdapterStatus;
    Button buttonON;
    Button buttonOFF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewBluetoothAdapterStatus = (TextView)findViewById(R.id.textViewBluetoothAdapterStatus);

        buttonON = (Button)findViewById(R.id.buttonON);
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonON();
            }
        });

        buttonOFF = (Button)findViewById(R.id.buttonOFF);
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonOFF();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    void startTimer() {
        h.post(timer_task);
    }

    void stopTimer() {
        h.removeCallbacks(timer_task);
    }

    Runnable timer_task = new Runnable() {
        @Override
        public void run() {
            onTimer();
            h.postDelayed(timer_task, 500);
        }
    };

    void onTimer() {
        updateGUIStatu();
    }

    void updateGUIStatu() {
        if (mBluetoothAdapter.isEnabled() == false) {
            textViewBluetoothAdapterStatus.setText("無効");
            buttonON.setEnabled(true);
            buttonOFF.setEnabled(false);
        }
        else {
            textViewBluetoothAdapterStatus.setText("有効");
            buttonON.setEnabled(false);
            buttonOFF.setEnabled(true);
        }
    }

    private void onButtonON() {
        // enableしてもmBluetoothAdapter.isEnabled()はすぐにtrueにならないので要注意。。
        mBluetoothAdapter.enable();
    }

    private void onButtonOFF() {
        mBluetoothAdapter.disable();
    }
}
