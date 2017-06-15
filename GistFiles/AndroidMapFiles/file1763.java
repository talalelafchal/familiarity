import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.exceptions.OnErrorNotImplementedException;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable disposables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button submitView = (Button) findViewById(R.id.submitButton);
        EditText nameView = (EditText) findViewById(R.id.editText);
        ProgressBar progressView = (ProgressBar) findViewById(R.id.progressBar);

        ObservableTransformer<SubmitAction, SubmitResult> submit =
                events -> events.flatMap(action -> service.setName(action.name)
                        .map(response -> SubmitResult.SUCCESS)
                        .onErrorReturn(t -> SubmitResult.failure(t.getMessage()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(SubmitResult.IN_FLIGHT));

        ObservableTransformer<CheckNameAction, CheckNameResult> checkName =
                actions -> actions.switchMap(action -> action
                        .delay(200, MILLISECONDS, AndroidSchedulers.mainThread())
                        .flatMap(action -> service.checkName(action.name)
                        .map(response -> CheckNameResult.SUCCESS)
                        .onErrorReturn(t -> CheckNameResult.failure(t.getMessage()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(CheckNameResult.IN_FLIGHT));

        ObservableTransformer<SubmitUiEvent, SubmitResult> submitUi = events -> null;

        disposables.add(RxView.clicks(submitView)
                .map(ignored -> new SubmitAction(nameView.getText().toString()))
                .compose(submit)
                .subscribe(model -> {
                    submitView.setEnabled(!model.inProgress);
                    progressView.setVisibility(model.inProgress ? View.VISIBLE : View.GONE);
                    if (!model.inProgress) {
                        if (model.sucess) finish();
                    else
                        Toast.makeText(this, "Failed to set name: " + model.errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }, t -> { throw new OnErrorNotImplementedException(t); }));
    }
}
