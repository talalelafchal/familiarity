String installationId = "INSTALLATION_ID"; // replace with correct installationId

Map<String, String> data = new HashMap<String, String>();
data.put("alert", "Hello there!"); // replace with correct message

Map<String, Object> params = new HashMap<String, Object>();
params.put("installationId", installationId);
params.put("data", data);

ParseCloud.callFunctionInBackground("push", params, new FunctionCallback<Object>() {
    @Override
    public void done(Object response, ParseException e) {
        if (e == null) {
            // push was sent
        }
    }
});
