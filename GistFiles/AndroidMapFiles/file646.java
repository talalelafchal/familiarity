package com.mariniero.aga.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.geo.dating.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.ui.IconGenerator;
import com.mariniero.aga.Config;
import com.mariniero.aga.ui.adapters.PlaceUsersAdapter;
import com.mariniero.aga.ui.adapters.PlacesAdapter;
import com.mariniero.aga.ui.animators.SearchTooltips;
import com.mariniero.aga.ui.comparators.UsersComparator;
import com.mariniero.aga.ui.fragments.FriendsMapFragment;
import com.mariniero.aga.ui.fragments.PlacesMapFragment;
import com.mariniero.aga.ui.fragments.UsersMapFragment;
import com.mariniero.aga.ui.map.ClusterManager;
import com.mariniero.aga.ui.map.DefaultClusterRenderer;
import com.mariniero.aga.ui.map.VisibleNonHierarchicalDistanceBasedAlgorithm;
import com.mariniero.aga.ui.widget.NonSwipeableViewPager;
import com.mariniero.aga.ui.widget.RangeSeekBar;
import com.mariniero.aga.ui.widget.SlidingTabLayout;
import com.mariniero.aga.utils.Lists;
import com.mariniero.aga.utils.UIUtils;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import geo.dating.engine.api.SearchPlacesFilters;
import geo.dating.engine.api.SearchUsersFilters;
import geo.dating.engine.enums.Sex;
import geo.dating.engine.general.LocationServer;
import geo.dating.engine.geo.City;
import geo.dating.engine.models.GeoDatingEngine;
import geo.dating.engine.models.Photo;
import geo.dating.engine.models.Place;
import geo.dating.engine.models.UserPlain;
import geo.dating.engine.results.PlacesOperationResult;
import geo.dating.engine.results.ProfileOperationResult;
import geo.dating.engine.results.UsersOperationResult;

//import com.google.maps.android.clustering.ClusterManager;

//import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class SearchActivity extends BaseNavigationActivity implements UsersMapFragment.Listener,
        FriendsMapFragment.Listener, PlacesMapFragment.Listener {

    private static final String LOG_TAG = SearchActivity.class.getSimpleName();
    private static final int NUM_SECTIONS_SEARCH = 3;
    private static final int SECTION_SEARCH_USERS = 0;
    private static final int SECTION_SEARCH_FRIENDS = 1;
    private static final int SECTION_SEARCH_PLACES = 2;

    private static final float CLOSE_ZOOM = 18;
    private static final int SEARCH_LIMIT = 50;

    private final int LOCATION_SETTING = 11000;
    private int mMarkerDimension;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout = null;
    private UsersMapFragment mUsersMapFragment;
    private FriendsMapFragment mFriendsMapFragment;
    private PlacesMapFragment mPlacesMapFragment;
    private ViewGroup mFiltersViewGroup;
    private TextView mMaxAgeTextView;
    private TextView mMinAgeTextView;
    private SearchUsersFilters mSearchUsersFilters;
    private SearchPlacesFilters mSearchPlacesFilters;
    private Spinner mCitiesSpinner;
    private List<City> mCities;
    private Resources mResources;
    private SharedPreferences mSharedPreferences;
    private long mMaxDistancePreference;
    private long mMinAgePreference;
    private long mMaxAgePreference;

    private List<Place> mPlaces;
    private List<UserPlain> mUsers;
    private List<UserPlain> mFriends;
    private ClusterManager<PlaceMarker> mPlacesClusterManager;
    private ClusterManager<UserMarker> mUsersClusterManager;
    private ClusterManager<UserMarker> mFriendsClusterManager;
    private ArrayList<PlaceMarker> mPlaceMarkers;
    private ArrayList<UserMarker> mUsersMarkers;
    private ArrayList<UserMarker> mFriendsMarkers;


    private View mPlacesFiltersLayout;
    private View mUsersFiltersLayout;
    private SeekBar mUsersRadiusSeekBar;
    private SeekBar mPlacesRadiusSeekBar;
    private TextView mUsersRadiusTextView;
    private TextView mPlacesRadiusTextView;

    private ImageButton mClosePlacesFiltersButton;
    private ImageButton mCloseUsersFiltersButton;
    private Button mSearchPlacesFiltersButton;
    private Button mSearchUsersFiltersButton;

    private PlacesOperationResult mPlacesOperationResult;
    private TextView mUsersFiltersLayoutTitleTextView;
    private EditText mPlacesSearchEdittext;
    private ImageButton mPlacesZoomInButton;
    private ImageButton mPlacesZoomOutButton;
    private ImageButton mPlacesZoomLocationButton;
    private EditText mUsersSearchEdittext;
    private ImageButton mUsersZoomInButton;
    private ImageButton mUsersZoomOutButton;
    private ImageButton mUsersZoomLocationButton;
    private EditText mFriendsSearchEdittext;
    private ImageButton mFriendsZoomInButton;
    private ImageButton mFriendsZoomOutButton;
    private ImageButton mFriendsZoomLocationButton;

    private String mSearchFriendsName;
    private String mSearchUsersName;
    private SearchTooltips mMapTooltip;
    private LinearLayout mTooltipHost;
    private Bitmap mMarkerPhotoEmptyBitmap;
    private DisplayMetrics mMetrics;
    private Spinner mPlacesCitiesSpinner;
    private PreCachingAlgorithmDecorator<PlaceMarker> mPlacesClusteringAlgorhitm;

    private BitmapDescriptor mPlaceholderBitmapDescriptor;
    private IconGenerator mIconGenerator;

    private MarkerOptions mOwnMarkerOptions;
    private Marker mPlacesOwnMarker;
    private Marker mUsersOwnMarker;
    private Marker mFriendsOwnMarker;
    private RadioGroup mSexRadioGroup;
    private RadioButton mPlaceUsersGenderFemaleRadioButton;
    private RadioButton mPlaceUsersGenderMaleRadioButton;
    private UltimateRecyclerView mFriendsRecyclerView;
    private UltimateRecyclerView mPlacesRecyclerView;
    private UltimateRecyclerView mUsersRecyclerView;
    private MenuItem mMapMenuItem;
    private MenuItem mListMenuItem;
    private LinearLayoutManager mFriendsLayoutManager;
    private LinearLayoutManager mPlacesLayoutManager;
    private LinearLayoutManager mUsersLayoutManager;
    private PlaceUsersAdapter mFriendsListAdapter;
    private PlaceUsersAdapter mUsersListAdapter;
    private PlacesAdapter mPlacesListAdapter;
    private PlacesOperationResult mPlacesListOperationResult;
    private RangeSeekBar<Long> mSeekBarAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mMarkerDimension = (int) getResources().getDimension(R.dimen.custom_marker_image);

        mFriendsListAdapter = new PlaceUsersAdapter(null, this);
        mFriendsListAdapter.setMinimize();
        mUsersListAdapter = new PlaceUsersAdapter(null, this);
        mUsersListAdapter.setMinimize();
        mPlacesListAdapter = new PlacesAdapter(this);
        mPlacesListAdapter.setMinimize();

        new Thread(new Runnable() {
            @Override
            public void run() {
                MapsInitializer.initialize(getApplicationContext());
                mIconGenerator = new IconGenerator(getApplicationContext());
                mIconGenerator.setColor(getResources().getColor(R.color.theme_primary));

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                mMarkerPhotoEmptyBitmap = BitmapFactory.decodeResource(mResources, R.drawable.cap_album, options);

                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(mMarkerDimension, mMarkerDimension));
                int padding = (int) getResources().getDimension(R.dimen.custom_marker_padding);
                imageView.setPadding(padding, padding, padding, padding);
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.cap_album));
                mIconGenerator.setContentView(imageView);

                initOwnMarker();
            }
        }).start();

        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSearchUsersFilters = new SearchUsersFilters();

        mMaxDistancePreference = mSharedPreferences.getLong(Config.Preferences.MAX_DISTANCE, Config.Filters.MAX_RADIUS);
        mMinAgePreference = mSharedPreferences.getLong(Config.Preferences.MIN_AGE, Config.Filters.MIN_AGE);
        mMaxAgePreference = mSharedPreferences.getLong(Config.Preferences.MAX_AGE, Config.Filters.MAX_AGE);

        mSearchUsersFilters = new SearchUsersFilters();
        mSearchPlacesFilters = new SearchPlacesFilters();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ProfileOperationResult operationResult = GeoDatingEngine.getInstance()
                        .loadUserProfile();
                if (operationResult == null || operationResult.getUserInfo() == null) {
                    return;
                }

                mCities = GeoDatingEngine.getInstance().getCities(operationResult.getUserInfo().getCountryId());
                mSearchUsersFilters.setCityId(operationResult.getUserInfo().getCityId());
                mSearchPlacesFilters.setCityId(operationResult.getUserInfo().getCityId());
            }
        }).start();

        mSearchUsersFilters.setDistanceTo(mMaxDistancePreference);
        mSearchPlacesFilters.setDistanceTo((int) mMaxDistancePreference);
        mSearchUsersFilters.setAgeFrom((int) mMinAgePreference);
        mSearchUsersFilters.setAgeTo((int) mMaxAgePreference);

        mResources = mContext.getResources();

        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle(mResources.getString(R.string.title_activity_search));

        getActionBarToolbar().setNavigationIcon(R.drawable.icon_navi_filter);
        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFiltersBar();
            }
        });


        mTooltipHost = (LinearLayout) findViewById(R.id.tooltip_host);
        mPagerAdapter = new SearchViewPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
