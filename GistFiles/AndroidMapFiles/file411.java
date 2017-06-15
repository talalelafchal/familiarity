import android.databinding.ObservableInt;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Me sobren paraules
 */
public class RxInt extends ObservableInt implements RxValue<Integer> {

    private BehaviorSubject<Integer> valueSubject = BehaviorSubject.create();

    public RxInt(int value) {
        super(value);
    }

    public RxInt() {
        super();
    }

    @Override
    public void set(int value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Integer> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Integer value) {
        valueSubject.onNext(value);
    }
}
