public class GGMainActivity extends Activity implements GifFlowControl {
    private static final String TAG = "GGMainActivity";
    static ArrayList<String> listOfFiles = new ArrayList<String>();
    static String gifFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    }

    @Override
    public void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ImageGrabFrag())
                .commit();
    }

    @Override
    public void startBuild() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new GifBuilderFrag())
                .commit();
    }

    @Override
    public void startDisplay() {

    }
}
