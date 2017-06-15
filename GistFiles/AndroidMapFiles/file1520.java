// GET

final String URL = "/volley/resource/12";
// pass second argument as "null" for GET requests
JsonObjectRequest req = new JsonObjectRequest(URL, null,
       new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               try {
                   VolleyLog.v("Response:%n %s", response.toString(4));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               VolleyLog.e("Error: ", error.getMessage());
           }
       });
       
       
// POST

final String URL = "/volley/resource/12";
// Post params to be sent to the server
HashMap<String, String> params = new HashMap<String, String>();
params.put("token", "AbCdEfGh123456");

JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
       new Response.Listener<JSONObject>() {
           @Override
           public void onResponse(JSONObject response) {
               try {
                   VolleyLog.v("Response:%n %s", response.toString(4));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
       }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {
               VolleyLog.e("Error: ", error.getMessage());
           }
       });
       
// GET LIST
final String URL = "/volley/resource/all?count=20";
JsonArrayRequest req = new JsonArrayRequest(URL, new Response.Listener<JSONArray> () {
    @Override
    public void onResponse(JSONArray response) {
        try {
            VolleyLog.v("Response:%n %s", response.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        VolleyLog.e("Error: ", error.getMessage());
    }
});
       
       
// add the request object to the queue to be executed
Application.getInstance().addToRequestQueue(req);



// SEND HEADERS
JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
           new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                   // handle response
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   // handle error                        
               }
           }) {

       @Override
       public Map<String, String> getHeaders() throws AuthFailureError {
           HashMap<String, String> headers = new HashMap<String, String>();
           headers.put("CUSTOM_HEADER", "Yahoo");
           headers.put("ANOTHER_CUSTOM_HEADER", "Google");
           return headers;
       }
   };