import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ro.rompetrol.mobileapp.BaseFragment;
import ro.rompetrol.mobileapp.R;
import ro.rompetrol.mobileapp.models.StationModel;
import ro.rompetrol.mobileapp.utils.UIUtil;
import rx.functions.Action1;

/**
 * Created by Alex Nitu on 5/13/2016.
 */
public class StationListFragment extends BaseFragment {

    public static final String TAG = StationListFragment.class.getSimpleName();

    public static final String EXTRA_STATION_TYPE_ID = "station_type_id";

    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.stations_rv) RecyclerView mStationsRv;

    private LinearLayoutManager mLayoutManager;

    private StationListAdapter mAdapter;

    private StationPagerAdapter mVpAdapter;

    private String mStationTypeId;

    private List<StationModel> mItems = new ArrayList<>();


    public static StationListFragment newInstance() {
        StationListFragment fragment = new StationListFragment();
        return fragment;
    }

    public static StationListFragment newInstance(String typeId) {
        Bundle args = new Bundle();
        args.putString(EXTRA_STATION_TYPE_ID, typeId);

        StationListFragment fragment = new StationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mStationTypeId = args.getString(EXTRA_STATION_TYPE_ID);
        }
    }

    //TODO handle no internet loading stuck

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new StationListAdapter(getActivity());
        mAdapter.setListItemHeight(getListItemHeight());

        mStationsRv.setHasFixedSize(true);
        mStationsRv.setLayoutManager(mLayoutManager);
        mStationsRv.addOnScrollListener(mRecyclerViewOnScrollListener);
        mStationsRv.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mProgressBar.setVisibility(View.VISIBLE);

        getStations();
    }

    private void getStations() {
        mStationHelper.stations(mStationTypeId, null, new Action1<List<StationModel>>() {
            @Override
            public void call(List<StationModel> stationModels) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mItems.addAll(stationModels);
                mAdapter.animateTo(mItems);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMoreItems() {
        mAdapter.addLoadingFooter();
        mStationHelper.stations(mStationTypeId, null, new Action1<List<StationModel>>() {
            @Override
            public void call(List<StationModel> stationModels) {
                mAdapter.removeLoadingFooter();
                mItems.addAll(stationModels);
                mAdapter.animateTo(mItems);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mAdapter.removeLoadingFooter();
                //TODO
            }
        });
    }

    private int getListItemHeight() {
        //Height has to be 1/3 of available space
        //(Total height - (StatusBar height + ActionBar height)) / 3
        return (int) ((UIUtil.getDisplayHeight(getActivity()) - UIUtil.convertDpToPixel(56 + 24)) / 3);
    }

    private RecyclerView.OnScrollListener mRecyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();

            if (mStationHelper.isLoading()) {
                return;
            }
            if (((visibleItemCount + firstVisibleItemPosition) >= totalItemCount)
                    && (firstVisibleItemPosition >= 0)
                    && (totalItemCount >= StationHelper.PAGE_SIZE)) {
                loadMoreItems();
            }
        }
    };

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mItems.clear();
            mAdapter.clear();
            getStations();
        }
    };
}
