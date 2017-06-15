public class ModulePlayerActivity extends M360Activity implements NavigationView.OnNavigationItemSelectedListener, PlayerViewContract {

    private final static String COURSE_ID = "COURSE_ID";
    private final static String PROGRAM_ID = "PROGRAM_ID";
    private final static String COURSE_MODE = "COURSE_MODE";

    public static Intent newIntent(Context c, String courseId) {
        Intent intent = new Intent(c, ModulePlayerActivity.class);
        intent.putExtra(COURSE_ID, courseId);
        return intent;
    }

    public static Intent newIntent(Context c, String courseId, String programId, CourseMode courseMode) {
        Intent intent = new Intent(c, ModulePlayerActivity.class);
        intent.putExtra(COURSE_ID, courseId);
        intent.putExtra(PROGRAM_ID, programId);
        intent.putExtra(COURSE_MODE, courseMode);
        return intent;
    }

    PlayerPresenter presenter;
    private CourseDetailed courseDetailed;
    private Handler handler = new Handler();

    //Views
    ...

    //Fragment mgmt
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private CourseLauncherFragment courseLauncherFragment;
    private CourseFinisherFragment courseFinisherFragment;

    //State
    ...



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //View binding
        ...

        //--------------------------- FIRST LAUNCH ---------------------------------------------------------------

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseId = extras.getString(COURSE_ID);

            isInOnLineMode = NetworkUtils.isNetworkAvailable(this);
            presenter = PlayerPresenter.get(isInOnLineMode, this, courseId, programId, courseMode);
        }

        //--------------------------- RESUME ---------------------------------------------------------------

        if (savedInstanceState != null) {

            presenter.onRestoreInstanceState(savedInstanceState);

        } else {
            //Bring courseLauncher to front
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            courseLauncherFragment = CourseLauncherFragment.newInstance(courseId);
            fragmentTransaction.replace(R.id.launcher_finish_frame_layout, courseLauncherFragment, LAUNCHER_FRAGMENT_TAG);
            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();


            Log.i("renaud", "presenter.initFirstLaunch()");
            presenter.initFirstLaunch();
        }

        //Register presenter for superclass to transmit lifecycle events
        setPresenter(presenter);

        courseDetailed = realm.where(CourseDetailed.class).contains("id", courseId).findFirst();
        
    }

    @Override
    public PlayerPresenter getPresenter() {
        return presenter;
    }
    
    }