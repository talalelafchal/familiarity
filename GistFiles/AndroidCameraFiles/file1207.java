public class StopMapClusterItemInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private final View view;

    private ClusterItem currentClusterItem;

    @BindView(R.id.item_map_window_stop_info_title)
    TextView tvTitle;

    public StopMapClusterItemInfoWindowAdapter(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.item_map_window_stop_info, null);
        ButterKnife.bind(this, view);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (currentClusterItem instanceof StopMapClusterItem) {
            StopMapClusterItem stopMapClusterItem = (StopMapClusterItem) currentClusterItem;
            tvTitle.setText(stopMapClusterItem.getTitle());
        }
        return view;
    }

    public void setCurrentClusterItem(ClusterItem currentClusterItem) {
        this.currentClusterItem = currentClusterItem;
    }
}
