public class FollowMap201308Fragment extends SupportMapFragment implements OnCameraChangeListener, OnMyLocationButtonClickListener {
    private final static long LOCATION_UPDATE_DURATION = 2000;
    private final static int LOCATION_SMALLEST_DISPLACEMENT_METER = 10;

    private GoogleMap mMap = null;
    private LocationSourceImpl mLocationSource = null;
    private boolean mDoFollow = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        onActivated();
    }

    @Override
    public void onPause() {
        onInactivated();
        super.onPause();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = getMap();
            if (mMap != null) {
                mLocationSource = new LocationSourceImpl();
                // LocationMangerに最終位置を問い合わせて初期位置を確定する
                double lat = 34.65;
                double lon = 135;
                LocationManager mgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); // 位置マネージャ取得
                Location loc = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc == null)
                    loc = mgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (loc != null) {
                    lat = loc.getLatitude();
                    lon = loc.getLongitude();
                }
                // カメラの初期位置や各種コントロール、リスナー等をセット
                CameraPosition.Builder builder = new CameraPosition.Builder().bearing(0).tilt(0).zoom(16).target(new LatLng(lat, lon));
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setLocationSource(mLocationSource);
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
                mMap.setOnCameraChangeListener(this);
            }
        }
    }

    private void onActivated() {
        setUpMapIfNeeded();
        if (mLocationSource != null) {
            mLocationSource.start();
        }
    }

    private void onInactivated() {
        if (mLocationSource != null) {
            mLocationSource.stop();
        }
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        // カメラポジションが変化したので、MyLocationと比較してフォローを続けるかどうかを判定する
        if (mMap != null) {
            Location myloc = mMap.getMyLocation();
            if (myloc != null) {
                double deltalat = Math.abs(myloc.getLatitude() - position.target.latitude);
                double deltalon = Math.abs(myloc.getLongitude() - position.target.longitude);
                mDoFollow = (deltalat <= 0.000005 && deltalon <= 0.000005);
            }
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (mMap != null) {
            mMap.stopAnimation();
            Location myloc = mMap.getMyLocation();
            if (myloc != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myloc.getLatitude(), myloc.getLongitude())));
            }
            mDoFollow = true;
        }
        return true;
    }

    // 現在位置を測位し、MapのMyLocationを更新するためのクラス
    // 旧来のGPS Providerによる測位と、Google Play Services Location APIの両方を使うハイブリッド仕様としている
    // Location APIで、GPS測位の立ち上がりが悪いための措置であり、新しいライブラリではもしかしたら改善されているかもしれない。
    private class LocationSourceImpl implements LocationSource, LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
        private LocationManager mLocMgr = null; // 位置マネージャ
        private LocationClient mLocClient = null;
        private boolean mReconnect = false;

        private long mLastLocMsec = 0l;
        private OnLocationChangedListener mSourceListener = null;

        private android.location.LocationListener mGpsListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationSourceImpl.this.onLocationChanged(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        LocationSourceImpl() {
            mLocClient = new LocationClient(getActivity(), this, this);
            mLocMgr = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE); // 位置マネージャ取得
        }

        void start() {
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_DURATION, LOCATION_UPDATE_DURATION, mGpsListener); // GPS測位開始
            if (!mLocClient.isConnected() && !mLocClient.isConnecting()) {
                mReconnect = false;
                mLocClient.connect();
            }
        }

        void stop() {
            mReconnect = false;
            try {
                mLocClient.removeLocationUpdates(this);
            } catch (IllegalArgumentException ignored) {
            }
            mLocClient.disconnect();
            try {
                mLocMgr.removeUpdates(mGpsListener);
            } catch (IllegalArgumentException ignored) {
            }
        }

        @Override
        public void activate(OnLocationChangedListener listener) {
            mSourceListener = listener;
        }

        @Override
        public void deactivate() {
            mSourceListener = null;
        }

        @Override
        public void onConnected(Bundle bundle) {
            mReconnect = true;
            LocationRequest req = LocationRequest.create();
            req.setFastestInterval(LOCATION_UPDATE_DURATION);
            req.setInterval(LOCATION_UPDATE_DURATION);
            req.setSmallestDisplacement(LOCATION_SMALLEST_DISPLACEMENT_METER);
            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocClient.requestLocationUpdates(req, this);
        }

        @Override
        public void onDisconnected() {
            if (mReconnect) {
                start(); // 意図しない切断のため再接続を試みる
            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // 接続失敗
        }

        @Override
        public void onLocationChanged(Location loc) {
            boolean gps = LocationManager.GPS_PROVIDER.equals(loc.getProvider());
            long curMsec = System.currentTimeMillis();
            // GPSプロバイダによるものか、一定時間以上経過したならMapのMyLocationを更新
            if (gps || (curMsec - mLastLocMsec) >= (LOCATION_UPDATE_DURATION - 200)) {
                mLastLocMsec = curMsec;
                if (mSourceListener != null) {
                    // MyLocationを更新
                    mSourceListener.onLocationChanged(loc);
                }
                if (mMap != null && mDoFollow) {
                    // フォロー中ならMyLocationへカメラポジションを移動
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
                }
            }
        }
    }
}
