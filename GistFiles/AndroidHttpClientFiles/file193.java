// https://github.com/mokelab/android-HTTPClient-Rx-OkHTTP
HTTPClient client = new HTTPClientImpl(new OkHttpClient());
// client.send()はObservableを返す
client.send(Method.GET, "https://gae-echoserver.appspot.com/test", null, null)
  .subscribeOn(Schedulers.computation()) // sendの中身はワーカースレッドで実行する
  .observeOn(AndroidSchedulers.mainThread()) // Observerの中はAndroidのメインスレッドで実行する
  .subscribe(new Observer<HTTPResponse>() {
    @Override
    public void onSubscribe(Disposable d) { }

    @Override
    public void onNext(HTTPResponse response) {
      // データが1つやってきたら呼ばれる
      // 成功時の処理と思ってOK
      result.put("response", response);
      result.put("exception", null);
      latch.countDown();
    }

    @Override
    public void onError(Throwable e) {
      result.put("response", null);
      result.put("exception", e);
      latch.countDown();
    }

    @Override
    public void onComplete() { }
  });