//        mViewPager.setOffscreenPageLimit(NUM_SECTIONS_SEARCH);

        mPagerAdapter = new SearchViewPagerAdapter(getFragmentManager());
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator);
        Resources resources = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(resources.getColor(R.color.tab_selected_strip));
        mSlidingTabLayout.setDistributeEvenly();
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mMapTooltip != null) {
                    mMapTooltip.destroy();
                }

                hideSoftKeyboard();
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });

        View slidingTab = findViewById(R.id.sliding_tabs);

        final boolean slidingTabVisible = slidingTab != null
                && slidingTab.getVisibility() == View.VISIBLE;
        int slidingTabClearance = slidingTabVisible ?
                mResources.getDimensionPixelSize(R.dimen.slidingtab_height) : 0;

        int actionBarClearance = UIUtils.calculateActionBarSize(this);

        Log.v(LOG_TAG, "AB clearance: " + actionBarClearance + " ST clearance: " + slidingTabClearance);
        setProgressBarTopWhenActionBarShown(actionBarClearance + slidingTabClearance);
        mViewPager.setPageMargin(actionBarClearance + slidingTabClearance);

        setupPlacesListPlaceholders();
    }

    private void initOwnMarker() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mIconGenerator == null) {
                    MapsInitializer.initialize(getApplicationContext());
                    if (mIconGenerator == null) {
                        mUIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initOwnMarker();
                            }
                        }, 1000);
                        return;
                    }
                }

                try {
                    IconGenerator iconGenerator = new IconGenerator(getApplicationContext());
                    iconGenerator.setBackground(null);
                    iconGenerator.setContentView(getLayoutInflater()
                            .inflate(R.layout.map_window_own_user_marker_layout, null));
                    mOwnMarkerOptions = new MarkerOptions();
                    mOwnMarkerOptions.position(new LatLng(GeoDatingEngine.getInstance()
                            .getLocationServerInstance().getLatitude(), GeoDatingEngine
                            .getInstance().getLocationServerInstance().getLongitude()))
                            .title("Ваша текущая позиция")
                            .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon()));
                    mPlaceholderBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon());
                } catch (NullPointerException exception) {
                    MapsInitializer.initialize(getApplicationContext());
                    mUIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initOwnMarker();
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void toggleFiltersBar() {
        switch (mViewPager.getCurrentItem()) {
            case SECTION_SEARCH_PLACES:
                inflateAndBindFilterPlacesLayout();
                break;
            default:
                inflateAndBindFilterUsersLayout();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LOCATION_SETTING: {
                requestDataRefresh();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void requestDataRefresh() {
        if (!LocationServer.isLocationEnable(mContext)) {
            LocationServer.showLocationSettings(this, LOCATION_SETTING);
            return;
        }
        super.requestDataRefresh();
        mOwnMarkerOptions.position(new LatLng(GeoDatingEngine.getInstance()
                .getLocationServerInstance().getLatitude(), GeoDatingEngine.getInstance()
                .getLocationServerInstance().getLongitude()));
        switch (mViewPager.getCurrentItem()) {
            case SECTION_SEARCH_PLACES:
                requestPlacesDataRefresh();
                break;
            case SECTION_SEARCH_FRIENDS:
                requestFriendsDataRefresh();
                break;
            case SECTION_SEARCH_USERS:
                requestUsersDataRefresh();
                break;
        }

        onRefreshingStateChanged(false);
    }

    private Marker updateOwnMarker(GoogleMap map, Marker marker) {
        marker.remove();
        mOwnMarkerOptions.position(new LatLng(GeoDatingEngine.getInstance()
                .getLocationServerInstance().getLatitude(), GeoDatingEngine.getInstance()
                .getLocationServerInstance().getLongitude()));
        return map.addMarker(mOwnMarkerOptions);
    }

    private void requestUsersDataRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SearchUsersFilters filters = mSearchUsersFilters.duplicate();
                filters.setShowFriend(false);
                filters.setName(mSearchUsersName);
                final UsersOperationResult operationResult = GeoDatingEngine.getInstance()
                        .searchUsers(filters);
                final UsersOperationResult listOperationResult = GeoDatingEngine.getInstance()
                        .getUsersListGeolocation(filters, 0, SEARCH_LIMIT);
                runOnUiThread(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        if (mUsersMapFragment != null) {
                            mUsersMapFragment.getGoogleMap().clear();
                            mUsersOwnMarker = mUsersMapFragment.getGoogleMap().addMarker(mOwnMarkerOptions);
                        }

                        if (mSearchUsersFilters == null) {
                            mUsersListAdapter.setDataset(listOperationResult.getUserList());
                        }
                        if (mSearchUsersFilters != null && mSearchUsersFilters.getAgeTo() == Config.Filters.MAX_AGE && mSearchUsersFilters.getAgeFrom() == Config.Filters.MIN_AGE) {
                            mUsersListAdapter.setDataset(listOperationResult.getUserList());
                        } else if (mSearchUsersFilters != null) {
                            List<UserPlain> list = listOperationResult.getUserList();
                            Collections.sort(list, UsersComparator.AGE_COMPARATOR);
                            mUsersListAdapter.setDataset(list);
                        }

                        mUsersClusterManager.clearItems();
                        if (failCheckOperationResult(operationResult) || !operationResult.isSuccess()) {
                            return;
                        }

                        mUsers = operationResult.getUserList();
                        mUsersMarkers = Lists.newArrayList();
                        for (UserPlain user : mUsers) {
                            mUsersMarkers.add(new UserMarker(user));
                        }

                        LatLngBounds.Builder builder = LatLngBounds.builder();
                        for (UserMarker user : mUsersMarkers) {
                            mUsersClusterManager.addItem(user);
                            builder.include(user.getPosition());
                        }

                        if (mUsersMarkers.size() != 0) {
                            final LatLngBounds bounds = builder.build();
                            if (mUsersMapFragment != null) {
                                mUsersMapFragment
                                        .getGoogleMap()
                                        .animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            }
                        }

                        mUsersClusterManager.cluster();
                        if (mUsersMapFragment != null && mUsersMapFragment.getProgress() != null) {
                            mUsersMapFragment.getProgress().setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    private void requestFriendsDataRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final SearchUsersFilters filters = mSearchUsersFilters.duplicate();
                filters.setShowFriend(true);
                filters.setName(mSearchUsersName);
                final UsersOperationResult operationResult = GeoDatingEngine.getInstance()
                        .searchUsers(filters);
                final UsersOperationResult listOperationResult = GeoDatingEngine.getInstance()
                        .getUsersListGeolocation(filters, 0, SEARCH_LIMIT);
                runOnUiThread(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        if (mFriendsMapFragment != null) {
                            mFriendsMapFragment.getGoogleMap().clear();
                            mFriendsOwnMarker = mFriendsMapFragment.getGoogleMap().addMarker(mOwnMarkerOptions);
                        }

                        if (mSearchUsersFilters == null) {
                            mFriendsListAdapter.setDataset(listOperationResult.getUserList());
                        }
                        if (mSearchUsersFilters != null && mSearchUsersFilters.getAgeTo() == Config.Filters.MAX_AGE && mSearchUsersFilters.getAgeFrom() == Config.Filters.MIN_AGE) {
                            mFriendsListAdapter.setDataset(listOperationResult.getUserList());
                        } else if (mSearchUsersFilters != null) {
                            List<UserPlain> list = listOperationResult.getUserList();
                            Collections.sort(list, UsersComparator.AGE_COMPARATOR);
                            mFriendsListAdapter.setDataset(list);
                        }

                        mFriendsClusterManager.clearItems();
                        if (failCheckOperationResult(operationResult) || !operationResult.isSuccess()) {
                            return;
                        }

                        mFriends = operationResult.getUserList();
                        mFriendsMarkers = Lists.newArrayList();
                        for (UserPlain user : mFriends) {
                            mFriendsMarkers.add(new UserMarker(user));
                        }

                        LatLngBounds.Builder builder = LatLngBounds.builder();
                        for (UserMarker user : mFriendsMarkers) {
                            mFriendsClusterManager.addItem(user);
                            builder.include(user.getPosition());
                        }

                        if (mFriendsMarkers.size() != 0) {
                            final LatLngBounds bounds = builder.build();
                            if (mFriendsMapFragment != null) {
                                mFriendsMapFragment
                                        .getGoogleMap()
                                        .animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            }
                        }

                        mFriendsClusterManager.cluster();
                        if (mFriendsMapFragment != null && mFriendsMapFragment.getProgress() != null) {
                            mFriendsMapFragment.getProgress().setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    private void requestPlacesDataRefresh() {
        if (mPlacesMapFragment.getProgress().getVisibility() == View.VISIBLE) {
            return;
        }

        mPlacesMapFragment.getProgress().setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlacesOperationResult = GeoDatingEngine.getInstance().searchPlaces(mSearchPlacesFilters);
                mPlacesListOperationResult = GeoDatingEngine.getInstance()
                        .getPlacesGeolocation(mSearchPlacesFilters, 0, SEARCH_LIMIT);
                runOnUiThread(new Runnable() { // This thread runs in the UI
                    @Override
                    public void run() {
                        if (mPlacesMapFragment != null) {
                            mPlacesMapFragment.getGoogleMap().clear();
                            mPlacesOwnMarker = mPlacesMapFragment.getGoogleMap().addMarker(mOwnMarkerOptions);
                        }

                        mPlacesListAdapter.setDataset(mPlacesListOperationResult.getPlaceList());

                        mPlacesClusterManager.clearItems();
                        if (failCheckOperationResult(mPlacesOperationResult) || !mPlacesOperationResult.isSuccess()) {
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mPlaces = mPlacesOperationResult.getPlaceList();
                                if (mPlaces != null && mPlaces.size() > 200) {
                                    mPlaces = mPlaces.subList(0, 200);
                                }

                                mPlaceMarkers = Lists.newArrayList();
                                for (Place place : mPlaces) {
                                    mPlaceMarkers.add(new PlaceMarker(place));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        LatLngBounds.Builder builder = LatLngBounds.builder();
                                        for (PlaceMarker place : mPlaceMarkers) {
                                            builder.include(place.getPosition());
                                        }

                                        mPlacesClusteringAlgorhitm.addItems(mPlaceMarkers);
                                        if (mPlaceMarkers.size() != 0) {
                                            final LatLngBounds bounds = builder.build();
                                            if (mPlacesMapFragment != null) {
                                                mPlacesMapFragment
                                                        .getGoogleMap()
                                                        .animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                                            }
                                        }

                                        mPlacesClusterManager.cluster();
                                        if (mPlacesMapFragment != null && mPlacesMapFragment.getProgress() != null) {
                                            mPlacesMapFragment.getProgress().setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }

    protected void requestPlacesDataRefreshWithOffset(final int offset) {
        if (offset < SEARCH_LIMIT) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final PlacesOperationResult operationResult = GeoDatingEngine.getInstance()
                        .getPlacesGeolocation(mSearchPlacesFilters, offset, SEARCH_LIMIT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        if (operationResult.isSuccess()) {
                            mPlacesListAdapter.addList(operationResult.getPlaceList());
                            onRefreshingStateChanged(false);
                        }
                    }
                });
            }
        }).start();
    }

    protected void requestUsersDataRefreshWithOffset(final int offset) {
        if (offset < SEARCH_LIMIT) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final SearchUsersFilters filters = mSearchUsersFilters.duplicate();
                filters.setShowFriend(false);
                filters.setName(mSearchUsersName);
                final UsersOperationResult operationResult = GeoDatingEngine.getInstance()
                        .getUsersListGeolocation(filters, 0, SEARCH_LIMIT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        if (operationResult.isSuccess()) {
                            if (mSearchUsersFilters == null) {
                                mUsersListAdapter.addList(operationResult.getUserList());
                            }
                            if (mSearchUsersFilters != null && mSearchUsersFilters.getAgeTo() == Config.Filters.MAX_AGE && mSearchUsersFilters.getAgeFrom() == Config.Filters.MIN_AGE) {
                                mUsersListAdapter.addList(operationResult.getUserList());
                            } else if (mSearchUsersFilters != null) {
                                List<UserPlain> list = operationResult.getUserList();
                                Collections.sort(list, UsersComparator.AGE_COMPARATOR);
                                mUsersListAdapter.addList(operationResult.getUserList());
                            }

                            onRefreshingStateChanged(false);
                        }
                    }
                });
            }
        }).start();
    }

    protected void requestFriendsDataRefreshWithOffset(final int offset) {
        if (offset < SEARCH_LIMIT) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final SearchUsersFilters filters = mSearchUsersFilters.duplicate();
                filters.setShowFriend(true);
                filters.setName(mSearchUsersName);
                final UsersOperationResult operationResult = GeoDatingEngine.getInstance()
                        .getUsersListGeolocation(filters, 0, SEARCH_LIMIT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        if (operationResult.isSuccess()) {
                            if (mSearchUsersFilters == null) {
                                mFriendsListAdapter.addList(operationResult.getUserList());
                            }
                            if (mSearchUsersFilters != null && mSearchUsersFilters.getAgeTo() == Config.Filters.MAX_AGE && mSearchUsersFilters.getAgeFrom() == Config.Filters.MIN_AGE) {
                                mFriendsListAdapter.addList(operationResult.getUserList());
                            } else if (mSearchUsersFilters != null) {
                                List<UserPlain> list = operationResult.getUserList();
                                Collections.sort(list, UsersComparator.AGE_COMPARATOR);
                                mFriendsListAdapter.addList(operationResult.getUserList());
                            }

                            onRefreshingStateChanged(false);
                        }
                    }
                });
            }
        }).start();
    }

    private void inflateAndBindFilterUsersLayout() {
        if (mUsersFiltersLayout != null) {
            mUsersFiltersLayoutTitleTextView.setText(getResources().getString(
                    mViewPager.getCurrentItem() == SECTION_SEARCH_USERS ?
                            R.string.search_filter_title_users :
                            R.string.search_filter_title_friends));
            animateAndShow(mUsersFiltersLayout);
            return;
        }

        mUsersFiltersLayout = LayoutInflater.from(mContext)
                .inflate(R.layout.search_filters_users, null);
        mFiltersViewGroup = (ViewGroup) mUsersFiltersLayout.findViewById(R.id.search_filters_root_layout);

        mUsersFiltersLayoutTitleTextView = (TextView) mUsersFiltersLayout
                .findViewById(R.id.search_filters_title_textview);
        mUsersFiltersLayoutTitleTextView.setText(getResources().getString(
                mViewPager.getCurrentItem() == SECTION_SEARCH_USERS ?
                        R.string.search_filter_title_users :
                        R.string.search_filter_title_friends));


        /** Age Labels*/
        LinearLayout.LayoutParams layoutParamsTextView = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams mapaParamsTextView = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTextView.gravity = Gravity.RIGHT;

        int bodyTextColor = mResources.getColor(R.color.body_text_1);
        int themeTextColor = mResources.getColor(R.color.theme_primary);

        TextView ageTextView = new TextView(mContext);
        mMaxAgeTextView = new TextView(mContext);
        mMinAgeTextView = new TextView(mContext);

        ageTextView.setLayoutParams(mapaParamsTextView);
        mMaxAgeTextView.setLayoutParams(layoutParamsTextView);
        mMinAgeTextView.setLayoutParams(layoutParamsTextView);

        ageTextView.setTextColor(bodyTextColor);
        mMaxAgeTextView.setTextColor(themeTextColor);
        mMinAgeTextView.setTextColor(themeTextColor);

        ageTextView.setText(R.string.search_filters_label_age);
        mMaxAgeTextView.setText(String.valueOf(mSearchUsersFilters.getAgeTo()) +
                (mSearchUsersFilters.getAgeTo() == Config.Filters.MAX_AGE ? "+ " : " ") +
                mResources.getString(R.string.profile_item_years));
        mMinAgeTextView.setText(String.valueOf(mSearchUsersFilters.getAgeFrom()) + " - ");

        mUsersRadiusTextView = (TextView) mUsersFiltersLayout.findViewById(R.id.search_filters_radius_textview);
        mUsersRadiusSeekBar = (SeekBar) mUsersFiltersLayout.findViewById(R.id.search_filters_radius_seekbar);
        mUsersRadiusSeekBar.setMax(Config.Filters.MAX_RADIUS);
        mUsersRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSearchUsersFilters.setDistanceTo((long) (progress<1000 ?  progress: (progress- (progress % 1000))));
                mUsersRadiusTextView.setText(String.valueOf(mSearchUsersFilters.getDistanceTo() >= 1000 ?
                                String.valueOf(mSearchUsersFilters.getDistanceTo() / 1000) +
                                        (mSearchUsersFilters.getDistanceTo() == Config.Filters.MAX_RADIUS ? "+ " : "")
                                        + mResources.getString(R.string.profile_item_kilometers) :
                                mSearchUsersFilters.getDistanceTo()
                ));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mUsersRadiusSeekBar.setProgress((int) mSearchUsersFilters.getDistanceTo());

        if (android.os.Build.VERSION.SDK_INT > 16) {
            ageTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            mMaxAgeTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_START);
            mMinAgeTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
        }

        RangeSeekBar.DEFAULT_COLOR = ContextCompat.getColor(mContext, R.color.theme_primary_dark);
        mSeekBarAge = new RangeSeekBar<>(Config.Filters.MIN_AGE, Config.Filters.MAX_AGE, mContext);
        mSeekBarAge.setOnRangeSeekBarChangeListener(
                new RangeSeekBar.OnRangeSeekBarChangeListener<Long>() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
                        // handle changed range values
                        if (maxValue == Config.Filters.MAX_AGE && minValue == Config.Filters.MIN_AGE) {
                            mSearchUsersFilters.setAgeTo(-1);
                            mSearchUsersFilters.setAgeFrom(-1);
                        } else {
                            mSearchUsersFilters.setAgeFrom(Integer.valueOf(String.valueOf(minValue)));
                            mSearchUsersFilters.setAgeTo(Integer.valueOf(String.valueOf(maxValue)));
                        }

                        mSearchUsersFilters.setAgeFrom(Integer.valueOf(String.valueOf(minValue)));
                        mSearchUsersFilters.setAgeTo(Integer.valueOf(String.valueOf(maxValue)));
                        mMaxAgeTextView.setText(String.valueOf(maxValue) +
                                (maxValue == Config.Filters.MAX_AGE ? "+ " : " ") +
                                mResources.getString(R.string.profile_item_years));
                        mMinAgeTextView.setText(String.valueOf(minValue) + " - ");
                    }
                });

        mSeekBarAge.setSelectedMinValue((long) mSearchUsersFilters.getAgeFrom());
        mSeekBarAge.setSelectedMaxValue((long) mSearchUsersFilters.getAgeTo());

        mPlaceUsersGenderMaleRadioButton = (RadioButton) mUsersFiltersLayout
                .findViewById(R.id.search_filter_gender_male);
        mPlaceUsersGenderFemaleRadioButton = (RadioButton) mUsersFiltersLayout
                .findViewById(R.id.search_filter_gender_female);
        mPlaceUsersGenderMaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchUsersFilters.getSex() == Sex.MALE) {
                    mSearchUsersFilters.setSex(Sex.NULL);
                    mSexRadioGroup.clearCheck();
                } else {
                    mSearchUsersFilters.setSex(Sex.MALE);
                }
            }
        });

        mPlaceUsersGenderFemaleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchUsersFilters.getSex() == Sex.FEMALE) {
                    mSearchUsersFilters.setSex(Sex.NULL);
                    mSexRadioGroup.clearCheck();
                } else {
                    mSearchUsersFilters.setSex(Sex.FEMALE);
                }

            }
        });

        mSexRadioGroup = (RadioGroup) mUsersFiltersLayout.findViewById(R.id.search_filters_gender_radiogroup);
        mCitiesSpinner = (Spinner) mUsersFiltersLayout.findViewById(R.id.search_filters_city_spinner);
        mCitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position - 1 >= 0 && position - 1 < mCities.size()) {
                    mSearchUsersFilters.setCityId(mCities.get(position - 1).getId());
                } else {
                    mSearchUsersFilters.setCityId(-1);
                }
                mUsersRadiusSeekBar.setAlpha(mSearchUsersFilters.getCityId() == -1 || mSearchUsersFilters.getCityId() == mCurrentProfile.getCityId() ? 1 : .2f);
                mUsersRadiusSeekBar.setEnabled(mSearchUsersFilters.getCityId() == -1 || mSearchUsersFilters.getCityId() == mCurrentProfile.getCityId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (mCities == null) {
            getCities();
        } else {
            populateCitiesSpinner();
        }

        Switch switcher = (Switch) mUsersFiltersLayout.findViewById(R.id.search_filter_online_only_checkbox);
        if (switcher != null) {
            switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                    mSearchUsersFilters.setShowOnline(value);
                }
            });
        }

        mFiltersViewGroup.addView(ageTextView);
        LinearLayout ageLinearLayout = new LinearLayout(mContext);
        LinearLayout minAgeLinearLayout = new LinearLayout(mContext);
        minAgeLinearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        minAgeLinearLayout.setGravity(Gravity.RIGHT);

        ageLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        ageLinearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        ageLinearLayout.setGravity(Gravity.CENTER);
        minAgeLinearLayout.addView(mMinAgeTextView);
        ageLinearLayout.addView(minAgeLinearLayout);
        ageLinearLayout.addView(mMaxAgeTextView);
        mFiltersViewGroup.addView(ageLinearLayout);
        mFiltersViewGroup.addView(mSeekBarAge);

        LinearLayout radiusLinearLayout = new LinearLayout(mContext);
        radiusLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        radiusLinearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        radiusLinearLayout.setGravity(Gravity.CENTER);
        mFiltersViewGroup.addView(radiusLinearLayout);


        mCloseUsersFiltersButton = (ImageButton) mUsersFiltersLayout
                .findViewById(R.id.search_filters_button_close);
        mCloseUsersFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersFiltersLayout.setAlpha(.0f);
                mUsersFiltersLayout.setVisibility(View.GONE);
            }
        });
        mSearchUsersFiltersButton = (Button) mUsersFiltersLayout
                .findViewById(R.id.search_filters_button_search);
        if (mSearchUsersFiltersButton != null) {
            mSearchUsersFiltersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestDataRefresh();
                    mUsersFiltersLayout.setAlpha(.0f);
                    mUsersFiltersLayout.setVisibility(View.GONE);
                }
            });
        }

        inflateLayout(mUsersFiltersLayout);
        animateAndShow(mUsersFiltersLayout);
    }

    private void inflateAndBindFilterPlacesLayout() {
        if (mPlacesFiltersLayout != null) {
            animateAndShow(mPlacesFiltersLayout);
            return;
        }

        mPlacesFiltersLayout = LayoutInflater.from(mContext)
                .inflate(R.layout.search_filters_places, null);

        mPlacesRadiusTextView = (TextView) mPlacesFiltersLayout.findViewById(R.id.search_filters_radius_textview);
        mPlacesRadiusSeekBar = (SeekBar) mPlacesFiltersLayout.findViewById(R.id.search_filters_radius_seekbar);
        mPlacesRadiusSeekBar.setMax(Config.Filters.MAX_RADIUS);
        mPlacesRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSearchPlacesFilters.setDistanceTo((long) progress<1000 ?  progress: (progress- (progress % 1000)));
                mPlacesRadiusTextView.setText(String.valueOf(mSearchPlacesFilters.getDistanceTo() >= 1000 ?
                                String.valueOf(mSearchPlacesFilters.getDistanceTo() / 1000) +
                                        (mSearchPlacesFilters.getDistanceTo() == Config.Filters.MAX_RADIUS ? "+ " : "")
                                        + mResources.getString(R.string.profile_item_kilometers) :
                                mSearchPlacesFilters.getDistanceTo()
                ));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mPlacesRadiusSeekBar.setProgress((int) mSearchPlacesFilters.getDistanceTo());

        mPlacesCitiesSpinner = (Spinner) mPlacesFiltersLayout.findViewById(R.id.search_filters_city_spinner);
        mPlacesCitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position - 1 >= 0 && position - 1 < mCities.size()) {
                    mSearchPlacesFilters.setCityId(mCities.get(position - 1).getId());
                } else {
                    mSearchPlacesFilters.setCityId(-1);
                }
                mPlacesRadiusSeekBar.setAlpha(mSearchUsersFilters.getCityId() == -1 || mSearchUsersFilters.getCityId() == mCurrentProfile.getCityId() ? 1 : .2f);
                mPlacesRadiusSeekBar.setEnabled(mSearchPlacesFilters.getCityId() == -1 || mSearchPlacesFilters.getCityId() == mCurrentProfile.getCityId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>
                (mContext, R.layout.spinner_item_blue);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mPlacesCitiesSpinner.setAdapter(adapter);
        adapter.add(mResources.getString(R.string.register_pick_city));
        if (mCities.size() > 0) {
            for (int i = 0; i < mCities.size(); i++) {
                adapter.add(mCities.get(i).getName());
                if (mSearchPlacesFilters.getCityId() == mCities.get(i).getId()) {
                    if (mPlacesCitiesSpinner != null) {
                        mPlacesCitiesSpinner.setSelection(i + 1);
                    }
                }
            }
        }

        Switch switcher = (Switch) mPlacesFiltersLayout.findViewById(R.id.search_filter_online_only_checkbox);
        if (switcher != null) {
            switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                    mSearchPlacesFilters.setFilterByFollower(value);
                }
            });
        }

        mClosePlacesFiltersButton = (ImageButton) mPlacesFiltersLayout.findViewById(R.id.search_filters_button_close);
        mClosePlacesFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlacesFiltersLayout.setAlpha(.0f);
                mPlacesFiltersLayout.setVisibility(View.GONE);
            }
        });
        mSearchPlacesFiltersButton = (Button) mPlacesFiltersLayout.findViewById(R.id.search_filters_button_search);
        if (mSearchPlacesFiltersButton != null) {
            mSearchPlacesFiltersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestDataRefresh();
                    mPlacesFiltersLayout.setAlpha(.0f);
                    mPlacesFiltersLayout.setVisibility(View.GONE);
                }
            });
        }

        inflateLayout(mPlacesFiltersLayout);
        animateAndShow(mPlacesFiltersLayout);
    }

    private void inflateLayout(View view) {
        ((ViewGroup) mMainContent.getRootView()).addView(view);
//        ((ViewGroup) getWindow().getDecorView().getRootView()).addView(view);
    }

    private void animateAndShow(View view) {
        view.setAlpha(1.0f);
        view.setVisibility(View.VISIBLE);
    }

    private void getCities() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ProfileOperationResult operationResult = GeoDatingEngine.getInstance()
                        .loadUserProfile();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (failCheckOperationResult(operationResult)) {
                            return;
                        }

                        mCities = GeoDatingEngine.getInstance()
                                .getCities(operationResult.getUserInfo().getCountryId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSearchUsersFilters.setCityId(operationResult.getUserInfo().getCityId());
                                mSearchPlacesFilters.setCityId(operationResult.getUserInfo().getCityId());
                                populateCitiesSpinner();
                            }
                        });
                    }
                }).start();
            }
        }).start();
    }

    private void populateCitiesSpinner() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>
                (mContext, R.layout.spinner_item_blue);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mCitiesSpinner.setAdapter(adapter);
        adapter.add(mResources.getString(R.string.register_pick_city));
        if (mCities.size() > 0) {
            for (int i = 0; i < mCities.size(); i++) {
                adapter.add(mCities.get(i).getName());
                if (mSearchUsersFilters.getCityId() == mCities.get(i).getId()) {
                    if (mCitiesSpinner != null) {
                        mCitiesSpinner.setSelection(i + 1);
                    }
                }
            }
        }
    }

    private void setupPlacesListPlaceholders() {
        mPlaces = new ArrayList<>();
//        for (int i = 0; i < 30; i++) {
//            Place place = new Place(null);
//            place.setId(i);
//            place.setName("place " + String.valueOf(i));
//            place.setMaleCount(i);
//            place.setFemaleCount(i);
//            place.setLon(i);
//            place.setLat(i);
//            mPlaces.add(place);
//        }
    }

    @Override
    public void onPlacesMapFragmentViewCreated(Fragment fragment) {
        mPlacesMapFragment = (PlacesMapFragment) fragment;
        if (mPlacesMapFragment == null) {
            return;
        }

        mPlacesSearchEdittext = (EditText) mPlacesMapFragment.getView()
                .findViewById(R.id.map_search_edittext);
        mPlacesRecyclerView = (UltimateRecyclerView) mPlacesMapFragment.getView()
                .findViewById(R.id.recycler);
        mPlacesLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPlacesLayoutManager.setSmoothScrollbarEnabled(true);
        mPlacesRecyclerView.setLayoutManager(mPlacesLayoutManager);
        mPlacesRecyclerView.setHasFixedSize(true);
        mPlacesRecyclerView.setAdapter(mPlacesListAdapter);
        mPlacesRecyclerView.enableLoadmore();
        mPlacesRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                requestPlacesDataRefreshWithOffset(itemsCount);
            }
        });

        if (mPlacesMapFragment.getGoogleMap() == null) {
            mPlacesSearchEdittext.setVisibility(View.GONE);
            return;
        }

        mPlacesClusterManager = new ClusterManager<PlaceMarker>(mContext, mPlacesMapFragment.getGoogleMap());
        mPlacesClusterManager.setRenderer(new PlaceRenderer());
        mPlacesClusterManager.setClusterOnlyVisibleArea();
