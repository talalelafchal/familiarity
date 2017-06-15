import android.databinding.ObservableChar;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Vehicle Lunar
 */
public class RxChar extends ObservableChar implements RxValue<Character> {

    private BehaviorSubject<Character> valueSubject = BehaviorSubject.create();

    public RxChar(char value) {
        super(value);
    }

    public RxChar() {
        super();
    }

    @Override
    public void set(char value) {
        super.set(value);
        valueSubject.onNext(value);
    }

    @Override
    public Observable<Character> valueObservable() {
        return valueSubject.asObservable();
    }

    @Override
    public void valueChanged(Character value) {
        valueSubject.onNext(value);
    }
}
