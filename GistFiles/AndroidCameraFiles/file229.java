public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {

    private Pubnub mPubnub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start PubNub
        [ . . . ]

        // Set up View: Map & Action Bar
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }