public  class JsonArrayPostRequest extends Request<JSONArray>{  
    private Map<String,String> mParam;  
    private Listener<JSONArray>  mListener;  
  
  
    public JsonArrayPostRequest(String url,Listener<JSONArray> listener, ErrorListener errorListener,Map param) {  
        super(Request.Method.POST, url, errorListener);  
        mListener=listener;  
        mParam=param;   
    }  
    @Override  
    protected Map<String, String> getParams() throws AuthFailureError {  
        return mParam;  
    }  
  
       @Override  
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {  
            try {  
                String jsonString =  
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));  
                return Response.success(new JSONArray(jsonString),  
                        HttpHeaderParser.parseCacheHeaders(response));  
            } catch (UnsupportedEncodingException e) {  
                return Response.error(new ParseError(e));  
            } catch (JSONException je) {  
                return Response.error(new ParseError(je));  
            }  
        }  
  
    @Override  
    protected void deliverResponse(JSONArray response) {  
        mListener.onResponse(response);  
          
    }  
  
}  