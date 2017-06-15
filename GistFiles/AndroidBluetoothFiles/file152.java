package andrej.jelic.attend;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class Warning extends Fragment implements View.OnClickListener {

    private static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    private static final String TAG = "Warning fragment";
    public static final String PREFS_NAME = "PrefsFile";

    SharedPreferences prefs;

    public Warning() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        markForOrientationChange();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_warning, container, false);
        v.setOnClickListener(this);
        return v;
    }

    private void markForOrientationChange() {
        prefs = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int brojac = 1;
        editor.putInt("Fragment_to_show", brojac);

        editor.apply();
        brojac = prefs.getInt("Fragment_to_show", 0);
        Log.e(TAG, "broj fragmenta  " + brojac);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        boolean largeScreen = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) > SCREENLAYOUT_SIZE_NORMAL;
        boolean isLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        Log.e(TAG, "velik ekran  " + largeScreen);
        Log.e(TAG, "pejzaz  " + isLandscape);

        FrameLayout frame = (FrameLayout) getActivity().findViewById(R.id.frame);
        FrameLayout frame2 = (FrameLayout) getActivity().findViewById(R.id.frame2);

        if (largeScreen && isLandscape) {
            frame.setLayoutParams(new LinearLayout.LayoutParams(0, MATCH_PARENT));
            frame2.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        } else {
           ////Replace Warning fragment with EnterData fragment
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame, new EnterData());
            transaction.addToBackStack(null);
            fragmentManager.executePendingTransactions();
            transaction.commit();
        }


    }

}
