Map message = new HashMap();
message.put("pn_apns", "<APNS Push Payload>");
message.put("pn_mpns", "<MPNS Push Payload>");
message.put("pn_gcm", "<GCM Push Payload>");
message.put("pn_other", "<non-push Payload>");

pubnub.publish()
        .message(message)
        .channel("chatChannel")
        .async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                if (status.isError()) {
                    System.out.println("publish failed!");
                } else {
                    System.out.println("push notification worked!");
                    System.out.println(result);
                }
            }
        });