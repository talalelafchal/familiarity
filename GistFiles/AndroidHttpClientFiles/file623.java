

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tx = (TextView)findViewById(R.id.textView3);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/tttt.ttf");

        tx.setTypeface(custom_font);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(5000) ; //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(MainActivity.this,
                            Popular.class);
                    startActivity(i);

                    finish();
                }
            }
        };
        welcomeThread.start();
    }
    }

