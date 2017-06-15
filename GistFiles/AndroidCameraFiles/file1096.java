@GET("weather/v1/7/1/nl/{geoAreaId}/5M/{startime}/{endtime}")
Observable<RainPrognose> getRainPrognose(
        @Path("geoAreaId") int geoAreaId,
        @Path("startime") int startime,
        @Path("endtime") int endtime);


weatherService.getRainPrognose(cityId, 0, 120)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(new Func1<RainPrognose, RainPrognose.Data[]>() {
            @Override
            public RainPrognose.Data[] call(RainPrognose contentElements) {
                int dataSize = contentElements.data.size();
                if (dataSize > 0) {
                    RainPrognose.Data[] dataArray = new RainPrognose.Data[dataSize];

                    for (int i = 0; i < dataSize; i++) {
                        RainPrognose.Data item = (RainPrognose.Data) contentElements.data.get(i);

                        Date date = TimeUtils.parseDateFromString(item.forecastDate, TimeUtils.DTF_DEFAULT);
                        item.forecastDate = TimeUtils.convertDateToString(date, TimeUtils.DTF_SHORT);

                        dataArray[i] = item;
                    }
                    return dataArray;
                } else
                    return null;
            }
        })
        .subscribe(new Subscriber<RainPrognose.Data[]>() {
            @Override
            public void onNext(RainPrognose.Data[] dataArray) {
                graphView.setChartData(dataArray);
                graphView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "e: " + e.getMessage());
            }
        });

/**
 * Obtains settings and timestamps, then makes requests for the radar images.
 */
private void fetchRadarImages() {
    Observable.zip(
            weatherService.getRadarSettings(cityId), weatherService.getRadarList(1, 2),
            new Func2<RadarSettings, RadarList, Map<String, String>>() {
                @Override
                public Map<String, String> call(RadarSettings response1, RadarList response2) {
                    RadarSettings.Data settings = (RadarSettings.Data) response1.data.get(0);
                    RadarList.Data[] radarList = (RadarList.Data[]) response2.data.toArray();

                    // Google Maps is in a projected coordinate system that is based on the wgs84 datum (EPSG 3857).
                    String imageRequestExtent = settings.restrictedExtentMinEpsg3857;

                    String[] splitExtentEpsg4326 = settings.restrictedExtentMinEpsg4326.split(","); // 1 <-> 0, 3 <-> 2)

                    LatLng soundWest = new LatLng(Double.parseDouble(splitExtentEpsg4326[1]), Double.parseDouble(splitExtentEpsg4326[0]));
                    LatLng northWest = new LatLng(Double.parseDouble(splitExtentEpsg4326[3]), Double.parseDouble(splitExtentEpsg4326[2]));

                    groundOverlayBounds = new LatLngBounds(soundWest, northWest);

                    Map<String, String> result = new LinkedHashMap<>(); // Guarantees a predictable iteration order.

                    for (RadarList.Data listItem : radarList) {
                        String layer = settings.layerActual;

                        Date radarTimestamp = TimeUtils.parseDateFromString(listItem.radarTimestamp, TimeUtils.DTF_TIMESTAMP);
                        Date timestamp = TimeUtils.parseDateFromString(listItem.timestamp, TimeUtils.DTF_TIMESTAMP);

                        String timeString = TimeUtils.convertDateToString(timestamp, TimeUtils.DTF_SHORT); // < must be here >

                        int elevation = 0;

                        if (timestamp.after(radarTimestamp)) { // -> future
                            layer = settings.layerForecast;
                            elevation = (int) TimeUtils.getDateDifference(radarTimestamp, timestamp, TimeUnit.MINUTES);
                            timestamp = radarTimestamp;
                        }

                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("http")
                                .authority(ApiConstants.RADAR_IMAGE_HOSTNAME)
                                .appendPath("geoserver")
                                .appendPath("wms")
                                .appendQueryParameter("FORMAT", ApiConstants.ImageFormatIdentifier.PNG) // TODO: GIF (?)
                                .appendQueryParameter("TRANSPARENT", "true")
                                .appendQueryParameter("WIDTH", String.valueOf(ApiConstants.REQUEST_IMAGE_SIZE))
                                .appendQueryParameter("HEIGHT", String.valueOf(ApiConstants.REQUEST_IMAGE_SIZE))
                                .appendQueryParameter("SRS", ApiConstants.SRS.EPSG3857)
                                .appendQueryParameter("BBOX", imageRequestExtent)
                                .appendQueryParameter("SERVICE", "WMS")
                                .appendQueryParameter("VERSION", "1.1.1")
                                .appendQueryParameter("REQUEST", "GetMap")
                                .appendQueryParameter("LAYERS", layer);
                        if (elevation > 0)
                            builder.appendQueryParameter("ELEVATION", String.valueOf(elevation));
                        builder.appendQueryParameter("TIME", TimeUtils.convertDateToString(timestamp, TimeUtils.DTF_TIMESTAMP));

                        result.put(builder.build().toString(), timeString);
                    }
                    return result;
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Map<String, String>>() {
                @Override
                public void call(Map<String, String> result) {
                    radarDataMap = result; // Avoid ConcurrentModificationException

                    for (String url : radarDataMap.keySet()) {
                        // We use fetch() to warm up the cache.
                        Picasso.with(getContext()).load(url).fetch(new Callback() {
                            @Override
                            public void onSuccess() {}

                            @Override
                            public void onError() {}
                        });
                    }
                    handler.postDelayed(radarAnimator, frameInterval * 2); // -> extra delay
                }
            });
}