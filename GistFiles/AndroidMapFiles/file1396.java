public class MyMapFragment extends Fragment {
	private View mMapViewContainer;
	private MapView mMapView;

	public static MyMapFragment newInstance() {
		MyMapFragment fragment = new MyMapFragment();

		Bundle args = new Bundle();
		// add any necessary args here
		fragment.setArguments( args );

		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView( inflater, container, savedInstanceState );

		// We can't grab the MapView onCreate() since Fragment#onCreate() is 
		// called before Activity#onCreate() (where the MapView is created).
		// We also can't do it in Fragment#onActivityCreated() since its called 
		// after Fragment#onCreateView().  So, we grab it every time here.
		//
		// Yes, its ugly that this fragment has to know that it lives inside
		// a MainActivity.
		MainActivity mainActivity = (MainActivity) getActivity();
		mMapViewContainer = mainActivity.mMapViewContainer;
		mMapView = mainActivity.mMapView;
	}

	// your other fragment code
}