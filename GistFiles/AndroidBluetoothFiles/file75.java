package andrej.jelic.attendance;


import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentPresentFragment extends ListFragment { //} implements LoaderManager.LoaderCallbacks<List<Student>> {

    private static final String TAG = "StudentPresentFragment";
    private static final int LOADER_ID = 1;

    private DatabaseHandler db;

    public StudentPresentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new DatabaseHandler(getActivity());

        Cursor cursor = db.getCursor();
        StudentCursor adapter = new StudentCursor(getActivity(), cursor);
        setListAdapter(adapter);
        setEmptyText(getActivity().getString(R.string.noStudents));

    }

}
