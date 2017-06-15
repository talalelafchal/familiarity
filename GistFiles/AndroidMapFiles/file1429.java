package org.mel.stats;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.jar.JarOutputStream;

public class StatsActivity extends Activity {

    TextView tvDisp;
    EditText etName;
    ToggleButton togButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tvDisp = (TextView) findViewById(R.id.tvDisplay);

        etName = (EditText) findViewById(R.id.etName);

        togButton = (ToggleButton) findViewById(R.id.togButton);

        Button btHi = (Button) findViewById(R.id.btHi);

        MyListener ml = new MyListener();
        btHi.setOnClickListener(ml);
        togButton.setOnClickListener(ml);


    }

    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btHi) {
                if (togButton.isChecked())
                    tvDisp.setText(Greeting.HELLO + " " + etName.getText());
                else{
                    tvDisp.setText(Greeting.SALUTON_MONDO + " " + etName.getText());
                }
            }
        }
    }

    private class Greeting {
        public static final String HELLO = "Hello!";
        public static final String SALUTON_MONDO = "Saluton Mondo!";
    }
}
