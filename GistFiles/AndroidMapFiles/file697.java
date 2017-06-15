    RxView.clicks(button)
            .observeOn(Schedulers.io())
            .debounce(2, TimeUnit.SECONDS)
            .flatMap(aVoid -> apiEndpoints.getData())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(...);