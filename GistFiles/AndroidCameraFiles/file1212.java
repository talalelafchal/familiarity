package com.horem.parachute.fragment;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.horem.parachute.R;
import com.horem.parachute.activity.ActivityLogin;
import com.horem.parachute.activity.BalloonItemInfoActivity;
import com.horem.parachute.activity.WatchingAliveActivity;
import com.horem.parachute.adapter.HomeFragmentVideoAdapter;
import com.horem.parachute.base.CustomApplication;
import com.horem.parachute.customview.ZProgressHUD;
import com.horem.parachute.entity.BalloonListBean;
import com.horem.parachute.entity.BalloonListSubBeanItem;
import com.horem.parachute.net.OkHttpClientManager;
import com.horem.parachute.util.HTTPUtils;
import com.horem.parachute.util.HttpUrlConstant;
import com.horem.parachute.util.LogUtil;
import com.horem.parachute.util.ToastManager;
import com.horem.parachute.util.Utils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.okhttp.Request;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static android.content.ContentValues.TAG;

/**
 * Created by DELL on 2016/11/8.
 */
public class HotBalloonVideoFragment extends Fragment {

    private XRecyclerView xRecyclerView;
    private HomeFragmentVideoAdapter mAdapter;
    private LatLng currentLatLng;
    private List<BalloonListSubBeanItem> dataLists = new ArrayList<>();

    private boolean isRefresh = true;
    private int currentPage;
    private static final int PageSize = 20;


    private ListItemsVisibilityCalculator mVisibilityCalculator;
    private SimpleMainThreadMediaPlayerListener mSimpleMainThreadMediaPlayerListener;
    private VideoPlayerManager<MetaData> mVideoPlayerManager;
    private ItemsPositionGetter mItemsPositionGetter;
    private int mScrollState;
    private LinearLayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVisibilityCalculator = new SingleListViewItemActiveCalculator(
                new DefaultSingleItemCalculatorCallback(), dataLists);
        mSimpleMainThreadMediaPlayerListener= new SimpleMainThreadMediaPlayerListener(){
                    @Override
                    public void onErrorMainThread(int what, int extra) {
                        Log.d(TAG, "onErrorMainThread");
                        mVideoPlayerManager.resetMediaPlayer();
                    }
                };

        initView(view);
        initData();
        mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
        xRecyclerView.setHasFixedSize(true);

        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, xRecyclerView);
        xRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if(!dataLists.isEmpty()){
                    // need to call this method from list view handler in order to have filled list
                    xRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mVisibilityCalculator.onScrollStateIdle(
                                        mItemsPositionGetter,
                                        mLayoutManager.findFirstVisibleItemPosition(),
                                        mLayoutManager.findLastVisibleItemPosition());
                            } catch (ArrayIndexOutOfBoundsException e) {

                            }
                        }
                    });

                    mScrollState = scrollState;
                    try {
                        mVisibilityCalculator.onScrollStateIdle(
                                mItemsPositionGetter,
                                mLayoutManager.findFirstVisibleItemPosition(),
                                mLayoutManager.findLastVisibleItemPosition());
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(!dataLists.isEmpty()){
                    // need to call this method from list view handler in order to have filled list
                    xRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mVisibilityCalculator.onScrollStateIdle(
                                        mItemsPositionGetter,
                                        mLayoutManager.findFirstVisibleItemPosition(),
                                        mLayoutManager.findLastVisibleItemPosition());
                            } catch (ArrayIndexOutOfBoundsException e) {

                            }
                        }
                    });

                    try {
                        mVisibilityCalculator.onScrollStateIdle(
                                mItemsPositionGetter,
                                mLayoutManager.findFirstVisibleItemPosition(),
                                mLayoutManager.findLastVisibleItemPosition());
                    } catch (ArrayIndexOutOfBoundsException e) {

                    }
                }
            }
        });
    }

    private void initView(View view) {
        xRecyclerView = (XRecyclerView) view.findViewById(R.id.home_fragment_xrecycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeFragmentVideoAdapter(getActivity(), dataLists);

        mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
            @Override
            public void onPlayerItemChanged(MetaData metaData) {
            }
        });
        mAdapter.setVideoPlayerManager(mVideoPlayerManager);
        xRecyclerView.setAdapter(mAdapter);
        initListener();
    }

    private void initListener() {
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                currentPage = 0;
                initData();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                currentPage++;
                initData();
            }
        });
        mAdapter.setOnCameraListener(new HomeFragmentVideoAdapter.OnCameraListener() {
            @Override
            public void onCameraClicked(int position) {
                if (!CustomApplication.getInstance().isLogin()) {
                    Intent intent = new Intent(getContext(), ActivityLogin.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), WatchingAliveActivity.class);
                    String balloonId = dataLists.get(position).getBalloonId();
                    intent.putExtra("balloonId", balloonId);
                    startActivity(intent);
                }
            }
        });

        mAdapter.setOnCommentListener(new HomeFragmentVideoAdapter.OnCommentListener() {
            @Override
            public void onCommentClicked(int position) {
                if (!CustomApplication.getInstance().isLogin()) {
                    Intent intent = new Intent(getContext(), ActivityLogin.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), BalloonItemInfoActivity.class);
                    String balloonId = dataLists.get(position).getBalloonId();
                    intent.putExtra("balloonId", balloonId);
                    startActivity(intent);
                }
            }
        });
    }
    private void initData() {
        HashMap<String, String> params = HTTPUtils.getBaseParams(
                (currentLatLng != null ? currentLatLng.longitude : 0) + "",
                (currentLatLng != null ? currentLatLng.latitude : 0) + "");
        params.put("pageSize", String.valueOf(PageSize));
        params.put("currentPage", String.valueOf(currentPage));

        OkHttpClientManager.postAsyn(HttpUrlConstant.URL_BALLOON_LIST, new
                OkHttpClientManager
                        .ResultCallback<BalloonListBean>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        Utils.stopLoading(getContext());
                        ToastManager.show(getContext(), "Net Error");
                    }

                    @Override
                    public void onResponse(BalloonListBean response) {
                        Utils.stopLoading(getContext());
                        if (response.getStatusCode() == -999) {
                            HTTPUtils.exitAccount();
                        } else if (response.getStatusCode() == -20) {
                            HTTPUtils.getNewAccessToken();
                        } else if (response.getStatusCode() == 1) {
                            if (isRefresh) {
                                dataLists.clear();
                                dataLists.addAll(response.getResult().getList());
                                mAdapter.RefreshData(dataLists);
                                xRecyclerView.refreshComplete();
                            } else {
                                dataLists.addAll(response.getResult().getList());
                                mAdapter.RefreshData(dataLists);
                                xRecyclerView.loadMoreComplete();
                            }
                        } else {
                            ToastManager.show(getContext(), "Net Error");
                        }
                    }
                }, params);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!dataLists.isEmpty()) {
            xRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    private AssetFileDescriptor getFile(String name) {
        try {
            return getActivity().getAssets().openFd(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
