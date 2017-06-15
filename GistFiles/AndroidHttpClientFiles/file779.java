LatLng fromPosition = new LatLng(13.687140112679154, 100.53525868803263);
LatLng toPosition = new LatLng(13.683660045847258, 100.53900808095932);

GoogleMap mMap = ((SupportMapFragment)getSupportFragmentManager()
        .findFragmentById(R.id.map)).getMap();
GMapV2Direction md = new GMapV2Direction();

Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
ArrayList<LatLng> directionPoint = md.getDirection(doc);
PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

for(int i = 0 ; i < directionPoint.size() ; i++) {          
rectLine.add(directionPoint.get(i));
}

mMap.addPolyline(rectLine);