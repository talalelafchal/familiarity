How to Use:
AppController.java is a Application class 
RequestQueueHelperWithTimeout.java, GsonRequest.java are the Util classes

........
Gradle:
    compile 'com.google.code.gson:gson:2.3.1'
    compile 'com.google.code.gson:gson:2.3.1'
    compile 'com.mcxiaoke.volley:library:1.0.18'
    compile('org.apache.httpcomponents:httpmime:4.3.6') {
        exclude module: "httpclient"
    }
    compile('org.apache.httpcomponents:httpcore:4.+') {
        exclude module: "httpclient"
    }
........

Add packaging options for gradle, For example

apply plugin: 'com.android.application'
apply plugin: 'io.fabric'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.0"

    defaultConfig {
        applicationId "com.project.one"
        minSdkVersion 15
        targetSdkVersion 23
        versionCode 9
        versionName "1.9"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    packagingOptions {
        exclude 'META-INF/DEPENDENCIES.txt'
        exclude 'META-INF/LICENSE.txt'
        exclude 'META-INF/NOTICE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/DEPENDENCIES'
        exclude 'META-INF/notice.txt'
        exclude 'META-INF/license.txt'
        exclude 'META-INF/dependencies.txt'
        exclude 'META-INF/LGPL2.1'
    }
}



........

Functionalities: 
GET - Request
POST - Request with small data and large files

-----------------------------------------------------------------------------------------------------------------------------

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static boolean activityVisible;

    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

   @Override
	public void onCreate() {
		super.onCreate();
	        mInstance = this;
	}
	
	public Map<String, String> getHeaders() {
-        Map<String, String> headers = new HashMap<String, String>();
-
-        String token = Prefs.getString(AppConstants.AUTHORIZATION,null);// get token logic
-        String usernameaA = Prefs.getString(AppConstants.USER_NAME,null);// get username logic
-
-        if (token != null) {
-
-            Log.d("userName",usernameaA);
-            Log.d("Authorization", token);
-
-            headers.put("username", usernameaA);
-            headers.put("Authorization", token);
-        }
-        return headers;
-    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

   

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
    
}

-----------------------------------------------------------------------------------------------------------------------------

public class RequestQueueHelperWithTimeout {

    private static final String TAG = "RequestQueueHelper";
    private static HashMap<String, Boolean> errorResolverFlags = new HashMap<>();

    static {
        errorResolverFlags.put("AuthFailureError", false);
        errorResolverFlags.put("ServerError", false);
        errorResolverFlags.put("NetworkError", false);
        errorResolverFlags.put("ParseError", false);
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void cancelPendingRequests(Object tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().cancelAll(tag);
        }
    }

    private static RequestQueue getRequestQueue() {
        return AppController.getInstance().getRequestQueue();
    }

    public static Response.ErrorListener responseErrorListener(final View rootView, final Activity context, final ProgressDialog progress) {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
                //Toast.makeText(EmergencyInfo.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT);
                //Log.e("Alerts Error", error.getMessage());
                Log.e("RequestQueueHelper", "status code: " + error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    progress.dismiss();
                    Log.e(this.getClass().getSimpleName(), "ServerError");
                    CommonFunctions.showSnakbarTypeFour(rootView, context,"ServerTimeout");
                } /*else if (error instanceof AuthFailureError) {
                    // TODO call retrieveAcceccToken and
                    Log.e(this.getClass().getSimpleName(), "AuthFailureError");
                    if(!errorResolverFlags.get("AuthFailureError")) {
                        STTGeneralRoutines gr = new STTGeneralRoutines();
                        gr.retrieveNewToken();
                        errorResolverFlags.put("AuthFailureError", true);
                    } else {
                        errorResolverFlags.put("AuthFailureError", false);
                    }
                }*/ else if (error instanceof ServerError) {
                    //TODO
                    progress.dismiss();
                    Log.e(this.getClass().getSimpleName(), "ServerError");
                    CommonFunctions.showSnakbarTypeFour(rootView, context,"ServerError");
                } else if (error instanceof NetworkError) {
                    //TODO
                    progress.dismiss();
                    Log.e(this.getClass().getSimpleName(), "NetworkError");
                    CommonFunctions.showSnakbarTypeFour(rootView, context,"NetworkError");
                } else if (error instanceof ParseError) {
                    //TODO
                    progress.dismiss();
                    Log.e(this.getClass().getSimpleName(), "ParseError");
                    CommonFunctions.showSnakbarTypeFour(rootView,context,"ParseError");
                }
            }
        };
    }
}
-----------------------------------------------------------------------------------------------------------------------------
public class GsonRequest<T> extends Request<T> {

    private final Gson gson = new Gson();
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<T> listener;
    private final Response.ErrorListener errorListener;
    private final Map<String, String> parameters;
    private File file;
    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;


