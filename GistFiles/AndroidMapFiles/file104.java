
  private void startVolley() {

        //queue
        RequestQueue postQueue = Volley.newRequestQueue(this);

        //サーバーのアドレス任意
        String POST_URL="サーバーのURL.edit.php";

        StringRequest stringReq=new StringRequest(Request.Method.POST,POST_URL,

                //通信成功
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(WriteActivity.this,"通信に成功しました。",Toast.LENGTH_SHORT).show();
                    }
                },

                //通信失敗
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(WriteActivity.this,"通信に失敗しました。",Toast.LENGTH_SHORT).show();
                    }
                }){

            //送信するデータを設定
            @Override
            protected Map<String,String> getParams(){

                //今回は[FastText：名前]と[SecondText：内容]を設定
                Map<String,String> params = new HashMap<String,String>();
                params.put("FastText",name.getText().toString());
                params.put("SecondText",text.getText().toString());
                return params;
            }
        };

       postQueue.add(stringReq);
    }

