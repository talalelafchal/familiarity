package com.ztt.criminalintent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import model.Crime;
import model.CrimeLab;

/**
 * Created by 123 on 14-11-9.
 */
public class CrimePagerActivity extends FragmentActivity {
    private static final String TAG="CrimePagerActivity";
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager =new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes= CrimeLab.get(this).getCrimes();

        FragmentManager fm=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                Crime crime=mCrimes.get(i);
                Log.v(TAG," public Fragment getItem(int i)");
                Fragment f=CrimeFragment.newInstance(crime.getId());
                return f;
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        /**
        *FragmentStatePagerAdapter会将以前的页面删掉防止oom     FragmentPagerAdapter会保留
         * *
         *
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                Crime crime=mCrimes.get(i);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });*/
        UUID crimeId=(UUID)getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for(int i=0;i<mCrimes.size();i++)
        {
            if(mCrimes.get(i).getId().equals(crimeId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                Crime crime=mCrimes.get(i);
                if(crime.getTitle()!=null)
                {
                    setTitle(crime.getTitle());
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                 switch (i)
                 {
                     case ViewPager.SCROLL_STATE_IDLE:
                         Toast.makeText(CrimePagerActivity.this,"idle",Toast.LENGTH_SHORT).show();break;
                     case ViewPager.SCROLL_STATE_DRAGGING:
                         Toast.makeText(CrimePagerActivity.this,"dragging",Toast.LENGTH_SHORT).show();break;
                     case ViewPager.SCROLL_STATE_SETTLING:
                         Toast.makeText(CrimePagerActivity.this,"SETTING",Toast.LENGTH_SHORT).show();break;

                 }
            }
        });
    }
}
