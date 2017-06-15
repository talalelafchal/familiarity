package br.edu.unidavi.tomararedearduino_1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements OnClickListener {

    private Button LigarButton, DesligarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LigarButton = (Button) findViewById(R.id.LigarButton);
        DesligarButton = (Button) findViewById(R.id.DesligarButton);

        LigarButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread t = new Thread() {
                    public void run() {
                        try {
                            URL url = new URL("http://192.168.100.110/?rd=1");
                            URLConnection conn = url.openConnection();
                            // Get the response
                            BufferedReader rd = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));

                        } catch (Exception e) {
                            Log.e("BUTTON LIGAR", "ERRO");
                            e.printStackTrace();
                        }

                    }
                };
                t.start();
            }
        });

        DesligarButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Thread t = new Thread() {
                    public void run() {
                        try {
                            URL url = new URL("http://192.168.100.110/?rd=0");
                            URLConnection conn = url.openConnection();
                            // Get the response
                            BufferedReader rd = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));

                        } catch (Exception e) {
                            Log.e("BUTTON LIGAR", "ERRO");
                            e.printStackTrace();
                        }

                    }
                };
                t.start();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

}
