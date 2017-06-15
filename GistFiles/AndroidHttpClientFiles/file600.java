package com.example.wisatajogja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHelper
{

	private static final String	TAG				= "JSONHelper";

	private InputStream			is				= null;
	private JSONObject			jsonObject		= null;
	private String				json			= "";

	private static final String	TAG_LOCATION	= "location";
	private static final String	TAG_NAMA		= "nama";
	private static final String	TAG_GAMBAR		= "gambar";
	private static final String	TAG_ALAMAT		= "alamat";
	private static final String	TAG_LAT			= "lat";
	private static final String	TAG_LNG			= "lng";

	public JSONObject getJSONFromURL(String url)
	{
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}

			is.close();
			json = sb.toString();
		} catch (Exception e)
		{
			Utils.TRACE(TAG, "error buffered reader");
		}

		try
		{
			Utils.TRACE("jsonadapter", "hasil json ->" + json);
			jsonObject = new JSONObject(json);

		} catch (JSONException e)
		{
			Utils.TRACE(TAG, "Error jsonObject");
		}

		return jsonObject;
	}

	public List<E_Lokasi> getAllLokasi(JSONObject obj)
	{
		List<E_Lokasi> listLokasi = new ArrayList<E_Lokasi>();

		try
		{
			JSONArray jsonArray = obj.getJSONArray(TAG_LOCATION);
			for (int i = 0; i < jsonArray.length(); i++)
			{
				JSONObject jObj = jsonArray.getJSONObject(i);

				E_Lokasi lokasi = new E_Lokasi();
				lokasi.setNama(jObj.getString(TAG_NAMA));
				lokasi.setAlamat(jObj.getString(TAG_ALAMAT));
				lokasi.setGambar(jObj.getString(TAG_GAMBAR));
				lokasi.setLat(jObj.getDouble(TAG_LAT));
				lokasi.setLng(jObj.getDouble(TAG_LNG));

				listLokasi.add(lokasi);
			}

		} catch (JSONException e)
		{
		}
		return listLokasi;
	}

}
