package net.abdulaziz.turorial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    //------------------------------------------------------------------------------------------
       int   Counter;
    TextView   Value;
     Button      Add;
     Button Subtract;
    //------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //------------------------------------------------------------------------------------------
        Counter  = 0;
        Value    = (TextView) findViewById(R.id.Value_TextView);
        Add      = (Button) findViewById(R.id.Add_Button);
        Subtract = (Button) findViewById(R.id.Subtract_Button);
        //------------------------------------------------------------------------------------------
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Counter++;
                Value.setText("Value= "+Counter);

            }
        });
        //------------------------------------------------------------------------------------------
        Subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Counter--;
                Value.setText("Value= "+Counter);
            }
        });

    }
}
