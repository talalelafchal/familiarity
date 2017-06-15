package neocom.dealerbook.models.layer.presenters;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.TileOverlay;
import com.androidmapsextensions.TileOverlayOptions;
import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import neocom.dealerbook.models.layer.exceptions.InvalidInputDataException;

/**
 * Created by wviana on 04/03/16.
 */
public class GeoJsonHeatPresenter<T> implements LayerPresenter<GeoJSONObject> {
    private HeatmapTileProvider tileProvider;
    private TileOverlay tileOverlay;

    @Override
    public void present(GeoJSONObject data, GoogleMap map) {
        List<T> points =  dataToList(data);
        updateTileProvider(points);

        if(tileOverlay == null){
            tileOverlay = createTileProvider(map);
        }

    }

    private TileOverlay createTileProvider(GoogleMap map) {
        return map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }

    private void updateTileProvider(List<T> points) {
        if(tileProvider == null){
            tileProvider = new HeatmapTileProvider.Builder()
                    .data(points)
                    .build();
        } else {
            tileProvider.setData(points);
        }
    }

    private List<T> dataToList(GeoJSONObject data) {
        List<T> latLngList = null;
        try {
            FeatureCollection featureCollection = (FeatureCollection) data;
            List<Feature> features = featureCollection.getFeatures();
            latLngList = new ArrayList<>();


            for (Feature f : features){
                Point point = (Point) f.getGeometry();
                LatLng latLng = new LatLng(point.getPosition().getLatitude(), point.getPosition().getLongitude());
                if(latLngList instanceof ArrayList<WeightedLatLng>){
                    JSONObject properties = f.getProperties();
                    latLngList.add(new T(latLng, properties.optDouble("Value")));
                } else {
                    latLngList.add((T) latLng);
                }
            }

        } catch (ClassCastException e) {
            throw new InvalidInputDataException("Invalid input data: ", e);
        }

        return latLngList;
    }

    @Override
    public void clear() {
        tileOverlay.remove();
        tileOverlay = null;
    }
}
