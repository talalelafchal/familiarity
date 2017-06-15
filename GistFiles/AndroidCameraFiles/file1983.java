package jinsheng.com.criminalintent;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CrimeListFragment extends ListFragment {
   private ArrayList<Crime> mCrimes;
   private static final String TAG ="CrimeListFragment";
   @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       getActivity().setTitle(R.string.crimes_title);
       mCrimes=CrimeLab.get(getActivity()).getCrimes();

       CrimeAdapter adapter=new CrimeAdapter(mCrimes);
       setListAdapter(adapter);
   }
    @Override
    public void onListItemClick(ListView l,View v,int position,long id){
        Crime c=(Crime)((CrimeAdapter)getListAdapter()).getItem(position);
        Log.d(TAG,c.getTitle()+"was clicked");
    }
    private class CrimeAdapter extends ArrayAdapter<Crime>{
        public CrimeAdapter(ArrayList<Crime> crimes){
            super(getActivity(),0,crimes);
        }
        @Override
        public View getView(int position,View convertView,ViewGroup parent){
            //if we weren't given a view,inflate one
            if(convertView ==null){
                convertView=getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime,null);
            }

            //configure the view for this Crime
            Crime c = getItem(position);

            TextView titleTextView=
                    (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.getTitle());
            TextView dateTextView=
                    (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());
            CheckBox solvedCheckBox=
                    (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}