//        mPlacesClusteringAlgorhitm = new PreCachingAlgorithmDecorator<PlaceMarker>(new NonHierarchicalDistanceBasedAlgorithm<PlaceMarker>());
        mPlacesClusteringAlgorhitm = new PreCachingAlgorithmDecorator<PlaceMarker>(new GridBasedAlgorithm<PlaceMarker>());

        mPlacesClusterManager.setAlgorithm(mPlacesClusteringAlgorhitm);
        mPlacesMapFragment.getGoogleMap().setOnCameraChangeListener(mPlacesClusterManager);
        mPlacesMapFragment.getGoogleMap().setOnMarkerClickListener(mPlacesClusterManager);
        mPlacesMapFragment.getGoogleMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMapTooltip != null) {
                    mMapTooltip.destroy();
                }
            }
        });

        mPlacesClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PlaceMarker>() {
            @Override
            public boolean onClusterItemClick(PlaceMarker placeMarker) {
                mMapTooltip = new SearchTooltips(placeMarker.getPlace(), SearchActivity.this, mTooltipHost);
                return false;
            }
        });

        mPlacesSearchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    requestDataRefresh();
                    hideSoftKeyboard();
                    return true;
                }

                if (mPlacesSearchEdittext.getText().toString().length() == 0) {
                    mPlacesSearchEdittext.clearFocus();
                }
                return false;
            }
        });
        mPlacesSearchEdittext.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mSearchPlacesFilters.setNameOrDescription(mPlacesSearchEdittext.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mPlacesZoomInButton = (ImageButton) mPlacesMapFragment.getView()
                .findViewById(R.id.map_zoom_in_button);
        mPlacesZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mPlacesMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mPlacesMapFragment.getGoogleMap().getCameraPosition().zoom + 1);
                mPlacesMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mPlacesZoomOutButton = (ImageButton) mPlacesMapFragment.getView()
                .findViewById(R.id.map_zoom_out_button);
        mPlacesZoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mPlacesMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mPlacesMapFragment.getGoogleMap().getCameraPosition().zoom - 1);
                mPlacesMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mPlacesZoomLocationButton = (ImageButton) mPlacesMapFragment.getView()
                .findViewById(R.id.map_zoom_location_button);
        mPlacesZoomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(GeoDatingEngine.getInstance().getLocationServerInstance().getLatitude(),
                                GeoDatingEngine.getInstance().getLocationServerInstance().getLongitude()))
                        .zoom(CLOSE_ZOOM);
                mPlacesMapFragment.getGoogleMap().animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition.build()));

                mPlacesOwnMarker = updateOwnMarker(mPlacesMapFragment.getGoogleMap(), mPlacesOwnMarker);
            }
        });


