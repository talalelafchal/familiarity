package com.chengyu.paginglistview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements AbsListView.OnScrollListener {

    private ListView list = null;
    private int page = 0;
    private int total = 100; // assume that we have 100 data.
    private boolean isLoading = false;
    private ArrayList<Map<String, String>> data = null;
    private PagingListAdapter mPLAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.lv);
        data = new ArrayList<Map<String, String>>(); // data

        mPLAdapter = new PagingListAdapter(this, data);
        list.setAdapter(mPLAdapter);
        list.setOnScrollListener(this);

        //load data
        nextPage();
        //
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount >0 && list != null) {
            int lastPosition = list.getLastVisiblePosition();
            if (lastPosition + 1 == totalItemCount && data.size() < total) {
                // loading
                nextPage();
            }
        }
    }

    private void nextPage() {
        if (!isLoading) {
            Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
            for (int i=0; i<20; i++) {
                Map<String, String> addData = new HashMap<String, String>();
                addData.put("msg1", String.valueOf(data.size()) + ". msg1");
                addData.put("msg2", String.valueOf(data.size()) + ". msg2");
                data.add(addData);
            }
            isLoading = false;

            mPLAdapter.refreshData(data);
        }


    }
}
