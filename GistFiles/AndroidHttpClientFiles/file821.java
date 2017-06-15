package com.example.ClientApplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TabHost;

public class MyActivity extends FragmentActivity
{
    TabHost mTabHost;
    ViewPager mViewPager;
    TabsAdapter mTabsAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tab_host );

        mTabHost = (TabHost)findViewById( android.R.id.tabhost );
        mTabHost.setup();

        // get ViewPager
        mViewPager = ( ViewPager )findViewById( R.id.pager );

        //TabHostとViewPagerを渡してAdapterを生成
        mTabsAdapter = TabsAdapter.getInstance( this,mTabHost,mViewPager );

        // add fragment
        mTabsAdapter.addTab(
            mTabHost.newTabSpec( "location" ).setIndicator( "location" ),
                FirstFragment.class,null);

        mTabsAdapter.addTab(
            mTabHost.newTabSpec( "time" ).setIndicator( "time" ),
                SecondFragment.class,null);

        if(savedInstanceState != null){
            mTabHost.setCurrentTabByTag( savedInstanceState.getString( "tab" ) );
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );

        outState.putString( "tab",mTabHost.getCurrentTabTag() );
    }
}
