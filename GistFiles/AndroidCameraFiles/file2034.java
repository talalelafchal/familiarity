public class MainFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, View.OnTouchListener {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final Handler handler = new Handler();

    private WeatherService weatherService;

    private LinearLayoutManager layoutManager;

    // --- CARD: Next hour ---
    @Bind(R.id.card_next_hour) LinearLayout cardNextHour;

    // --- CARD: 48-hour forecast ---
    @Bind(R.id.card_48_hours) LinearLayout card48Hours;

    // --- CARD: Rain radar ---
    @Bind(R.id.map_view) MapView mapView;
    @Bind(R.id.radar_timestamp) TextView timeIndicator;

    private GoogleMap googleMap;
    private GroundOverlay imageOverlay;

    private Map<String, String> radarDataMap = new HashMap<>();
    private int radarFrameIndex = 10; // first loop -> from 20 minutes before
    private int frameInterval = 500;
    private LatLngBounds groundOverlayBounds;

    // --- Card: Rain graph ---
    @Bind(R.id.graph_view) RainGraphView graphView;

    // --- Card: 14-days forecast ---
    @Bind(R.id.recycler_view) RecyclerView recyclerView;

    private RecyclerViewAdapter recyclerAdapter;
    private List<RecyclerViewEntry> recyclerData = new ArrayList<>();

    // Store instance variables (???)
    private int page;
    private String cityName;
    private int cityId;
    private double cityLat;
    private double cityLng;
    private int countryId;


    public static MainFragment newInstance(int page, String cityName, int cityId, double cityLat, double cityLng, int countryId) {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        args.putString("cityName", cityName);
        args.putInt("cityId", cityId);
        args.putDouble("cityLat", cityLat);
        args.putDouble("cityLng", cityLng);
        args.putInt("countryId", countryId);
        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weatherService = ApiManager.getService();

        page = getArguments().getInt("page");
        cityName = getArguments().getString("cityName");
        cityId = getArguments().getInt("cityId");
        cityLat = getArguments().getDouble("cityLat");
        cityLng = getArguments().getDouble("cityLng");
        countryId = getArguments().getInt("countryId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        // --- CARD: Next hour ---

        // --- CARD: 48-hour forecast ---

        // --- CARD: Rain radar ---
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // --- Card: Rain graph ---

        // --- CARD: 14-days forecast ---
        // We use this setting to improve performance because we know that changes
        // in content do not change the layout size of the RecyclerView.
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerScrollListener());

        recyclerAdapter = new RecyclerViewAdapter(getContext(), recyclerData);
        recyclerView.setAdapter(recyclerAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        loadDataAsync();

        // TODO: TEMPORARY CONDITION
        if (page == 0) fetchRadarImages();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        handler.removeCallbacksAndMessages(null);
        radarFrameIndex = 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        Log.e(LOG_TAG, "onMapReady");

        map.setMapType(GoogleMap.MAP_TYPE_NONE);

        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                int reversedY = (1 << zoom) - y - 1;
                String s = String.format(Locale.getDefault(), ApiConstants.TILE_SERVER_COUNTRY, countryId, zoom, x, reversedY);
                URL url;
                try {
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
                return url;
            }
        };

        map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        // Add a marker in Utrecht and move the camera.
        LatLng currentPosition = new LatLng(cityLat, cityLng); // TODO: Bind to real data

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 7));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentPosition);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_overlay));
        markerOptions.anchor(0.5f, 0.5f);
        map.addMarker(markerOptions);

        googleMap = map;
    }

    @OnClick({R.id.card_notification, R.id.card_next_hour, R.id.card_48_hours, R.id.map_touch_layer, R.id.card_rain_graph, R.id.card_two_weeks})
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        if (v.getParent().getParent() instanceof ScrollView) {
            Bundle extras = new Bundle();
            // TODO: Fill with real values
            extras.putString(CustomToolbar.TOOLBAR_TITLE, "Zeist");
            extras.putString(CustomToolbar.TOOLBAR_SUBTITLE, "Laatste update 00:14");
            //
            intent.putExtras(extras);
        }

        switch (v.getId()) {
            case R.id.card_notification:

                // TODO: TESTING PURPOSES ONLY
                // --- START ---
                Toast.makeText(getContext(), "Not implemented yet!", Toast.LENGTH_SHORT).show();
                // --- END ---

//                final ScrollView scrollView = (ScrollView) v.getParent().getParent();
//                ValueAnimator valueAnimator = ValueAnimator.ofInt(scrollView.getScrollY(), scrollView.getHeight()); // TODO:
//                valueAnimator.setDuration(500);
//                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        int scrollTo = (Integer) animation.getAnimatedValue();
//                        scrollView.scrollTo(0, scrollTo);
//                    }
//                });
//                valueAnimator.start();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.findViewById(R.id.card_rain_graph).performClick();
//                    }
//                }, 350);
                break;
            case R.id.card_next_hour:
                intent.setClassName(getContext(), NextHourActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.card_48_hours:
                intent.setClassName(getContext(), ForecastGraphActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.map_touch_layer:
                intent.setClassName(getContext(), MapActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.card_rain_graph:
                intent.setClassName(getContext(), RainGraphActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.card_two_weeks:
                intent.setClassName(getContext(), TwoWeeksActivity.class.getName());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @OnTouch({R.id.card_next_hour, R.id.card_48_hours, R.id.map_touch_layer, R.id.card_rain_graph, R.id.card_two_weeks})
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // If touch is on the map layer, delegate this event to view parent.
        if (v.getId() == R.id.map_touch_layer)
            v = (RelativeLayout) v.getParent();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.pop_in);
            v.startAnimation(anim);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.pop_out);
            v.startAnimation(anim);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.pop_out);
            anim.setDuration(150);
            v.startAnimation(anim);
        }
        return false;
    }

    /**
     * Makes specific API calls for all the cards in this fragment (except radar).
     */
    private void loadDataAsync() {

        // Bind data => CARD: Next hour
        weatherService.getForecast(cityId, ApiConstants.ForecastInterval.ONE_HOUR, 0, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Forecast>() {
                    @Override
                    public void onNext(Forecast contentElements) {
                        Forecast.Data[] dataArray = (Forecast.Data[]) contentElements.data.toArray();

                        if (dataArray.length > 0) {
                            Forecast.Data forecast = dataArray[0];

                            ImageView nextHourSymbol = ButterKnife.findById(cardNextHour, R.id.next_hour_symbol);
                            TextView nextHourTemp = ButterKnife.findById(cardNextHour, R.id.next_hour_temp);
                            ImageView nextHourDigit = ButterKnife.findById(cardNextHour, R.id.next_hour_digit);

                            nextHourSymbol.setImageResource(getWeatherSymbolId(getContext(), forecast.weatherSymbol));
                            nextHourTemp.setText(String.valueOf((int) forecast.temperature));
                            nextHourDigit.setImageResource(getWeatherDigitId(getContext(), forecast.weatherDigit));
                        }
                    }

                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "e: " + e.getMessage());
                    }
                });

        // Bind data => CARD: 48-hour forecast
        weatherService.getForecast(cityId, ApiConstants.ForecastInterval.ONE_DAY, 0, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Forecast>() {
                    @Override
                    public void onNext(Forecast contentElements) {
                        Forecast.Data[] dataArray = (Forecast.Data[]) contentElements.data.toArray();

                        if (dataArray.length > 0) {
                            LinearLayout container = (LinearLayout) card48Hours.getChildAt(1); // 0 => title, 1 => container
                            container.removeAllViews(); // [sic]

                            Calendar calendar = Calendar.getInstance();
                            int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

                            Map<int[], String[]> forecastMap = new HashMap<>();

                            if (timeOfDay >= 5 && timeOfDay < 12) { // [Ochtend]
                                forecastMap.put(
                                        new int[]{0, 0, 0, 1},
                                        new String[]{getString(R.string.morning), getString(R.string.afternoon), getString(R.string.evening), getString(R.string.night)});
                            } else if (timeOfDay >= 12 && timeOfDay < 17) { // [Middag]
                                forecastMap.put(
                                        new int[]{0, 0, 1, 1},
                                        new String[]{getString(R.string.afternoon), getString(R.string.evening), getString(R.string.night), getString(R.string.morning)});
                            } else if (timeOfDay >= 17 && timeOfDay < 21) { // [Avond]
                                forecastMap.put(
                                        new int[]{0, 1, 1, 1},
                                        new String[]{getString(R.string.evening), getString(R.string.night), getString(R.string.morning), getString(R.string.afternoon)});
                            } else if (timeOfDay >= 21 && timeOfDay < 5) { // [Nacht]
                                forecastMap.put(
                                        new int[]{0, 0, 0, 0},
                                        new String[]{getString(R.string.night), getString(R.string.morning), getString(R.string.afternoon), getString(R.string.evening)});
                            }

                            Map.Entry<int[], String[]> entry = forecastMap.entrySet().iterator().next();
                            int[] offsets = entry.getKey();
                            String[] titles = entry.getValue();

                            for (int i = 0; i < titles.length; i++) {
                                LinearLayout element = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.day_part_view, card48Hours, false);

                                TextView dayPartTitle = ButterKnife.findById(element, R.id.day_part_title);
                                ImageView dayPartSymbol = ButterKnife.findById(element, R.id.day_part_symbol);
                                TextView dayPartTemp = ButterKnife.findById(element, R.id.day_part_temp);

                                int offset = offsets[i];
                                String title = titles[i];

                                Forecast.Data forecast = dataArray[offset];
                                String weatherSymbol = null;
                                int temperature = 0;

                                if (title.equals(getString(R.string.morning))) {
                                    weatherSymbol = forecast.weatherSymbolMorning;
                                    temperature = forecast.temperatureMorning;
                                } else if (title.equals(getString(R.string.afternoon))) {
                                    weatherSymbol = forecast.weatherSymbolAfternoon;
                                    temperature = forecast.temperatureAfternoon;
                                } else if (title.equals(getString(R.string.evening))) {
                                    weatherSymbol = forecast.weatherSymbolEvening;
                                    temperature = forecast.temperatureEvening;
                                } else if (title.equals(getString(R.string.night))) {
                                    weatherSymbol = forecast.weatherSymbolNight;
                                    temperature = forecast.temperatureNight;
                                }

                                dayPartTitle.setText(title);
                                dayPartSymbol.setImageResource(getWeatherSymbolId(getContext(), weatherSymbol));
                                dayPartTemp.setText(getString(R.string.temperature_string, temperature));

                                container.addView(element);
                            }
                        }
                    }

                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "e: " + e.getMessage());
                    }
                });

        // Bind data => CARD: Rain graph
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

        // Bind data => CARD: 14-days forecast
        weatherService.getForecast(cityId, ApiConstants.ForecastInterval.ONE_DAY, 0, 13)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Forecast>() {
                    @Override
                    public void onNext(Forecast contentElements) {
                        Forecast.Data[] dataArray = (Forecast.Data[]) contentElements.data.toArray();

                        if (dataArray.length > 0) {
                            for (Forecast.Data item : dataArray) {
                                Date date = TimeUtils.parseDateFromString(item.forecastDate, TimeUtils.DTF_DEFAULT);
                                int resource = getWeatherSymbolId(getContext(), item.weatherSymbol);
                                String temperature = getString(R.string.temperature_string, item.temperatureMax);

                                recyclerData.add(new RecyclerViewEntry(date, resource, temperature));
                            }
                            recyclerAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, "e: " + e.getMessage());
                    }
                });

    }

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

    private Runnable radarAnimator = new Runnable() {
        @Override
        public void run() {
//            Log.e(LOG_TAG, "run()");

            if (radarFrameIndex == radarDataMap.size())
                radarFrameIndex = 0;

//            Log.e(LOG_TAG, "frame number: " + radarFrameIndex);

            String url = (new ArrayList<>(radarDataMap.keySet())).get(radarFrameIndex);
            String timeString = (new ArrayList<>(radarDataMap.values())).get(radarFrameIndex);

            new UpdateRadarTask().execute(url, timeString);

            radarFrameIndex++;
            handler.postDelayed(this, frameInterval);
        }
    };

    private class UpdateRadarTask extends AsyncTask<String, Void, Bitmap> {

        String timeString; // => time indicator string

        @Override
        protected Bitmap doInBackground(String... params) {
//            Log.e(LOG_TAG, "doInBackground");

            String url = params[0];
            timeString = params[1];
            Bitmap bitmap = null;
            try {
                bitmap = Picasso.with(getContext()).load(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null)
                return;

            if (imageOverlay == null) {
                GroundOverlayOptions options = new GroundOverlayOptions();
                options.positionFromBounds(groundOverlayBounds);
                options.image(BitmapDescriptorFactory.fromBitmap(bitmap));

                // The order in which this ground overlay is drawn with respect to other overlays (including
                // Polylines and TileOverlays, but not Markers). An overlay with a larger zIndex is drawn
                // over overlays with smaller zIndexes. The order of overlays with the same zIndex value is
                // arbitrary. This is optional and the default zIndex is 0.
                options.zIndex(1);

                imageOverlay = googleMap.addGroundOverlay(options);
            } else
                imageOverlay.setImage(BitmapDescriptorFactory.fromBitmap(bitmap));

            timeIndicator.setText(timeString);

            if (timeIndicator.getVisibility() == View.INVISIBLE) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                timeIndicator.startAnimation(anim);
                timeIndicator.setVisibility(View.VISIBLE);
            }

//            Log.e(LOG_TAG, "end onPostExecute");
        }
    }

    private class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            ImageView pagination = ButterKnife.findById(getActivity(), R.id.pagination);
            int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();

            if (lastVisibleItem <= 7)
                pagination.setImageResource(R.drawable.ic_pagination1);
            else if (lastVisibleItem <= 10)
                pagination.setImageResource(R.drawable.ic_pagination2);
            else if (lastVisibleItem <= 13)
                pagination.setImageResource(R.drawable.ic_pagination3);
        }
    }

    // TODO: Move to HelperUtils.java (?)
    private int getWeatherSymbolId(Context context, String weatherSymbol) {
        String imageName = "ic_weather_" + weatherSymbol.replaceAll("_", "");
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    private int getWeatherDigitId(Context context, int weatherDigit) {
        String imageName = "ic_weather_digit_" + weatherDigit;
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
}