package br.edu.unidavi.tomararedearduino_1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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
