package andrej.jelic.attendance;

import android.app.Fragment;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveStudentPresentFragment extends ListFragment {


    private static final String TAG = "ActiveStudentPresent";
    private static final int LOADER_ID = 0;

    private DatabaseHandler db;

    public ActiveStudentPresentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        db = new DatabaseHandler(getActivity());

        Cursor cursor = db.getActiveCursor();
        ActiveStudentCursor adapter = new ActiveStudentCursor(getActivity(), cursor);
        setListAdapter(adapter);

    }
}


