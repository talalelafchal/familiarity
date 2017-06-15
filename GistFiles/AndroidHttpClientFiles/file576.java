package edu.ualr.cpsc7398.updatechecker.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.ConclusionAdapter;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataConclusion;

public class ConclusionFragment extends Fragment {

    private static final String TAG = "ConclusionFragment";
    public static Context mConclusionContext;
    public static ConclusionAdapter mAdapter;
    protected RecyclerView mConclusionRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<UrlDataConclusion> listUrlDataConclusion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listUrlDataConclusion = new ArrayList<>();
        listUrlDataConclusion = initDataset(); //returns List<UrlData>
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag_conclusion, container, false); //recycler_view_frag1 is a fragment
        rootView.setTag(TAG);

        mConclusionContext = getActivity().getApplicationContext();

        mConclusionRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView4);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);


        mConclusionRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ConclusionAdapter(listUrlDataConclusion, mConclusionContext);

        mConclusionRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    private List<UrlDataConclusion> initDataset() {

        listUrlDataConclusion.add(new UrlDataConclusion("This is the conclusion we got from above process."));

        return listUrlDataConclusion;


    }

}