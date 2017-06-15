@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		mv = (MapView) findViewById(R.id.mapView);
		mv.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mv.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mv.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mv.onResume();
                MapsInitializer.initialize(this);
	}
