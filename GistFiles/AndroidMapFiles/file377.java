public class GsonRequest<Passenger> extends Request<Passenger> {
    private final Gson gson = new Gson();
    private final Class<Passenger> clazz;
    private final Map<String, String> headers;
    private final Response.Listener<Passenger> listener;

    private final Map<String, String> params;

    /**
     * Make a GET request and return a parsed object from JSON.
     *  @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     * @param listener
     */
    public GsonRequest(String url, Class<Passenger> clazz, Map<String, String> headers, Map<String, String> params,
                       Response.Listener<Passenger> listener, Response.ErrorListener errorListener) {

        super(Method.POST, String.valueOf(url), errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;

        this.params = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected void deliverResponse(Passenger response) {
        listener.onResponse(response);
    }

//    @Override
//    public String getBodyContentType() {
//        return "application/json";
//    }

    @Override
    protected Response<Passenger> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(

                    response.data,
                    HttpHeaderParser.parseCharset(response.headers) );


            return Response.success(
                    gson.fromJson(json, clazz ),
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}