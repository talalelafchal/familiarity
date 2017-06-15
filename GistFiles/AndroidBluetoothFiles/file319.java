//From: 
apiClient.getMyData()
    .subscribe(myData -> {
        // handle data fetched successfully
    }, throwable -> {
        // handle error event
    }, () -> {
        // handle on complete event
    });
//To:
apiClient.getMyData()
    .subscribe(myData -> {
        // handle data fetched successfully and API call completed
    }, throwable -> {
        // handle error event
    });