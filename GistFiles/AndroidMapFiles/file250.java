import android.databinding.ObservableField;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Me sobren paraules
 */
public class RxField<T> extends ObservableField<T> implements RxValue<T> {

    private BehaviorSubject<T> valueSubject = BehaviorSubject.create();

    public RxField(T value) {
        super(value);
    }

    public RxField() {
        super();
    }

    @Override
    public void set(T value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<T> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(T value) {
        valueSubject.onNext(value);
    }
}
