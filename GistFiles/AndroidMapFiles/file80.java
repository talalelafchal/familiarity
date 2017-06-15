  PNConfiguration pnc = new PNConfiguration()
          .setSubscribeKey("subscribeKey")
          .setPublishKey("publishKey")
          .setCipherKey("cipherKey"); // secret key used to create AES signature
  PubNub pubnub = new PubNub(pnc);

  Map message = new HashMap();
  message.put("text", "hi");
  pubnub.publish()
          .message(message)
          .channel("chatChannel")
          .async(new PNCallback<PNPublishResult>() {
              @Override
              public void onResponse(PNPublishResult result, PNStatus status) {
                  if (status.isError()) {
                      System.out.println("publish failed!");
                  } else {
                      System.out.println("encrypted publish worked!");
                      System.out.println(result);
                  }
              }
          });