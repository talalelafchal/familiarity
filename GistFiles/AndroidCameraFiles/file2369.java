package sagarpreet97.reminder;

/**
 * Created by sagarpreet chadha on 26-07-2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A fragment representing the back of the card.
 */
public  class  CardBackFragment extends Fragment {

    View v ;
    backtListener mListener ;
    public  interface backtListener
    {
        void getDesc(String mtitle) ;
    }

    public void setBackFragmentListener(backtListener listener) {
        mListener = listener;
    }
    public CardBackFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_card_back, container, false);
//        EditText editText=(EditText)v.findViewById(R.id.editText4) ;
//        String temp=editText.getText().toString() ;
//        mListener.getDesc(temp);
        return v ;
    }
}