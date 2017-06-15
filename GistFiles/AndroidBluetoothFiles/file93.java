package andrej.jelic.attend;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class EnterData extends Fragment implements View.OnClickListener {


    public static final String PREFS_NAME = "PrefsFile";
    private static final String TAG = "EnterDataActivity";
    public static final String CURRENT_STATE = "currentState";
    private EditText eName;
    private EditText eLastName;
    private EditText eIndex;
    private String name;
    private String lastName;
    private String index;
    private boolean isEmpty = true;
    private int mCurrentState;
    private String proba;
    SharedPreferences prefs;

    public EnterData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        markForOrientationChange();

        View v = inflater.inflate(R.layout.fragment_enter_data, container, false);
        eName = (EditText) v.findViewById(R.id.firstName_enter);
        eLastName = (EditText) v.findViewById(R.id.lastName_enter);
        eIndex = (EditText) v.findViewById(R.id.indexNumber_enter);
        Button loginButton = (Button) v.findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(this);
        return v;
    }

    private void markForOrientationChange() {
        prefs = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        prefs.edit();
        int brojac = 2;
        editor.putInt("Fragment_to_show", brojac);

        editor.apply();
        brojac = prefs.getInt("Fragment_to_show", 0);
        Log.e(TAG, "broj fragmenta  " + brojac);
    }

    @Override
    public void onClick(View v) {

        name = eName.getText().toString();
        lastName = eLastName.getText().toString();
        index = eIndex.getText().toString();

        checkEntry();
        if (isEmpty) {
            return;
        }

        writeToPrefs();

        //Call Main Activity
        Intent main = new Intent(Intent.ACTION_VIEW);
        main.setClass(getActivity(), MainActivity.class);
        startActivity(main);

        /* Provjera da li se podaci zapisuju u PrefsFile
        Log.e(TAG, "Name " + name);
        proba = prefs.getString("Name", "noName");
        Log.e(TAG, "name prefs " +  proba); */

    }

    private void writeToPrefs() {
        prefs = this.getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Name", name);
        editor.putString("LastName", lastName);
        editor.putString("IndexNumber", index);
        editor.putBoolean("hasLoggedIn", true);
        editor.apply();
    }

    // Provjera da unos nije prazan
    private void checkEntry() {
        if (name.length() == 0) {
            eName.requestFocus();
            Toast.makeText(getActivity(), "Write your name!", Toast.LENGTH_SHORT).show();
            return;
        } else if (lastName.length() == 0) {
            eLastName.requestFocus();
            Toast.makeText(getActivity(), "Write your last name!", Toast.LENGTH_SHORT).show();
            return;
        } else if (index.length() == 0) {
            eIndex.requestFocus();
            Toast.makeText(getActivity(), "Write your index number! ", Toast.LENGTH_SHORT).show();

            return;
        }
        isEmpty = false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentState = savedInstanceState.getInt(CURRENT_STATE);
            name = savedInstanceState.getString("name");
            Log.e(TAG, "pokrenut sam ponovno  " + name);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_STATE, mCurrentState);
        outState.putString("name", name);

    }

    @Override
    public void onStop() {
        super.onStop();

    }



}
