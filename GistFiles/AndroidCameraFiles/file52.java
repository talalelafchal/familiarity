//center map to area
LatLngBounds latLngBounds = new LatLngBounds.Builder()
              .include(new LatLng(36.532128, -93.489121)) // Northeast
              .include(new LatLng(25.837058, -106.646234)) // Southwest
              .build();

            mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);


//Title - areaName
setTitle(area.getName);


//polygon
		poligonOptions.getPolygon().setId(area.get_id());
MarkerViewOptions markerViewOptions = new MarkerViewOptions()
        .position(new LatLng(-33.85699436, 151.21510684));

private LatLng drawPolygon(PolygonOptions polygonOptions, LatLngBounds latLngBounds, MarkerViewOptions markerViewOptions){
		mMap.addPolygon(poligonOptions);
		mMap.addMarker(markerViewOptions)
		mMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 50), 5000);
    return latLng;
	}
//onclick
//TODO: store Map areaId-polygonId
//I solved this with a little workaround:

public class PointPolygonComparator {

    /**
     * @param coordsOfPoint
     * @param pol
     * @return
     */
    public boolean isPointInPolygon(LatLng coordsOfPoint, Polygon pol) {

        List<LatLng> latlngsOfPolygon =  extractPolygonToPoints(pol);
        int i;
        int j;
        boolean contains = false;
        for (i = 0, j = latlngsOfPolygon.size() - 1; i < latlngsOfPolygon.size(); j = i++) {
            if ((latlngsOfPolygon.get(i).getLongitude() > coordsOfPoint.getLongitude()) != (latlngsOfPolygon.get(j).getLongitude() > coordsOfPoint.getLongitude()) &&
                    (coordsOfPoint.getLatitude() < (latlngsOfPolygon.get(j).getLatitude() - latlngsOfPolygon.get(i).getLatitude()) * (coordsOfPoint.getLongitude() - latlngsOfPolygon.get(i).getLongitude()) / (latlngsOfPolygon.get(j).getLongitude() - latlngsOfPolygon.get(i).getLongitude()) + latlngsOfPolygon.get(i).getLatitude())) {
                contains = !contains;
            }
        }
        return contains;
    }

    /**
     *
     * @param p
     * @return
     */
    public List<LatLng> extractPolygonToPoints(Polygon p) {
        List <LatLng> latlngsOfPolygon = new ArrayList<>();
        for (int x = 0; x < p.getPoints().size(); ++x) {
            LatLng coords = new LatLng(p.getPoints().get(x).getLatitude(), p.getPoints().get(x).getLongitude());
            latlngsOfPolygon.add(coords);
        }
        return latlngsOfPolygon;
    }
}
//You can use this class in any activity together with the onMapClickedListener, for example like this:
mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) { 
            for (Polygon pol : allPolygonsOnMap) {
                if (comparator.isPointInPolygon(new LatLng(point.getLatitude(), point.getLongitude()), pol.getPolygon())) {
                    startMapActivity(pol.getId());
                    }
                }
            }
        });



//TODO DataHelper add func
// add colors resources
// update db version
public int getAreaColor(int areaID);
private void saveCropColors(List<Area> areas);
/*
Table Crops
String crop;
int _id;
int color;
*/








