public class MainActivity extends android.support.v4.app.FragmentActivity {
	// Only one MapView instance is allowed per MapActivity,
	// so we inflate it in the MainActivity and tie its
	// lifetime here to the MainActivity.  Package scope
	// so we can grab them from different instances of map 
	// fragments.
	//
	// The other option was to make them static, but that causes
	// memory leaks on screen rotation.
	View mMapViewContainer;
	MapView mMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// only init fragment the first time onCreate() is called.  Even if the Activity is
		// killed / recreated, FragmentManager will add the Fragment once the Activity is back.
		if( null == savedInstanceState ) {
			// do not add to backstack, or user will be able to press back and
			// view MainActivity's blank layout with nothing in it.
			// In this case, we want the back button to exit the app.
			getSupportFragmentManager()
				.beginTransaction()
				.add( R.id.main_layout, MyMapFragment.newInstance() )
				.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
				.commit();
		}
		
		mMapViewContainer = LayoutInflater.from( this ).inflate( R.layout.mapview, null );
		mMapView = (MapView)mMapViewContainer.findViewById( R.id.map );
	}
}