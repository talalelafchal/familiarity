@Override
public NetworkResponse performRequest(Request<?> request) throws VolleyError {
    long requestStart = SystemClock.elapsedRealtime();
    while (true) {
        HttpResponse httpResponse = null;
        byte[] responseContents = null;
        Map<String, String> responseHeaders = new HashMap<String, String>();
        try {
            // 略
        } catch (SocketTimeoutException e) {
            attemptRetryOnException("socket", request, new TimeoutError());
        } catch (ConnectTimeoutException e) {
            attemptRetryOnException("connection", request, new TimeoutError());
        }
    }
}