package com.example.SmsService;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.devsmart.android.ui.HorizontalListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dd on 23.11.2014.
 */
public class VideoView extends Activity {
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    public static Activity fa;
    ImageView image_list_icon;
    int mSelectedItem=0;
    android.widget.VideoView vidView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fa=this;

        //WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //int ip= wifiManager.getConnectionInfo().getIpAddress();

        vidView = (android.widget.VideoView)findViewById(R.id.videoView);
        String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/Interstellar.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);
        vidView.start();
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);

        final HorizontalListView listview = (HorizontalListView) findViewById(R.id.listview);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedItem = position;
                mAdapter.notifyDataSetChanged();
            }
        });


        }
        private static String[] dataObjects = new String[]{ "Text #1",
            "Text #2",
            "Text #3","Text #4","Text #5" };

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;//
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_leyout, null);
            TextView title = (TextView) retval.findViewById(R.id.title);
            image_list_icon = (ImageView)retval.findViewById(R.id.image_fromlist);
            title.setText(dataObjects[position%dataObjects.length]);

            if (title.getText() == "Text #1") {
                //new ImgLoad().execute();
                Picasso.with(retval.getContext()).load("http://i.imgur.com/DvpvklR.png").into(image_list_icon);

            } else
            if (title.getText() == "Text #2"){
                Picasso.with(retval.getContext()).load("http://127.0.0.1:59777/smb/192.168.0.250/Files/1.jpg").into(image_list_icon);
            }else
            if (title.getText() == "Text #3"){
                Picasso.with(retval.getContext()).load("http://127.0.0.1:59777/smb/192.168.0.250/Files/2.jpg").into(image_list_icon);
            }else
            if (title.getText() == "Text #4"){
                Picasso.with(retval.getContext()).load("http://127.0.0.1:59777/smb/192.168.0.250/Files/3.jpg").into(image_list_icon);
            }

            if (position == mSelectedItem) {
               retval.setBackgroundColor(Color.WHITE);
                retval.getBackground().setAlpha(50);

                if (title.getText() == "Text #1") {
                    String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/a.mp4";
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.start();

                } else
                if (title.getText() == "Text #2"){
                    String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/b.mp4";
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.start();
                }else
                if (title.getText() == "Text #3"){
                    String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/c.mp4";
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.start();
                }else
                if (title.getText() == "Text #4"){
                    String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/Interstellar.mp4";
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.start();
                }
                if (title.getText() == "Text #5"){
                    String vidAddress = "http://127.0.0.1:59777/smb/192.168.0.250/Files/Interstellar.mp4";
                    Uri vidUri = Uri.parse(vidAddress);
                    vidView.setVideoURI(vidUri);
                    vidView.start();
                }

            }
            return retval;
        }


    };


}