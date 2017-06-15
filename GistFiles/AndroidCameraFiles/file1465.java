public class carTrackerActivity extends LocationTrackerActivity {

    private GoogleMap mMap;

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

    private static final LatLng LHR = new LatLng(51.471547, -0.460052);

    private static final LatLng LAX = new LatLng(33.936524, -118.377686);

    private static final LatLng JFK = new LatLng(40.641051, -73.777485);

    private static final LatLng AKL = new LatLng(-37.006254, 174.783018);

    private final static String LINE = "rvumEis{y[}DUaBGu@EqESyCMyAGGZGdEEhBAb@DZBXCPGP]Xg@LSBy@E{@SiBi@wAYa@AQGcAY]I]KeBm@_Bw@cBu@ICKB}KiGsEkCeEmBqJcFkFuCsFuCgB_AkAi@cA[qAWuAKeB?uALgB\\eDx@oBb@eAVeAd@cEdAaCp@s@PO@MBuEpA{@R{@NaAHwADuBAqAGE?qCS[@gAO{Fg@qIcAsCg@u@SeBk@aA_@uCsAkBcAsAy@AMGIw@e@_Bq@eA[eCi@QOAK@O@YF}CA_@Ga@c@cAg@eACW@YVgDD]Nq@j@}AR{@rBcHvBwHvAuFJk@B_@AgAGk@UkAkBcH{@qCuAiEa@gAa@w@c@o@mA{Ae@s@[m@_AaCy@uB_@kAq@_Be@}@c@m@{AwAkDuDyC_De@w@{@kB_A}BQo@UsBGy@AaA@cLBkCHsBNoD@c@E]q@eAiBcDwDoGYY_@QWEwE_@i@E}@@{BNaA@s@EyB_@c@?a@F}B\\iCv@uDjAa@Ds@Bs@EyAWo@Sm@a@YSu@c@g@Mi@GqBUi@MUMMMq@}@SWWM]C[DUJONg@hAW\\QHo@BYIOKcG{FqCsBgByAaAa@gA]c@I{@Gi@@cALcEv@_G|@gAJwAAUGUAk@C{Ga@gACu@A[Em@Sg@Y_AmA[u@Oo@qAmGeAeEs@sCgAqDg@{@[_@m@e@y@a@YIKCuAYuAQyAUuAWUaA_@wBiBgJaAoFyCwNy@cFIm@Bg@?a@t@yIVuDx@qKfA}N^aE@yE@qAIeDYaFBW\\eBFkANkANWd@gALc@PwAZiBb@qCFgCDcCGkCKoC`@gExBaVViDH}@kAOwAWe@Cg@BUDBU`@sERcCJ{BzFeB";

    private static final String TAG = "carTrackerActivity";

    LatLng latLng;

    private Marker marker;

    private List<LatLng> decodedPath;
    public int endIndex = 0;

    private CompositeDisposable _disposables;

    boolean isFirstSet  = false;

    boolean isAniminating = false;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_tracker);
        ButterKnife.bind(this);
        setUpMapIfNeeded();

        final Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.red_car);


        _disposables = new CompositeDisposable();

        decodedPath = new ArrayList<>();

         Disposable dispos = relay.filter(new Predicate<Location>() {
            @Override
            public boolean test(Location location) {

                if(!isFirstSet){
                    isFirstSet = true;
                    return true;
                }
                else if(mCurrentLocation.distanceTo(location) > 1){
                    return true;
                }



              return false;
            }

        }).subscribe(new Consumer<Location>() {
            @Override
            public void accept(Location location) throws Exception {

                Log.d(TAG, "accept: "+location.toString());

                   decodedPath.add(new LatLng(location.getLatitude(), location.getLongitude()));

                      if(marker == null) {
                          marker = mMap.addMarker(new MarkerOptions()
                                  .icon(BitmapDescriptorFactory.fromBitmap(largeIcon))
                                  .anchor(0.5f, 0.3f)
                                  .position(new LatLng(location.getLatitude(), location.getLongitude())));

                       }

                        if(!isAniminating){
                            animateMarker(marker,new LatLng(location.getLatitude(), location.getLongitude()));
                        }




                Toast.makeText(carTrackerActivity.this, ""+location, Toast.LENGTH_SHORT).show();
                mCurrentLocation = location;

            }
        });

        _disposables.add(dispos);



    }


    @Override
    public void onResume() {
        super.onResume();

        //setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMapAsync(new OnMapReadyCallback() {



                    @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (mMap != null) {

                    mMap.getUiSettings().setRotateGesturesEnabled(false);
                    mMap.getUiSettings().setTiltGesturesEnabled(false);




                    GoogleDirection.withServerKey(getResources().getString(R.string.direction_api_key))
                            .from(new LatLng(24.87854,67.06409))
                            .to(new LatLng(24.92612,67.06412))
                            .execute(new DirectionCallback() {



                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    if(direction.isOK()) {

                                        decodedPath = direction.getRouteList().get(0).getOverviewPolyline().getPointList();
                                       // decodedPath = PolyUtil.decode(LINE);
                                        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.red_car);
                                        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));









                                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(decodedPath.get(0),15));

                                       // animateMarker(marker,decodedPath.get(startIndex));

                                    } else {

                                        Log.d(TAG, "onDirectionSuccess: "+rawBody);

                                        Toast.makeText(carTrackerActivity.this, ""+rawBody, Toast.LENGTH_SHORT).show();
                                        // Do something
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something
                                }
                            });







                }

            }
        });


    }

     void animateMarker(final Marker marker, LatLng finalPosition) {

         isAniminating = true;


        final Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marker,property,typeEvaluator,decodedPath.get(0));
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.addListener(animatorListenerAdapter);

        //animator.addListener(animatorListenerAdapter);



    }






    final TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {

            LatLng latLng =  SphericalUtil.interpolate(startValue, endValue,fraction);

            Location startLoca = new Location("startLocation");
            startLoca.setLatitude(marker.getPosition().latitude);
            startLoca.setLongitude(marker.getPosition().longitude);

            Location endLoca = new Location("endLocation");
            endLoca.setLatitude(latLng.latitude);
            endLoca.setLongitude(latLng.longitude);



            float testbearing = startLoca.bearingTo(endLoca);

            // double bearing = SphericalUtil.computeHeading(startValue,latLng);


            //Log.d(TAG, "testbearing: "+testbearing);

            //Log.d(TAG, "bearing: "+bearing);



//                float rot = (float) (fraction * bearing + (1 - fraction) * startRotation);
//                float rota = -rot > 180 ? rot / 2 : rot;
//                Log.d(TAG, "rotation: with  brearing "+rota);


            float rotataion = fraction * testbearing + (1 - fraction) * marker.getRotation();
            float Trota = -rotataion > 180 ? rotataion / 2 : rotataion;


            marker.setRotation(Trota);


            return latLng;
        }
    };


    Animator.AnimatorListener animatorListenerAdapter =  new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

            Log.d(TAG, "onAnimationStart: ");

            isAniminating = true;

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            Log.d(TAG, "onAnimationEnd: ");

            decodedPath.remove(0);

            if(decodedPath.size() < 1) {
                isAniminating = false;
                return;
            }
            else {

                ObjectAnimator animator = (ObjectAnimator) animation.clone();
                animator.setObjectValues(decodedPath.get(0));
                animator.start();
                animation.addListener(this);

            }


        }

        @Override
        public void onAnimationCancel(Animator animation) {
            Log.d(TAG, "onAnimationCancel: ");
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            Log.d(TAG, "onAnimationRepeat: ");
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        _disposables.clear();
    }



}