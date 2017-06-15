package com.example.summer.newapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.summer.newapp.MosOblClass.mosObl;


public class MosOblFragment extends Fragment {

    public static final int IDM_A = 101;
    public static final int IDM_B = 102;

    RecyclerAdapter.Listener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // View view = inflater.inflate(R.layout.fragment_oblasty,null);
        final RecyclerView moscowRecycler = (RecyclerView)inflater.inflate(R.layout.fragment_oblasty,
                container, false);


        final String[] oblastyName = new String[mosObl.length];
       for (int i = 0; i < oblastyName.length; i++){
            oblastyName[i] = mosObl[i].getName();
        }

        final String[] geo = new String[mosObl.length];
        for (int i = 0; i < geo.length; i++){
            geo[i] = mosObl[i].getGeo();
        }




        int[]moscowImage = new int[mosObl.length];
        for (int i = 0; i < moscowImage.length; i++) {
            moscowImage[i] = mosObl[i].getImageResourceId();
        }


        RecyclerAdapter adapter = new RecyclerAdapter(oblastyName,geo, moscowImage);
        moscowRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        moscowRecycler.setLayoutManager(layoutManager);

        adapter.setListener(new RecyclerAdapter.Listener() {
            public void onClick(int position) {


                Intent mapintent = new Intent(getActivity(),GameActivity.class);
                mapintent.putExtra("geo:",mosObl[position].getGeo());
                mapintent.putExtra("name", mosObl[position].getName());
                mapintent.putExtra("latitube",geo);
              //  mapintent.putExtra(Intent.EXTRA_TEXT, "Text");
                //mapintent.putExtra(OblastyMoscowActivity.EXTRA_OBLASTYMO, position);
                getActivity().startActivity(mapintent);


                /*Intent intent = new Intent(getActivity(), ScrollingActivity.class);

                getActivity().startActivity(intent);
                */

            }
        });
        return  moscowRecycler;


        
        
    }

}
