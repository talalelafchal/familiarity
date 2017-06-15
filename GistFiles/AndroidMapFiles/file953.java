package com.marwinxxii.rxretain;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
  private TaggedObservableCache cache;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cache = (TaggedObservableCache) getLastCustomNonConfigurationInstance();
    if (cache == null) {
      cache = new TaggedObservableCache();
    }
    final String cacheKey = "RxRetain";
    cache.withRetained(cacheKey, Observable.defer(new Func0<Observable<List<String>>>() {
      @Override
      public Observable<List<String>> call() {
        return networkObservable();
      }
    }))
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Action1<List<String>>() {
        @Override
        public void call(List<String> strings) {
          Toast.makeText(MainActivity.this, strings.get(0), Toast.LENGTH_SHORT).show();
        }
      });
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return cache;
  }

  public static Observable<List<String>> networkObservable() {
    return Observable.timer(10, TimeUnit.SECONDS)
      .map(new Func1<Long, List<String>>() {
        @Override
        public List<String> call(Long aLong) {
          return Arrays.asList("Hello", "World", "Hello Rx");
        }
      });
  }

  public static class TaggedObservableCache {
    private final HashMap<String, Observable> map = new HashMap<>();

    public <T> Observable<T> withRetained(String key, Observable<T> target) {
      if (map.containsKey(key)) {
        return map.get(key);
      }
      Observable<T> cached = target.replay().autoConnect();
      map.put(key, cached);
      return cached;
    }
  }
}
