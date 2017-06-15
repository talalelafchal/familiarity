    public void orderCart(final String idCustomer, final String cartId, final String addressInvoice, final String addressDelivery, final String carrierId, final String paymentMethod) {
        progressDialog.show();
        String urlOrderCart = StaticRegisterClass.urlOrder;
        StringRequest postReq = new StringRequest(Request.Method.POST,
                urlOrderCart,
                createMyReqSuccessListenerOrder(),
                createMyReqErrorListener()) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put(StaticRegisterClass.varGlobalAuthKey, StaticRegisterClass.keyAuthNumb);
                params.put(StaticRegisterClass.varGlobalShopPath, StaticRegisterClass.keyShopPath);
                params.put(StaticRegisterClass.varIdCustomers, idCustomer);
                params.put(StaticRegisterClass.varIdCart, cartId);
                params.put(StaticRegisterClass.varIdAddressInvoice, addressInvoice);
                params.put(StaticRegisterClass.varIdAddressDelivery, addressDelivery);
                params.put(StaticRegisterClass.varIdCarrier, carrierId);
                params.put(StaticRegisterClass.varPaymentMethod, paymentMethod);
                return params;
            }
        };
        postReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rqToServer.add(postReq);
    }