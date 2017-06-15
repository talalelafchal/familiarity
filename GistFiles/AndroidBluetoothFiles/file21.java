package andrej.jelic.attendance;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishedFragment extends Fragment {

    public static final String PREFS_NAME = "PrefsFile";
    private static final String TAG = "Finished fragment ";
    SharedPreferences preferences;
    private Calendar calendar = Calendar.getInstance();

    private DatabaseHandler db;

    private int hour;
    private int min;
    private int end_hour;
    private int end_min;
    private EditText name;


    public FinishedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = init(inflater, container);
        return v;
    }

    private View init(LayoutInflater inflater, ViewGroup container) {
        end_hour = calendar.get(Calendar.HOUR_OF_DAY);
        end_min = calendar.get(Calendar.MINUTE);
        View v = inflater.inflate(R.layout.fragment_finished, container, false);

        preferences = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        hour = preferences.getInt("Hour", 0);
        min = preferences.getInt("Minutes", 0);

        TextView startTime = (TextView) v.findViewById(R.id.view_start_time_fragment_finished);
        TextView endTime = (TextView) v.findViewById(R.id.view_end_time_fragment_finished);

        TextView studentPresent = (TextView) v.findViewById(R.id.view_student_present_fragment_finished);
        studentPresent.setText(Integer.toString(db.getAllStudentsCount()));

        startTime.setText(new StringBuilder().append(pad(hour)).append(" : ").append(pad(min)));
        endTime.setText(new StringBuilder().append(pad(end_hour)).append(" : ").append(pad(end_min)));

        Button generate = (Button) v.findViewById(R.id.button_generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAs();
                export();
            }


        });

        return v;
    }

    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else return "0" + String.valueOf(c);
    }

    public void saveAs() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(R.string.saveAs);

        name = (EditText) dialog.findViewById(R.id.tableName);
        name.setText(DateFormat.getDateTimeInstance().format(new Date()));
        Button save = (Button) dialog.findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = name.getText().toString();
                new CreateNew().execute(tableName);
                dialog.dismiss();

            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void export() {
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String proba = db.composeJSONfromSQLite();
        Log.e(TAG, "Proba Json " + proba);


        params.put("studentsJSON", db.composeJSONfromSQLite());
        client.post("http://andrejkeno.net46.net/insertStudents.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        Log.e(TAG, "Success " + content);
                    }
                }

        );
    }

    public class CreateNew extends AsyncTask<String, String, Boolean> {

        private String tableName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            tableName = params[0];
            boolean check = db.checkTable(new Tables(tableName));
            boolean created = false;

            Log.e(TAG, "Check table OK " + check);

            if (check) {

                created = db.createTable(tableName);
                db.addTableName(new Tables(tableName));
            } else Log.e(TAG, "Tablica vec postoji " + tableName);


            Log.e(TAG, "Created " + created);

            return created;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(getActivity(), R.string.created, Toast.LENGTH_SHORT).show();
                new SaveToTable().execute(tableName);
            } else Toast.makeText(getActivity(), R.string.exists, Toast.LENGTH_SHORT).show();
        }
    }

    public class SaveToTable extends AsyncTask<String, String, Boolean> {

        private String tableName;

        @Override
        protected Boolean doInBackground(String... params) {

            tableName = params[0];
            boolean saved = db.save(new Tables(tableName));

            return saved;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                Toast.makeText(getActivity(), R.string.saved, Toast.LENGTH_SHORT).show();
            } else Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_SHORT).show();
        }
    }
}

