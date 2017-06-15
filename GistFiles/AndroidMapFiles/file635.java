public static MarkerOptions createMarker(Context context, LatLng point, int bedroomCount) {
    MarkerOptions marker = new MarkerOptions();
    marker.position(point);
    int px = context.getResources().getDimensionPixelSize(R.dimen.map_marker_diameter);
    View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_circle_text, null);
    markerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    markerView.layout(0, 0, px, px);
    markerView.buildDrawingCache();
    TextView bedNumberTextView = (TextView)markerView.findViewById(R.id.bed_num_text_view);
    Bitmap mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(mDotMarkerBitmap);
    bedNumberTextView.setText(bedroomCount + "");
    markerView.draw(canvas);
    marker.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
    return marker;
}