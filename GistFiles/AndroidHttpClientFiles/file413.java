package com.example.wisatajogja;

import java.util.List;

import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity
{

	private static final String	URL	= "http://10.0.2.2:8888/example/aplikasi_wisata/webservice.php?get=lokasi";

	private JSONHelper			json;
	private ImageLoader			imageLoader;
	private MyAdapter			adapter;
	private ListView			lv_lokasi;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv_lokasi = (ListView) findViewById(R.id.lv_lokasi);

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageUtils.getImgConfig(this));

		json = new JSONHelper();

		new AsyncData().execute(URL);
	}

	private class AsyncData extends AsyncTask<String, Void, List<E_Lokasi>>
	{
		@Override
		protected List<E_Lokasi> doInBackground(String... params)
		{
			JSONObject obj = json.getJSONFromURL(params[0]);
			return json.getAllLokasi(obj);
		}

		@Override
		protected void onPostExecute(List<E_Lokasi> result)
		{
			super.onPostExecute(result);

			adapter = new MyAdapter(MainActivity.this, result, imageLoader);
			lv_lokasi.setAdapter(adapter);

		}

		@Override
		protected void onPreExecute()
		{
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	}

}
