package com.feri3095111172.mymedicine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
  /**
	 * @uml.property  name="jObject"
	 * @uml.associationEnd  
	 */
	private JSONObject jObject;
	/**
	 * @uml.property  name="jsonResult"
	 */
	private String jsonResult ="";
	/**
	 * @uml.property  name="id_obat" multiplicity="(0 -1)" dimension="1"
	 */
	String[] id_obat;
	//String[] id_apotik;
	//String[] nama;
	/**
	 * @uml.property  name="id_kategori" multiplicity="(0 -1)" dimension="1"
	 */
	String[] id_kategori;
	/**
	 * @uml.property  name="kategori" multiplicity="(0 -1)" dimension="1"
	 */
	String[] kategori;
	/**
	 * @uml.property  name="sub_kategori" multiplicity="(0 -1)" dimension="1"
	 */
	String[] sub_kategori;
	/**
	 * @uml.property  name="id_produsen" multiplicity="(0 -1)" dimension="1"
	 */
	String[] id_produsen;
	/**
	 * @uml.property  name="nama_produsen" multiplicity="(0 -1)" dimension="1"
	 */
	String[] nama_produsen;
	/**
	 * @uml.property  name="id_golongan" multiplicity="(0 -1)" dimension="1"
	 */
	String[] id_golongan;
	/**
	 * @uml.property  name="golongan" multiplicity="(0 -1)" dimension="1"
	 */
	String[] golongan;	
	/**
	 * @uml.property  name="nama_obat" multiplicity="(0 -1)" dimension="1"
	 */
	String[] nama_obat;
	/**
	 * @uml.property  name="isi_obat" multiplicity="(0 -1)" dimension="1"
	 */
	String[] isi_obat;
	/**
	 * @uml.property  name="dosis" multiplicity="(0 -1)" dimension="1"
	 */
	String[] dosis;
	/**
	 * @uml.property  name="efek_samping" multiplicity="(0 -1)" dimension="1"
	 */
	String[] efek_samping;
	/**
	 * @uml.property  name="indikasi" multiplicity="(0 -1)" dimension="1"
	 */
	String[] indikasi;
	/**
	 * @uml.property  name="kontra_indikasi" multiplicity="(0 -1)" dimension="1"
	 */
	String[] kontra_indikasi;
	/**
	 * @uml.property  name="kemasan" multiplicity="(0 -1)" dimension="1"
	 */
	String[] kemasan;
	/**
	 * @uml.property  name="perhatian" multiplicity="(0 -1)" dimension="1"
	 */
	String[] perhatian;
	/**
	 * @uml.property  name="logo_golongan" multiplicity="(0 -1)" dimension="1"
	 */
	String[] logo_golongan;
	//String[] URL;
	/**
	 * @uml.property  name="menu"
	 * @uml.associationEnd  
	 */
	Menu menu;
	protected Object listView1;
	public static MainActivity ma;
	private ArrayList<String> array_sort= new ArrayList<String>();
	int textlength=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 ma=this; 
		 	LoadList();
	        CekStatusJaringan();
	        CekStatusGPS();
	}
	
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void RefreshList(final String url2) {
    	try {
				jsonResult = getRequest(url2);         	
	        	jObject = new JSONObject(jsonResult);
				final JSONArray menuitemArray = jObject.getJSONArray("obat");
				id_obat 		= new String[menuitemArray.length()];
				id_golongan 	= new String[menuitemArray.length()];
				golongan 		= new String[menuitemArray.length()];
				logo_golongan	= new String[menuitemArray.length()];
				id_kategori 	= new String[menuitemArray.length()]; 
				kategori 		= new String[menuitemArray.length()];
				sub_kategori	= new String[menuitemArray.length()];
				id_produsen 	= new String[menuitemArray.length()];
				nama_produsen	= new String[menuitemArray.length()];
				nama_obat 		= new String[menuitemArray.length()];
				isi_obat 		= new String[menuitemArray.length()];
				dosis 			= new String[menuitemArray.length()];
				efek_samping 	= new String[menuitemArray.length()];
				indikasi 		= new String[menuitemArray.length()];
				kontra_indikasi = new String[menuitemArray.length()];
				kemasan 		= new String[menuitemArray.length()];
				perhatian 		= new String[menuitemArray.length()];
	
				for (int i = 0; i < menuitemArray.length(); i++)
				{
					id_obat[i] 			= menuitemArray.getJSONObject(i).getString("id_obat").toString();
					id_golongan[i] 		= menuitemArray.getJSONObject(i).getString("id_golongan").toString();
					golongan[i] 		= menuitemArray.getJSONObject(i).getString("golongan").toString();
					logo_golongan[i] 	= menuitemArray.getJSONObject(i).getString("logo_golongan").toString();
					id_kategori[i] 		= menuitemArray.getJSONObject(i).getString("id_kategori").toString();
					sub_kategori[i] 	= menuitemArray.getJSONObject(i).getString("sub_kategori").toString();
					kategori[i] 		= menuitemArray.getJSONObject(i).getString("kategori").toString();
					id_produsen[i] 		= menuitemArray.getJSONObject(i).getString("id_produsen").toString();
					nama_produsen[i] 		= menuitemArray.getJSONObject(i).getString("nama_produsen").toString();
					nama_obat[i] 		= menuitemArray.getJSONObject(i).getString("nama_obat").toString();
					isi_obat[i] 		= menuitemArray.getJSONObject(i).getString("isi_obat").toString();
					dosis[i] 			= menuitemArray.getJSONObject(i).getString("dosis").toString();
					efek_samping[i] 	= menuitemArray.getJSONObject(i).getString("efek_samping").toString();
					indikasi[i] 		= menuitemArray.getJSONObject(i).getString("indikasi").toString();
					kontra_indikasi[i] 	= menuitemArray.getJSONObject(i).getString("kontra_indikasi").toString();
					kemasan[i] 			= menuitemArray.getJSONObject(i).getString("kemasan").toString();
					perhatian[i] 		= menuitemArray.getJSONObject(i).getString("perhatian").toString();
				}
				
			} catch (final JSONException e) {
				// TODO Auto-generated catch block
					try {
						final AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
								// Setting Dialog Title
								alertDialog1.setTitle("Pesan Peringatan: ");
					            // Setting Dialog Message
					            alertDialog1.setMessage("Data Tidak Ada!!");
					            // Setting Icon to Dialog
					            alertDialog1.setIcon(R.drawable.tanda_seru);
					            // Setting OK Button
					            alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {
									
					            	@Override
					            	public void onClick(final DialogInterface dialog, final int which) {
					            		//LoadingBiasa();
					            		}
					            	});
			            // Showing Alert Message
			            alertDialog1.show();
					} catch (final Exception e2) {
						// TODO: handle exception
						Toast.makeText(getApplicationContext(), "Gagal terhubung ke json pada Daftar List Obat", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
			}
    	
		final ListView listView1 = (ListView)findViewById(R.id.listView1);
		listView1.setAdapter(new ArrayAdapter<Object>(this, android.R.layout.simple_list_item_1, nama_obat));
		listView1.setSelected(true);
		final TextView inputSearch=(TextView)findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				// When user changed the Text
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
//				inputSearch.setText(null);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int textlength = inputSearch.getText().length();
				array_sort.clear();
				for (int i = 0; i < nama_obat.length; i++)
					{
						if (textlength <= nama_obat[i].length())
						{
							if(inputSearch.getText().toString().equalsIgnoreCase((String)nama_obat[i].subSequence(0,textlength)))
							{
							array_sort.add(nama_obat[i]);
							
							}
						}
					}
				listView1.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, array_sort));
				listView1.setSelected(true);
			}
		});
        inputSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				inputSearch.setText(null);
			}
		});
		listView1.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	 public void onItemClick(final AdapterView<?> arg0, final View arg1, final int arg2,final long arg3) {
				final String selectionidobat 			= id_obat[arg2]; 
				//final String selectionidapotik 			= id_apotik[arg2];
				//final String selectionnmapotik 			= nama[arg2];
				final String selectionidgolongan		= id_golongan[arg2];
				final String selectiongolongan 			= golongan[arg2];
				final String selectionlogogolongan		= logo_golongan[arg2];
				final String selectionidkategori		= id_kategori[arg2];
				final String selectionkategori 			= kategori[arg2];
				final String selectionsubkategori 		= sub_kategori[arg2];
				final String selectionidprodusen		= id_produsen[arg2];
				final String selectionnmprodusen		= nama_produsen[arg2];
				final String selectionnamaobat			= nama_obat[arg2]; 
				final String selectionisiobat			= isi_obat[arg2]; 
				final String selectiondosis		 		= dosis[arg2];
				final String selectionefeksamping		= efek_samping[arg2]; 
				final String selectionindikasi			= indikasi[arg2]; 
				final String selectionkontrakindikasi	= kontra_indikasi[arg2]; 
				final String selectionkemasan		 	= kemasan[arg2]; 
				final String selectionperhatian			= perhatian[arg2]; 
				//final String selectionid				= id_obat[arg2];
				//final String selectionURL				= URL[arg2];
		    	final CharSequence[] dialogitem 	= {"Ya", "Tidak"};
		    	
		    	final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		        builder.setTitle("Ingin Melihat Detail Obat?");
		        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int item) {
						switch(item){
						case 0 :
							final Intent i = new Intent(getApplicationContext(), DetailObatActivity.class);
							//Intent j = new Intent(getApplicationContext(), LokasiApotik.class);
							LoadingBiasa();
							i.putExtra("id_obat", selectionidobat);
							//i.putExtra("id_apotik", selectionidapotik);
							//i.putExtra("nama", selectionnmapotik);
							i.putExtra("id_golongan", selectionidgolongan);
							i.putExtra("golongan", selectiongolongan);
							i.putExtra("logo_golongan", selectionlogogolongan);
							i.putExtra("id_kategori", selectionidkategori);
							i.putExtra("kategori", selectionkategori);
							i.putExtra("sub_kategori", selectionsubkategori);
							i.putExtra("id_produsen", selectionidprodusen);
							i.putExtra("nama_produsen", selectionnmprodusen);
							i.putExtra("nama_obat", selectionnamaobat);
							i.putExtra("isi_obat", selectionisiobat);
							i.putExtra("dosis", selectiondosis);
							i.putExtra("efek_samping", selectionefeksamping);
							i.putExtra("indikasi", selectionindikasi);
							i.putExtra("kontra_indikasi", selectionkontrakindikasi);
							i.putExtra("kemasan", selectionkemasan);
							i.putExtra("perhatian", selectionperhatian);					    	
					    	Toast.makeText(getApplicationContext(), "Mohon tunggu...", Toast.LENGTH_SHORT).show();
					    	startActivity(i);
							break;
						case 1 :
              
							break;
						}/**/
					}
				});
		        builder.create().show();
			}});
        ((ArrayAdapter)listView1.getAdapter()).notifyDataSetInvalidated();       
    }
    
	/**
	 * Method untuk Mengirimkan data ke server
	 */
	public String getRequest(String Url){
		String sret="";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Url);
		try{
			HttpResponse response = client.execute(request);
			sret =request(response);
		}catch(Exception ex){
			Toast.makeText(this,"Gagal memuat Daftar Obat"+sret, Toast.LENGTH_SHORT).show();
		}
		return sret;
	}
	
	/**
	 * Method untuk Menerima data dari server
	 */
	public static String request(HttpResponse response){
		String result = "";
		try{
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null){
				str.append(line + "\n");
			}
			in.close();
			result = str.toString();
		}catch(Exception ex){
			result = "Error saat meminta Daftar Obat";
		}
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;    	
    	menu.add(0, 1, 0, "Cari Semua Lokasi Apotik").setIcon(android.R.drawable.ic_menu_mylocation);
    	menu.add(0, 2, 0, "Muat Ulang Daftar").setIcon(android.R.drawable.ic_menu_rotate);
        menu.add(0, 3, 0, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
        	LoadingBiasa();
        	Intent i = new Intent(MainActivity.this, SemuaLokasiActivity.class);
        	startActivity(i);
            return true;
        case 2:
        	Toast.makeText(getApplicationContext(), "Muat Ulang Daftar...", Toast.LENGTH_SHORT).show();
        	ReloadListMain();
        	//LoadingBiasa();
    		return true;
        case 3:
        	//LoadingExit();
            finish();
            return true;
        }
    	return false;
    }
	
	 public void LoadList(){
			 final ProgressDialog myProgressDialog1=ProgressDialog.show(MainActivity.this, "Memuat Data Obat", "Mohon Tunggu...", true);
			 new Thread() {
				 @Override
				public void run() {
					 try{
			            String url="http://vnot.pusku.com/mymed/daftarobat.php";
			            RefreshList(url);
						Thread.sleep(5000);
					 } catch (Exception e) {}
					 myProgressDialog1.dismiss();
				 }}.start(); 
		 }
	 public void LoadingCepat(){
		 final ProgressDialog myProgressDialog2=ProgressDialog.show(MainActivity.this, "Memuat Daftar Obat", "Mohon Tunggu...", true);
		 new Thread() {
			 @Override
			public void run() {
				 try{
					Thread.sleep(3000);
				 } catch (Exception e) {}
				 myProgressDialog2.dismiss();
			 }}.start(); 
	 }
	 public void LoadingBiasa(){
		 final ProgressDialog myProgressDialog3=ProgressDialog.show(MainActivity.this, "Memuat Data", "Mohon Tunggu...", true);
		 new Thread() {
			 @Override
			public void run() {
				 try{
					Thread.sleep(6000);
				 } catch (Exception e) {}
				 //Intent i = new Intent(getApplicationContext(), MainActivity.class);
				 myProgressDialog3.dismiss();
				 //MainActivity.this.finish();
				 //startActivity(i); 
			 }}.start(); 
	 }
	 public void LoadingExit(){
		 final ProgressDialog myProgressDialog4=ProgressDialog.show(getApplicationContext(), "Tutup Aplikasi", "Menutup Aplikasi...", true, true);
		 new Thread() {
			 @Override
			public void run() {
				 try{
					Thread.sleep(3000);
					finish();
				 } catch (Exception e) {}
				 myProgressDialog4.dismiss();
				 finish();
			 }}.start(); 
	 }
	 public void ReloadListMain(){
		 final ProgressDialog myProgressDialog1=ProgressDialog.show(MainActivity.this, "Memuat Ulang Daftar Obat", "Mohon Tunggu...", true);
		 new Thread() {
			 @Override
			public void run() {
				 try{
		            String url="http://vnot.pusku.com/mymed/daftarobat.php";
		            RefreshList(url);
					Thread.sleep(3000);
					
				 } catch (Exception e) {}
				 myProgressDialog1.dismiss();
				 Intent j = new Intent(getApplicationContext(), MainActivity.class);
				 startActivity(j);
			 }}.start(); 
	 }
	 

}
