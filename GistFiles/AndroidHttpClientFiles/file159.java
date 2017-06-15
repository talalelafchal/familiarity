// By passing in the layout XML here, we don't need to manually call
// `setContentView()` in `onCreate()`.
@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't add a fragment if onCreate() is called on a config change,
        // one already exists.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    // Note the underscore postfix. AndroidAnnotations works
                    // by auto-generating a subclass with code that you
                    // would normally have to write.
                    .add(R.id.container, new MainFragment_())
                    .commit();
        }
    }

}