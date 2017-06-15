public abstract class ActivityPresenter extends AbstractPresenter {

    protected ActivityPresenter(M360Activity activity) {
        activity.presenter = this;
    }

    private ArrayList<FragmentPresenter> presenters = new ArrayList<>();

    public void registerFragmentPresenter(FragmentPresenter presenter) {
        presenters.add(presenter);
    }
}