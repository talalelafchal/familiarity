package jinsheng.com.criminalintent;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class CrimeListActivity extends SingleFragmentActivity {
  @Override
    protected Fragment createFragment(){
      return new CrimeListFragment();
  }

}
