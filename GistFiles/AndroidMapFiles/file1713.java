public class LocationManager extends ReactContextBaseJavaModule {
    public static final String TAG = "LocationManager";
    private static final int LOCATION_TIMEOUT_IN_SECONDS = 10;
    private static final int LOCATION_UPDATE_INTERVAL = 1000;

    public LocationManager(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethodObservable
    public Observable<WritableMap> getLocation() {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getReactApplicationContext());

        Observable<Location> observableFromCache = locationProvider.getLastKnownLocation();

        final LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setExpirationDuration(TimeUnit.SECONDS.toMillis(LOCATION_TIMEOUT_IN_SECONDS))
                .setInterval(LOCATION_UPDATE_INTERVAL)
                .setNumUpdates(1);
        Observable<Location> observableFromRealRequest = locationProvider.getUpdatedLocation(locationRequest)
                .timeout(LOCATION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .first();

        return Observable.<Location>concat(observableFromCache, observableFromRealRequest)
                .first()
                .flatMap(new Func1<Location, Observable<WritableMap>>() {
                    @Override
                    public Observable<WritableMap> call(Location location) {
                        if (location == null) { return Observable.just(null); }
                        WritableMap retObject = Arguments.createMap();
                        retObject.putDouble("latitude", location.getLatitude());
                        retObject.putDouble("longitude", location.getLongitude());
                        return Observable.just(retObject);
                    }
                });
    }
}