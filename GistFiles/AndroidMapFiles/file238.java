private ObservableManager obsManager = new ObservableManager(EventBus.getDefault());

@Override
public Subscription getStores() {
  // Get the observable if exists and is not too old
  Observable<StoresList> observable = obsManager.get(ObservableManager.Types.STORES);
  if (observable == null) {
    // If is null create it and us cache to keep it in memeroy
    observable = api.getStoresList()
        .compose(applySchedulers(api.getStoresList()))
        .cache();
    // Put it inside our Manager to access it again
    obsManager.put(ObservableManager.Types.STORES.getKey(), observable);
  }

  // Subscribe to it.
  return observable.subscribe(
      getDefaultSubscriber(StoresList.class, ObservableManager.Types.STORES.getKey()));
}
  
/**
 * Global operators to apply to all or most of the observables, return the observable with the
 * default operators applied.
 *
 * @param toBeResumed origin observable to retry in case of fail
 */
private <T> Observable.Transformer<T, T> applySchedulers(Observable<T> toBeResumed) {
  return observable -> observable.subscribeOn(Schedulers.io())
      .retry((attempts, throwable) -> throwable instanceof SocketTimeoutException
          && attempts < Globals.RETRY_NETWORK_FAIL)
          
      // Global method to refresh token, you can ignore if you don't need auth
      .onErrorResumeNext(refreshTokenAndRetry(toBeResumed))
      .observeOn(AndroidSchedulers.mainThread());
}

private <T extends BaseModel<T>> Subscriber<T> getDefaultSubscriber(final Class<T> tClass, final Integer key) {
  return new Subscriber<T>() {
    @Override
    public void onCompleted() {
    }
  
    @Override
    public void onError(Throwable e) {
      // Delete the Cached observable from our Manager because it was a fail and we want to executed again
      obsManager.remove(key);
      // Handle error
    }
  
    @Override
    public void onNext(T t) {
      // Do what you need to do with the data
    }
  };
}