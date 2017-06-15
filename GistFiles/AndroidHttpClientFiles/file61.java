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
import edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter.WhatsGoingOnAdapter;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralWhatsGoingOn;

public class WhatsGoingOnFragment extends Fragment {

    private static final String TAG = "WhatsGoingOnFragment";
    public static Context mWhatsGoingOnContext;
    public static WhatsGoingOnAdapter mAdapter;
    protected RecyclerView mWhatsGoingOnRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<UrlDataGeneralWhatsGoingOn> listUrlDataGeneralWhatsGoingOn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listUrlDataGeneralWhatsGoingOn = new ArrayList<>();
        listUrlDataGeneralWhatsGoingOn = initDataset(); //returns List<UrlData>
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag3_whatsgoingon, container, false); //recycler_view_frag1 is a fragment
        rootView.setTag(TAG);

        mWhatsGoingOnContext = getActivity().getApplicationContext();

        mWhatsGoingOnRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView3FragmentWhatsgoingon);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);


        mWhatsGoingOnRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new WhatsGoingOnAdapter(listUrlDataGeneralWhatsGoingOn, mWhatsGoingOnContext);

        mWhatsGoingOnRecyclerView.setAdapter(mAdapter);

        mWhatsGoingOnRecyclerView.setItemAnimator(null);  //this should be done for making not null the partial object payload to update partial row


        RecyclerView.ItemAnimator animator = mWhatsGoingOnRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    private List<UrlDataGeneralWhatsGoingOn> initDataset() {

        listUrlDataGeneralWhatsGoingOn.add(new UrlDataGeneralWhatsGoingOn("Last-Modified:  ", ""));
        listUrlDataGeneralWhatsGoingOn.add(new UrlDataGeneralWhatsGoingOn("Unique Last-Modified:  ", ""));
        listUrlDataGeneralWhatsGoingOn.add(new UrlDataGeneralWhatsGoingOn("Modified Interval:  ", ""));
        listUrlDataGeneralWhatsGoingOn.add(new UrlDataGeneralWhatsGoingOn("Largest Modified Interval:  ", ""));
        listUrlDataGeneralWhatsGoingOn.add(new UrlDataGeneralWhatsGoingOn("Least Modified Interval:  ", ""));

        return listUrlDataGeneralWhatsGoingOn;


    }
}
