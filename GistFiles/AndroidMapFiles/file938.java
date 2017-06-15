import android.databinding.ObservableBoolean;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Me sobren paraules
 */
public class RxBoolean extends ObservableBoolean implements RxValue<Boolean> {

    private BehaviorSubject<Boolean> valueSubject = BehaviorSubject.create();

    public RxBoolean(boolean value) {
        super(value);
    }

    public RxBoolean() {
        super();
    }

    @Override
    public void set(boolean value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Boolean> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Boolean value) {
        valueSubject.onNext(value);
    }
}
