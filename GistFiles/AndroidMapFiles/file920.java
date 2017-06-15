PNConfiguration pnc = new PNConfiguration()
        .setSubscribeKey("subscribeKey")
        .setPublishKey("publishKey")
        .setSecretKey("secretKey");
PubNub pubnub = new PubNub(pnc);

pubnub.grant()
        .channels(Arrays.asList("coolChannel", "coolchannel2"))
        .authKeys(Arrays.asList("authKey1", "authKey2"))
        .read(true)
        .write(true)
        .async(new PNCallback<PNAccessManagerGrantResult>() {
            @Override
            public void onResponse(PNAccessManagerGrantResult result, PNStatus status) {
                if (status.isError()){
                    System.out.println("grant failed!");
                } else {
                    System.out.println("Grant passed!");
                    System.out.println(result);
                }
            }
        });