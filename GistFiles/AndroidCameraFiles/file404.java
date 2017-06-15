package com.ctrlsmart.fpcx;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ctrlsmart.bean.EditListInfo;
import com.ctrlsmart.excuter.MyAsyncTask;

import java.util.List;


public class MainActivity extends Activity implements ResultListFragment.OnListCallback ,MyAsyncTask.ReturnStringCallBack {
    Button hIBtn,sIBtn,cxBtn;
    View footerView,headerView;
    public EditListFragment lf;
    public ResultListFragment rf;
    public EditListFragment getEditListFragmen(){
        return lf;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null){
            lf = new EditListFragment();
            getFragmentManager().beginTransaction().add(R.id.groupView, lf).commit();

        }
        initView();
    }

    private void initView(){
        footerView = (View)findViewById(R.id.footer_view);
        headerView = (View)findViewById(R.id.header_view);
        hIBtn = (Button)findViewById(R.id.hI_btn);
        hIBtn.setOnClickListener(click);
        sIBtn = (Button)findViewById(R.id.sI_btn);
        sIBtn.setOnClickListener(click);
        cxBtn = (Button)findViewById(R.id.cx_btn);
        cxBtn.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
                switch (v.getId()){
                    case R.id.hI_btn:
                        lf.addItemView();
                        break;
                    case R.id.sI_btn:
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.cx_btn:
                        if(getFragmentManager().getBackStackEntryCount()==0) {
                            lf.loadListData();
                            replaceFragment();
                            cxBtn.setText(getString(R.string.returnBack));
                        }else{
                            getFragmentManager().popBackStack();
                            cxBtn.setText(getString(R.string.select));
                        }
                        break;
                }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<EditListInfo> getList() {
        if(lf == null)
            return null;
        return lf.getList();
    }

//    @Override
//    public void changeFragment(EditListInfo info) {
//        if(rf == null) {
//            rf = new ResultListFragment();
//            getFragmentManager().beginTransaction().replace(R.id.groupView, rf).commit();
//        }
//        Bundle args = new Bundle();
//        args.putSerializable("info", info);
//        rf.setArguments(args);
//
//    }

    /**
     *
     *
     *
     *
     * */
    private void replaceFragment(){
        if(rf == null) {
            rf = new ResultListFragment();

        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.groupView, rf);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void changeFragment() {
        rf.notifyListChange();
    }
}
