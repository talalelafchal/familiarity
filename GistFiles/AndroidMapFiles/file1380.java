
public class Application extends android.app.Application {

    private static Application sInstance;

    @Override
    public void onCreate() {
        sInstance = this;
        Log.d("APP", "Iniciando App");
        super.onCreate();
    }

    public static Application getApplication() {
        return sInstance;
    }

    public void sayHello(){
        Log.d("APP", "Hola!!");
    }
}
