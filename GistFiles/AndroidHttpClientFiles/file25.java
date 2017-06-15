// interfaces de apoyo
// abtraccion de procesos
public interface ProcessMethod {
      public Object doProcess() throws Exception;
}
// abraccion de tareas concluidas
public interface ProcessOnContext {

	public void endProcess(Object o);
	public void doError(Exception e);
}
//


// Clase para envio conexiones HTTP
public class HttpSimpleClient {
	private static final String TAG = "HttpClient";

	public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) throws Exception {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString= convertStreamToString(instream);
				instream.close();
				resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

				// Transform the String into a JSONObject
				JSONObject jsonObjRecv = new JSONObject(resultString);
				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");

				return jsonObjRecv;
			} 

		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	public static String SendHttpPostString(String URL, JSONObject jsonObjSend) throws Exception {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			Log.v("SERVER",URL);
			Log.v("MENSAJE",jsonObjSend.toString());
			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString= convertStreamToString(instream);
				instream.close();
				//resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+resultString+"\n</JSONObject>");

				return resultString;
			} 

		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	public static String SendHttpPostString(String URL, String mensaje) throws Exception {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(mensaje);

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				Header contentEncoding = response.getFirstHeader("Content-Encoding");
				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString= convertStreamToString(instream);
				instream.close();
				//resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+resultString+"\n</JSONObject>");

				return resultString;
			} 

		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
			throw e;
		}
		return null;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 * 
		 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
//  Dialog Fragment 
/* para gestionar conexiones de forma adecuada evita probmeas de rotación */
public class DialogProcessFragment extends Fragment implements
		LoaderCallbacks<Void> {

	public static final String TAG = "Test";

	private LayoutInflater mInflater;
	private Button mBtnReload;

	private boolean mFirstRun = true;

	private final Handler mHandler = new Handler();

	private static final int SLEEP = 2000;

	private Resources mRes;
	private boolean isRunning = false;

	private ProcessMethod metodo;
	private Object result;
	private Exception error;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LoaderManager lm = getLoaderManager();
		if (lm.getLoader(0) != null && isRunning) {
			lm.initLoader(0, null, this);
		}
	}

	public void startLoading(ProcessMethod pMethod) {
		metodo = pMethod;
		showDialog();

		if (mFirstRun) {

			mFirstRun = false;
			getLoaderManager().initLoader(0, null, this);
			isRunning = true;
		} else {
			Log.d(TAG, "restartLoading(): re-starting loader");
			getLoaderManager().restartLoader(0, null, this);
			isRunning = true;
		}
		// first time we call this loader, so we need to create a new one
	}

	@Override
	public Loader<Void> onCreateLoader(int id, Bundle args) {
		AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getActivity()) {

			@Override
			public Void loadInBackground() {
				error = null;
				try {
					// simulate some time consuming operation going on in the
					// background
					/*
					 * Log.d(TAG, "loadInBackground(): doing some work....");
					 * for (int i = 0; i < 5; ++i) { Log.v(TAG, "proceasndo " +
					 * i); Thread.sleep(SLEEP); }
					 */
					result = null;
					if (metodo != null)
						result = metodo.doProcess();

				} catch (Exception e) {
					isRunning = false;
					error = error;
				}
				return null;
			}
		};
		// somehow the AsyncTaskLoader doesn't want to start its job without
		// calling this method
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Void> loader, Void result) {

		hideDialog();
		if (error == null)
			((ProcessOnContext) getActivity()).endProcess(this.result);
		else
			((ProcessOnContext) getActivity()).doError(error);
		isRunning = false;

		Log.d(TAG, "onLoadFinished(): done loading!");

	}

	@Override
	public void onLoaderReset(Loader<Void> loader) {
	}

	public static class DialogFragmentProcess extends DialogFragment {
		/*
		 * All subclasses of Fragment must include a public empty constructor.
		 * The framework will often re-instantiate a fragment class when needed,
		 * in particular during state restore, and needs to be able to find this
		 * constructor to instantiate it. If the empty constructor is not
		 * available, a runtime exception will occur in some cases during state
		 * restore.
		 */
		public DialogFragmentProcess() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ProgressDialog progress = new ProgressDialog(getActivity());
			progress.setMessage("Cargando....");

			progress.setCanceledOnTouchOutside(false);
			setCancelable(false);

			return progress;
		}
	}

	private void showDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		// Fragment prev =
		// getFragmentManager().findFragmentByTag(getActivity().getClass().getName()+"_ProcessFrament");
		if (prev != null) {
			ft.remove(prev);
		}

		// Create and show the dialog.
		DialogFragment newFragment = new DialogFragmentProcess();
		newFragment.show(ft, "dialog");
		// newFragment.show(ft,
		// getActivity().getClass().getName()+"_ProcessFrament");
	}

	private void hideDialog() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager()
						.findFragmentByTag("dialog");
				// Fragment prev =
				// getFragmentManager().findFragmentByTag(getActivity().getClass().getName()+"_ProcessFrament");
				if (prev != null) {
					ft.remove(prev).commit();
				}
			}
		});
	}

	public void restoreProcess(
	/* Bundle savedInstanceState, */Activity activity) {
		// dialogProcessFragment = new DialogProcessFragment();
		// if (savedInstanceState == null) {
		activity.getFragmentManager().beginTransaction()
				.add(this, activity.getClass().getName() + "_ProcessFrament")
				.commit();
		// }

	}

}
// --  forma de invocación de fragment
// en el activity en su metodo onCreate

		dialogProcessFragment = (DialogProcessFragment) getFragmentManager()
				.findFragmentByTag(getClass().getName() + "_ProcessFrament");
		if (dialogProcessFragment == null) {
			dialogProcessFragment = new DialogProcessFragment();
			dialogProcessFragment.restoreProcess(this);
		}

// cuando se inicia

			dialogProcessFragment.startLoading(new ProcessMethod() {

				@Override
				public Object doProcess() throws Exception {
					// TODO Auto-generated method stub
					ObjectMapper mapper = new ObjectMapper();
					
                    .... serializar objeto
                    
					String registroString = mapper.writeValueAsString(registro);
					Log.v(TAG, registroString);
					return HttpSimpleClient.SendHttpPostString(
							URL,
							registroString);
				}
			});

// cuando termina el proceso de envio
	@Override
	public void endProcess(Object o) {
		.... 
	}
	
		@Override
	public void doError(Exception e) {
             ......
        }




// Manifest - agregar activity dentro de application
        <activity
            android:name="pe.edu.upc.moviles.servicios.ActivityTemas"
            android:label="@string/app_name"
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        // agrega permisos
        
            <uses-permission android:name="android.permission.INTERNET" />


