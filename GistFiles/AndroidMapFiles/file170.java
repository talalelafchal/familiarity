Api.with(getActivity()).request()
        .authors()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map(authorJsonArray -> authorJsonArray.data)
        .subscribe(authors -> {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(authors);
            realm.commitTransaction();
        });
