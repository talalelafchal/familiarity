package com.example.ClientApplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;

public class TabsAdapter extends FragmentPagerAdapter
    implements TabHost.OnTabChangeListener,
        ViewPager.OnPageChangeListener
{


    private final Context mContext;
    private final TabHost mTabHost;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private static TabsAdapter mInstance;

    public static TabsAdapter getInstance(FragmentActivity fragmentActivity,TabHost tabHost,ViewPager pager){
        if(mInstance == null){
            mInstance = new TabsAdapter( fragmentActivity,tabHost,pager );
        }
        return mInstance;
    }


    /**
     * コンストラクタ
     * @param fragmentActivity
     * @param tabHost
     * @param pager
     */
    private TabsAdapter(FragmentActivity fragmentActivity,TabHost tabHost,ViewPager pager) {
        super( fragmentActivity.getSupportFragmentManager() );
        mContext = fragmentActivity;
        mTabHost = tabHost;
        mViewPager = pager;

        //Tabが切り替わった時のListenerをセット
        mTabHost.setOnTabChangedListener( this );
        mViewPager.setAdapter( this );
        mViewPager.setOnPageChangeListener( this );

    }

    public void addTab(TabHost.TabSpec tabSpec,Class<?> cls,Bundle arg){
        tabSpec.setContent( new DummyTabFactory( mContext ) );

        String tag = tabSpec.getTag();

        //タブinfo生成
        TabInfo info =  new TabInfo( tag,cls,arg );
        //タブリストに追加
        mTabs.add( info );
        //タブを追加
        mTabHost.addTab( tabSpec );
        //データセットの変化を通知
        notifyDataSetChanged();
    }


    @Override
    public void onTabChanged( String tabId ) {
        //タブが切り替わった時に呼び出される
        //現在のタブ位置を取得
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem( position );
    }

    @Override
    public void onPageSelected( int position ) {
        //ViewPager のViewがスワイプされた際に呼ばれる
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();

        widget.setDescendantFocusability( ViewGroup.FOCUS_BLOCK_DESCENDANTS );

        // Switching tab
        mTabHost.setCurrentTab( position );
        widget.setDescendantFocusability( oldFocusability );
    }

    @Override
    public Fragment getItem( int position ) {
        TabInfo info = mTabs.get( position );
        return Fragment.instantiate( mContext,info.cls.getName(),info.args );
    }

    @Override
    public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
    }


    @Override
    public void onPageScrollStateChanged( int state ) {
    }

    @Override
    public int getCount() {
    //    return mTabs.size();
        return 0;
    }

    /**
     * タブの情報（タグ、クラス名、引数）を保持するクラス
     */
    final static class TabInfo{
        public final String tag;
        public final Class<?> cls;
        public final Bundle args;

        TabInfo( String tag, Class<?> cls, Bundle args ) {
            this.tag = tag;
            this.cls = cls;
            this.args = args;
        }
    }
    final static class DummyTabFactory
            implements TabHost.TabContentFactory{
        private final Context mContext;

        DummyTabFactory( Context mContext ) {
            this.mContext = mContext;
        }

        @Override
        public View createTabContent( String tag ) {
            View v = new View(mContext);
            v.setMinimumWidth( 0 );
            v.setMinimumHeight( 0 );
            return v;
        }
    }

}
