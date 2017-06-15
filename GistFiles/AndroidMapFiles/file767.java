public abstract class M360Fragment extends android.support.v4.app.Fragment {

    public Realm realm;
    protected ActivityPresenter activityPresenter;

    public ActivityPresenter getActivityPresenter() {
        return activityPresenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();

        if (presenter != null) {
            presenter.onStart();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    //-- PRESENTATION -

    private FragmentPresenter presenter;

    protected void setPresenter(FragmentPresenter presenter) {
        this.presenter = presenter;

    }

    public FragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (presenter != null) {
            presenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof M360Activity) {
            M360Activity m360Activity = (M360Activity) context;
            activityPresenter = m360Activity.getPresenter();
        }
    }

}