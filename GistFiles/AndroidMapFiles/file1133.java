import android.app.Application;

/**
 * @author Gerónimo Oñativia <geronimox@gmail.com>
 */
public class App extends Application {
    private static App instance;
    private static Analytics analytics;

    public static App instance() {
        return instance;
    }

    public static Analytics analytics() {
        if (analytics == null)
            analytics = Analytics.instance(instance(), BuildConfig.ANALYTICS_TRACKING_ID, BuildConfig.DEBUG);
        return analytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
