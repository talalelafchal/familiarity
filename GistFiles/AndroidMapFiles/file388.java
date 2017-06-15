// MODEL

 public Debtor(JSONObject response) throws JSONException {
        setId(response.getString("id"));
        setName(response.getString("name"));
        setAmount(Integer.parseInt(response.getString("amount")));
        setSendMoto(response.getBoolean("sendMoto"));
    }

// REQUEST

mDebtorsList = new ArrayList<>();

        String URL = "https://sheetsu.com/apis/v1.0/fddc517a";
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("REQ", response.toString());

                for(int i = 0; i<response.length(); i++){
                    try {
                        JSONObject o = (JSONObject) response.get(i);
                        Debtor d = new Debtor(o);
                        Log.d("DEBT", d.getName());
                        Log.d("DEBT", "" + d.isSendMoto());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("REQ", error.toString());
            }
        });

        Application.getApplication().addToRequestQueue(request);