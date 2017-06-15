package com.example.administrator.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by Administrator on 2014/10/2.
 */
public class EX0315 extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ex0315);

        ((Gallery) findViewById(R.id.myGallery)).setAdapter(new ImageAdapter(this));
    }

    public class ImageAdapter extends BaseAdapter {
        private Context myContext;

        private int[] myImageIds = {
                R.drawable.ssd_poker_ace,
                R.drawable.ssd_poker_back,
                R.drawable.ssd_poker_q,
                R.drawable.ssd_poker_k,
                R.drawable.ic_launcher_maps,
                R.drawable.ic_launcher_camera,
                R.drawable.ic_launcher_gallery
        };

        public ImageAdapter(Context c) {this.myContext = c;}
        public int getCount() {return this.myImageIds.length;}
        public Object getItem(int position) {return position;}
        public long getItemId(int position) {return position;}

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(this.myContext);
            iv.setImageResource(this.myImageIds[position]);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);

            iv.setLayoutParams(new Gallery.LayoutParams(250, 350));
            return iv;
        }

        public float getScale(boolean focused, int offset) {
            return Math.max(0, 1.0f/(float)Math.pow(2, Math.abs(offset)));
        }
    }
}