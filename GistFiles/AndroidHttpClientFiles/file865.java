package net.learn2develop.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.util.Log;


public class ResultsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);

        View backgroundimage = findViewById(R.id.background);
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(20);

		try {
			JSONArray j = new JSONArray(getIntent().getStringExtra("results"));

            Log.i("JSON","Elements in json: "+j.length());

            TextView matricula = (TextView) findViewById(R.id.matricula);
            matricula.setText(j.getJSONObject(0).get("matricula").toString());

            TextView carrera = (TextView) findViewById(R.id.carrera);
            carrera.setText(j.getJSONObject(0).get("carrera").toString());

            TextView puntaje = (TextView) findViewById(R.id.puntaje);
            puntaje.setText(j.getJSONObject(0).get("puntaje").toString());


            String mensaje, status;

            if (j.getJSONObject(0).get("status").toString().equals("A") ){
                status =  getString(R.string.StatusA);
                mensaje = getString(R.string.messageStatusA);
            }
            else{
                status = getString(R.string.StatusN);
                mensaje = getString(R.string.messageStatusN);
            }

            TextView tStatus = (TextView) findViewById(R.id.status);
            tStatus.setText(status);


            Toast.makeText(getBaseContext(),mensaje,Toast.LENGTH_LONG).show();
			/*Vector<String> subjects = new Vector<String>();
			Vector<String> scores = new Vector<String>();
			for(int i=0; i<j.length(); i++){
				subjects.add(j.getJSONObject(i).get("Subject").toString());
				scores.add(j.getJSONObject(i).get("Score").toString());
			}*/
			/*
			String[] subs = subjects.toArray(new String[]{});
			String[] scrs = scores.toArray(new String[]{});

			String[] fields = new String[] {"subject", "score"};
			int[] res = new int[] { R.id.score, R.id.subject};
			 
			List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
			 
			for(int i = 0; i < scrs.length; i++){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("subject", "" + subs[i]);
				map.put("score", "" + scrs[i]);
				data.add(map);
			}
			 
			SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.result_list_item, fields, res);
			
			ListView list = (ListView) findViewById(R.id.califList) ;
			list.setAdapter(adapter);
            */
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
			
		
		
		
		
		
		
			}

	

}
