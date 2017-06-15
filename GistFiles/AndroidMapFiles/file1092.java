public class AddressFromLocationTask extends AsyncTask<LatLng, Void, android.location.Address> {

    private WeakReference<AddressCallback> mWeakCallback;
    private Exception exception;

    public AddressFromLocationTask(AddressCallback callback) {
        mWeakCallback = new WeakReference<>(callback);
    }

    // Decode image in background.
    @Override protected android.location.Address doInBackground(LatLng... params) {
        Geocoder coder = new Geocoder(App.getInstance().getApplicationContext(), Locale.getDefault());

        try {
            LatLng latLng = params[0];
            ArrayList<android.location.Address> addresses = (ArrayList<android.location.Address>)
                    coder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }
            exception = new IOException("No valid locations found for that address");
        } catch (IOException e) {
            exception = e;
        }
        return null;
    }

    @Override protected void onPostExecute(android.location.Address address) {
        AddressCallback callback = mWeakCallback.get();
        if (callback != null) {
            if (exception == null){
                callback.onLoad(address);
            } else{
                callback.onError(exception);
            }
        }
    }
}