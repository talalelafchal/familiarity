SomeService someService =
        SomeServiceGenerator
                .createService(SomeService.class,
                               httpClient,
                               SomeServiceThing.class,
                               new SomeServiceThingDeserializer());

someService
        .loginUserRx(username, password)
        .doOnNext(doSomeStuff()) // do your housecleaning here
        .flatMap(doMoreStuff()) // do you next thing and return an Observable
        .doOnNext(doMoreMoreStuff()) // do more housecleaning here
        .flatMap(doMoreMoreMoreStuff()) // do you next thing and return an Observable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<User>() {
            @Override
            public void onCompleted() {
                // Yay
            }

            @Override
            public void onError(Throwable e) {
                // It broke :(
            }

            @Override
            public void onNext(User user) {
                // Do your thing
            }
        });