//        mPlacesMapFragment.getGoogleMap().get
        requestPlacesDataRefresh();
    }

    @Override
    public void onPlacesMapFragmentAttached(PlacesMapFragment fragment) {
    }

    @Override
    public void onPlacesMapFragmentDetached(PlacesMapFragment fragment) {
        mPlacesMapFragment = null;
    }

    @Override
    public void onUsersMapFragmentViewCreated(Fragment fragment) {
        mUsersMapFragment = (UsersMapFragment) fragment;
        if (mUsersMapFragment == null) {
            return;
        }

        mUsersSearchEdittext = (EditText) mUsersMapFragment.getView()
                .findViewById(R.id.map_search_edittext);
        mUsersRecyclerView = (UltimateRecyclerView) mUsersMapFragment.getView()
                .findViewById(R.id.recycler);
        mUsersLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mUsersLayoutManager.setSmoothScrollbarEnabled(true);
        mUsersRecyclerView.setLayoutManager(mUsersLayoutManager);
        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.setAdapter(mUsersListAdapter);
        mUsersRecyclerView.enableLoadmore();
        mUsersRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                requestUsersDataRefreshWithOffset(itemsCount);
            }
        });

        if (mUsersMapFragment.getGoogleMap() == null) {
            mUsersSearchEdittext.setVisibility(View.GONE);
            return;
        }

        mUsersClusterManager = new ClusterManager<UserMarker>(mContext, mUsersMapFragment.getGoogleMap());
        mUsersClusterManager.setRenderer(new UserRenderer(mUsersMapFragment.getGoogleMap(), mUsersClusterManager));
        mUsersClusterManager.setClusterOnlyVisibleArea();
        mUsersClusterManager.setAlgorithm(new VisibleNonHierarchicalDistanceBasedAlgorithm<UserMarker>(mMetrics.widthPixels, mMetrics.heightPixels));
        mUsersMapFragment.getGoogleMap().setOnCameraChangeListener(mUsersClusterManager);
        mUsersMapFragment.getGoogleMap().setOnMarkerClickListener(mUsersClusterManager);
        mUsersMapFragment.getGoogleMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMapTooltip != null) {
                    mMapTooltip.destroy();
                }
            }
        });

        mUsersClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<UserMarker>() {
            @Override
            public boolean onClusterItemClick(UserMarker user) {
                mMapTooltip = new SearchTooltips(user.getUser(), SearchActivity.this, mTooltipHost);
                return false;
            }
        });

