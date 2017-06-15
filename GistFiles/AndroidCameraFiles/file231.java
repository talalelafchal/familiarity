package com.example.jenny.myapplication.client;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jenny.myapplication.BaseActivity;
import com.example.jenny.myapplication.R;
import com.example.jenny.myapplication.data.Photo;
import com.example.jenny.myapplication.service.FlickrServiceImpl;
import com.example.jenny.myapplication.service.ImageServiceImpl;

import java.util.List;

import javax.inject.Inject;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Main activity with a recycler view to display photos from Flickr.
 */
public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeView;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Photo> photos;

    @Inject
    FlickrServiceImpl apiService;

    @Inject
    ImageServiceImpl imageService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new PhotoAdapter(this, imageService);
        apiService.getPhotosSearchData(adapter);

        recyclerView.setAdapter(adapter);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeView.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        apiService.getPhotosSearchData(adapter);
        adapter.setPhotos(photos);
        adapter.notifyDataSetChanged();
        swipeView.setRefreshing(false);
    }
}