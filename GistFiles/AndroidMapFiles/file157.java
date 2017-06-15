/**
 * This is an example of how to send Adjust attribution information to Leanplum,
 * via the Android SDK.
 *
 * For more details, please see Adjust's documentation here:
 * https://github.com/adjust/android_sdk/tree/master#16-set-listener-for-attribution-changes
 *
 * Questions or suggestions? Feel free to contact support@leanplum.com.
 */

public class YourApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // configure Adjust
        String appToken = "{YourAppToken}";
        String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        AdjustConfig config = new AdjustConfig(this, appToken, environment);

        config.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                Map<String, String> info = new HashMap<String, String>();
                info.put("publisherName", attribution.network);
                info.put("publisherSubCampaign", attribution.campaign);
                info.put("publisherSubAdGroup", attribution.adgroup);
                info.put("publisherSubAd", attribution.creative);
                Leanplum.setTrafficSourceInfo(info);
            }
        });
        
        Adjust.onCreate(config);
        
        // Put the rest of Leanplum initialization below...

    }
}