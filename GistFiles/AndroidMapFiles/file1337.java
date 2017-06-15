package com.m4bce.DrawableManager;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.BasicManagedEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class DrawableManager {
        
        // HTTP 1.0
        private static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 0);
//      private static final String LOG_TAG = "DrawableManager";

        protected final Map<String, SoftReference<Drawable>> drawableMap;
        
        protected static DrawableManager _instance;
        
        public static DrawableManager getInstance(){
                if(_instance == null) _instance = new DrawableManager();
                return _instance;
        }
        
        protected DrawableManager() {
                drawableMap = new HashMap<String, SoftReference<Drawable>>();
        }
        
        public void setDrawable(final String urlString, final ImageView view){
                if(drawableMap.containsKey(urlString)){
                        Drawable drawable = drawableMap.get(urlString).get();
                        if(drawable != null){
                                view.setImageDrawable(drawable);
                                return;
                        }
                        else drawableMap.remove(urlString);
                }

                // Set the httpClient if necessary
                final HttpClient httpClient = new DefaultHttpClient();
                
//              final int millis = (int)(System.currentTimeMillis() % 10000);
                final Handler handler = new Handler() {
//                      final int id = millis;
                        public void handleMessage(Message msg) {
                                if(msg.obj != null){
                                        view.setImageDrawable((Drawable) msg.obj);
                                }
                        };
                };
                
                final Thread thread = new Thread(){
//                      final int id = millis;
//                      final HttpClient client = httpClient;
                        @Override
                        public void run() {
//                              Log.i(LOG_TAG, urlString);
                                HttpGet request = new HttpGet(urlString);
                                HttpParams params = new BasicHttpParams();
                                HttpConnectionParams.setConnectionTimeout(params, 2000);
                                HttpConnectionParams.setSoTimeout(params, 1000);
                                HttpProtocolParams.setVersion(params, PROTOCOL_VERSION);
                                request.setParams(params);
                                BasicManagedEntity entity = null;

                                try{
//                                      long t = System.currentTimeMillis();
                                        HttpResponse response = httpClient.execute(request);
//                                      Log.i(LOG_TAG, response.getEntity().getClass().toString());

                                        entity = (BasicManagedEntity)response.getEntity();
                                }
                                catch (ClientProtocolException e) {
                                        handler.sendEmptyMessage(NORM_PRIORITY);
                                        e.printStackTrace();
                                }
                                catch (IOException e) {
                                        handler.sendEmptyMessage(NORM_PRIORITY);
                                        e.printStackTrace();
                                }
                                if(entity != null){
//                                      Log.i(LOG_TAG, entity.getContentType().toString());
//                                      Log.i(LOG_TAG, entity.getContentEncoding().toString());
//                                      Bitmap bmp = BitmapFactory.decodeStream(entity.getContent());
                                        Drawable drawable = null;
                                        try {
                                                drawable = Drawable.createFromStream(entity.getContent(), "www");
                                        } catch (IOException e) {
                                                handler.sendEmptyMessage(NORM_PRIORITY);
                                                e.printStackTrace();
                                        }
                                        if(drawable == null){
                                                handler.sendEmptyMessage(NORM_PRIORITY);
                                        }
                                        else{
                                                handler.sendMessage(handler.obtainMessage(NORM_PRIORITY,drawable));
                                        }
                                }
                                else{
                                        handler.sendEmptyMessage(NORM_PRIORITY);
                                }

                                
//                              handler.sendMessage(handler.obtainMessage(id, fetchDrawable(urlString)));
                        }
                };
                
//              final Handler handlerControl = new Handler(){
//                      @Override
//                      public void handleMessage(Message msg) {
//                              if(thread.isAlive()){
//                                      thread.stop();
//                                      BitmapFactory.decode
//                                      Bitmap bmp = new Bitmap();
//                                      Canvas canvas = new Canvas();
//                                      Drawable drawable = view.getDrawable();
//                                      new Canvas(drawable.get)
//                              }
//                      }
//              };
                
//              Thread threadControl = new Thread(){
//                      @Override
//                      public void run() {
//                      }
//              };
                thread.start();
        }
        
//      private Drawable fetchDrawable(String urlString){
//              if(drawableMap.containsKey(urlString)){
//                      Drawable drawable = drawableMap.get(urlString).get();
//                      if(drawable != null) return drawable;
//                      drawableMap.remove(urlString);
//              }
//              
//              try{
//                      InputStream is = (new URL(urlString)).openConnection().getInputStream();
//                      Drawable drawable = Drawable.createFromStream(is, "src");
//                      drawableMap.put(urlString, new SoftReference<Drawable>(drawable));
//                      return drawable;
//              } catch (MalformedURLException e) {
//                      return null;
//              } catch (IOException e) {
//                      return null;
//              }
//      }
}