    /**
     * Make a request and return a parsed object from JSON.
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param method Method.GET, Method.POST, Method.UPDATE, Method.DELETE etc.
     * @param parameters
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener, int method, Map<String, String> parameters) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.parameters = parameters;
        this.errorListener = errorListener;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        buildEntity();
    }

    /**
     * Make a request and return a parsed object from JSON.
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param listener on success listener
     * @param errorListener on error listener
     * @param method Method.GET, Method.POST, Method.UPDATE, Method.DELETE etc.
     * @param parameters parameters to be passed in GET or POST
     * @param sequence sequence of request in the queue
     * @param errorListener
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener,
                       int method, Map<String, String> parameters, int sequence, Response.ErrorListener errorListener1) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.parameters = parameters;
        this.errorListener = errorListener;
        this.setSequence(sequence);
    }

    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                       Response.Listener<T> listener, Response.ErrorListener errorListener,
                       int method, Map<String, String> parameters, File file) {
        super(method, url, errorListener);
        this.file=file;
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.parameters = parameters;
        this.errorListener = errorListener;
        entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        buildMultipartEntity();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if(this.errorListener != null) {
            this.errorListener.onErrorResponse(error);
        }
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Log.d("Request<T>", "response - " + json + ", statusCode - " + response.statusCode);
            //this.setStatusCode(response.statusCode);
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity = entity.build();
            httpentity.writeTo(bos);
        } catch (IOException e) {
            //Volley Log.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    private void buildMultipartEntity() {
        entity.addPart("myfile", new FileBody(file));
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            entity.addTextBody(entry.getKey(), entry.getValue());
        }
    }

    private void buildEntity() {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            entity.addTextBody(entry.getKey(), entry.getValue());
        }
    }


    public Response.ErrorListener responseErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
                //Toast.makeText(EmergencyInfo.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT);

                Log.e("Alerts Error", error.getMessage() + ", ststus code: " + error.networkResponse.statusCode);
            }
        };
    }

    public RetryPolicy getTimeoutPolicy(Integer socketTimeout) {
        // Login time increased to 30 seconds
        socketTimeout = ((socketTimeout==null)? 3000 : socketTimeout);
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return policy;
    }
}



public class RequestQueueHelper {

    private static final String TAG = "RequestQueueHelper";
    private static HashMap<String, Boolean> errorResolverFlags = new HashMap<>();

    static {
        errorResolverFlags.put("AuthFailureError", false);
        errorResolverFlags.put("ServerError", false);
        errorResolverFlags.put("NetworkError", false);
        errorResolverFlags.put("ParseError", false);
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void cancelPendingRequests(Object tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().cancelAll(tag);
        }
    }

    private static RequestQueue getRequestQueue() {
        return AppController.getInstance().getRequestQueue();
    }

    public static Response.ErrorListener responseErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
                //Toast.makeText(EmergencyInfo.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT);
                //Log.e("Alerts Error", error.getMessage());
                Log.e("RequestQueueHelper", "status code: " + error.toString());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {

                } /*else if (error instanceof AuthFailureError) {
                    // TODO call retrieveAcceccToken and
                    Log.e(this.getClass().getSimpleName(), "AuthFailureError");
                    if(!errorResolverFlags.get("AuthFailureError")) {
                        STTGeneralRoutines gr = new STTGeneralRoutines();
                        gr.retrieveNewToken();
                        errorResolverFlags.put("AuthFailureError", true);
                    } else {
                        errorResolverFlags.put("AuthFailureError", false);
                    }
                }*/ else if (error instanceof ServerError) {
                    //TODO
                    Log.e(this.getClass().getSimpleName(), "ServerError");
                } else if (error instanceof NetworkError) {
                    //TODO
                    Log.e(this.getClass().getSimpleName(), "NetworkError");
                } else if (error instanceof ParseError) {
                    //TODO
                    Log.e(this.getClass().getSimpleName(), "ParseError");
                }
            }
        };
    }
}

-----------------------------------------------------------------------------------------------------------------------------

 private void startNetworkRequestForDrugList() {
        String api = AppConstants.MASTER_PARAM_API;
        String mType = AppConstants.MASTER_MEDICATION;
        int getOrPost = Request.Method.GET;
        MasterDrugListCall(ResponseMasterMedications.class, ActEditPatientMedication.this, 0, api, mType, getOrPost);
    }


    //------------------------------------ DRUGS LIST API CALL ---------------------------------------------------//
    public <T> void MasterDrugListCall(Class<T> theClass, Activity context, int position, String mApi, String mType, int mGetOrPost) {

        //progress = CommonFunctions.showLoadingDialog(progress, ActEditPatientMedication.this);

        Map<String, String> params = new HashMap<String, String>();

        Iterator it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Log.d(context.getClass().getCanonicalName(), "params - " + pairs.getKey() + ", " + pairs.getValue());
        }

        String url = AppConstants.SERVER_URL + "/" + mApi + "?type=" + mType;
        Log.d("FinalUrl:->", url);

        GsonRequest<T> myReq = new GsonRequest<T>(
                url,
                theClass,
                AppController.getInstance().getHeaders(),
                MasterDrugListCallSuccessListener(position, theClass, context),
                RequestQueueHelperWithTimeout.responseErrorListener(main_content, context,progress),
                mGetOrPost, params);
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                AppConstants.serverTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueHelper.addToRequestQueue(myReq, "");

    }

    private <T> Response.Listener<T> MasterDrugListCallSuccessListener(final int position, Class<T> theClass, final Context context) {

        return new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {

                CommonFunctions.dismissLoadingDialog(progress);

                responseMasterMedications = (ResponseMasterMedications) response;

                if (responseMasterMedications.getCode() == 200) {
                    System.out.println("RESPONSE-SUCCESSFUL");

                    //SET PROBLEMS LIST IN COLLECTION

                } else {
                    System.out.println("RESPONSE-NOT-SUCCESSFUL");
                    Snackbar.make(main_content, "RESPONSE-NOT-SUCCESSFUL", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }
        };
    }
    //------------------------------------ DRUGS LIST API CALL ---------------------------------------------------//
