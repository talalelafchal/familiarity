package com.mykey.shared.ui.search;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.mykey.shared.MyKeyApp;
import com.mykey.shared.R;
import com.mykey.shared.R2;
import com.mykey.shared.data.models.Listing;
import com.mykey.shared.ui.filter.FilterActivity;
import com.mykey.shared.ui.listings.SharedListingsFragment;
import com.mykey.shared.utils.Dialogs;
import com.mykey.shared.utils.Utils;
import com.mykey.shared.widget.AppSpinner;
import com.mykey.shared.widget.ListingView;
import com.mykey.shared.widget.LocationsSearchView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchFragment extends SharedListingsFragment<SearchPresenter> implements SearchView {

    @BindView(R2.id.searchView)
    LocationsSearchView locationsSearchView;
    @BindView(R2.id.mapListButtonsContainer)
    View mapListButtonsContainer;
    @BindView(R2.id.btnMap)
    View btnMap;
    @BindView(R2.id.btnList)
    View btnList;
    @BindView(R2.id.listContainer)
    View listContainer;
    @BindView(R2.id.spSort)
    AppSpinner spSort;
    @BindView(R2.id.mapContainer)
    View mapContainer;
    @BindView(R2.id.mapView)
    MapView mapView;
    @BindView(R2.id.blackout)
    View blackout;

    private GoogleMap googleMap;
    private AnimatorSet animatorUpIn;
    private AnimatorSet animatorUpOut;
    private AnimatorSet animatorDownIn;
    private AnimatorSet animatorDownOut;
    private ListingView openedListingView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter(this, this, activity);
    }

    @Override
    protected int getEmptyViewTitleRes() {
        return R.string.empty_listings;
    }

    @Override
    protected int getEmptyViewMessageRes() {
        return 0;
    }

    @Override
    protected boolean hasListingParams() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        spSort.setSelectedItem(getString(R.string.sort_by_date));
        spSort.setOnItemsSelectedListener(new AppSpinner.OnItemsSelectedListener() {
            @Override
            public void onSelected() {
                presenter.getSortListings(spSort.getSelectedItem());
            }
        });
        locationsSearchView.setOnSearchItemClick(new LocationsSearchView.OnSearchItemClick() {
            @Override
            public void onClick(int position) {
                locationsSearchView.setHint(locationsSearchView.getItemByPosition(position));
                Utils.hideKeyboard(activity);
                presenter.getListings(locationsSearchView.getItemByPosition(position));
            }
        });
        locationsSearchView.setOnBackButtonPressed(new LocationsSearchView.OnBackButtonClick() {
            @Override
            public void onClick() {
                Utils.hideKeyboard(activity);
                presenter.clearLocationSubscription();
                mapListButtonsContainer.setVisibility(View.VISIBLE);
                blackout.setVisibility(View.GONE);
            }
        });
        locationsSearchView.setLocationsOnFocusChangedListener(new LocationsSearchView.OnFocusChangedListener() {
            @Override
            public void onClick() {
                mapListButtonsContainer.setVisibility(View.GONE);
                blackout.setVisibility(View.VISIBLE);
            }
        });
        locationsSearchView.setOnTextChanged(new LocationsSearchView.OnTextChanged() {
            @Override
            public void onClick(String query) {
                presenter.getLocationList(query, true);
            }
        });

        srListings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getListings(true);
            }
        });

        presenter.getFilter();
        initMap(savedInstanceState);
        initAnimation();
        return view;
    }

    @Override
    public void onDestroyView() {
        mapView.onPause();
        mapView.onDestroy();
        super.onDestroyView();
    }

    @OnClick(R2.id.btnMap)
    public void onMap() {
        setMapVisibility(true);
    }

    @OnClick(R2.id.btnList)
    public void onList() {
        setMapVisibility(false);
    }

    @OnClick(R2.id.btnFilter)
    public void onFilter() {
        activity.startActivity(new Intent(activity, FilterActivity.class));
    }

    @OnClick(R2.id.btnMapType)
    public void onMapType() {
        if (googleMap == null) return;
        if (googleMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void initMap(@Nullable Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        final Listing listing = (Listing) marker.getTag();
                        if (listing != null) {
                            openedListingView = new ListingView(activity, true);
                            openedListingView.init(listing);
                            openedListingView.setLikeClickListener(new ListingView.OnLikeClickListener() {
                                @Override
                                public void onClick(Listing listing) {
                                    presenter.updateListingLikeStatus(listing);
                                }
                            });
                            openedListingView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyKeyApp.showListingDetails(activity, listing.id);
                                }
                            });
                            Dialog dialog = Dialogs.showContentDialog(activity, openedListingView);
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    openedListingView = null;
                                }
                            });
                        }
                        return true;
                    }
                });
            }
        });
    }

    private void initAnimation() {
        int cameraDistance = (int) getResources().getDimension(R.dimen.camera_distance);
        mapView.setCameraDistance(cameraDistance);
        listContainer.setCameraDistance(cameraDistance);
        animatorUpIn = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.flip_up_in);
        animatorUpOut = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.flip_up_out);
        animatorDownIn = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.flip_down_in);
        animatorDownOut = (AnimatorSet) AnimatorInflater.loadAnimator(activity, R.animator.flip_down_out);
        animatorUpIn.setTarget(mapContainer);
        animatorUpOut.setTarget(listContainer);
        animatorDownIn.setTarget(listContainer);
        animatorDownOut.setTarget(mapContainer);
        animatorUpIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                btnMap.setVisibility(View.GONE);
                btnList.setVisibility(View.VISIBLE);
                mapContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listContainer.setVisibility(View.GONE);
                updateMapZoom();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorDownIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                btnList.setVisibility(View.GONE);
                btnMap.setVisibility(View.VISIBLE);
                listContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mapContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void setMapVisibility(boolean needShow) {
        if (!animatorUpIn.isRunning() && !animatorUpOut.isRunning()
                && !animatorDownIn.isRunning() && !animatorDownOut.isRunning()) {
            if (needShow) {
                animatorUpIn.start();
                animatorUpOut.start();
            } else {
                animatorDownIn.start();
                animatorDownOut.start();
            }
        }
    }

    private void addMapMarkers(List<Listing> listings) {
        if (googleMap == null || listings == null || listings.isEmpty()) return;
        IconGenerator iconGenerator = new IconGenerator(activity);
        TextView mapMarkerView = (TextView) LayoutInflater.from(activity).inflate(R.layout.view_map_marker, null);
        iconGenerator.setBackground(null);
        iconGenerator.setContentView(mapMarkerView);
        for (Listing listing : listings) {
            if (listing.price > 0) {
                String priceValue = Utils.formatValue(listing.price, true);
                mapMarkerView.setText(getString(R.string.format_price, priceValue));
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()))
                        .position(new LatLng(listing.latitude, listing.longitude)));
                marker.setTag(listing);
            }
        }
        updateMapZoom();
    }

    private void updateMapZoom() {
        if (googleMap == null || mapContainer.getVisibility() != View.VISIBLE) return;
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (int i = 0; i < adapter.getItemCount(); i++) {
            Listing listing = adapter.getItem(i);
            bounds.include(new LatLng(listing.latitude, listing.longitude));
        }
        try {
            int padding = (int) (getResources().getDisplayMetrics().widthPixels * 0.15);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), padding));
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showFirstListings(List<Listing> listings) {
        super.showFirstListings(listings);
        if (googleMap != null) {
            googleMap.clear();
        }
        addMapMarkers(listings);
    }

    @Override
    public void showNextListings(List<Listing> listings) {
        super.showNextListings(listings);
        addMapMarkers(listings);
    }

    @Override
    public void updateListingLikeStatus(Listing listing) {
        super.updateListingLikeStatus(listing);
        if (openedListingView != null) {
            openedListingView.updateLikeStatus(listing.liked);
        }
    }

    @Override
    public void onKeyboardHiddenChanged(boolean hidden) {
        super.onKeyboardHiddenChanged(hidden);
        if (hidden) {
            locationsSearchView.onBackButtonPressed();
        }
    }

    @Override
    public void onLocationsLoaded(List<String> locationsList) {
        if (locationsList != null && locationsSearchView.getSearchQuery().length() > 0) {
            locationsSearchView.addAll(locationsList);
        }
    }
}