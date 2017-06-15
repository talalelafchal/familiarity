/*
mMap.addMarker(new MarkerOptions()
    .position(new LatLng(latitude, longitude))
    .title("Marker Title")
    .snippet("Marker snippet")
    .icon(getBitmapDescriptor(R.drawable.ic_place_black_48dp)));
*/

private BitmapDescriptor getBitmapDescriptor(int id) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

        int h = vectorDrawable.getIntrinsicHeight();
        int w = vectorDrawable.getIntrinsicWidth();

        vectorDrawable.setBounds(0, 0, w, h);

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bm);

    } else {
            return BitmapDescriptorFactory.fromResource(id);
    }
}