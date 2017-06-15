package cn.changsha.xzw.Gen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.*;
import cn.changsha.xzw.Plugins.PullToRefresh.PullToRefreshView;
import cn.changsha.xzw.R;
import sense.support.v1.DataProvider.Activity.ActivityCollections;
import sense.support.v1.DataProvider.Activity.ActivityData;
import sense.support.v1.DataProvider.Activity.ActivityDataOperateType;
import sense.support.v1.Tools.HttpClientStatus;

import java.util.Date;

/**
 * Created by zcoffice on 2014/12/4.
 */
public class DefaultGen extends BaseGen implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {


    PullToRefreshView pullToRefreshView;
    int PageSize = 10;
    int PageIndex = 1;
    //ActivityCollections activityCollectionsOfListAll;
    //ActivityListAdapter activityListAdapter;
    private ListView listViewOfActivityList;

    Spinner spRegion;
    Spinner spPrice;
    Spinner spOthers;

    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.default_page);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.common_title_bar);

        //registerBtn=(Button)findViewById(R.id.registerBtn);
        //registerBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), UserRegisterGen.class);
                //startActivity(intent);
        //    }
        //});

        //listViewOfActivityList = (ListView) findViewById(R.id.listViewOfActivityList);
        //pullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);

        LoadData(PageIndex, PageSize);


        //注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        //mMapView=(MapView)findViewById(R.id.bMapsView);
        //mMapView.setBuiltInZoomControls(true);
        //设置启用内置的缩放控件
        //MapController mMapController=mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        //GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
        //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        //mMapController.setCenter(point);//设置地图中心点
        //mMapController.setZoom(12);//设置地图zoom级别
    }

    private void LoadData(int pageIndex, int pageSize) {
        //String activityGetListOfAllUrl = getString(R.string.config_activity_get_list_of_all_url) + "&site_id=" + getString(R.string.config_siteid);
        //ActivityOfAllHandler activityOfAllHandler = new ActivityOfAllHandler();
        //ActivityData activityData = new ActivityData(activityGetListOfAllUrl, activityOfAllHandler);
        //activityData.setPageIndex(pageIndex);
        //activityData.setPageSize(pageSize);
        //activityData.GetDataFromHttp(ActivityDataOperateType.GetList);
    }


    private class ActivityOfAllHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            HttpClientStatus httpClientStatus = HttpClientStatus.values()[msg.what];
            switch (httpClientStatus) {
                case START_GET:
                    //ToastObject.Show(DefaultGen.this, getString(R.string.message_load_begin));
                    break;
                case FINISH_GET:
/*
                    if (activityCollectionsOfListAll != null && activityListAdapter != null && activityCollectionsOfListAll.size() > 0) {
                        activityCollectionsOfListAll.addAll((ActivityCollections) msg.obj);
                        activityListAdapter.notifyDataSetChanged();
                        listViewOfActivityList.setSelection(activityListAdapter.getCount());
                    } else {
                        activityCollectionsOfListAll = (ActivityCollections) msg.obj;
                        activityListAdapter = new ActivityListAdapter(DefaultGen.this, R.layout.activity_list_all_item, activityCollectionsOfListAll);
                        listViewOfActivityList.setAdapter(activityListAdapter);
                        listViewOfActivityList.setOnScrollListener(scrollListener);
                        pullToRefreshView.setOnHeaderRefreshListener(DefaultGen.this);
                        pullToRefreshView.setOnFooterRefreshListener(DefaultGen.this);
                    }
                    pullToRefreshView.setLastUpdated(new Date().toLocaleString());
                    */
                    break;
                case ERROR_GET:
                    //ToastObject.Show(DefaultGen.this, getString(R.string.message_load_failure));
                    break;
                default:
                    System.out.println("nothing to do");
                    break;
            }
        }
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshView.onFooterRefreshComplete();
                //if (activityCollectionsOfListAll.size() >= PageSize) { //只有当前页的数据等于每页显示数时，才进行加载
                PageIndex++;
                LoadData(PageIndex, PageSize);
                //}
            }
        }, 1000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        pullToRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                pullToRefreshView.onHeaderRefreshComplete(getString(R.string.pull_to_refresh_update_tips) + new Date().toLocaleString());
                if (activityCollectionsOfListAll != null) {
                    activityCollectionsOfListAll.clear();
                    activityCollectionsOfListAll = null;
                    PageIndex = 1;
                }
                */
                LoadData(PageIndex, PageSize);
            }
        }, 1000);
    }

    @Override
    protected void onDestroy(){
        //mMapView.destroy();
        //if(mBMapMan!=null){
        //    mBMapMan.destroy();
        //    mBMapMan=null;
        //}
        super.onDestroy();
    }
    @Override
    protected void onPause(){
        //mMapView.onPause();
        //if(mBMapMan!=null){
        //    mBMapMan.stop();
        //}
        super.onPause();
    }
    @Override
    protected void onResume(){
        //mMapView.onResume();
        //if(mBMapMan!=null){
        //    mBMapMan.start();
        //}
        super.onResume();
    }

    AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                    //activityListAdapter.setFlagBusy(true);
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                    //activityListAdapter.setFlagBusy(false);
                    break;
                case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    //activityListAdapter.setFlagBusy(false);
                    break;
                default:
                    break;
            }
            //activityListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    };
}