//        mUsersClusterManager.setAlgorithm(new VisibleNonHierarchicalDistanceBasedAlgorithm<UserMarker>(mMetrics.widthPixels, mMetrics.heightPixels));

        mUsersSearchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    requestDataRefresh();
                    hideSoftKeyboard();
                    return true;
                }

                if (mUsersSearchEdittext.getText().toString().length() == 0) {
                    mUsersSearchEdittext.clearFocus();
                }
                return false;
            }
        });
        mUsersSearchEdittext.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mSearchUsersName = mUsersSearchEdittext.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mUsersZoomInButton = (ImageButton) mUsersMapFragment.getView()
                .findViewById(R.id.map_zoom_in_button);
        mUsersZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mUsersMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mUsersMapFragment.getGoogleMap().getCameraPosition().zoom + 1);
                mUsersMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mUsersZoomOutButton = (ImageButton) mUsersMapFragment.getView()
                .findViewById(R.id.map_zoom_out_button);
        mUsersZoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mUsersMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mUsersMapFragment.getGoogleMap().getCameraPosition().zoom - 1);
                mUsersMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mUsersZoomLocationButton = (ImageButton) mUsersMapFragment.getView()
                .findViewById(R.id.map_zoom_location_button);
        mUsersZoomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(GeoDatingEngine.getInstance().getLocationServerInstance().getLatitude(),
                                GeoDatingEngine.getInstance().getLocationServerInstance().getLongitude()))
                        .zoom(CLOSE_ZOOM);
                mUsersMapFragment.getGoogleMap().animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition.build()));
                mUsersOwnMarker = updateOwnMarker(mUsersMapFragment.getGoogleMap(), mUsersOwnMarker);
            }
        });

        requestUsersDataRefresh();
    }

    @Override
    public void onUsersMapFragmentAttached(UsersMapFragment fragment) {

    }

    @Override
    public void onUsersMapFragmentDetached(UsersMapFragment fragment) {

    }

    @Override
    public void onFriendsMapFragmentViewCreated(Fragment fragment) {
        mFriendsMapFragment = (FriendsMapFragment) fragment;
        if (mFriendsMapFragment == null) {
            return;
        }

        mFriendsSearchEdittext = (EditText) mFriendsMapFragment.getView()
                .findViewById(R.id.map_search_edittext);
        mFriendsRecyclerView = (UltimateRecyclerView) mFriendsMapFragment.getView()
                .findViewById(R.id.recycler);
        mFriendsLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mFriendsLayoutManager.setSmoothScrollbarEnabled(true);
        mFriendsRecyclerView.setLayoutManager(mFriendsLayoutManager);
        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setAdapter(mFriendsListAdapter);
        mFriendsRecyclerView.enableLoadmore();
        mFriendsRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                requestFriendsDataRefreshWithOffset(itemsCount);
            }
        });

        if (mFriendsMapFragment.getGoogleMap() == null) {
            mFriendsSearchEdittext.setVisibility(View.GONE);
            return;
        }

        mFriendsClusterManager = new ClusterManager<UserMarker>(mContext, mFriendsMapFragment.getGoogleMap());
        mFriendsClusterManager.setRenderer(new UserRenderer(mFriendsMapFragment.getGoogleMap(), mFriendsClusterManager));
        mFriendsClusterManager.setClusterOnlyVisibleArea();
        mFriendsClusterManager.setAlgorithm(new VisibleNonHierarchicalDistanceBasedAlgorithm<UserMarker>(mMetrics.widthPixels, mMetrics.heightPixels));
        mFriendsMapFragment.getGoogleMap().setOnCameraChangeListener(mFriendsClusterManager);
        mFriendsMapFragment.getGoogleMap().setOnMarkerClickListener(mFriendsClusterManager);
        mFriendsMapFragment.getGoogleMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mMapTooltip != null) {
                    mMapTooltip.destroy();
                }
            }
        });

        mFriendsClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<UserMarker>() {
            @Override
            public boolean onClusterItemClick(UserMarker user) {
                mMapTooltip = new SearchTooltips(user.getUser(), SearchActivity.this, mTooltipHost);
                return false;
            }
        });

