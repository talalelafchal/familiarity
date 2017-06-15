package com.ztt.criminalintent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import net.WebFetch;

import java.io.IOException;

import javax.net.ssl.KeyManagerFactory;

/**
 * Created by 123 on 14-11-13.
 */
public class PhotoGalleryFragment extends Fragment {

    private static final String TAG="PhotoGalleryFragment";
    GridView mGridView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
        就是可以在Activity重新创建时可以不完全销毁Fragment，
        以便Fragment可以恢复。在onCreate()方法中调用setRetainInstance(true/false)
        方法是最佳位置。当在onCreate()方法中调用了setRetainInstance(true)后，
        Fragment恢复时会跳过onCreate()和onDestroy()方法，
        因此不能在onCreate()中放置一些初始化逻辑，切忌！
        * */
        setRetainInstance(true);
        new FetchItemsTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_photo_gallery,null);
        mGridView=(GridView)v.findViewById(R.id.gridView);
        return v;
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String result=new WebFetch().getUrl("http://www.baidu,com");
                Log.i(TAG,"Fetched contents of URL:"+result);
            } catch (IOException e) {
                Log.i(TAG,"Failed to fetch contents of URL:"+e);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
