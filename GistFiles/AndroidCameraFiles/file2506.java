package com.webserveis.app.testpatternnavigation;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentA extends Fragment {
    private static final String ARG_ID = "param_id";
    private static final String ARG_TITLE = "param_title";
    private static final String TAG = FragmentA.class.getSimpleName();

    private int mParam1;
    private String mParam2;


    public FragmentA() {
    }

    public static FragmentA newInstance(int param1, String param2) {
        FragmentA fragment = new FragmentA();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, param1);
        args.putString(ARG_TITLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_ID);
            mParam2 = getArguments().getString(ARG_TITLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        //Al rescatar de la pila necesario resaltar el menu y cambiar el texto
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(mParam1).setChecked(true);
        getActivity().setTitle(mParam2);

        TextView tvLabel = (TextView) view.findViewById(R.id.label);
        tvLabel.setText(mParam2);

        Log.d(TAG, "onCreateView: " + mParam1 + " " + mParam2);

        return view;
    }

}
