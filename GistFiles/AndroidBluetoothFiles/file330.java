package com.ortiz.sangredeportiva;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class PosicionesActivity extends Activity{

	private String pos, name, points, played, win, draw, lost, goals_score, goals_conc;
	private int num_pos=1;

   
  
   private String respuesta ="";
   private HttpClient client;
   private HttpGet request;
   private HttpResponse response;
   private JSONObject jsonResponse;
   private ListView lv;
   final List<Positions> posiciones= new ArrayList<Positions>();
   
   @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.list_posiciones);
	new getPosiciones().execute(1);
}
   
   private class getPosiciones extends AsyncTask<Integer, Void, Void>{

	
	protected Void doInBackground(Integer... arg0) {
		
		 lv = (ListView) findViewById(R.id.list_pos);
		
		client = new DefaultHttpClient();
        request = new HttpGet("http://desafiofem.com/index.php/tabla-de-posiciones?mobileApp=1");
		
        try {
			response = client.execute(request);
	        InputStream in = response.getEntity().getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder str = new StringBuilder();
	      
	        String line = null;
	        
	        while((line = reader.readLine()) != null)
	        {
	            str.append(line);
	        }
	        
	        respuesta = str.toString(); 
	        
	        
    	} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	   
	@Override
	protected void onPreExecute() {
		Toast.makeText(getApplicationContext(), "Cargando tabla", Toast.LENGTH_LONG).show();
	}
	
	protected void onPostExecute(Void unused) {
		try {
			jsonResponse = new JSONObject(respuesta);
		
        JSONObject resultObject = jsonResponse.getJSONObject("titles");
        JSONArray jsonarray = resultObject.getJSONArray("v_table");
       
        int lengthJsonArr = jsonarray.length();
  
        for (int j = 0; j < lengthJsonArr; j++) {
            JSONObject jsonChildNode = jsonarray.getJSONObject(j);
           pos=Integer.toString(num_pos);
            name = jsonChildNode.getString("name");
            points = jsonChildNode.getString("points");
            played = jsonChildNode.getString("played");
            win = jsonChildNode.getString("win");
            draw = jsonChildNode.getString("draw");
            lost = jsonChildNode.getString("lost");
            goals_score = jsonChildNode.getString("goals_score");
            goals_conc = jsonChildNode.getString("goals_conc");
            num_pos++;
            Positions p= new Positions(pos, name, points, played, win, draw, lost, goals_score, goals_conc);
            posiciones.add(p);
      
        }
      
            lv.setAdapter(new PosicionesAdapter(getApplicationContext(), R.layout.tabla_posic, posiciones));
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
        }
	}
	  
  
	   
	   
   }

	
	

