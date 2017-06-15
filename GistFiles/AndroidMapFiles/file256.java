import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.List;

public class CountryCodeLoader {

    // You don't know how hard would be to retrieve country code from actual device's position.
  
    private final AppPreferences appPreferences;
    private final RxBus rxBus;
    private final ReactiveLocationProvider locationProvider; // uses https://github.com/mcharmas/Android-ReactiveLocation

    private Subscription subscription;

    public CountryCodeLoader(Context context, AppPreferences appPreferences, RxBus rxBus) {
        this.appPreferences = appPreferences;
        this.rxBus = rxBus;
        this.locationProvider = new ReactiveLocationProvider(context);
    }

    public void subscribe(@NonNull Action1<String> observer, @NonNull Action1<Throwable> error) {
        subscription = rxBus.toObservable()
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        // we are interested only in location updates
                        return o instanceof LocationUpdateEvent;
                    }
                })
                .map(new Func1<Object, Location>() {
                    @Override
                    public Location call(Object o) {
                        // parse the location object
                        return ((LocationUpdateEvent) o).getLocation();
                    }
                })
                .flatMap(new Func1<Location, Observable<List<Address>>>() {
                    @Override
                    public Observable<List<Address>> call(Location location) {
                        // reverse geocode location to address
                        return locationProvider.getReverseGeocodeObservable(location.getLatitude(),
                                location.getLongitude(), 1);
                    }
                })
                .filter(new Func1<List<Address>, Boolean>() {
                    @Override
                    public Boolean call(List<Address> addresses) {
                        // only emmit valid items
                        return addresses != null && !addresses.isEmpty();
                    }
                })
                .map(new Func1<List<Address>, String>() {
                    @Override
                    public String call(List<Address> addresses) {
                        // emit locale of first address resolution
                        return addresses.get(0).getCountryCode();
                    }
                })
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String code) {
                        // only emmit changes
                        return code != null && !code.equals(appPreferences.getSelectedCountryCode());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer, error);
    }

    public void unsubscribe() {
        RxUtils.possiblyUnsubscribe(subscription);
    }
}
