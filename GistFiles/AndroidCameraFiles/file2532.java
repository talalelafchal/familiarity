package com.paad.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.paad.EventInterface.OnNewItemAddedListener;
import com.paad.todolist.todolist.R;

/**
 * Created by WangWei on 2014/12/9.
 */
public class NewItemFragment extends Fragment {

    private OnNewItemAddedListener onNewItemAddedListener;
    private EditText myEditText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.new_item_fragment,container,false);

        myEditText = (EditText) view.findViewById(R.id.myEditText);
        myEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    if((keyCode == KeyEvent.KEYCODE_ENTER)||(keyCode == KeyEvent.KEYCODE_DPAD_CENTER)){

                        String newItem = myEditText.getText().toString();
                        onNewItemAddedListener.onNewItemAdded(newItem);
                        myEditText.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onNewItemAddedListener = (OnNewItemAddedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"Must implement OnNewItemAddListener");
        }
    }
}
