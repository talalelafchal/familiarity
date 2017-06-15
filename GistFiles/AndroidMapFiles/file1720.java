import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import ro.rompetrol.mobileapp.LocationService;
import ro.rompetrol.mobileapp.R;
import ro.rompetrol.mobileapp.models.StationModel;
import ro.rompetrol.mobileapp.utils.DirectionsHelper;
import rx.Observer;

/**
 * Created by Alex Nitu on 4/14/2016.
 */
public class StationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = StationListAdapter.class.getSimpleName();

    public static final int STATION_ITEM_VIEW = 0;
    public static final int LOADING_ITEM_VIEW = 1;

    public static final String LANGUAGE_ROMANIAN = "ro";

    private Context mContext;
    private int mListItemHeight;

    private List<StationModel> mItems = new ArrayList<>();

    private LatLng mMyLastLocation;

    private boolean mIsLoadingFooterAdded;

    private DirectionsHelper mDirectionsHelper = new DirectionsHelper();
    private Map<StationViewHolder, Observer> mRequestMap = new HashMap<>();

    public StationListAdapter(Context context) {
        mContext = context;
        mMyLastLocation = LocationService.mLastLocation;
    }

    public void setItems(List<StationModel> items) {
        mItems = items;
    }

    public void addAll(Collection<StationModel> items) {
        mItems.addAll(items);
    }

    public void animateTo(List<StationModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public StationModel removeItem(int position) {
        final StationModel model = mItems.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, StationModel model) {
        mItems.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final StationModel model = mItems.remove(fromPosition);
        mItems.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void clear() {
        mItems.clear();
        mIsLoadingFooterAdded = false;
        notifyDataSetChanged();
    }

    public void addLoadingFooter() {
        mIsLoadingFooterAdded = true;
        addItem(mItems.size(), new StationModel());
    }

    public void removeLoadingFooter() {
        if (!mIsLoadingFooterAdded) {
            return;
        }
        mIsLoadingFooterAdded = false;

        int position = mItems.size() - 1;
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setListItemHeight(int listItemHeight) {
        mListItemHeight = listItemHeight;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case STATION_ITEM_VIEW:
                View itemView = inflater.inflate(R.layout.list_item_station, parent, false);

                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                lp.height = mListItemHeight;

                return new StationViewHolder(itemView);
            case LOADING_ITEM_VIEW:
                itemView = inflater.inflate(R.layout.list_item_loading, parent, false);
                return new LoadingViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case STATION_ITEM_VIEW:
                StationModel item = mItems.get(position);

                StationViewHolder svh = (StationViewHolder) holder;
                svh.mStationNameTv.setText(item.getName());
//                svh.mDistanceTv.setText(item.getDistance());
                //holder.mEtaTv.setText("");
                Glide.with(mContext)
                        .load("")
                        .placeholder(R.drawable.ic_station_placeholder)
                        .centerCrop()
                        .into(svh.mStationIv);

                String distance = item.getDistance();
                if (TextUtils.isEmpty(distance)) {
                    requestDistance(item, svh);

                    svh.mDirectionsPb.setVisibility(View.VISIBLE);
                    svh.mDistanceTv.setVisibility(View.INVISIBLE);
                    svh.mEtaTv.setVisibility(View.INVISIBLE);
                } else {
                    svh.mDirectionsPb.setVisibility(View.INVISIBLE);
                    svh.mDistanceTv.setVisibility(View.VISIBLE);
                    svh.mEtaTv.setVisibility(View.VISIBLE);

                    svh.mDistanceTv.setText(distance);
                    svh.mEtaTv.setText(item.getEta());
                }
                break;
            case LOADING_ITEM_VIEW:
                LoadingViewHolder lvh = (LoadingViewHolder) holder;
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ((position == (mItems.size() - 1)) && mIsLoadingFooterAdded) ? LOADING_ITEM_VIEW : STATION_ITEM_VIEW;
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    private void applyAndAnimateRemovals(List<StationModel> newModels) {
        for (int i = mItems.size() - 1; i >= 0; i--) {
            final StationModel model = mItems.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<StationModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final StationModel model = newModels.get(i);
            if (!mItems.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<StationModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final StationModel model = newModels.get(toPosition);
            final int fromPosition = mItems.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void requestDistance(final StationModel stationModel, final StationViewHolder svh) {
        if (TextUtils.isEmpty(stationModel.getLng()) || TextUtils.isEmpty(stationModel.getLat())) {
            svh.mDistanceTv.setText("-");
            svh.mEtaTv.setText("");
            return;
        }

        final Observer<Direction> observer = new Observer<Direction>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "getDirections error");
                svh.mDistanceTv.setText("-");
                svh.mEtaTv.setText("-");
            }

            @Override
            public void onNext(Direction direction) {
                Log.d(TAG, "getDirections success");
                String status = direction.getStatus();
                String distance = "";
                String eta = "";
                if (status.equals(RequestResult.OK)) {
                    Leg mainLeg = direction.getRouteList().get(0).getLegList().get(0);
                    eta = mainLeg.getDuration().getText();
                    distance = mainLeg.getDistance().getText();
                }
                stationModel.setDistance(distance);
                stationModel.setEta(eta);

                boolean isViewStillVisible = mRequestMap.get(svh) == this;
                if (isViewStillVisible) {
                    svh.mEtaTv.setVisibility(View.VISIBLE);
                    svh.mDistanceTv.setVisibility(View.VISIBLE);
                    svh.mDirectionsPb.setVisibility(View.INVISIBLE);

                    svh.mDistanceTv.setText(!TextUtils.isEmpty(distance) ? distance : "-");
                    svh.mEtaTv.setText(!TextUtils.isEmpty(eta) ? eta : "");
                }
                mRequestMap.remove(svh);
            }
        };

        mRequestMap.put(svh, observer);

        mDirectionsHelper.calculateDistance(new LatLng(44.426767, 26.102538),
                new LatLng(Double.parseDouble(stationModel.getLat()), Double.parseDouble(stationModel.getLng())),
                observer);
    }

    class StationViewHolder extends RecyclerView.ViewHolder {

        UUID mUUID = UUID.randomUUID();

        @Bind(R.id.station_iv) ImageView mStationIv;
        @Bind(R.id.station_name_tv) TextView mStationNameTv;
        @Bind(R.id.direction_progressBar) ProgressBar mDirectionsPb;
        @Bind(R.id.distance_tv) TextView mDistanceTv;
        @Bind(R.id.eta_tv) TextView mEtaTv;

        public StationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(mOnClickListener);
        }

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(mItems.get(getAdapterPosition()));
                mContext.startActivity(new Intent(mContext, StationDetailsActivity.class));
            }
        };

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StationViewHolder that = (StationViewHolder) o;

            return mUUID != null ? mUUID.equals(that.mUUID) : that.mUUID == null;

        }

        @Override
        public int hashCode() {
            return mUUID != null ? mUUID.hashCode() : 0;
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        View mItemView;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
        }
    }
}