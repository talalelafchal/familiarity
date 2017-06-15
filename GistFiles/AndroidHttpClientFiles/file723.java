package com.squaar.comparar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.squaar.comparar.MylocalData;
import com.squaar.comparar.ConexionHttpGet;

public class MainActivity extends Activity {
	
	LinearLayout listLayoutContent;
	ConexionHttpGet c;//clase para carga de datos por GET / POST
	private Handler mHandler = new Handler();//avisa cuando termino la carga de datos
	ProgressDialog dialog = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	MylocalData.getInstance().context = this.getApplicationContext();
    	    	
    	c = new ConexionHttpGet();
    	    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        listLayoutContent = (LinearLayout)findViewById(R.id.ListScrollContent);
        
        dialog = ProgressDialog.show(MainActivity.this, "", 
                "Cargando Productos...", true);
		 new Thread(new Runnable() {
			 
				public void run() {

					try {
						c.getItems();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}		    	
			    	
			    	 mHandler.post(new Runnable() {
	                     public void run() {	             						
	                    	 
	             			createItems();

	                    	dialog.dismiss();
	                    	 
	                     }
	                 });
			    			    	
			    }
				
			  }).start();
        
        
    }
    
    
    void createItems(){
    	
    	int sizeItems = MylocalData.getInstance().arrItems.length();
    	
    	System.out.println("sizeItems "+sizeItems);
    	
    	for(int i=0;i<sizeItems;i++){
    		
    		try {
    			
				JSONObject jsonItem = MylocalData.getInstance().arrItems.getJSONObject(i);
				
				LinearLayout itemContent = new LinearLayout(this);
	    		TextView itemTitle = new TextView(this);
	    		ImageView foto = new ImageView(this);
	    			    		
	    		LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,50);
	    		itemContent.setLayoutParams(contentParams);
	    		itemTitle.setText(jsonItem.getString("title"));
	    		
	    		LinearLayout.LayoutParams thumbParams = new LinearLayout.LayoutParams(50, 50);
				thumbParams.setMargins(0, 0, 0, 0);
	    		URL url = new URL(jsonItem.getString("thumbnail"));
				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				foto.setLayoutParams(thumbParams);
	    		foto.setImageBitmap(bmp);
	    		
	    		itemContent.addView(foto);
	    		itemContent.addView(itemTitle);
	    		listLayoutContent.addView(itemContent);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
    }

    
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
