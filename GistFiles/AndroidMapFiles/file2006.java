   private void logFlurryPayment() {
    
    // Add any additional params

    HashMap<String, String> params = new HashMap<>();
    
    // Log payment

    FlurryEventRecordStatus recordStatus = FlurryAgent.logPayment("candy", "yummy_candy", 1, 2.99, "USD", "123456789", params);
   }