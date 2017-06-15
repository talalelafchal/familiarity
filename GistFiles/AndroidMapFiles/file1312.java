public class ConvininenceSearchActivity extends ActionBarActivity {
    /** デバッグ用タグ */
    private static final String LOGTAG = ConvininenceSearchActivity.class
            .getSimpleName();
    /** Yahoo!Map SDK のID */
    private static final String YOLP_ID = "";
    /** LastKnowLocationで得られる情報の古さの限界。5分 */
    private static final long LAST_KNOWN_MIN = 5 * 60 * 1000L;
    /** 位置情報の更新間隔。60秒に1回更新 */
    private static final int MIN_TIME = 60 * 1000;
    /** 位置情報の更新間隔メートル.10mに1回更新 */
    private static final int MIN_DISTANCE = 10;

    /** 画面に表示するMapView */
    protected transient MapView mapView;
    /** 地図のコントローラー */
    protected transient MapController mapController;
    /** 現在位置を表示するオーバーレイ */
    protected transient NowLocationOverlay locationOverlay;
    /** ロケーションマネージャー。位置情報をマネジメント！！ */
    protected transient LocationManager locationManager;

    /** Activity生成時により呼び出されるメソッド。地図の初期化なんかを行う。 */
    @Override
    public void onCreate(final Bundle sIState) {
        super.onCreate(sIState);
        mapView = new MapView(this, YOLP_ID);
        mapView.setBuiltInZoomControls(true);
        setContentView(mapView);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final String provider = getProvider(locationManager);
        locationOverlay = new NowLocationOverlay(this, mapView);
        final Location lastKnown = getLastKnownLocation();
        if (lastKnown != null) {
            locationOverlay.moveMap(lastKnown, mapController);
        }
        locationManager.requestLocationUpdates(provider, MIN_TIME,
                MIN_DISTANCE, locationOverlay);
        mapView.getOverlays().add(locationOverlay);

    }

    /**
     * 最後に取得した緯度経度を得る。 5分以内の値であれば有効な値として採用する
     * 
     * @return 最後に取得した緯度経度
     */
    protected Location getLastKnownLocation() {
        Location resultLocation = null;
        final Location lastKnownLocation = locationManager
                .getLastKnownLocation(getProvider(locationManager));
        if (lastKnownLocation != null
                && (new Date().getTime() - lastKnownLocation.getTime()) < LAST_KNOWN_MIN) {
            resultLocation = new Location(lastKnownLocation);
        }
        return resultLocation;
    }

    /**
     * 位置選択のためのプロバイダを選択する.方位、速度、高度は不要であるためそれらを消去した上で、残ったものを選択する
     * 
     * @return　選択されたプロバイダ
     */
    protected String getProvider(final LocationManager locationManager) {
        final Criteria criteria = new Criteria();
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        // return locationManager.getBestProvider(criteria, true);
        return LocationManager.GPS_PROVIDER;
    }

    // @Override
    // protected void onResume() {
    // super.onResume();
    // }

    @Override
    protected void onPause() {
        locationManager.removeUpdates(locationOverlay);
        super.onPause();
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

}