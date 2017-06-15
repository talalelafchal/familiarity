public void delete(Debtor debtor){
        String URL = "https://sheetsu.com/apis/v1.0/fddc517a/id/" + debtor.getId();
        Toast.makeText(getApplicationContext(), "Deleting " + debtor.getName(), Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.DELETE, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("DELETE", response);
                Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                listAllDebtors(); // reloader
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("DELETE", error.toString());
            }
        });
        Application.getApplication().addToRequestQueue(request);
    }