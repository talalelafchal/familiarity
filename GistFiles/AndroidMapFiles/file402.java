import android.util.Log;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

/**
 * Google maps provides a non-null {@link GoogleMap} instance via the {@link OnMapReadyCallback}.
 * However, this does not guarantee that the map has undergone layout. Calling a method which uses the
 * map dimensions can lead to an IllegalStateException in case the layout pass has not been performed.
 * <a href="https://developers.google.com/android/reference/com/google/android/gms/maps/OnMapReadyCallback">source</a>
 * To overcome this problem this class provides a single observable {@link #onMapAndLayoutReadyObservable(MapView)}
 * which emits the google map instance provided by {@link OnMapReadyCallback} only when both
 * the callbacks have been completed.
 */
public final class OnMapAndLayoutReady {

    private OnMapAndLayoutReady() {
    }

    /**
     * Converts {@link OnMapReadyCallback} to an observable.
     * Note that this method calls {@link MapView#getMapAsync(OnMapReadyCallback)} so you there is no
     * need to initialize google map view manually.
     */
    private static Observable<GoogleMap> loadMapObservable(final MapView mapView) {
        return Observable.create(new Observable.OnSubscribe<GoogleMap>() {
            @Override
            public void call(final Subscriber<? super GoogleMap> subscriber) {
                OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        subscriber.onNext(googleMap);
                    }
                };
                mapView.getMapAsync(mapReadyCallback);
            }
        });
    }

    /**
     * Converts {@link ViewTreeObserver.OnGlobalLayoutListener} to an observable.
     * This methods also takes care of removing the global layout listener from the view.
     */
    private static Observable<MapView> globalLayoutObservable(final MapView view) {
        return Observable.create(new Observable.OnSubscribe<MapView>() {
            @Override
            public void call(final Subscriber<? super MapView> subscriber) {
                final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        subscriber.onNext(view);
                    }
                };
                view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
            }
        });
    }

    /**
     * Takes {@link #globalLayoutObservable(MapView)} and {@link #loadMapObservable(MapView)} and zips their result.
     * This means that the subscriber will only be notified when both the observables have emitted.
     */
    public static Observable<GoogleMap> onMapAndLayoutReadyObservable(final MapView mapView) {
        return Observable.zip(globalLayoutObservable(mapView), loadMapObservable(mapView), new Func2<MapView, GoogleMap, GoogleMap>() {
            @Override
            public GoogleMap call(MapView mapView, GoogleMap googleMap) {
                return googleMap;
            }
        });
    }
}