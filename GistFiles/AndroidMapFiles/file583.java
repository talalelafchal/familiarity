import android.databinding.ObservableShort;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Alegria
 */
public class RxShort extends ObservableShort implements RxValue<Short> {

    private BehaviorSubject<Short> valueSubject = BehaviorSubject.create();

    public RxShort(short value) {
        super(value);
    }

    public RxShort() {
        super();
    }

    @Override
    public void set(short value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Short> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Short value) {
        valueSubject.onNext(value);
    }
}
