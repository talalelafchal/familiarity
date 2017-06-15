map = (MapView) findViewById(R.id.map); //get the map instance
TiledLayer DAY_LAYER = new OfflineTiledLayer(this, new File(Environment.getExternalStorageDirectory(), "services"), "/RoadMapsWebMercator101010/MapServer/", "index.html", "/Day/tile/"); //create a layer
map.addLayer(DAY_LAYER); //add this layer to the map
