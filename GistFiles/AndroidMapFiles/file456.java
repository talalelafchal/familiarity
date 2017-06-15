...

  HashMap<String, String> params = new HashMap<String, String>();

  params.put("chNFE", "Teste do chnfe");
  params.put("qrcode", "Teste do qrcode");
  params.put("format", "Teste do format ");

  Kumulos.call("new", params, new ResponseHandler() {

    @Override

    public void didCompleteWithResult(Object result) {

      // Do updates to UI/data models based on result
      System.out.println(result.toString());

    }

  });


...