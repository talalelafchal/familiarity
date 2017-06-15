public static int metersToEquatorPixels(GoogleMap map, LatLng base, float meters) {
    final double OFFSET_LON = 0.5d;

    Location baseLoc = new Location("");
    baseLoc.setLatitude(base.latitude);
    baseLoc.setLongitude(base.longitude);

    Location dest = new Location("");
    dest.setLatitude(base.latitude);
    dest.setLongitude(base.longitude + OFFSET_LON);

    double degPerMeter = OFFSET_LON / baseLoc.distanceTo(dest); // 1m は何度？
    double lonDistance = meters * degPerMeter; // m を度に変換

    Projection proj = map.getProjection();
    Point basePt = proj.toScreenLocation(base);
    Point destPt = proj.toScreenLocation(new LatLng(base.latitude, base.longitude + lonDistance));

    return Math.abs(destPt.x - basePt.x);
}