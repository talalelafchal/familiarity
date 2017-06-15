class myLocationOverlay extends com.google.android.maps.Overlay {

    private static final double defaultLatitude = Double.parseDouble("default_latitude");
    private static final double defaultLongitude = Double.parseDouble("default_longitude");
    private static final float defaultAccuracy = 250f; // or whatever


    Location currentLocation; // this should be already known

    private Paint accuracyPaint;
    private Point center;
    private Point left;
    private Drawable drawable;
    private int width;
    private int height;

    @Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        super.draw(canvas, mapView, shadow);

        accuracyPaint = new Paint();
        accuracyPaint.setAntiAlias(true);
        accuracyPaint.setStrokeWidth(2.0f);

        drawable = mapView.getContext().getResources().getDrawable(R.drawable.my_location_dot);
        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();
        center = new Point();
        left = new Point();
        double latitude;
        double longitude;
        float accuracy;
        Projection projection = mapView.getProjection();

        if(currentLocation == null) {
            latitude = defaultLatitude;
            longitude = defaultLongitude;
            accuracy = defaultAccuracy;
        } else {
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            accuracy = currentLocation.getAccuracy();
        }            

        float[] result = new float[1];

        Location.distanceBetween(latitude, longitude, latitude, longitude + 1, result);
        float longitudeLineDistance = result[0];

        GeoPoint leftGeo = new GeoPoint((int)(latitude * 1E6), (int)((longitude - accuracy / longitudeLineDistance) * 1E6));
        projection.toPixels(leftGeo, left);
        projection.toPixels(myLocationPoint, center);
        int radius = center.x - left.x;

        accuracyPaint.setColor(0xff6666ff);
        accuracyPaint.setStyle(Style.STROKE);
        canvas.drawCircle(center.x, center.y, radius, accuracyPaint);

        accuracyPaint.setColor(0x186666ff);
        accuracyPaint.setStyle(Style.FILL);
        canvas.drawCircle(center.x, center.y, radius, accuracyPaint);

        drawable.setBounds(center.x - width / 2, center.y - height / 2, center.x + width / 2, center.y + height / 2);
        drawable.draw(canvas);

        return true;
    }
}
