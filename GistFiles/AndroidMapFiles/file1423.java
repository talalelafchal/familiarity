private void createComment(final String body, final String reportId, final boolean isAnon) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(Constants.NETWORK_SCHEME)
                .authority(Constants.API_ENDPOINT)
                .path(Constants.CREATE_COMMENT_PATH);

        final StringRequest request = new StringRequest(StringRequest.Method.POST, builder.build().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (photoPathsLists.size() == 0) {
                    ((MapActivity) getActivity()).newReportCreated(reportId);
                    showPopUp();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String error = volleyError.getLocalizedMessage();
                if (error != null) {
                    //Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    Log.e("CreateReportFragment", "Create comment: " + error);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("body", body);
                params.put("report_id", reportId);

                if (isAnon) params.put("anonymous", "1");

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(Integer.MAX_VALUE, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);
    }

01-12 18:02:44.155    1860-1908/com.agavelab.avisoramap E/Volley﹕ [179] NetworkDispatcher.run: Unhandled exception java.lang.IllegalArgumentException: timeout < 0
    java.lang.IllegalArgumentException: timeout < 0
            at java.net.Socket.setSoTimeout(Socket.java:520)
            at com.squareup.okhttp.internal.http.HttpConnection.isReadable(HttpConnection.java:159)
            at com.squareup.okhttp.Connection.isReadable(Connection.java:236)
            at com.squareup.okhttp.OkHttpClient$1.isReadable(OkHttpClient.java:101)
            at com.squareup.okhttp.internal.http.RouteSelector.next(RouteSelector.java:110)
            at com.squareup.okhttp.internal.http.HttpEngine.connect(HttpEngine.java:317)
            at com.squareup.okhttp.internal.http.HttpEngine.sendRequest(HttpEngine.java:241)
            at com.squareup.okhttp.internal.huc.HttpURLConnectionImpl.execute(HttpURLConnectionImpl.java:420)
            at com.squareup.okhttp.internal.huc.HttpURLConnectionImpl.connect(HttpURLConnectionImpl.java:105)
            at com.squareup.okhttp.internal.huc.HttpURLConnectionImpl.getOutputStream(HttpURLConnectionImpl.java:239)
            at com.android.volley.toolbox.HurlStack.addBodyIfExists(HurlStack.java:240)
            at com.android.volley.toolbox.HurlStack.setConnectionParametersForRequest(HurlStack.java:210)
            at com.android.volley.toolbox.HurlStack.performRequest(HurlStack.java:106)
            at com.android.volley.toolbox.BasicNetwork.performRequest(BasicNetwork.java:93)
            at com.android.volley.NetworkDispatcher.run(NetworkDispatcher.java:110)