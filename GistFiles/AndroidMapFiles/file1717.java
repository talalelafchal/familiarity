public Map<String, Object> createOptions() {
  final Map<String, Object> options = new HashMap<String, Object>();
  options.put(PushSdk.KEY_ACTIVITY, YOUR_ACTIVITY); // Required.
  options.put(PushSdk.KEY_SENDER_ID, YOUR_SENDER_ID); // Required.
  options.put(PushSdk.KEY_SERVER_URL, "https://api-push.cloud.toast.com"); // Optional. Default: https://api-push.cloud.toast.com
  options.put(PushSdk.KEY_CHANNEL, "default-channel"); // Optional. Default: ""(empty).
  options.put(PushSdk.KEY_PUSH_TYPE, YOUR_PUSH_TYPE); // Optional.  PushSdk.PUSH_TYPE_GCM or PushSdk.PUSH_TYPE_TENCENT. Default: PushSdk.PUSH_TYPE_GCM.
  options.put(PushSdk.KEY_AGREE_NOTIFICATION, true); // Optional. Default: false.
  options.put(PushSdk.KEY_AGREE_AD, true); // Optional. Default: false.
  options.put(PushSdk.KEY_AGREE_NIGHT_AD, true); // Optional. Default: false.
  options.put(PushSdk.KEY_COUNTRY, "KR"); // Optional. Default: "US".
  options.put(PushSdk.KEY_LANGUAGE, "ko"); // Optional. Default: "en".
  options.put(PushSdk.KEY_TIMEOUT, 30.0); // Optional. Time Unit: Second. Default: 30.
        
  // Only TENCENT
  if (PushSdk.PUSH_TYPE_TENCENT.equals(pushType)) {
    options.put(PushSdk.KEY_ACCESS_ID, ACCESS_ID); // Required.
    options.put(PushSdk.KEY_ACCESS_KEY, ACCESS_KEY); // Required.
  }
  return options;
}