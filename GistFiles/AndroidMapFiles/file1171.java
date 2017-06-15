package test.fragment.list;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ListTestActivity extends Activity {

    private static final String TAG_LIST = "list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_LIST);
        if (fragment == null) {
            fragment = TestListFragment.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, fragment, TAG_LIST);
            ft.commit();
        }
    }
}
