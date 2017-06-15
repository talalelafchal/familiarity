import android.databinding.ObservableByte;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Vehicle Lunar
 */
public class RxByte extends ObservableByte implements RxValue<Byte> {

    private BehaviorSubject<Byte> valueSubject = BehaviorSubject.create();

    public RxByte(byte value) {
        super(value);
    }

    public RxByte() {
        super();
    }

    @Override
    public void set(byte value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Byte> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Byte value) {
        valueSubject.onNext(value);
    }
}
