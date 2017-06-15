public  class JsonObjectPostRequest extends Request<JSONObject>{  
    private Map<String,String> mParam;  
    private Listener<JSONObject>  mListener;  
  
  
    public JsonObjectPostRequest(String url,Listener<JSONObject> listener, ErrorListener errorListener,Map param) {  
        super(Request.Method.POST, url, errorListener);  
        mListener=listener;  
        mParam=param;  
          
    }  
    @Override  
    protected Map<String, String> getParams() throws AuthFailureError {  
        return mParam;  
    }  
  
       @Override  
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {  
            try {  
                String jsonString =  
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));  
                return Response.success(new JSONObject(jsonString),  
                        HttpHeaderParser.parseCacheHeaders(response));  
            } catch (UnsupportedEncodingException e) {  
                return Response.error(new ParseError(e));  
            } catch (JSONException je) {  
                return Response.error(new ParseError(je));  
            }  
        }  
  
    @Override  
    protected void deliverResponse(JSONObject response) {  
        mListener.onResponse(response);  
          
    }  
  
} 