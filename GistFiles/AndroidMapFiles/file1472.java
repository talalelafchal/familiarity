public abstract class FragmentPresenter extends AbstractPresenter {

    protected ActivityPresenter activityPresenter;

    protected FragmentPresenter(M360Fragment fragment) {
        fragment.setPresenter(this);
        activityPresenter = fragment.getActivityPresenter();
        activityPresenter.registerFragmentPresenter(this);
    }
}