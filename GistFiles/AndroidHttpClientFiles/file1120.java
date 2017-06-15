package com.demohour.ui.fragment;

import android.view.View;

import com.demohour.R;
import com.demohour.adapter.ProductAdapter;
import com.demohour.domain.BaseLogic;
import com.demohour.domain.ProductLogic;
import com.demohour.domain.model.ProductListModel;
import com.demohour.ui.fragment.base.BaseFragment;
import com.demohour.widget.cube.GridViewWithHeaderAndFooter;
import com.demohour.widget.cube.LoadMoreContainer;
import com.demohour.widget.cube.LoadMoreGridViewContainer;
import com.demohour.widget.cube.LoadMoreHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionRes;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by 李涛 on 15-1-27.
 */
@EFragment(R.layout.fragment_main_product)
public class MainProductFragment extends BaseFragment implements BaseLogic.DHPullRefreshHandle,PtrHandler,LoadMoreHandler {

    @ViewById(R.id.load_more_grid_view_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;
    @ViewById(R.id.load_more_grid_view)
    GridViewWithHeaderAndFooter mGridView;
    @ViewById(R.id.load_more_grid_view_container)
    LoadMoreGridViewContainer mLoadMoreContainer;

    @Bean
    ProductAdapter adapter;

    private int page=1;

    @DimensionRes
    float space1;

    @AfterViews
    void init(){
        initRefreshView();
        initView();
        ProductLogic.Instance().getProjectList(getActivity(),httpClient, BaseLogic.RefreshType.NORMAL,this,1,"latest");
    }

    private void initView(){
        httpClient=getHttpClicet();
        //添加滑动事件，来处理滑动时图像重写加载得卡顿问题
        mGridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false,true));



    }
    private void initRefreshView(){
        MaterialHeader ptrHeader = new MaterialHeader(getActivity());
        PtrFrameLayout.LayoutParams lp = new PtrFrameLayout.LayoutParams(-1, -2);
        ptrHeader.setLayoutParams(lp);
        ptrHeader.setPadding(0, (int)space1, 0, (int)space1);
        ptrHeader.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setHeaderView(ptrHeader);
        mPtrFrameLayout.addPtrUIHandler(ptrHeader);
        mPtrFrameLayout.setPtrHandler(this);
        mLoadMoreContainer.setAutoLoadMore(true);
        mLoadMoreContainer.useDefaultHeader();
        mGridView.setAdapter(adapter);
//        mLoadMoreContainer.setLoadMoreHandler(this);

    }

    @Override
    public void pullUpRefresh(Object responseObj) {
        ProductListModel model=(ProductListModel)responseObj;
        adapter.reloadList(model.getProjects());
    }

    @Override
    public void pullDownRefresh(Object responseObj) {
        ProductListModel model=(ProductListModel)responseObj;
        adapter.appendList(model.getProjects());
    }

    @Override
    public void loadFinish() {
        mPtrFrameLayout.refreshComplete();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        httpClient.cancelAllRequests(true);
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View view, View header) {
        return PtrDefaultHandler.checkContentCanBePulledDown(frame, mGridView, header);
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        page=1;
        ProductLogic.Instance().getProjectList(getActivity(),httpClient, BaseLogic.RefreshType.NORMAL,this,page,"latest");
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {
        ProductLogic.Instance().getProjectList(getActivity(),httpClient, BaseLogic.RefreshType.NORMAL,this,++page,"latest");
    }
}
