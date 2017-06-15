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


public class ManejoFixture extends Activity{
	private String fecha, hora, nombre, equipo_local, equipo_visitante, sc_local, sc_visitante, jugado;
	   private String [] fechas;
	  
	   
	   private String respuesta ="";
	   private HttpClient client;
	   private HttpGet request;
	   private HttpResponse response;
	   private  JSONObject jsonResponse;
	   
	   private ListView lv;
	   ArrayList<Partido> partidos= new ArrayList<Partido>();
	   
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lis_fixr);
		new getCalendario().execute(1);
	}
	   
	   private class getCalendario extends AsyncTask<Integer, Void, Void>{

		
		@SuppressWarnings("unchecked")
		protected Void doInBackground(Integer... arg0) {
			lv = (ListView) findViewById(R.id.list_fixt);
			
			/*
		        client = new DefaultHttpClient();
		        
		        request = new HttpGet("http://desafiofem.com/index.php/fixture?mobileApp=1");
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
			        jsonResponse = new JSONObject(respuesta);
			        
			        JSONArray jsonMainNode = jsonResponse.optJSONArray("matches");
			     
			        
			        int lengthJsonArr = jsonMainNode.length();
			  
			        for (int j = 0; j < lengthJsonArr; j++) {
			            JSONObject jsonChildNode = jsonMainNode.getJSONObject(j);
			          
			            fecha = jsonChildNode.getString("m_date");
			            hora = jsonChildNode.getString("m_time");
			            nombre = jsonChildNode.getString("m_name");
			            equipo_local = jsonChildNode.getString("home");
			            equipo_visitante = jsonChildNode.getString("away");
			            sc_local = jsonChildNode.getString("score1");
			            sc_visitante = jsonChildNode.getString("score2");
			            jugado = jsonChildNode.getString("m_played");
			           
			            
			            fechas=nombre.split(" ");
			         if(jugado.equals("1")){
			        	 jugado = "final";
			         }else{
			        	 jugado = "-";
			         }
			         
			         Partido p= new Partido(fecha, hora, fechas[1], equipo_local, equipo_visitante, sc_local, sc_visitante, jugado);
			         partidos.add(p);
			        } 
		    	} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (JSONException e) {
					e.printStackTrace();
				}
		        */
			Consultas s = new Consultas();
		s.execute(1);
			return null;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(getApplicationContext(), "Cargando calendario", Toast.LENGTH_LONG).show();
		}
		
		protected void onPostExecute(Void unused) {
				
		        lv.setAdapter(new PartidosAdapter(getApplicationContext(), partidos));
			}
	   }
}
