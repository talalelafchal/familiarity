public class NotificationInstanceIdServiceTemp extends FirebaseInstanceIdService {

    private static final String APPLICATION_ARN = "AWSSNSでコピーしたApplication ARN";
    private static final String ENDPOINT = "https://sns.ap-northeast-1.amazonaws.com";
    private static final String ACCESS_KEY = "AWSのアクセスキー";
    private static final String SECRET_KEY = "AWSのSecretKey";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        AmazonSNSClient client = new AmazonSNSClient(generateAWSCredentials());
        client.setEndpoint(ENDPOINT);
        //SharedPreferenceに保存したendpointArnが存在したらそちらから取得するようにしてもOK
        String endpointArn = createEndpointArn(token, client);
        HashMap<String, String> attr = new HashMap<>();
        attr.put("Token", token);
        attr.put("Enabled", "true");
        SetEndpointAttributesRequest req = new SetEndpointAttributesRequest().withEndpointArn(endpointArn).withAttributes(attr);
        client.setEndpointAttributes(req);
    }

    private String createEndpointArn(String token, AmazonSNSClient client) {
        String endpointArn;
        try {
            System.out.println("Creating platform endpoint with token " + token);
            CreatePlatformEndpointRequest cpeReq =
                    new CreatePlatformEndpointRequest()
                            .withPlatformApplicationArn(APPLICATION_ARN)
                            .withToken(token);
            CreatePlatformEndpointResult cpeRes = client
                    .createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();
        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            System.out.println("Exception message: " + message);
            Pattern p = Pattern
                    .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                            "with the same token.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                endpointArn = m.group(1);
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe;
            }
        }
        storeEndpointArn(endpointArn);
        return endpointArn;
    }

    private AWSCredentials generateAWSCredentials() {
        return new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return ACCESS_KEY;
            }

            @Override
            public String getAWSSecretKey() {
                return SECRET_KEY;
            }
        };
    }

    private void storeEndpointArn(String endpointArn) {
        //SharedPreferenceにでもendpointArnを保存して、次回以降はcreateEndpointArnの処理を省略しても良い(公式はその方式になってる)
    }

    private String getEndPointArn() {
        //SharedPreferenceからendpointArnを取得して、次回以降はcreateEndpointArnの処理を省略しても良い(公式はその方式になってる)
    }
}
