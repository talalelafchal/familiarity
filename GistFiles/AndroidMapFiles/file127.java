public class AddressFromStringTask extends AsyncTask<String, Void, android.location.Address> {

    private WeakReference<AddressCallback> mWeakListener;
    private Exception exception;

    public AddressFromStringTask(AddressCallback listener) {
        mWeakListener = new WeakReference<>(listener);
    }

    // Decode image in background.
    @Override protected Address doInBackground(String... params) {
        Geocoder coder = new Geocoder(App.getInstance().getApplicationContext(), Locale.getDefault());

        try {
            ArrayList<android.location.Address> addresses = (ArrayList<android.location.Address>)
                    coder.getFromLocationName(params[0], 1);

            if (addresses != null && addresses.size() > 0) {
                return addresses.get(0);
            }
            exception = new IOException("No valid locations found for that address");
        } catch (IOException e) {
            exception = e;
        }
        return null;
    }

    @Override protected void onPostExecute(Address address) {
        AddressCallback listener = mWeakListener.get();
        if (listener != null) {
            if (exception == null){
                listener.onLoad(address);
            } else{
                listener.onError(exception);
            }
        }
    }
}