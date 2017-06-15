// Install with 0 revenue
kenshooTracking.trackInstall();


// Install with defined revenue and currency
kenshooTracking.trackInstall(1.0, "EUR");


// A purchase event with defined revenue and currency
final double purchaseRevenue = 45.2;
final String purchaseCurrency = "PNY";
final String purchaseEventName = "purchase";

kenshooTracking.trackEvent(purchaseEventName, purchaseRevenue, purchaseCurrency);


// An event with with defined revenue and currency and with additional parameters map
final Map<String, String> additionalParametersMap = new HashMap<String, String>();
additionalParametersMap.put("additionalParamName", "additionalParamValue");

kenshooTracking.trackEvent("level-up", 5.5, "USD", additionalParametersMap);