public class LoginInteractorImpl implements LoginInteractor {


    OnLoginFinishedListener listener;

    Context context;

    ProgressDialog prgDialog;

    @Override
    public void login(final Context context, final String username, final String password, final OnLoginFinishedListener listener) {
        // Mock login. I'm creating a handler to delay the answer a couple of seconds

        this.listener = listener;

        this.context = context;

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                boolean error = false;
                if ( ! Utility.isNotNull(username)  ){
                    listener.onUsernameEmptyError();
                    error = true;
                }
                if ( ! Utility.isNotNull(password) ){
                    listener.onPasswordError();
                    error = true;
                }

                if( ! Utility.validate(username) ){
                    listener.onUsernameInvalidError();
                    error = true;
                }

                if (!error){
                    //execute the Login Async task...

                    prgDialog = new ProgressDialog(context);
                    // Set Progress Dialog Text
                    prgDialog.setMessage("Please wait...");
                    // Set Cancelable as False
                    prgDialog.setCancelable(false);

                    prgDialog.show();

                    //String response = callWebservice(username, password);


                    callGsonWeb(username, password);

                }

            }
        }, 2);
    }


    void callGsonWeb(String username, String password){

        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-api-key", Utility.getXapikey());
        headers.put("latitude", String.valueOf(0.0f));
        headers.put("longitude", String.valueOf(0.0f));
        headers.put("userid", "123");
        headers.put("device_id", "");
        headers.put("user-agent", "Android");

        HashMap<String, String> params = new HashMap<>();
        params.put( "username", username );
        params.put( "password", password );
        params.put( "device_id", "" );
        params.put( "logintype", "email" );


        MyVolley.init(context);
        RequestQueue queue = MyVolley.getRequestQueue();

        GsonRequest<Passenger> myReq = new GsonRequest<>(Utility.getAbsoluteUrl(context.getResources().getString(R.string.kLogin)),
                Passenger.class,
                headers,
                params,
                createMyReqSuccessListener(),
                createMyReqErrorListener());


        queue.add(myReq);

    }

    private Response.Listener<Passenger> createMyReqSuccessListener() {
        return new Response.Listener<Passenger>() {
            @Override
            public void onResponse(Passenger response) {

                Log.d("LoginInteractor", "onResponse()");
                // Do whatever you want to do with response;
                // Like response.tags.getListing_count(); etc. etc.
                Log.d("Name:, ", response.first_name );

                //obj.getString("registration_status").equals( context.getResources().getString(R.string.kRegisterationStatusComplete) ) && obj.getString("user_type").equals("passenger")

                if( response.registration_status.equals("complete") && response.user_type.equals("passenger") ){

                    downloadUserProfileImage(response.image);

                }
                
//                prgDialog.dismiss();

            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("LoginInteractor", "onErrorResponse()");

                int statusCode = error.networkResponse.statusCode;

                String response = "";

                try {
                    response = new String(error.networkResponse.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject obj = null;

                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if( obj != null ) {

                    String message = null;

                    try {

                        message = obj.getString("message");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    Log.d("status_code", statusCode + "");

                }

                prgDialog.dismiss();
            }
        };
    }

    String callWebservice(String userName, String password){

        // Instantiate Http Request Param Object
        RequestParams requestParams = new RequestParams();
        // When Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(userName) && Utility.isNotNull(password)){
            // When Email entered is Valid
            if(Utility.validate(userName)){
                // Put Http parameter username with value of Email Edit View control
                requestParams.put("username", userName);
                // Put Http parameter password with value of Password Edit Value control
                requestParams.put("password", password);
                // Put Http parameter device_id with value of device_id
                if(BuildConfig.DEBUG)
                    requestParams.put("device_id", "");
                else
                    requestParams.put("device_id", getDeviceID(context.getApplicationContext()));
                // Put Http parameter logintype with value of Password
                requestParams.put("logintype", "email");

                invokeWS(requestParams);
            }
            // When Email is invalid
            else{

                return "Please enter valid email";
                //Toast.makeText(context.getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        }
        // When any of the Edit View control left blank
        else{

            return "Please fill the form, don't leave any field blank";
            // Toast.makeText(context.getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

        return "";
    }

//    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            listener.onSuccess();
//            prgDialog.dismiss();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            prgDialog = new ProgressDialog(context);
//            // Set Progress Dialog Text
//            prgDialog.setMessage("Please wait...");
//            // Set Cancelable as False
//            prgDialog.setCancelable(false);
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//
//        }
//
//    }

    public  String getDeviceID(Context context) {
        TelephonyManager manager =
                (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId;
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            //Tablet
            deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

        } else {
            //Mobile
            deviceId = manager.getDeviceId();

        }
        return deviceId;
    }


    String name;
    // invoking webService
    public void invokeWS(RequestParams params){

        // Show Progress Dialog
        if(Utility.internetStatus( context.getApplicationContext() )){

            // Make RESTful webservice call using AsyncHttpClient object
          // Utility.setLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
//            AsyncHttpClient client = Utility.getHttpClient();

            AsyncHttpClient client = new AsyncHttpClient();

            client.addHeader("x-api-key", Utility.getXapikey() );
            client.addHeader("latitude", String.valueOf( 0.0f ));
            client.addHeader("longitude",String.valueOf( 0.0f ));
            client.addHeader("userid","123");
            client.addHeader("device_id", "");
            client.addHeader("user-agent", "Android");
            client.setConnectTimeout(5000);
            client.setTimeout(7000);

            client.post( Utility.getAbsoluteUrl(context.getResources().getString(R.string.kLogin)),params ,new AsyncHttpResponseHandler()
            {

                @Override
                public void onSuccess(int statusCode, Header[] arg1, byte[] bytes) {
                    // TODO Auto-generated method stub

                    try {

                        String response = new String(bytes, "UTF-8");

                        JSONObject obj = new JSONObject(response);

                        name = obj.getString("first_name") +  obj.getString("last_name");

                        //Utility.setCurrentUser( new User(obj));

                        // When the JSON response has status boolean value assigned with true
                        if(obj.getString("registration_status").equals( context.getResources().getString(R.string.kRegisterationStatusComplete) ) && obj.getString("user_type").equals("passenger")){

                            downloadUserProfileImage(obj.getString("image"));

                        }

                        else{

                            Toast.makeText(context.getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Toast.makeText(context.getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();

                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] arg1, byte[] bytes,
                                      Throwable arg3) {
                    // TODO Auto-generated method stub
//                    prgDialog.hide();
                    try {
                        String response = new String(bytes, "UTF-8");
                        if(statusCode == 404){
                            Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        } else if(statusCode == 400){
                            Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if(statusCode == 500){
                            Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }

            });

        }

        /* showing the internet connectivity error message */

        else {
            Toast.makeText(context.getApplicationContext(), R.string.kNetworkStatusOffline, Toast.LENGTH_LONG).show();
        }

    }

    public void downloadUserProfileImage (String imageUrl){

        String filedirectory ="";

        try {


            filedirectory = context.getPackageManager().getPackageInfo("com.itverticals.capxi", 0).applicationInfo.dataDir+"/media";

        }


        catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final String filedirectory2 = filedirectory;
        File fileDir = new File(filedirectory);

        if (!fileDir.exists()) {

            fileDir.mkdir();
        }
        if (fileDir.exists()){



            final File file = new File(filedirectory
                    + File.separator + "profileImage.jpg" );

            if(!file.exists() ||(file.exists() && file.delete())){

                AsyncHttpClient httpClient = new AsyncHttpClient();

                httpClient.addHeader("x-api-key", Utility.getXapikey() );
                httpClient.addHeader("latitude", String.valueOf( 0.0f ));
                httpClient.addHeader("longitude",String.valueOf( 0.0f ));
                httpClient.addHeader("userid","123");
                httpClient.addHeader("device_id", "");
                httpClient.addHeader("user-agent", "Android");
                httpClient.setConnectTimeout(5000);
                httpClient.setTimeout(7000);

                httpClient.get(imageUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int arg0, Header[] arg1, byte[] fileData) {
                        // TODO Auto-generated method stub
                        Bitmap bitmapImage = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
                        Bitmap resizedProfileBitmap = ThumbnailUtils.extractThumbnail(bitmapImage, bitmapImage.getWidth(), bitmapImage.getHeight());
                        try {

                            Toast.makeText(context.getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();

                            String image_path = filedirectory2 + File.separator + "profileImage.jpg";

                            File file = new File(filedirectory2
                                    + File.separator + "profileImage.jpg" );

                            FileOutputStream fos = null;

                            fos = new FileOutputStream(file);

                            // Use the compress method on the BitMap object to write image to the OutputStream
                            resizedProfileBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            prgDialog.dismiss();
//                            navigateRequestACab();

                            listener.onSuccess(name, image_path );


                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
                        // TODO Auto-generated method stub

                    }
                });
            }

        }
    }
}