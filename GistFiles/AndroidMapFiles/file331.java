// code
// Do not call this function from the main thread. Otherwise,
// an IllegalStateException will be thrown.
public void getIdThread() {

    AdvertisingIdClient.Info adInfo = null;
    try {
        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this);
    }
    catch (IOException e) {
        // Unrecoverable error connecting to Google Play services (e.g.,
        // the old version of the service doesn't support getting AdvertisingId).
        e.printStackTrace();
    }
    catch (GooglePlayServicesNotAvailableException e) {
        // Google Play services is not available entirely.
        e.printStackTrace();
    }
    catch (GooglePlayServicesRepairableException e) {
        e.printStackTrace();
    }
    final String id = adInfo.getId();
    final boolean isLAT = adInfo.isLimitAdTrackingEnabled();

    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(MyActivity.this, id, Toast.LENGTH_SHORT).show();
        }
    });
}

// dependencies
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:support-v4:20.0.0'
    compile 'com.android.support:appcompat-v7:20.0.0'
    compile 'com.google.android.gms:play-services:5.0.89'
}

// android manifest
<meta-data
    android:name="com.google.android.gms.version"
    android:value="5089000" />

<!-- about a google map -->
<uses-library android:name="com.google.android.maps" />
<meta-data
    android:name="com.google.android.maps.v2.API_KEY"
    android:value="AIzaSyB4CUWyxfR2p2sf59C4opnpRr5zHb0JLZk" />