public  class GetGeocodingTask extends AsyncTask<Void, Void, List<Address>> {
	private static final String TAG = GetGeocodingTask.class.getSimpleName();

	private Context mContext;
	private NetworkServiceListener mListener;
	private String mQuery;
	private Location mLocation;
	private LatLngBounds mBounds;

	private Exception mThrownException;
	
	public interface NetworkServiceListener {
		
		public void getGeocodingPerformed(List<Address> addressList);
		
		public void getGeocodingFailed(Throwable reason);
		
	}
	
	
	public GetGeocodingTask(Context context, NetworkServiceListener listener, String query) {
		this(context, listener, query, null);
	}

	public GetGeocodingTask(Context context, NetworkServiceListener listener, String query, LatLngBounds bounds) {
		this(context, listener);
		mQuery = query;
		mBounds = bounds;
	}
	
	public GetGeocodingTask(Context context, NetworkServiceListener listener, Location location) {
		this(context, listener);
		mLocation = location;
	}
	
	public GetGeocodingTask(Context context, NetworkServiceListener listener) {
		super();
		if (listener == null) throw new RuntimeException("NetworkServiceListener can't be null.");
		mContext = context;
		mListener = listener;
	}


	/**
	 * Get a Geocoder instance, get the latitude and longitude
	 * look up the address, and return it
	 *
	 * @params params One or more Location objects
	 * @return A string containing the address of the current
	 * location, or an empty string if no address can be found,
	 * or an error message
	 */
	@Override
	protected List<Address> doInBackground(Void... _) {
		Geocoder geocoder =	new Geocoder(mContext, Locale.getDefault());
		// Get the current location from the input parameter list
		// Create a list to contain the result address
		List<Address> addresseList = null;
		
		if ( mLocation != null ) addresseList = this.doReverseGeocoding(geocoder);
		else if ( mQuery != null && mBounds != null ) addresseList = this.doGeocodingWithBounds(geocoder);
		else if ( mQuery != null ) addresseList = this.doGeocoding(geocoder);
		
		if ( mThrownException != null ) mListener.getGeocodingFailed(mThrownException);
		
		return addresseList;
	}

	@Override
	protected void onPostExecute(List<Address> addressList) {
		super.onPostExecute(addressList);

		if ( mThrownException == null ) mListener.getGeocodingPerformed(addressList);
	}
	
	
	/**
	 * Facility methods
	 */
	
	private List<Address> doReverseGeocoding( Geocoder geocoder ) {

		List<Address> addresseList = null;
		
		try {
			addresseList = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // 1 address for reverse geocoding
		} catch (IOException e1) {
			Log.e(TAG,	"IO Exception in doReverseGeocoding()");
			mThrownException = e1;
		} catch (IllegalArgumentException e2) {
			// Error message to post in the log
			String errorString = "Illegal arguments " +	Double.toString(mLocation.getLatitude()) 
					+ " , " +	Double.toString(mLocation.getLongitude()) + " passed to address service";
			Log.e(TAG, errorString);
			mThrownException = e2;
		}
		
		return addresseList;
	}
	
	private List<Address> doGeocoding( Geocoder geocoder ) {

		List<Address> addresseList = null;
		
		try {
			addresseList = geocoder.getFromLocationName(mQuery, 15);
		} catch (IOException e1) {
			Log.e(TAG,	"IO Exception in doGeocoding()");
			mThrownException = e1;
		}
		
		return addresseList;
	}
	
	private List<Address> doGeocodingWithBounds( Geocoder geocoder ) {

		List<Address> addresseList = null;
		
		try {
			addresseList = geocoder.getFromLocationName(mQuery
					, 15
					, mBounds.southwest.latitude
					, mBounds.southwest.longitude
					, mBounds.northeast.latitude
					, mBounds.northeast.longitude
					);
		} catch (IOException e1) {
			Log.e(TAG,	"IO Exception in doGeocodingWithBounds()");
			mThrownException = e1;
		}
		
		return addresseList;
	}

}