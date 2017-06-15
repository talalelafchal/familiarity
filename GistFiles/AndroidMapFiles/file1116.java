public class MapViewListItemView extends LinearLayout {

    protected MapView mMapView;

    public MapViewListItemView(Context context) {
        this(context, null);
    }

    public MapViewListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupView();
    }

    private void setupView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_map_view, this);
        mMapView = (MapView) view.findViewById(R.id.list_item_map_view_mapview);

        setOrientation(VERTICAL);
    }

    public void mapViewOnCreate(Bundle savedInstanceState) {
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        }
    }

    public void mapViewOnResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    public void mapViewOnPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    public void mapViewOnDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    public void mapViewOnLowMemory() {
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    public void mapViewOnSaveInstanceState(Bundle outState) {
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }
}