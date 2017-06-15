verificarUsuario(new VolleyCallback() {
    @Override
    public void onSuccess(String data) {
    
        Log.e("DATA" , data);

    }
});
                        
public interface VolleyCallback{
    void onSuccess(String string);
}

public void getString(final VolleyCallback callback){

    StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SERVER, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
        
                callback.onSuccess(response);
        
            }
        },
    
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR EN LA RESPUESTA", error.toString());
            }
        }
    )
        {
            @Override
            protected Map<String, String> getParams()
            {
                //DATOS QUE ENVIA POR POST
                Map<String, String>  params = new HashMap<>();
                params.put("var1", "HF");
                params.put("var2", var2);

                return params;
            }
        };

    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(postRequest);
 
}