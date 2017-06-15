package edu.ualr.cpsc7398.updatechecker.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.StatisticsAdapter;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralStatistics;

public class StatisticsFragment extends Fragment {

    private static final String TAG = "StatisticsFragment";
    public static Context mStatisticsContext;
    public static RecyclerView mStatisticsRecyclerView;
    public static StatisticsAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<UrlDataGeneralStatistics> listUrlDataGeneralStatistics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listUrlDataGeneralStatistics = new ArrayList<>();
//        listUrlDataGeneralStatistics = new LinkedList<>();
        listUrlDataGeneralStatistics = initDataset(); //returns List<UrlData>
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag2_statistics, container, false); //recycler_view_frag1 is a fragment
        rootView.setTag(TAG);

        mStatisticsContext = getActivity().getApplicationContext(); //set the context


        mStatisticsRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView2FragmentStatistics);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mStatisticsRecyclerView.setLayoutManager(mLayoutManager);
        mStatisticsRecyclerView.setItemAnimator(null);  //this should be done for making not null the partial object payload to update partial row
        RecyclerView.ItemAnimator animator = mStatisticsRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        mAdapter = new StatisticsAdapter(listUrlDataGeneralStatistics, mStatisticsContext);

        mStatisticsRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    private List<UrlDataGeneralStatistics> initDataset() {

        listUrlDataGeneralStatistics.add(new UrlDataGeneralStatistics("Requests No:  ", ""));
        listUrlDataGeneralStatistics.add(new UrlDataGeneralStatistics("Request Sent Time:  ", ""));

        return listUrlDataGeneralStatistics;


    }

}