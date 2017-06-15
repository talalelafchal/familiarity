package com.irving.cares.webservices;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebServiceCauses {
	
	private HttpClient cliente;
	
	public WebServiceCauses(){
		cliente=new DefaultHttpClient();
	}
	
	public String[][] getDataCauses(){
		JSONArray array=consultarWebService();
		
		
		if(array!=null){
			
			ArrayList<String[]> listaExterna=new ArrayList<String[]>();
			
			for(int i=0;i<array.length();i++){
				String[] listaInterna=new String[10];
				try {
					JSONObject objetoJson=array.getJSONObject(i);
					listaInterna[0]=objetoJson.getString("cause_id");
					listaInterna[1]=objetoJson.getString("cause_type");
					listaInterna[2]=objetoJson.getString("name");
					listaInterna[3]=objetoJson.getString("title");
					listaInterna[4]=objetoJson.getString("cause_image");
					listaInterna[5]=objetoJson.getString("number_supporter");
					listaInterna[6]=objetoJson.getString("number_follower");
					listaInterna[7]=objetoJson.getString("days_remain");
					listaInterna[8]=objetoJson.getString("desc");
					listaInterna[9]=objetoJson.getString("thankyou");
					
					listaExterna.add(listaInterna);
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			String[][] data=new String[listaExterna.size()][10];
			for(int i=0;i<listaExterna.size();i++){
				String []temporalArray=listaExterna.get(i);
				for(int j=0;j<10;j++){
					data[i][j]=temporalArray[j];
				}
			}
			
			return data;
		}
		
		else 
			return null;

	}

	private JSONArray consultarWebService(){
		HttpGet get=new HttpGet("http://pones_aqui_tu_url_LOL");
		
		HttpResponse respuesta=null;

		try {
			 respuesta=cliente.execute(get);
		} catch (ClientProtocolException e) {
			Log.e("Tag", "Error en Protocolo en cliente");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e("Tag", "Error IO");
			e.printStackTrace();
			return null;
		}
		
		String response=null;
		
		try {
			 response=EntityUtils.toString(respuesta.getEntity());
		} catch (ParseException e) {
			Log.e("Tag", "Error en parser de respuesta");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			Log.e("Tag", "Error IO en respuesta");
			e.printStackTrace();
			return null;
		}
		
		if(response==null){
			Log.i("TAG", "La respuesta es null");
			return null;
		}
			
		
		Log.i("TAG", "La respuesta de es =" + response);
		
		JSONArray res=null;
		
		try {
			res=new JSONArray(response);
		} catch (JSONException e) {
			Log.e("Tag", "Error en parser JSON");
			e.printStackTrace();
			return null;
		}
		
		Log.i("TAG","RESPONSE: " + res);
		
		return res;
	}
}