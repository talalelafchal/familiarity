public class StopMapClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private final String title;

    public StopMapClusterItem(LatLng mPosition, String title) {
        this.mPosition = mPosition;
        this.title = title;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return title;
    }
}
