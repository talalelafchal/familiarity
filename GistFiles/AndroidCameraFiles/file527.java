package sagarpreet97.reminder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;




public  class CardFrontFragment extends android.app.Fragment {

    frontListener mListener ;
    View v ;
    public  interface frontListener
    {
        void getTitle(String title) ;
    }

    public void setFragmentListener(frontListener listener) {
        mListener = listener;
    }

    public CardFrontFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v= inflater.inflate(R.layout.fragment_card_front, container, false);
//          EditText title=(EditText)v.findViewById(R.id.editText3) ;
//                 String mt=title.getText().toString() ;
//        mListener.getTitle(mt);

        return v ;
    }
}