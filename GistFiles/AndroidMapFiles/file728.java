package com.example.summer.newapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import goldzweigapps.tabs.Builder.EasyTabsBuilder;
import goldzweigapps.tabs.Items.TabItem;
import goldzweigapps.tabs.View.EasyTabs;

public class GameActivity extends AppCompatActivity {

    EasyTabs easyTabs;

    NavFragment navFragment;
    InfoFragment infoFragment;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initTabs();
        setTitle(getIntent().getStringExtra("name"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navFragment = new NavFragment();
        infoFragment = new InfoFragment();


    }

    private void initTabs() {

        easyTabs = (EasyTabs) findViewById(R.id.easy_tabs);
        EasyTabsBuilder.with(easyTabs)
                .addTabs(new TabItem(new NavFragment(), "Навигация"),
                new TabItem(new InfoFragment(), "Информация"))

                .Build();

    }

    }


