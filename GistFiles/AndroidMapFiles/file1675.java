import android.databinding.ObservableDouble;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Vehicle Lunar
 */
public class RxDouble extends ObservableDouble implements RxValue<Double> {

    private BehaviorSubject<Double> valueSubject = BehaviorSubject.create();

    public RxDouble(double value) {
        super(value);
    }

    public RxDouble() {
        super();
    }

    @Override
    public void set(double value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Double> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Double value) {
        valueSubject.onNext(value);
    }
}
