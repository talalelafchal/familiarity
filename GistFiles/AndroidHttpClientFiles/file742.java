package com.irving.cares.webservices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebServiceDoTransaction {
	
	private HttpClient cliente;

	public WebServiceDoTransaction() {
		cliente=new DefaultHttpClient();
	}
	
	private String consultarWebService(String strinJson){
		HttpPost post=new HttpPost("http://aqui_tu_url/Bla_bla");
		
		Date date = new java.util.Date(); 
		SimpleDateFormat sdf=new java.text.SimpleDateFormat("yyyy-MM-dd");
		
		String followDate=sdf.format(date);
		Log.i("TAG", followDate);
		
		Calendar calendario = new GregorianCalendar();
		int hora, minutos, segundos;
		hora =calendario.get(Calendar.HOUR_OF_DAY);
		minutos = calendario.get(Calendar.MINUTE);
		segundos = calendario.get(Calendar.SECOND);

		String stringTime="" + hora + ":" + minutos + ":" + segundos;
		
		JSONObject objeto=null;
		try {
			objeto  = new JSONObject(strinJson);
			objeto.put("donation_time", stringTime);
			objeto.put("donation_date", followDate);
			Log.i("TAG","Objeto " + objeto.toString());
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
		
		post.setHeader("Content-type", "application/json");
		
		try {
			post.setEntity(new StringEntity(objeto.toString()));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		HttpResponse respuesta=null;

		try {
			 respuesta=cliente.execute(post);
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
		
		return response;
	}
	
	public boolean doTransaction(String json){
		
		String res=consultarWebService(json);
		
		if(res==null)
			return false;
		else
			return true;
	}

}