class CameraPresenter implements CameraContract.Presenter {

    private CameraContract.View mView;
    private final ProductRepository mRepository;
    private Subscription mSubscription = Subscriptions.empty();

    CameraPresenter(@NonNull CameraContract.View view, @NonNull ProductRepository repository) {
        mView = view;
        mRepository = repository;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
        mSubscription = null;
    }

    @Override
    public void searchProducts(@Nullable String code) {
        // TODO: 7/8/16 check NPE here
        unsubscribe();
        mView.stopCamera();
        mView.showProgress(true);

        mSubscription = mRepository
                .searchProducts(code)
                .first(products -> products != null && !products.isEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(products -> {
                            if (products.isEmpty()) {
                                mView.onEmptyResponse();
                            } else {
                                mView.onSearchSuccess(products);
                            }
                        },
                        e -> {
                            mView.showProgress(false);
                            if (e instanceof IOException) {
                                mView.showNetworkError();
                            } else {
                                mView.showInAppError();
                            }
                        }, () -> mView.showProgress(false));
    }
}