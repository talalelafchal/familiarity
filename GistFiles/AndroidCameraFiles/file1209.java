package com.example.summer.newapp;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.summer.newapp.PskovClass.pskovObl;


public class PskovFragment extends Fragment {


    public PskovFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView pskovRecycler = (RecyclerView)inflater.inflate(R.layout.fragment_oblasty,
                container, false);

        String[] oblastyName = new String[pskovObl.length];
        for (int i = 0; i < oblastyName.length; i++){

            oblastyName[i] = pskovObl[i].getName();
        }

       final String[] geo = new String[pskovObl.length];
        for (int i = 0; i < geo.length; i++) {

            oblastyName[i] = pskovObl[i].getName();
        }


        int[]moscowImage = new int[pskovObl.length];
        for (int i = 0; i < moscowImage.length; i++) {
            moscowImage[i] = pskovObl[i].getImageResourceId();
        }




        RecyclerAdapter adapter = new RecyclerAdapter(oblastyName, geo, moscowImage);
        pskovRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        pskovRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new RecyclerAdapter.Listener() {
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra("name", pskovObl[position].getName());

                //  intent.putExtra(OblastyMoscowActivity.EXTRA_OBLASTYMO, position);
                  getActivity().startActivity(intent);
            }
        });
        return  pskovRecycler;
    }
}
