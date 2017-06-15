/**
 * Created by marius on 1/9/15.
 *
 * @music Beware of safety - Kaura
 */
public class RxValuesSampleViewModel {

    public RxBoolean enabled = new RxBoolean();

    public void toggle() {
        enabled.set(!enabled.get());
    }

    [...]
}


public class RxValuesSampleView {

    private final RxValuesSampleViewModel viewModel = new RxValuesSampleViewModel();

    [...]

    public void bind() {
        viewModel.enabled.valueObservable().
                subscribe(button::setEnabled);
    }
}
