/**
 * Sketch Project Studio
 * Created by Angga 20/04/2016 19:32
 */
public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Fragment fragment;
        Object objectFragment = objectPooling.find("object1");
        if (objectFragment == null) {
            fragment = ArticleFragment.newInstanceFeatured(1, "object1");
            objectPooling.pool(fragment, ArticleFragment.FEATURED_HEADLINE);
        } else {
            fragment = (ArticleFragment) objectFragment;
        }
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }
}