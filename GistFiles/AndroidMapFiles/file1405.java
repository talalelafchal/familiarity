Observable<String> getLocales() {
        return Observable.fromCallable(() -> assetManager.getLocales())
                .flatMapIterable(strings -> Lists.newArrayList(strings));
    }