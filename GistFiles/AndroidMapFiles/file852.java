import android.databinding.ObservableLong;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Vehicle Lunar
 */
public class RxLong extends ObservableLong implements RxValue<Long> {

    private BehaviorSubject<Long> valueSubject = BehaviorSubject.create();

    public RxLong(long value) {
        super(value);
    }

    public RxLong() {
        super();
    }

    @Override
    public void set(long value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Long> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Long value) {
        valueSubject.onNext(value);
    }
}
