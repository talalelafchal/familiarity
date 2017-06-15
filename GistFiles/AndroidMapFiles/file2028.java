Observable.just(1) // 1 will be emited in the IO thread pool
    .subscribeOn(Schedulers.io())
    .flatMap(...) // will be in the IO thread pool
    .observeOn(Schedulers.computation())
    .flatMap(...) // will be executed in the computation thread pool
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(); // will be executed in the Android main thread (if you're running your code on Android)