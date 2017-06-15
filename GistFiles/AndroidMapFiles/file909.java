package com.example.summer.newapp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OblastyFragment extends Fragment {
    MosOblFragment mosOblFragment;
    OblastyFragment oblastyFragment;
    PskovFragment PskovFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final RecyclerView oblastyRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_oblasty,
                container, false);

        //View view = inflater.inflate(R.layout.fragment_oblasty,container,false);

        mosOblFragment = new MosOblFragment();
        PskovFragment = new PskovFragment();
        oblastyFragment = new OblastyFragment();







        final String[] oblastyNames = new String[RussianState.city.length];
        for (int i = 0; i < RussianState.city.length; i++) {

            oblastyNames[i] = RussianState.city[i].getName();
        }


        final String[] geo = new String[RussianState.city.length];
        for (int i = 0; i < RussianState.city.length; i++) {

            oblastyNames[i] = RussianState.city[i].getName();

        }


        int[] moscowImages = new int[RussianState.city.length];
        for (int i = 0; i < RussianState.city.length; i++) {
            moscowImages[i] = RussianState.city[i].getImageResourceId();
        }


        RecyclerAdapter adapter = new RecyclerAdapter(oblastyNames, geo, moscowImages);
        oblastyRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        oblastyRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new RecyclerAdapter.Listener() {
            public void onClick(int position) {

                switch (position) {
                    case 0:
                        getFragmentManager(). beginTransaction()
                                .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_in_right)
                                .replace(R.id.content_main, new MosOblFragment())
                                .addToBackStack(null)
                                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                        break;

                    case 1:
                        getFragmentManager().beginTransaction()
                                .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_in_right)
                                .replace(R.id.content_main,new PskovFragment())
                                .addToBackStack(null)
                                //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit();
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;


                }

            }
        });

        return oblastyRecycler;

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}