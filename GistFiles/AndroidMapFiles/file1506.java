Marker aMarker;
Circle aCircle;
List<Circle> allCircles = new ArrayList<Circle>();
List<Marker> allMarkers = new ArrayList<Marker>();

public void createNewMarkerAndCircle() {
      aMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(dLat, dLon))
                    .title(nombre_punto)
                    .snippet(introduccion_punto)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
      allMarkers.add(aMarker);
      aCircle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(dLat, dLon))
                            .radius(150)
                            .strokeColor(Color.RED)
      allCircles.add(aCircle);
}

// in the LocationListener iterate over all stored Circles and check the distances agains each one of them
// as you add the circles and markers in the same method you'll have a correspondence between them
// so when you find yourself inside a circle the marker will be at the same position in allMarkers
for (int i = 0; i < allCircles.size(); i++) {
  Circle c = allCircles.get(i);
  Location.distanceBetween(
                            location.getLatitude(),
                            location.getLongitude(),
                            c.getCenter().latitude,
                            c.getCenter().longitude, distance);
  if (distance[0] > c.getRadius()) {
    Log.i("myLogs", "Outside");
  } else {
    String markerTitle;
    markerTitle = allMarkers.get(i).getTitle();
    Log.i("myLogs", "I´ in the circle" + " " + markerTitle);
    // you found a circles so you may want to break out of the for loop, break;

                    }