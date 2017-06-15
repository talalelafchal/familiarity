package com.example.costs;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends TabActivity {
    TabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        tabHost = getTabHost();
////		Расходы
//        TabHost.TabSpec outcomeTab = tabHost.newTabSpec("outcomeTab");
//        outcomeTab.setIndicator(getString(R.string.outcome));
//        Intent outcomeIntent = new Intent(this,OutcomeActivity.class);
//        outcomeTab.setContent(outcomeIntent);
////		Доходы
//        TabHost.TabSpec incomeTab = tabHost.newTabSpec("incomeTab");
//        incomeTab.setIndicator(getString(R.string.income));
//        Intent incomeIntent = new Intent(this,IncomeActivity.class);
//        incomeTab.setContent(incomeIntent);
//
//        tabHost.addTab(outcomeTab);
//        tabHost.addTab(incomeTab);
//        tabHost.setOnTabChangedListener(new AnimatedTabHostListener(tabHost));

        List<View> pages = new ArrayList<View>();
        LayoutInflater inflater = LayoutInflater.from(this);
        View page = inflater.inflate(R.layout.tab_outcome_layout, null);
        pages.add(page);
        page = inflater.inflate(R.layout.tab_income_layout, null);
        pages.add(page);
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        setContentView(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
