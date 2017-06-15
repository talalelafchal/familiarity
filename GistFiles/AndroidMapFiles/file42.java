    /** 画面に表示するMapView */
    transient MapView mapView;
    /** 地図のコントローラー */
    transient MapController mapController;
    /** 現在位置を表示するオーバーレイ */
    transient NowLocationOverlay locationOverlay;

    /** Activity生成時により呼び出されるメソッド。地図の初期化なんかを行う。 */
    @Override
    public void onCreate(final Bundle sIState) {
        super.onCreate(sIState);
        mapView = new MapView(this, YOLP_ID);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getMapController();
        // MyLocationOverlay のサブクラスであるNowLocationOverlayのインスタンスを作成
        locationOverlay = new NowLocationOverlay(getApplicationContext(),
                mapView);
        // 現在地取得開始
        locationOverlay.enableMyLocation();
        // センサーにはGPSを選択
        locationOverlay.onProviderEnabled(LocationManager.GPS_PROVIDER);
        // MapViewにOverlayを追加
        mapView.getOverlays().add(locationOverlay);
        mapView.invalidate();
        setContentView(mapView);
    }

    @Override
    protected void onPause() {
        // 現在位置取得終了
        locationOverlay.disableMyLocation();
        // 不要？
        locationOverlay.onProviderDisabled(LocationManager.GPS_PROVIDER);
        super.onPause();
    }