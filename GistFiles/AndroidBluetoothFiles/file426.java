package andrej.jelic.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;


public class History extends Activity implements AdapterView.OnItemClickListener {

    private static final String TAG = "History";
    private static final int LOADER_ID = 1;
    private static final String TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT = "student_present_fragment";

    private DatabaseHandler db;
    private TableAdapter adapter;
    private Cursor cursor;
    private FragmentManager fm;
    private String tableName;
    private FragmentTransaction transaction;
    private ActionBar bar;
    private boolean delete = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);

        bar = getActionBar();
        bar.hide();

        ListView tablesView = (ListView) findViewById(R.id.ListTables);

        bar.setDisplayShowTitleEnabled(false);
        fm = getFragmentManager();

        db = new DatabaseHandler(this);
        cursor = db.getTables();
        adapter = new TableAdapter(this, cursor);
        tablesView.setAdapter(adapter);
        tablesView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        bar.show();
        tableName = cursor.getString(cursor.getColumnIndex(DatabasesContract.HistoryDatabase.COLUMN_TABLENAME));
        StudentHistoryPresent studentHistoryPresent = new StudentHistoryPresent();

        Bundle args = new Bundle();
        args.putString("key", tableName);
        studentHistoryPresent.setArguments(args);


        transaction = fm.beginTransaction();
        transaction.add(android.R.id.content, studentHistoryPresent, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
        fm.executePendingTransactions();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void export() {
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String proba = db.composeJSONfromSQLite();
        Log.e(TAG, "Proba Json " + proba);


        params.put("studentsJSON", db.composeJSONfromSQLite(tableName));
        client.post("http://andrejkeno.net46.net/insertStudents.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        Log.e(TAG, "Success " + content);
                    }
                }

        );
    }

    @Override
    public void onBackPressed() {
        //make addToBackStack work
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            bar.hide();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.export:
                export();
                return true;

            case R.id.viewAndExport:

                bar.hide();
                export();

                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }

                WebViewFragment webViewFragment = new WebViewFragment();
                transaction = fm.beginTransaction();
                transaction.add(android.R.id.content, webViewFragment, TAG_ACTIVE_STUDENT_PRESENT_FRAGMENT);
                fm.executePendingTransactions();
                transaction.addToBackStack(null);
                transaction.commit();
                return true;

            case R.id.delete:

                delete = true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
