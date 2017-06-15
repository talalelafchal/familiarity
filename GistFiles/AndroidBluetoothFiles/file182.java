package com.ortiz.sangredeportiva;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;


public class SangreDeportivaSeminarioActivity extends Activity {
	 /**
	un activity es  una pantalla de la aplicacion practicamente, todas las pantallas que aparezcan en la app deben tener su activity 
	y las tenes que declarar en el androidmanifest.xml, que es un archivo donde se ponen las configuraciones basicas 
	*/
	
	   private String fecha_date, fecha, local, visitante;
	   private int year, month, day;
	   String [] fechas;
	   private boolean alerta=false;
	   private String respuesta ="";
	   
	   private HttpClient client;
	   private HttpGet request;
	   private HttpResponse response;
	   private TableLayout tabla;
	   private JSONObject jsonResponse;
	   
	   private TextView alerta_partidos;
	   private ImageButton b_calendario;
	   private ImageButton b_posiciones;
	   private ImageButton b_goleadores;
	   
	   final Calendar c = Calendar.getInstance();
	   private ListView lv;
	   final List<Partido> partidos= new ArrayList<Partido>();

  public static Intent i;
  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        //definis el layout con el cual va a arrancar la pantalla, los layout estan en la carpeta res/layout
        setContentView(R.layout.activity_principal);
        
        new getFechas().execute(1);
        
        b_calendario = (ImageButton)findViewById(R.id.bt_calendario);
        b_posiciones = (ImageButton)findViewById(R.id.bt_posiciones);
        b_goleadores = (ImageButton) findViewById(R.id.bt_goleadores);
        
        b_goleadores.setOnClickListener(new View.OnClickListener() {
			
     			@Override
     			public void onClick(View arg0) {
     				
     				// sirve para invocar a las activities , en este caso y ole digo que vaya desde esta clase a la de manejogoleadores
     				//se usa tmb para eventos, llamar a aplicaciones externas
     				i=new Intent(SangreDeportivaSeminarioActivity.this, ManejoGoleadoresActivity.class);
     				startActivity(i);
     				
     			}
     		});
        
        b_calendario.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				i=new Intent(SangreDeportivaSeminarioActivity.this, ManejoFixture.class);
				startActivity(i);
				
			}
		});
        
        b_posiciones.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				i=new Intent(SangreDeportivaSeminarioActivity.this, PosicionesActivity.class);
				startActivity(i);
				
			}
		});
  
    }
    /*
  
    permite realizar tareas de segundo plano, tiene un valor de entrada, uno de salida y un progreso, la parte que mas consuma memoria
    se realiza en el doinbackground, y lo que devuelve se maneja en el postexecute, en este caso esta mal (lo cambie erroneamente)
    en el postexecute deberia solamente setearle el adapter. todo se tendria que hacer en el doinbackground (tmp se puede realizar
    		cambios en la interfaz grafica desde el doinback.).
    */
    
	private class getFechas extends AsyncTask<Integer, Void, Void>{

		
		protected Void doInBackground(Integer... arg0) {
			
			//busco una lista con id:list que esta en el layout de activity_principal asi los detos que quiera mostrar
			//se van a representar como esa lista
			lv = (ListView) findViewById(R.id.list);
			
			
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH)+1;
			day = c.get(Calendar.DAY_OF_MONTH);
			
			fecha_date = year+"-"+month+"-"+day;
			
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
		       
	    	} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		return null;
		}
    	
    	
		protected void onPostExecute(Void unused) {
			
			alerta_partidos = (TextView)findViewById(R.id.alerta_partidos);
			  tabla = (TableLayout)findViewById(R.id.alerta_tabla);
             
              try {
				jsonResponse = new JSONObject(respuesta);
			
		        JSONArray jsonMainNode = jsonResponse.optJSONArray("matches");
		       
		        int lengthJsonArr = jsonMainNode.length();
		        
		        for (int j = 0; j < lengthJsonArr; j++) {
		            JSONObject jsonChildNode = jsonMainNode.getJSONObject(j);
		            fecha = jsonChildNode.getString("m_date");
		            
		            if(fecha.compareTo(fecha_date)==0){
		            	alerta=true;
		            	local = jsonChildNode.getString("home");
			            visitante = jsonChildNode.getString("away");
			            
		            Partido p = new Partido(local, visitante);
		            partidos.add(p);
		            }
		          
		        }
		        //seteo a traves de un listview  el adapter que cree (principaladapter) 
		        lv.setAdapter(new PrincipalAdapter(getApplicationContext(), partidos));
		        System.out.println("alertaaa: "+alerta);
		        if(alerta){
		        	//hago visible la parte de la tabla en el layput de la pantalla principal para que aparezcan los partidos
	              	tabla.setVisibility(TableLayout.VISIBLE);

	              }else{
	            
	              alerta_partidos.setVisibility(TextView.VISIBLE);
	              } 
		        
		        
              } catch (JSONException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
		}		
    	
    	
    }
}