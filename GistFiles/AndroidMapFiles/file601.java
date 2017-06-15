	/**
	 * starts google maps w/ lat lon
	 */
	public static void showOnMap(Context c, double lat, double lng, String name) {
		String geoUri = "geo:"+lat+","+lng+"?q="+lat+","+lng+"("+name+")";
		Uri geo = Uri.parse(geoUri);
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setData(geo);
		intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
	    if (intent.resolveActivity(c.getPackageManager()) != null) {
	        c.startActivity(intent);
	    }
	}