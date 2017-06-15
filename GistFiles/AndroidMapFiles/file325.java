package vn.tpf.andping;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

import java.util.HashMap;

public class AndPingFragmentActivity extends FragmentActivity implements TabHost.OnTabChangeListener{

    private TabHost mTabHost;
    private HashMap<String,TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
    private TabInfo mLastTab = null;

    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle agrs;
        private Fragment fragment;

        TabInfo (String tag,Class<?> clss, Bundle agrs){
            this.tag = tag;
            this.clss = clss;
            this.agrs = agrs;
        }
    }


    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        TabFactory(Context mContext) {
            this.mContext = mContext;
        }

        public View createTabContent (String tag){

            View v = new View(mContext);
            v.setMinimumHeight(0);
            v.setMinimumWidth(0);
            return v;
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        initTabhost (savedInstanceState);

        if(savedInstanceState != null){
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
    }


    protected void onSaveInstanceState(Bundle outState){
        outState.putString("tab",mTabHost.getCurrentTabTag()); //Save current select tab
        super.onSaveInstanceState(outState);
    }

    private void initTabhost(Bundle args) {
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        AndPingFragmentActivity.addTab(this,this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Ping"),(tabInfo = new TabInfo("Tab1",TabPing.class,args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
       AndPingFragmentActivity.addTab(this,this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Lookup"), (tabInfo = new TabInfo("Tab2",TabLookup.class,args)));
        this.mapTabInfo.put(tabInfo.tag,tabInfo);


        //Default to 1st tab
        onTabChanged("Tab1");
        mTabHost.setOnTabChangedListener(this);


    }



    private static void addTab(AndPingFragmentActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo){
        tabSpec.setContent(activity.new TabFactory(activity));
        String tag = tabSpec.getTag();
        tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()){
            FragmentTransaction mFT = activity.getSupportFragmentManager().beginTransaction();
            mFT.detach(tabInfo.fragment);
            mFT.commit();
            activity.getSupportFragmentManager().executePendingTransactions();
        }

        tabHost.addTab(tabSpec);

    }

    public void onTabChanged(String tag){
        TabInfo newTab = this.mapTabInfo.get(tag);
        if(mLastTab != newTab){
            FragmentTransaction mFT = this.getSupportFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if(mLastTab.fragment != null){
                    mFT.detach(mLastTab.fragment);
                }
            }

            if (newTab!=null){
                if (newTab.fragment == null){
                    newTab.fragment = Fragment.instantiate(this,newTab.clss.getName(), newTab.agrs);
                    mFT.add(R.id.realtabcontent,newTab.fragment, newTab.tag);
                }
                else {
                    mFT.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            mFT.commit();
            this.getSupportFragmentManager().executePendingTransactions();
        }

    }
}
