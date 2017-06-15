 public static double EarthRadius = 6378137.0;
    static double TwoPi = Math.PI * 2, DegreesToRadians = 0.0174532925, RadiansToDegrees = 57.2957795;

    public static LatLng getLatLng(LatLng source, double range, double bearing) {
        range = range * 1000;
        double latA = source.latitude * DegreesToRadians;
        double lonA = source.longitude * DegreesToRadians;
        double angularDistance = range / EarthRadius;
        double trueCourse = bearing * DegreesToRadians;

        double lat = Math.sin(
                Math.sin(latA) * Math.cos(angularDistance) +
                        Math.cos(latA) * Math.sin(angularDistance) * Math.cos(trueCourse));

        double dlon = Math.atan2(
                Math.sin(trueCourse) * Math.sin(angularDistance) * Math.cos(latA),
                Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat));

        double lon = ((lonA + dlon + Math.PI) % TwoPi) - Math.PI;

        Log.i("LATLNG-" + range, lat * RadiansToDegrees + "::" + lon * RadiansToDegrees);
        return new LatLng(
                lat * RadiansToDegrees,
                lon * RadiansToDegrees
        );
    }