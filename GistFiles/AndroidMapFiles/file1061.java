private LatLng mLatLng;

Callback subscribeCallback = new Callback() {

    @Override
    public void successCallback(String channel, Object message) {
        JSONObject jsonMessage = (JSONObject) message;
        try {
            double mLat = jsonMessage.getDouble("lat");
            double mLng = jsonMessage.getDouble("lng");
            mLatLng = new LatLng(mLat, mLng);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePolyline();
                updateCamera();
                updateMarker();
            }
        });
    }
};