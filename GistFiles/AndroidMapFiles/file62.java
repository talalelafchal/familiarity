public static void createCheckout(Context context,final string phoneNumber,final String orderId,final float amount){
    mCreateCheckoutResponse.requestStarted();
    RequestQueue queue = Volley.newRequestQueue(context);
    StringRequest sr = new StringRequest(Request.Method.POST,"https://example.com/checkout", new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            mCreateCheckoutResponse.requestCompleted();
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mCreateCheckoutResponse.requestEndedWithError(error);
        }
    }){
        @Override
        protected Map<String,String> getParams(){
            Map<String,String> params = new HashMap<String, String>();
            params.put("reference", orderId);
            params.put("amount", String.valueOf(amount));
            params.put("return_url", "http://example.com");
            params.put("cancel_url", "http://example.com");
            params.put("phone", phoneNumber);
            //Use your favorite hashing library to sign http://sam.co.zw/gava-checkout-signing-reference/
            params.put("signature", TODO);

            return params;
        }
    };
    queue.add(sr);
}

public interface CreateCheckoutResponseListener {
    public void requestStarted();
    public void requestCompleted();
    public void requestEndedWithError(VolleyError error);
}