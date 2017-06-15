import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements SampleFragment.OnArticleSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onArticleSelected() {
        Toast.makeText(this, "Article selected.", Toast.LENGTH_SHORT).show();
    }
}