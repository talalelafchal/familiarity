public class GoogleServiceAuthExampleActivity extends ListActivity {
    private static final String TAG = GoogleServiceAuthExampleActivity.class.getName();

    GoogleServiceAuthenticator authenticator;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authenticator = new GoogleServiceAuthenticator(this);
        Account[] accounts = authenticator.getGoogleAccounts();
        this.setListAdapter(new ArrayAdapter<Account>(this, android.R.layout.simple_list_item_1, accounts));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
            Account account = (Account)getListView().getItemAtPosition(position);

            authenticator.setHostname("yourappl.appspot.com");
            authenticator.setAppPath("http://localhost/");

            try {
                authenticator.execute(account, GOOGLE_ACCOUNT_TYPE.APPENGINE,
                        new GoogleServiceAuthenticator.PostExecuteCallback() {

                            @Override
                            public void run(String acsid) {

                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                HttpPost httpPost = new HttpPost("http://yourappl.appspot.com/sign");
                                HttpResponse httpResponse = null;

                                try {
                                    List<BasicNameValuePair> parms = new ArrayList<BasicNameValuePair>();
                                    parms.add(new BasicNameValuePair("content", "InputData=" + new SimpleDateFormat().format(new Date()) ));
                                    httpPost.setEntity(new UrlEncodedFormEntity(parms, HTTP.UTF_8));

                                    httpPost.setHeader("Cookie", acsid);

                                    httpResponse = httpClient.execute(httpPost);

                                } catch (UnsupportedEncodingException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (ClientProtocolException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                if (httpResponse != null) {
                                    int status = httpResponse.getStatusLine().getStatusCode();
                                    StringBuilder buf = new StringBuilder();
                                    buf.append(String.format("status:%d", status));
                                    try {
                                        InputStream in = httpResponse.getEntity().getContent();
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                        String l = null;
                                        while((l = reader.readLine()) != null) {
                                            buf.append(l + "\n");
                                        }
                                        if (status != HttpStatus.SC_OK) {
                                            Log.e(TAG, buf.toString());
                                        }

                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                    (Toast.makeText(
                                            GoogleServiceAuthExampleActivity.this,
                                            buf.toString(),
                                            Toast.LENGTH_LONG)).show();
                                    Log.d(TAG, buf.toString());
                                }
                            }

                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

}