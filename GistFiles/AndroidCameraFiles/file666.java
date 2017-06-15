package com.ctrlsmart.fpcx;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ctrlsmart.adapter.EditlListAdapter;
import com.ctrlsmart.bean.EditListInfo;
import com.ctrlsmart.excuter.MyAsyncTask;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Administrator on 2015/3/28.
 */
public class EditListFragment extends ListFragment implements EditlListAdapter.DelAfterCallBack,EditlListAdapter.ShowToastCallBack{
    private EditlListAdapter elAdapter;
    private List<EditListInfo> list;
    private static ExecutorService allTaskExecutor = null;
    MyAsyncTask.ReturnStringCallBack activity;
    public List<EditListInfo> getList(){
       return elAdapter.getList();
    }
    public void loadListData(){

       list = getList();
//       for(int i =0;i<list.size();i++){
//           String str1 = list.get(i).getFpCode();
//           String str2 = list.get(i).getFpNum();
//           if(str1.length()!=12 && str2.length()!=8){
//               showToast("第 "+(i+1)+" 行输入错误");
//               break;
//           }
        for(EditListInfo info:list){
            MyAsyncTask localMyAsyncTask = new MyAsyncTask(getActivity(),info,this.activity);
            localMyAsyncTask.executeOnExecutor(allTaskExecutor);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = ( MyAsyncTask.ReturnStringCallBack)activity;
    }
    public void showToast(String s) {
        Toast toast = Toast.makeText(getActivity(),s,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 70);
        toast.show();
    }

    public void addItemView(){
        elAdapter.addItem();
        elAdapter.notifyDataSetChanged();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elAdapter = new EditlListAdapter(getActivity(),1);
        elAdapter.setCallBack(this,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.editlist_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(elAdapter);
        allTaskExecutor = Executors.newCachedThreadPool(new ThreadFactoryTest());
//        allTaskExecutor = Executors.newFixedThreadPool(3, new ThreadFactoryTest());
    }
    private static class ThreadFactoryTest implements ThreadFactory
    {

        @Override
        public Thread newThread(Runnable r)
        {
            Thread thread = new Thread(r);
            thread.setName("fpcx_ThreadFactory");
            thread.setDaemon(true); // 将用户线程变成守护线程,默认false
            return thread;
        }
    }


    @Override
    public void delItemAfter() {
        elAdapter.notifyDataSetChanged();
    }
}
