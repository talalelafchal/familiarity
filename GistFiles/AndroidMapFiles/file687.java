  /**
     * get android id of device.
     *
     * @param context app context
     * @return android id
     */
    public static String getAndroidID(Context context) {
        if (context != null) {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            return "N/A";
        }
    }

    /**
     * get query parameter from url
     *
     * @param url url
     * @return <key,value> pair of query parameter
     */
    public static Map<String, String> getQueryMap(String url) {
        String[] params = url.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }