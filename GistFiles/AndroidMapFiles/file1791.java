package com.takeit2eleven.towniemeeting.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockFragment;
import com.takeit2eleven.towniemeeting.android.R;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Mark Wagner
 *         Date: 2/28/13
 *         Time: 10:08 AM
 */
public class MapsFragment extends SherlockFragment {

    Button mHotelMapButton;
    Button mExhibitorButton;
    GoogleMapFragment mGoogleMapFragment;
    ScaleImageViewFragment mScaleImageViewFragment;
    boolean mShowingGoogleMap = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mShowingGoogleMap = savedInstanceState.getBoolean("showingGoogleMap");
        }
        mGoogleMapFragment = new GoogleMapFragment();
        mScaleImageViewFragment = new ScaleImageViewFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showingGoogleMap", mShowingGoogleMap);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.maps_fragment, null);
        mHotelMapButton = (Button) view.findViewById(R.id.hotel_button);
        mExhibitorButton = (Button) view.findViewById(R.id.exhititor_map_button);

        mExhibitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowingGoogleMap = false;
                getChildFragmentManager().beginTransaction()
                        .remove(mGoogleMapFragment)
                        .remove(mScaleImageViewFragment)
                        .replace(R.id.fragment_placeholder, mScaleImageViewFragment, "scaleImage")
                        .commit();

            }
        });

        mHotelMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowingGoogleMap = true;
                getChildFragmentManager().beginTransaction()
                        .remove(mGoogleMapFragment)
                        .remove(mScaleImageViewFragment)
                        .replace(R.id.fragment_placeholder, mGoogleMapFragment, "mapFragment")
                        .commit();

            }
        });

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        getChildFragmentManager().beginTransaction()
                .remove(mGoogleMapFragment)
                .remove(mScaleImageViewFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mShowingGoogleMap){
            getChildFragmentManager().beginTransaction()
                    .remove(mGoogleMapFragment)
                    .remove(mScaleImageViewFragment)
                    .replace(R.id.fragment_placeholder, mGoogleMapFragment, "mapFragment").commit();
        }else{
            getChildFragmentManager().beginTransaction()
                    .remove(mGoogleMapFragment)
                    .remove(mScaleImageViewFragment)
                    .replace(R.id.fragment_placeholder, mScaleImageViewFragment, "scaleImage")
                    .commit();
        }
    }
}