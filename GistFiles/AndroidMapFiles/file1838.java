import rx.Observable;

/**
 * Created by marius on 31/8/15.
 *
 * @music Antonia Font - Alegria
 */
public interface RxValue<T> {

    Observable<T> valueObservable();

    void valueChanged(T value);
}
