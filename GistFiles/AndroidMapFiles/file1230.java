package com.eatfirst.android.utils;

import android.support.design.widget.TextInputLayout;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Func1;

/**
 * Created by marcel on 31/07/15.
 */
public class RxViewUtils {

  public static Observable<Boolean> verifyInput(TextInputLayout input,
      Func1<? super String, Boolean> action) {
    return WidgetObservable.text(input.getEditText(), true).compose(apply(action));
  }

  public static Func1<String, Boolean> checkPhone() {
    // TODO implement
    return s -> s.length() > 4;
  }

  public static Func1<String, Boolean> checkText(int minLength) {
    return s -> s.length() > minLength;
  }

  public static Func1<String, Boolean> checkEmail() {
    return s -> s.length() > 4 && s.contains("@") && s.contains(".");
  }

  private static Observable.Transformer<? super OnTextChangeEvent, Boolean> apply(
      Func1<? super String, Boolean> action) {
    return observable -> observable.debounce(600, TimeUnit.MILLISECONDS)
        .map(event -> event.text().toString())
        .map(action)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(AndroidSchedulers.mainThread());
  }
}