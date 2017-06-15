package com.andraskindler.sandbox.activity;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ApiManager {

    private interface ApiManagerService {
        @GET("/weather")
        WeatherData getWeather(@Query("q") String place, @Query("units") String units);
    }

    private static final RestAdapter restAdapter = new RestAdapter.Builder()
        .setEndpoint("http://api.openweathermap.org/data/2.5")
        .build();

    private static final ApiManagerService apiManager = restAdapter.create(ApiManagerService.class);

    public static Observable<WeatherData> getWeatherData(final String city) {
        return Observable.create(new Observable.OnSubscribe<WeatherData>() {
            @Override
            public void call(Subscriber<? super WeatherData> subscriber) {
                if (!subscriber.isUnsubscribed())
                    try {
                        subscriber.onNext(apiManager.getWeather(city, "metric"));
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
            }
        }).subscribeOn(Schedulers.io());
    }

}
