package andrej.jelic.attendance;


import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentHistoryPresent extends Fragment {

    private DatabaseHandler db;
    private Cursor cursor;
    private StudentCursor adapter;

    private static final String TAG = "History";


    public StudentHistoryPresent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_student_history_present, container, false);

        String tableName = getArguments().getString("key");
        Log.e(TAG, " Table name from bundle: " + tableName);

        ListView studentsView = (ListView) v.findViewById(R.id.ListStudents);
        TextView textView = (TextView) v.findViewById(R.id.textView_tableName);
        textView.setText(tableName);

        db = new DatabaseHandler(getActivity());
        cursor = db.getCursor(tableName);
        adapter = new StudentCursor(getActivity(), cursor);
        studentsView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return v;
    }


}
