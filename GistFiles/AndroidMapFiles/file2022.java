public interface LifeCycleSubscriber {

    void onPause();

    void onResume();

    void onStart();

    void onSaveInstanceState(Bundle outState);

    void onRestoreInstanceState(Bundle savedState);
}