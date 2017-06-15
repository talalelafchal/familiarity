public class CourseLauncherPresenter extends FragmentPresenter {

    public static final String TAG = "CourseLauncherPresenter";

    CourseLauncherFragment courseLauncherFragment;
    PlayerPresenter playerPresenter;

    private Realm realm;

    CourseDetailed courseDetailed;
    //private List<NavItem> navItems;

    public CourseLauncherPresenter(CourseLauncherFragment fragment) {
        super(fragment);
        playerPresenter = (PlayerPresenter) activityPresenter;
        courseLauncherFragment = fragment;
        realm = Realm.getDefaultInstance();
        Log.i("renaud", "CourseLauncherPresenter constructor");
    }

    public void init(String courseId) {
        courseDetailed = realm.where(CourseDetailed.class).equalTo("id", courseId).findFirst();
    }

    @Override
    public void onPause() {
        realm.close();
    }


    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {
        
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {

    }
}