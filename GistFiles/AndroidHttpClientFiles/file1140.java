void setupMapView() {
  	setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
	}

	
	void drawRoute(double slat,double slon,double dlat,double dlon) {
		
		ArrayList<?> all_geo_points = Directions.getDirections(
				slat, slon, dlat,dlon );
		mapOverlays.add(new MyOverlay(all_geo_points));
		controller.setZoom(12);
		mapView.invalidate();
	}

		void drawLocation(GeoPoint current_location) {
		Drawable drawable = this.getResources().getDrawable(R.id.map_icon);
		OverlayItem overlayItem = new OverlayItem(current_location, "", "");
		itemizedOverlay = new MyItemizedOverlay(drawable, this);
		itemizedOverlay.addOverlay(overlayItem);
		mapView.getOverlays().add(itemizedOverlay);
		mapView.invalidate();
	}

	