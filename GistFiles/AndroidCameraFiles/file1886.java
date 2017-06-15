package cardexc.com.practicework;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewFragment extends Fragment {

    MyListAdapter adapter;
    ArrayList<Place> places = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.view_layout, container, false);

        ListView mainListView = (ListView) view.findViewById(R.id.mainListView);

        adapter = new MyListAdapter(getActivity().getApplicationContext(), places);
        mainListView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.action_add): {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddFragment())
                        .addToBackStack(null)
                        .commit();

                return true;
            }
        }
        return true;

    }

    public void addPlace(Intent intent) {

        String place = intent.getStringExtra("place");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");

        byte[] byteArray = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        places.add(new Place(place, date + "; " + time, image));

        adapter.notifyDataSetChanged();

    }


}


