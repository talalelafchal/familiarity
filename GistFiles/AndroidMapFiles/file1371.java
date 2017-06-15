import com.google.android.gms.maps.model.LatLngBounds;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * LazyZoneLoader loads zones (predefined Google Map circular or poly objects) from local database based on given GoogleMap projection - LatLngBounds. 
 * First subscribe for receiving events and then call onNewCameraBounds() every time GoogleMap camera moves.
 * It tries to load zones ONLY whose following conditions are met:
 * 1. Zone is within selected country code. Country code is taken from device's settings or from user selection.
 * 2. Zone is within given GoogleMap projection - LatLngBounds.
 * Each load has debounce rate - it is timeout for load in milliseconds. It's a time when user have time to move map again without extra loading.
 * Loader has it's own in-memory cache. First load will be from local database, next loads will be from this in-memory cache; ONLY when following conditions are met:
 * 1. Selected country code is the same as was in previous load.
 * 2. ZoneVersion is the same as was in previous load.
 * If those conditions are NOT met, loader tries to load from local database again.
 *
 */
public class ZoneLazyLoader {

    private static final int PROJECTION_MOVE_DEBOUNCE_RATE = 600;

    private final UserRepository userRepository;
    private final AppPreferences appPreferences;

    private final Subject<LatLngBounds, LatLngBounds> subject;
    private final List<Zone> CACHE;

    private int lastZoneVersion;
    private String lastCountryCode;
    private CompositeSubscription subscriptions;

    public ZoneLazyLoader(UserRepository userRepository, AppPreferences appPreferences) {
        this.userRepository = userRepository;
        this.CACHE = new ArrayList<>();
        this.subject = new SerializedSubject<>(PublishSubject.<LatLngBounds>create());
        this.appPreferences = appPreferences;
        this.subscriptions = new CompositeSubscription();
        this.lastCountryCode = appPreferences.getSelectedCountryCode();
        this.lastZoneVersion = appPreferences.getZoneVersion(lastCountryCode);
    }

    /**
     * Subscribe to lazy load events. Use must subscribe to it, otherwise you won't get load results back.
     * @param observer
     */
    public void subscribe(Observer<List<Zone>> observer) {
        Subscription subscription = subject
                .debounce(PROJECTION_MOVE_DEBOUNCE_RATE, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<LatLngBounds, Observable<List<Zone>>>() {
            @Override
            public Observable<List<Zone>> call(LatLngBounds latLngBounds) {
                return zonesObservable(latLngBounds);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        subscriptions.add(subscription);
    }

    private void invalidateCacheIfRequired() {
        if (lastCountryCode != null && !lastCountryCode.equals(appPreferences.getSelectedCountryCode())) {
            final String newCode = appPreferences.getSelectedCountryCode();
            this.lastCountryCode = newCode;
            this.lastZoneVersion = appPreferences.getZoneVersion(newCode);
            invalidateCache();
        }
    }

    /**
     * Unsubscribe from load events. Use this in e.g. in onDestroy() methods etc. when you don't want receive events anymore.
     */
    public void unsubscribe() {
        subscriptions.clear();
    }

    /**
     * Triggers new lazy load. Call this everytime GoogleMap projection has changed - when user moves camera.
     * @param latLngBounds GoogleMap projection.
     */
    public void onNewCameraBounds(final LatLngBounds latLngBounds) {
        subject.onNext(latLngBounds);
    }

    private Observable<List<Zone>> zonesObservable(final LatLngBounds bounds) {
        Observable<List<Zone>> observable;

        invalidateCacheIfRequired();

        // if cache is not filled yet or new zone sync happened
        if (CACHE.isEmpty() || lastZoneVersion < appPreferences.getZoneVersion(lastCountryCode)) {
            observable = getStorageZoneObservable();
        } else {
            observable = getCachedZoneObservable();
        }
        return observable
                .map(new Func1<List<Zone>, List<Zone>>() {
                    @Override
                    public List<Zone> call(List<Zone> zones) {
                        return ZonesUtil.filterVisibleByCountry(zones);

                    }
                }).map(new Func1<List<Zone>, List<Zone>>() {
                    @Override
                    public List<Zone> call(List<Zone> zones) {
                        return ZonesUtil.filterInBounds(zones, bounds);
                    }
                });
    }

    private Observable<List<Zone>> getStorageZoneObservable() {
        return userRepository.getZones(lastCountryCode)
                .doOnNext(new Action1<List<Zone>>() {
                    @Override
                    public void call(List<Zone> zones) {
                        // fill the zones cache
                        invalidateCache();
                        CACHE.addAll(zones);
                    }
                });
    }

    private Observable<List<Zone>> getCachedZoneObservable() {
        return Observable.create(new Observable.OnSubscribe<List<Zone>>() {
            @Override
            public void call(Subscriber<? super List<Zone>> subscriber) {
                subscriber.onNext(CACHE);
            }
        });
    }

    /**
     * Force invalid cache.
     */
    public void invalidateCache() {
        CACHE.clear();
    }

}
