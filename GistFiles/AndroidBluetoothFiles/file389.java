public class ReportActivity extends AppCompatActivity implements 
                                          ServiceListener.OnSucceessRequestProcessResultListener, 
                                          ServiceListener.OnErrorRequestProcessResultListener {

    private static final String LOG_CAT = "ReportActivity";
                                            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    @Override
    public void OnErrorRequestProcessResult(NetPayError.Error error) {
        Log.e(LOG_CAT, "OnErrorRequestProcessResult(): " + error);
    }

    @Override
    public void onRequestOnlineProcessResult(String jsonResponse) {
        Log.e(LOG_CAT, "onResponse: " + jsonResponse);
    }
}