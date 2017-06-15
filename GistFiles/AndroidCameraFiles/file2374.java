public class ActivityModelInformation extends InformationActivity {
  
    private HeaderViewHolder headerInformationHolder;

    private TabLayout tabLayout;
    private DeactivatableViewPager viewPager;
    private MapViewFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_information);
        super.baseSetup();
        setupViewPager();
        setupMapFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //CALL PRESENTER TO FETCH DATA
    }

    @Override
    protected void initViews() {
        super.initViews();
        viewPager = (DeactivatableViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        headerInformationHolder = new HeaderViewHolder(findViewById(android.R.id.content));
        //INITIALIZE PRESENTER.
      
    }
  
    @Override
    protected void setInformationData() {
        Bundle bundle = getIntent().getExtras();
        //SET CLASS VARIABLES from BUNDLE
        updateUIHeader();
    }

    /**
     * This method is NOT used by this class.
     * @param isCollapsed state of toolbar.
     */
    @Override
    protected void onToolbarCollapsed(boolean isCollapsed) {
        //IMPORTANT! Do nothing
    }

    /**
     * Retrieves a custom activity title for display on toolbar.
     * @return activity title.
     */
    @Override
    public String getActivityTitle() {
        return "Custom Activity Title";
    }

    /**
     * Updates the flowmeter header information.
     */
    private void updateUIHeader(){
        //UPDATE VALUES ON HEADER VIEW HOLDER
    }


    /**
     * Setup the view pager that shows information about the parameters.
     */
    private void setupViewPager() {
        //INITIALIZE THE ADAPTER
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            public void onPageSelected(int position) {
                boolean pagingEnable = true;
                /*if(position == MyAdapter.PAGE_NEED_IT_TO_MATCH){
                    pagingEnable = false;
                }*/
                viewPager.setPagingEnabled(pagingEnable);
            }
        });
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * This method setups a {@link MapViewFragment} to show the flowmeter location.
     * This method also validates if Google Play Services are available on the current device.
     * If there is no Google Play Services API available the map is not displayed on UI.
     */
    private void setupFlowmeterMapFragment() {
        final int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            FrameLayout mapContainer = (FrameLayout) findViewById(R.id.mapContainer);
            mapContainer.setVisibility(View.GONE);
        } else {
            mapFragment = (MapViewFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapContainer);

            if (mapFragment == null) {
                mapFragment = MapViewFragment.newInstance(/* Parameters if you need */);
                ActivityHelper.addFragmentToActivity(
                        getSupportFragmentManager(), mapFragment, R.id.mapContainer);
            }
        }
    }
}
