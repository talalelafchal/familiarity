public class MyRequest {
    private static Context context;
    private static SharedPreferences sharedPrefs;
    private static String serverUrl = "https://serverURL";

    public MyRequest(Context context) {
        this.context = context;
        this.sharedPrefs = context.getSharedPreferences("preference", Context.MODE_PRIVATE);
    }

    public static String getUrlApi(String service){
        return serverUrl+service;
    }

    public static JSONObject getJsonPayload(JSONObject params, String service){

        JSONObject jsonData = null;
        JSONObject request = null;
        try {
            jsonData = new JSONObject();
                request = new JSONObject();
                    request.put("device",android.os.Build.BRAND + " " + android.os.Build.DEVICE + " " + android.os.Build.MODEL + " - v." + Build.VERSION.RELEASE);
                    request.put("device_platform","android");
                    request.put("device_type","android");
                    request.put("params",params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    public void sendRequest(String urlAPI, JSONObject params, final VolleyCallback callback){

        JSONObject jsonData = getJsonPayload(params,urlAPI);
        String url = getUrlApi(urlAPI);
        HttpsTrustManager.allowMySSL();
        JsonObjectRequest postRequest = new JsonObjectRequest(url, jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject resp){
                        callback.onSuccess(resp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("ERRORE", "Error: " + error.getMessage());
                        callback.onError();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                //Log.i("SETHEADERJSON",headers.toString());
                return headers;
            }
        };

        int socketTimeout = 10000;//10 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);

        AppController.getInstance().addToRequestQueue(postRequest);
    }
}