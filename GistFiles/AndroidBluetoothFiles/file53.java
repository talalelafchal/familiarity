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

import android.os.AsyncTask;
import android.widget.ListView;

public class Consultas extends AsyncTask<Integer, Void, List<Partido> >{
	
	private String fecha, hora, nombre, equipo_local, equipo_visitante, sc_local, sc_visitante, jugado;
	   private String [] fechas;
	  
	   
	   private String respuesta ="";
	   private HttpClient client;
	   private HttpGet request;
	   private HttpResponse response;
	   private  JSONObject jsonResponse;
	   
	   private ListView lv;
	   final List<Partido> partidos= new ArrayList<Partido>();
	@Override
	protected List<Partido>  doInBackground(Integer... arg0) {
		

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
        
	return partidos;
}
		
	protected List<Partido> onPostExecute(Void unused) {
		
		return partidos;
	}

}
