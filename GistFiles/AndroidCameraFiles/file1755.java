package com.csci5115.limelight;

/**
 * Created by Abdulrahman Filimban on 11/10/13.
 */
import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;


public class MovieNightFragment extends Fragment {

    public static Fragment newInstance(Context context) {
        ThreeFragment f = new ThreeFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_three, container, false);

        return rootView;
    }

}