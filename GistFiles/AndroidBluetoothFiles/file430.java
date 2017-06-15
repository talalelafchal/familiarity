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

public class ManejoGoleadoresActivity extends Activity{
	
	private String nombre, apellido, goles;
	
	private String respuesta ="";
	   private HttpClient client;
	   private HttpGet request;
	   private HttpResponse response;
	   private ListView lv;
	   final List<Goleador> goleadores= new ArrayList<Goleador>();
	   private JSONArray array;
	   
	   protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.list_goleadores);
			new getGoleadores().execute(1);
		}
	   
	   private class getGoleadores extends AsyncTask<Integer, Void, Void>{

			@Override
			protected Void doInBackground(Integer... arg0) {
				
				lv = (ListView) findViewById(R.id.list_goles);
				
				 client = new DefaultHttpClient();
			        
			        request = new HttpGet("http://desafiofem.com/index.php/tabla-de-posiciones?mobileApp=2");
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
				Toast.makeText(getApplicationContext(), "Cargando goleadores", Toast.LENGTH_LONG).show();
			}
			
			protected void onPostExecute(Void unused) {
				 try {
					array = new JSONArray(respuesta);
				
				  
			        for (int j = 0; j < 10; j++) {
			        	 JSONObject jsonChildNode = array.getJSONObject(j);
			            nombre = jsonChildNode.getString("Nombre");
			            apellido = jsonChildNode.getString("Apellido");
			            goles = jsonChildNode.getString("Goles");
			       
			            String name_goleador = nombre+" "+apellido;
			            Goleador g = new Goleador(name_goleador, goles);
			            System.out.println("trae de parsear goleadores "+g.toString());
			            goleadores.add(g);
			            System.out.println("to string de goleadores: "+goleadores.toString());
		    	        
			        }
			        
			        lv.setAdapter(new GoleadoresAdapter(getApplicationContext(),R.layout.tabla_goleadores, goleadores));
				 } catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
				        
			}
			   
		   }

}
