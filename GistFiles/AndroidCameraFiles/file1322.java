package neocom.dealerbook.models.layer;

import android.os.AsyncTask;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.utils.LatLngBoundsUtils;
import com.cocoahero.android.geojson.GeoJSON;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import neocom.dealerbook.models.MapBoundZoom;
import neocom.dealerbook.network.dataGetter.DataGetter;

/**
 * Created by wviana on 03/03/16.
 */
public class Layer {
    private DataGetter dataGetter;
    private List<LayerPresenter> presentAbilities;
    private int minZoom;
    private String name;
    private LayerPresenter currentPresenter;
    private boolean enabled;

    private Layer() {
        presentAbilities = new ArrayList<LayerPresenter>();
        minZoom = 10;
        name = "";
        enabled = false;
    }

    public void plotLayer(final GoogleMap map){
        MapBoundZoom mapBoundZoom = new MapBoundZoom(
                map.getCameraPosition().zoom,
                map.getProjection().getVisibleRegion().latLngBounds
        );

        new AsyncTask<MapBoundZoom, Void, GeoJSON>(){
            @Override
            protected GeoJSON doInBackground(MapBoundZoom... mapBoundZooms) {
                return dataGetter.getData(mapBoundZooms[0]);
            }

            @Override
            protected void onPostExecute(GeoJSON geoJSON) {
                if (enabled) {
                    currentPresenter.present(geoJSON, map);
                }
            }
        }.doInBackground(mapBoundZoom);

        /*GeoJSON data = dataGetter.getData(mapBoundZoom);
        currentPresenter.present(data, map);*/
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static class Builder{
        private final Layer layer = new Layer();

        public Builder dataGetter(DataGetter dataGetter){
            layer.dataGetter = dataGetter;
            return this;
        }

        public Builder addPresentAbilitie(LayerPresenter presenter){
            layer.presentAbilities.add(presenter);
            return this;
        }

        public Builder addPresentAbilitie(List<LayerPresenter> presenters){
            layer.presentAbilities.addAll(presenters);
            return this;
        }

        public Builder minZoom(int zoom){
            layer.minZoom = zoom;
            return this;
        }

        public Builder name(String name){
            layer.name = name;
            return this;
        }

        public Builder currentPresenter(int i){
            layer.currentPresenter = layer.presentAbilities.get(i);
        }

        public Builder currentPresenter(LayerPresenter presenter){
            if (layer.presentAbilities.contains(presenter) ){
                layer.currentPresenter = presenter;
            } else {
                throw new NotAbleLayerPresenterException();
            }
        }

        public Builder enabled(boolean enabled){
            layer.enabled = enabled;
            return this;
        }

        public Layer build(){
            boolean hasDataGetter = layer.dataGetter != null;
            boolean hasAnyPresentAbility = layer.presentAbilities.size() > 0;
            boolean isPresentAbilitySet = layer.currentPresenter != null;


            if (hasDataGetter && hasAnyPresentAbility){
                if(!isPresentAbilitySet){
                    setPresentAbillityToFirstAvaliableAbillity();
                }

                return layer;
            } else {
                throw new NoDataGetterExcption(e);
            }
        }

        private void setPresentAbillityToFirstAvaliableAbillity() throws NoPresenterAbilityException {
            try {
                layer.currentPresenter = layer.presentAbilities.get(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoPresenterAbilityException(e);
            }
        }
    }
}
