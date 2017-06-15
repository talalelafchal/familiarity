Map<String, String> transaction = new HitBuilders.TransactionBuilder()
    .setTransactionId(ads.getItemId())
    .setAffiliation(memberModel.getmember_id())
    .setRevenue(Double.parseDouble(ads.getPrice()))
    .setTax(0)
    .setShipping(0)
    .setCurrencyCode("")
    .build();
                
Map<String, String> item = new HitBuilders.ItemBuilder()
    .setTransactionId(ads.getItemId())              // item id
    .setName(ads.getTitle())                        // item title
    .setSku(memberModel.getmember_id())             // member id
    .setCategory(ads.getCategory().getName())       // category name
    .setPrice(Double.parseDouble(ads.getPrice()))   // price
    .setQuantity(1)                                 // fixed 1
    .setCurrencyCode("")                            // empty value
    .build();