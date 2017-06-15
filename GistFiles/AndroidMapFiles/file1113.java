private Request<?> mRequest;
public ExRetryPolicy(Request<?> request) {
    super(CONNECTION_TIMEOUT, CONNECTION_RETRY_COUNT, 1f);
    mRequest = request;
}

@Override
public void retry(VolleyError error) throws VolleyError {
    NetworkResponse response = error.networkResponse;
    if (response != null && response.statusCode >= 500 && response.statusCode < 600) {
        // サーバーエラー時はリトライしない
        throw error;
    }
    if(mRequest != null && mRequest.isCanceled()) {
        // キャンセル済みならリトライしない（エラー処理されない）
        throw error;
    }

    if (mInterval > 0) {
        try {
            Thread.sleep(mInterval);
        } catch (InterruptedException e) {
        }
    }
    VolleyLog.d("Network Retry count : %d", getCurrentRetryCount());
    super.retry(error);
}