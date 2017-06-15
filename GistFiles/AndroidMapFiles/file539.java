package masxdeveloper.peternakan.ModelData.GoogleDirection;

import com.google.android.gms.maps.model.LatLng;

public class LatLngBoundData {

    private LatLng Northeast;
    private LatLng Southwest;

    public LatLngBoundData(LatLng northeast, LatLng southwest) {
        Northeast = northeast;
        Southwest = southwest;
    }

    public LatLng getNortheast() {
        return Northeast;
    }

    public LatLng getSouthwest() {
        return Southwest;
    }
}
