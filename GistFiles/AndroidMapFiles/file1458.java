package com.tehmou.book.chapter7coffeebreak;

import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Observable<String> titleObservable =
                RxTextView
                        .textChanges((TextView) findViewById(R.id.title_edit_text))
                        .map(Object::toString);

        final Observable<String> messageObservable =
                RxTextView
                        .textChanges((TextView) findViewById(R.id.message_edit_text))
                        .map(Object::toString);

        final Observable<Void> clickEvents =
                RxView.clicks(findViewById(R.id.action_button));

        final Observable<Pair<String, String>> dialogContentsObservable =
                Observable.combineLatest(titleObservable, messageObservable, Pair::new);


        final Observable<Pair<String, String>> showDialogEventObservable =
                clickEvents.withLatestFrom(dialogContentsObservable,
                        (ignore, dialogContents) -> dialogContents);

        showDialogEventObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dialogContents ->
                        new AlertDialog.Builder(this)
                                .setTitle(dialogContents.first)
                                .setMessage(dialogContents.second)
                                .show()
                );

    }
}
