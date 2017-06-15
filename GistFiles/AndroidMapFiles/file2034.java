pubnub.history()
    .channel("chatChannel")
    .async(new PNCallback<PNHistoryResult>() {
        @Override
        public void onResponse(PNHistoryResult result, PNStatus status) {
            for (PNHistoryItemResult historyItemResult : result.getMessages()) {
                System.out.println(historyItemResult);
            }
        }
    });