//        mFriendsClusterManager.setAlgorithm(new VisibleNonHierarchicalDistanceBasedAlgorithm<UserMarker>(mMetrics.widthPixels, mMetrics.heightPixels));

        mFriendsSearchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    requestDataRefresh();
                    hideSoftKeyboard();
                    return true;
                }

                if (mFriendsSearchEdittext.getText().toString().length() == 0) {
                    mFriendsSearchEdittext.clearFocus();
                }
                return false;
            }
        });
        mFriendsSearchEdittext.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                mSearchFriendsName = mFriendsSearchEdittext.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        mFriendsZoomInButton = (ImageButton) mFriendsMapFragment.getView()
                .findViewById(R.id.map_zoom_in_button);
        mFriendsZoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mFriendsMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mFriendsMapFragment.getGoogleMap().getCameraPosition().zoom + 1);
                mFriendsMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mFriendsZoomOutButton = (ImageButton) mFriendsMapFragment.getView()
                .findViewById(R.id.map_zoom_out_button);
        mFriendsZoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder builder = CameraPosition
                        .builder(mFriendsMapFragment.getGoogleMap().getCameraPosition())
                        .zoom(mFriendsMapFragment.getGoogleMap().getCameraPosition().zoom - 1);
                mFriendsMapFragment.getGoogleMap().animateCamera(CameraUpdateFactory
                        .newCameraPosition(builder.build()));
            }
        });
        mFriendsZoomLocationButton = (ImageButton) mFriendsMapFragment.getView()
                .findViewById(R.id.map_zoom_location_button);
        mFriendsZoomLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition.Builder cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(GeoDatingEngine.getInstance().getLocationServerInstance().getLatitude(),
                                GeoDatingEngine.getInstance().getLocationServerInstance().getLongitude()))
                        .zoom(CLOSE_ZOOM);
                mFriendsMapFragment.getGoogleMap().animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition.build()));
                mFriendsOwnMarker = updateOwnMarker(mFriendsMapFragment.getGoogleMap(), mFriendsOwnMarker);
            }
        });

        requestFriendsDataRefresh();
    }

    @Override
    public void onFriendsMapFragmentAttached(FriendsMapFragment fragment) {

    }

    @Override
    public void onFriendsMapFragmentDetached(FriendsMapFragment fragment) {

    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public void onBackPressed() {
        if (mMapTooltip != null && mMapTooltip.isVisible()) {
            mMapTooltip.destroy();
            return;
        }
        if (mUsersFiltersLayout != null && mUsersFiltersLayout.getAlpha() != .0f) {
            mUsersFiltersLayout.setAlpha(.0f);
            mUsersFiltersLayout.setVisibility(View.GONE);
        } else if (mPlacesFiltersLayout != null && mPlacesFiltersLayout.getAlpha() != .0f) {
            mPlacesFiltersLayout.setAlpha(.0f);
            mPlacesFiltersLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected int getContentViewId() {
        return R.layout.activity_workspace;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_map, menu);
        mListMenuItem = menu.findItem(R.id.action_list);
        mMapMenuItem = menu.findItem(R.id.action_map);
        switch (mViewPager.getCurrentItem()) {
            case SECTION_SEARCH_PLACES:
                if (mPlacesRecyclerView.getVisibility() == View.VISIBLE) {
                    mListMenuItem.setVisible(false);
                    mMapMenuItem.setVisible(true);
                } else {
                    mListMenuItem.setVisible(true);
                    mMapMenuItem.setVisible(false);
                }
                break;
            case SECTION_SEARCH_FRIENDS:
                if (mFriendsRecyclerView.getVisibility() == View.VISIBLE) {
                    mListMenuItem.setVisible(false);
                    mMapMenuItem.setVisible(true);
                } else {
                    mListMenuItem.setVisible(true);
                    mMapMenuItem.setVisible(false);
                }
                break;
            case SECTION_SEARCH_USERS:
                if (mUsersRecyclerView.getVisibility() == View.VISIBLE) {
                    mListMenuItem.setVisible(false);
                    mMapMenuItem.setVisible(true);
                } else {
                    mListMenuItem.setVisible(true);
                    mMapMenuItem.setVisible(false);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list: {
                hideSoftKeyboard();
                switch (mViewPager.getCurrentItem()) {
                    case SECTION_SEARCH_PLACES:
                        mPlacesRecyclerView.setVisibility(View.VISIBLE);
                        mPlacesRecyclerView.bringToFront();
                        mPlacesSearchEdittext.bringToFront();
                        mPlacesMapFragment.getMapView().setVisibility(View.GONE);
                        break;
                    case SECTION_SEARCH_FRIENDS:
                        mFriendsRecyclerView.setVisibility(View.VISIBLE);
                        mFriendsRecyclerView.bringToFront();
                        mFriendsSearchEdittext.bringToFront();
                        mFriendsMapFragment.getMapView().setVisibility(View.GONE);
                        break;
                    case SECTION_SEARCH_USERS:
                        mUsersRecyclerView.setVisibility(View.VISIBLE);
                        mUsersRecyclerView.bringToFront();
                        mUsersSearchEdittext.bringToFront();
                        mUsersMapFragment.getMapView().setVisibility(View.GONE);
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
            case R.id.action_map: {
                hideSoftKeyboard();
                switch (mViewPager.getCurrentItem()) {
                    case SECTION_SEARCH_PLACES:
                        mPlacesMapFragment.getMapView().setVisibility(View.VISIBLE);
                        mPlacesRecyclerView.setVisibility(View.GONE);
                        mPlacesRecyclerView.bringToFront();
                        break;
                    case SECTION_SEARCH_FRIENDS:
                        mFriendsMapFragment.getMapView().setVisibility(View.VISIBLE);
                        mFriendsRecyclerView.setVisibility(View.GONE);
                        break;
                    case SECTION_SEARCH_USERS:
                        mUsersMapFragment.getMapView().setVisibility(View.VISIBLE);
                        mUsersRecyclerView.setVisibility(View.GONE);
                        break;
                }
                invalidateOptionsMenu();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SearchViewPagerAdapter extends FragmentPagerAdapter {
        public SearchViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SECTION_SEARCH_USERS:
                    return new UsersMapFragment();
                case SECTION_SEARCH_FRIENDS:
                    return new FriendsMapFragment();
                case SECTION_SEARCH_PLACES:
                    return new PlacesMapFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_SECTIONS_SEARCH;
        }

        @Override
        public float getPageWidth(int position) {
            Log.v(LOG_TAG, "Page #" + position + " width is: " + super.getPageWidth(position));
            return super.getPageWidth(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int id = 0;
            switch (position) {
                case SECTION_SEARCH_USERS:
                    id = R.string.slidingtab_title_search_users;
                    break;
                case SECTION_SEARCH_FRIENDS:
                    id = R.string.slidingtab_title_search_friends;
                    break;
                case SECTION_SEARCH_PLACES:
                    id = R.string.slidingtab_title_search_places;
                    break;
            }

            return getResources().getString(id).toUpperCase();
        }
    }

    public class UserMarker implements ClusterItem {
        private final LatLng mPosition;
        private final UserPlain mUser;

        public UserMarker(UserPlain user) {
            mPosition = new LatLng(user.getLatitude(), user.getLongitude());
            mUser = user;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public UserPlain getUser() {
            return mUser;
        }
    }

    public class PlaceMarker implements ClusterItem {
        private final LatLng mPosition;
        private final Place mPlace;

        private final ImageView mImageView;
        private BitmapDescriptor mDescriptor;

        public PlaceMarker(final Place place) {
            mPosition = new LatLng(place.getLat(), place.getLon());
            mPlace = place;
            mImageView = new ImageView(mContext);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mMarkerDimension, mMarkerDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_marker_padding);
            mImageView.setPadding(padding, padding, padding, padding);
        }

        public void render(final Marker marker) {

            final Photo placeIcon = mPlace.getIcon();
            if (placeIcon != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Bitmap bitmap = Picasso.with(mContext)
                                    .load(placeIcon.getUrl())
                                    .config(Bitmap.Config.RGB_565)
                                    .get();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImageView.setImageBitmap(bitmap);
                                    mIconGenerator.setContentView(mImageView);
                                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(mIconGenerator.makeIcon()));
                                }
                            });
                        } catch (IOException e) {
                            Log.e(LOG_TAG, e.getLocalizedMessage());
                        }
                    }
                }).start();
            } else {
                marker.setIcon(mPlaceholderBitmapDescriptor);
            }
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public Place getPlace() {
            return mPlace;
        }

        public BitmapDescriptor getBitmapDescriptor() {
            return mDescriptor;
        }
    }


    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PlaceRenderer extends DefaultClusterRenderer<PlaceMarker> {

        public PlaceRenderer() {
            super(getApplicationContext(), mPlacesMapFragment.getGoogleMap(), mPlacesClusterManager);
            mIconGenerator.setColor(getResources().getColor(R.color.theme_primary));
        }

        @Override
        protected void onBeforeClusterItemRendered(final PlaceMarker place, final MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(place, markerOptions);
        }

        @Override
        protected void onClusterItemRendered(final PlaceMarker place, final Marker marker) {
            super.onClusterItemRendered(place, marker);
            place.render(marker);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 5;
        }
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class UserRenderer extends DefaultClusterRenderer<UserMarker> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        ClusterManager<UserMarker> mClusterManager;

        public UserRenderer(GoogleMap map, ClusterManager<UserMarker> clusterManager) {
            super(getApplicationContext(), map, clusterManager);
            mClusterManager = clusterManager;
            mIconGenerator.setColor(getResources().getColor(R.color.theme_primary));
            mImageView = new ImageView(mContext);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mMarkerDimension, mMarkerDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_marker_padding);
            mImageView.setPadding(padding, padding, padding, padding);
        }

        @Override
        protected void onBeforeClusterItemRendered(UserMarker user, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(user, markerOptions);
            markerOptions.icon(mPlaceholderBitmapDescriptor);
        }

        @Override
        protected void onClusterItemRendered(final UserMarker user, final Marker marker) {
            super.onClusterItemRendered(user, marker);
            final Photo userIcon = user.getUser().getIconPhoto();
            if (userIcon != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Bitmap bitmap = Picasso.with(mContext)
                                    .load(userIcon.getUrl())
                                    .config(Bitmap.Config.RGB_565)
                                    .get();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mImageView.setImageBitmap(bitmap);
                                    mIconGenerator.setContentView(mImageView);
                                    BitmapDescriptor descriptor = BitmapDescriptorFactory
                                            .fromBitmap(mIconGenerator.makeIcon());
                                    marker.setIcon(descriptor);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
                marker.setIcon(mPlaceholderBitmapDescriptor);
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 5;
        }
    }
}
