package bpst.met.imagelisttest;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;


import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends Activity {


    Integer[] sizes = {100, 200, 300, 400, 500, 600, 700, 800, 900, 1024}; // init size massive
    ArrayList<String> images = new ArrayList<String>(); // init link massive
    StaggeredGridView grid;
    FrameLayout reset_back;
    SampleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       grid = (StaggeredGridView)findViewById(R.id.grid);
        Button  refresh = (Button) findViewById(R.id.refresh);
        reset_back = (FrameLayout)findViewById(R.id.reset_back);
        adapter = new SampleAdapter(this, android.R.layout.simple_list_item_1, images);
       itemRefresh();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemRefresh();
            }
        });
        grid.setOnScrollListener(scrollListener);
        grid.setAdapter(adapter);
    }

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            float max = i3, curent = i+i2;

               // float proc = max / 1000 * curent;
            float proc = curent / max;
            if(i>0)
                reset_back.setAlpha(proc);
            if(i==0)
                reset_back.setAlpha(0);

        }
    };
    /*
    *Method for refreshing grid
     */
    void itemRefresh(){
       reset_back.setAlpha(0);
        images.clear();
         adapter.clear();
        for(int i=0;i < 50;i++ ){
            String url = "http://placekitten.com/g/{0}/{1}"; //Link for get image
            Random rnd = new Random();

            url =   url.replace("{0}", sizes[rnd.nextInt(sizes.length)]+"");
            url =  url.replace("{1}", sizes[rnd.nextInt(sizes.length)]+"");
            images.add(url);
        }
        adapter.notifyDataSetChanged();

    }

}
