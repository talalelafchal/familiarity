package edu.ualr.cpsc7398.updatechecker.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.InformationAdapter;
import edu.ualr.cpsc7398.updatechecker.controller.service.IntentServiceForBackgroundWork;
import edu.ualr.cpsc7398.updatechecker.controller.service.utils.DataBean;
import edu.ualr.cpsc7398.updatechecker.controller.service.utils.HttpRequestRunnable;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralInformation;


public class InformationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "InformationFragment";
    public static InformationAdapter mAdapter;
    public static Context mInformationContext;
    protected RecyclerView mInformationRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<UrlDataGeneralInformation> listUrlDataGeneralInformation;
    private Button okBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listUrlDataGeneralInformation = new ArrayList<>();
        listUrlDataGeneralInformation = initDataset(); //returns List<UrlData>
    }

    //draw the fragment (Create GUI of fragment)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag1_information, container, false); //recycler_view_frag1 is a fragment
        rootView.setTag(TAG);

        mInformationContext = getActivity().getApplicationContext(); //set the context

        mInformationRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewInformationFragment);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mInformationRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new InformationAdapter(listUrlDataGeneralInformation, mInformationContext);
        mInformationRecyclerView.setAdapter(mAdapter); //now Adapter's activity starts

        okBtn = (Button) getActivity().findViewById(R.id.okButton);
        okBtn.setOnClickListener(this);
        okBtn.setOnFocusChangeListener(new View.OnFocusChangeListener() { //this is because single click only performs focus
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.performClick();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() { //onStart comes after onCreateView() callback
        DataBean.setUrl(mAdapter.getItemFromPredefinedPosition(0).getValue());
        DataBean.setTotalUpdates(Integer.parseInt(mAdapter.getItemFromPredefinedPosition(1).getValue()));
        DataBean.setSeed(Integer.parseInt(mAdapter.getItemFromPredefinedPosition(2).getValue()));
        super.onStart();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    private List<UrlDataGeneralInformation> initDataset() {


//default initialization of recyclerview's row
        listUrlDataGeneralInformation.add(new UrlDataGeneralInformation("URL:  ", "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson"));
        listUrlDataGeneralInformation.add(new UrlDataGeneralInformation("Total Updates to know:  ", "5"));
        listUrlDataGeneralInformation.add(new UrlDataGeneralInformation("Time-Interval (Seed) :  ", "9"));

        Log.i("Url: ", listUrlDataGeneralInformation.get(0).toString()); //returns the address of first element of the listUrlDataGeneralInformation list
        Log.i("Total Updates: ", listUrlDataGeneralInformation.get(1).toString());
        Log.i("Time Interval: ", listUrlDataGeneralInformation.get(2).toString());

        return listUrlDataGeneralInformation;
    }

    @Override
    public void onClick(View v) {
        DataBean dataBean = new DataBean(); //reset values
        if (v.getId() == okBtn.getId()) { //or v.getId() == R.id.okButton

//getting data from recycler view using adapter
//            Log.i("Url: ", mAdapter.getItemFromPredefinedPosition(0).getValue()); //getName() gives value of TextView. This is according to what defined in Model
//            Log.i("Total Updates: ", mAdapter.getItemFromPredefinedPosition(1).getValue());
//            Log.i("Time Interval: ", mAdapter.getItemFromPredefinedPosition(2).getValue());


//inserting data to recycler view using adapter
//            mAdapter.insert(0, new UrlDataGeneralInformation("uddhav", "k cha?"));

//updating data to recycler view using adapter
//            mAdapter.update(0, new UrlDataGeneralInformation("uddhav", "k cha?"));

//            String url = mAdapter.getItemFromPredefinedPosition(0).getValue(); //adapter always gets old value, because adapter has not been notified
//            int totalUpdatestoKnow = Integer.parseInt(mAdapter.getItemFromPredefinedPosition(1).getValue());
//            int requestTimePeriod = Integer.parseInt(mAdapter.getItemFromPredefinedPosition(2).getValue());


            ConclusionFragment.mAdapter.removeAll();

            new HttpRequestRunnable(); //initialie the information

            Intent intent = new Intent(getActivity().getApplicationContext(), IntentServiceForBackgroundWork.class);
//            Bundle mBundle = new Bundle();
//            mBundle.putString("URL", url);
//            mBundle.putInt("TOTAL_UPDATES", totalUpdatestoKnow);
//            mBundle.putInt("TIME_PERIOD", requestTimePeriod);
//            intent.putExtras(mBundle);
            getActivity().startService(intent);
        }
    }
}
