
/**
 * Check if the application is in the foreground or background.
 * *
 * Register this callbacks for an application
 * Application application = (Application) context.getApplicationContext();
 * application.registerActivityLifecycleCallbacks(new BaseLifeCycleCallbacks());
 * *
 * Note: These callbacks can be registered at any level of the application lifecycle.
 * Previous methods to get the application lifecycle forced the lifecycle callbacks to be registered
 * at the start of the application in a dedicated Application class.
 */
public class AppLifeCycle implements Application.ActivityLifecycleCallbacks {


    HashMap<String, Integer> activities;

    public AppLifeCycle() {
        activities = new HashMap<>();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //map Activity unique class name with 1 on foreground
        activities.put(activity.getLocalClassName(), 1);
        applicationStatus();
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //map Activity unique class name with 0 on foreground
        activities.put(activity.getLocalClassName(), 0);
        applicationStatus();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * Check if any activity is in the foreground
     */
    private boolean isBackGround() {
        for (String s : activities.keySet()) {
            if (activities.get(s) == 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Log application status.
     */
    private void applicationStatus() {
        Log.d("ApplicationStatus", "Is application background" + isBackGround());
        if (isBackGround()) {
            //Do something if the application is in background
        }
    }
}