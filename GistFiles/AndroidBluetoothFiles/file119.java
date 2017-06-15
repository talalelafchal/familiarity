//From: 
apiClient.updateMyData(myUpdatedData)
    .subscribe(myData -> {
        // handle data fetched successfully and API call completed
    }, throwable -> {
        // handle error event
    }, () -> {
        // handle completion - what we actually care about
    });
//To:
apiClient.updateMyData(myUpdatedData)
    .subscribe(() -> {
        // handle completion
    }, throwable -> {
        // handle error
    });
//Or:
apiClient.updateMyData(myUpdatedData)
    .andThen(performOtherOperation()) // a Single<OtherResult>
    .subscribe(otherResult -> {
        // handle otherResult
    }, throwable -> {
        // handle error
    });