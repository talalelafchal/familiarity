import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import rx.Observer;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * This service is responsible for loading zones objects (Google map circular or poly objects) from remote server or local database. If the device is offline or zones status version is up to date,
 * it loads them from database. Otherwise from remote server. Result is send to subscriber via RxBus. Listen to ZonesSyncEvent events for load results.
 */
public class ZonesService extends Service {

    private static final String TAG = ZonesService.class.getSimpleName();
    private static final String EXTRA_COUNTRY_CODE = "extra:country_code";

    public static void load(Context context, String countryCode) {
        Intent start = new Intent(context, ZonesService.class);
        start.putExtra(EXTRA_COUNTRY_CODE, countryCode);
        context.startService(start);
    }

    @Inject AppPreferences preferences;
    @Inject NetworkUserRepository networkUserRepository;
    @Inject LocalUserRepository localUserRepository;
    @Inject RxBus rxBus;

    public ZonesService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.component(this)
                .inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String countryCode = getCountryCode(intent);

        if (NetworkUtils.isDeviceOnline(this)) {
            checkZonesVersion(countryCode);
        } else {
            loadLocally(countryCode);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private String getCountryCode(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_COUNTRY_CODE)) {
            return ZonesUtil.getCountryCode(this);
        } else {
            return intent.getStringExtra(EXTRA_COUNTRY_CODE);
        }
    }

    private void checkZonesVersion(final String countryCode) {
        GetZoneVersion usecase = new GetZoneVersion(networkUserRepository);
        usecase.execute(new StubObserver<ZoneVersion>() {
            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error checking zone version: " + e.getMessage());
                loadLocally(countryCode);
            }

            @Override
            public void onNext(ZoneVersion zoneVersion) {
                final int newVersion = zoneVersion.getVersion();
                if (preferences.getZoneVersion(countryCode) < newVersion) {
                    updateZones(newVersion, countryCode);
                } else {
                    loadLocally(countryCode);
                }
            }
        });
    }

    private void updateZones(final int version, final String countryCode) {

        SyncZones usecase = new SyncZones(networkUserRepository, localUserRepository, preferences, version, countryCode);
        usecase.execute(new Observer<ZonesCollection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error syncing zones: " + e.getMessage());

                if (e instanceof NoDataException) {
                    rxBus.send(new ZonesSyncEvent(e, true));
                    stopSelf();
                    return;
                }

                loadLocally(countryCode);
            }

            @Override
            public void onNext(ZonesCollection collection) {
                rxBus.send(new ZonesSyncEvent(collection, true));
                stopSelf();
            }
        });
    }

    /**
     * Tries to load zones locally
     */
    private void loadLocally(String countryCode) {

        GetZones usecase = new GetZones(localUserRepository, preferences.getZoneVersion(countryCode), countryCode);
        usecase.execute(new Observer<ZonesCollection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error loading zones locally: " + e.getMessage());
                rxBus.send(new ZonesSyncEvent(e, false));
                stopSelf();
            }

            @Override
            public void onNext(ZonesCollection collection) {
                Timber.v(TAG, "Collection of zones has loaded: " + collection.toString());
                rxBus.send(new ZonesSyncEvent(collection, false));
                stopSelf();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Service can't be bound");
    }
}
