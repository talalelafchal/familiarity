package com.ctrlsmart.fpcx;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ctrlsmart.adapter.ResultListAdapter;
import com.ctrlsmart.bean.EditListInfo;

import java.util.List;

/**
 * Created by Administrator on 2015/4/1.
 */
public class ResultListFragment extends ListFragment {
    ResultListAdapter rltAdapter;
    OnListCallback cb;
    //List<EditListInfo> rList;
    EditListInfo rInfo;
    public interface OnListCallback {
        public List getList();
    }

    public void notifyListChange(){
        rltAdapter.notifyDataSetChanged();
    }
    public ResultListFragment(){

    }
//    public ResultListFragment(EditListInfo info){
//        rInfo = info;
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            cb = (OnListCallback) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rltAdapter = new ResultListAdapter(getActivity(),cb.getList());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.resultlist_layout,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(rltAdapter);
    }
}
