package com.andraskindler.sandbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.andraskindler.sandbox.R;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private WeatherListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Subscription subscription;
    private final String[] cities = new String[]{"Budapest", "Santiago", "Barcelona", "Madrid", "London", "Moscow", "Berlin", "New York", "San Francisco", "Dublin"};

    private Observable<WeatherData> observable = Observable.from(cities)
        .flatMap(new Func1<String, Observable<WeatherData>>() {
            @Override
            public Observable<WeatherData> call(String s) {
                return ApiManager.getWeatherData(s);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    private final Observer<WeatherData> observer = new Observer<WeatherData>() {
        @Override
        public void onCompleted() {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(WeatherData weatherData) {
            adapter.update(weatherData);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_main);
        swipeRefreshLayout.setColorScheme(R.color.blue, R.color.green, R.color.red, R.color.yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!subscription.isUnsubscribed())
                    subscription.unsubscribe();
                adapter.clear();
                subscription = observable.subscribe(observer);
            }
        });

        adapter = new WeatherListAdapter();
        ((ListView) findViewById(R.id.lv_main)).setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        subscription = observable.subscribe(observer);

    }

}