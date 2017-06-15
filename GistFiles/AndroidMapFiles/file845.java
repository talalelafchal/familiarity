import android.databinding.ObservableFloat;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Vehicle Lunar
 */
public class RxFloat extends ObservableFloat implements RxValue<Float> {

    private BehaviorSubject<Float> valueSubject = BehaviorSubject.create();

    public RxFloat(float value) {
        super(value);
    }

    public RxFloat() {
        super();
    }

    @Override
    public void set(float value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Float> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Float value) {
        valueSubject.onNext(value);
    }
}
