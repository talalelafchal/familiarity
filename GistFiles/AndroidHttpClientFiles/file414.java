private RomInfo doInBackground(Void... notused) {
//Check Network availability
ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
params.add(new BasicNameValuePair("device",android.os.Build.DEVICE.toLowerCase()));
            params.add(new BasicNameValuePair("rom", Utils.getRomID()));

            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(SERVER_PULL_URL + "?" +  URLEncodedUtils.format(params, "UTF-8"));
            HttpResponse r = client.execute(get);
            int status = r.getStatusLine().getStatusCode();
            HttpEntity e = r.getEntity();
            if (status == 200) {
               String data = EntityUtils.toString(e);
               JSONObject json = new JSONObject(data);
                if (json.has("error")) {
                    error = json.getString("error");
                    return null;
                }

                return new RomInfo(
                        json.getString("rom"),
                        json.getString("version"),
                        json.getString("changelog"),
                        json.getString("url"),
                        json.getString("md5"),
                        Utils.parseDate(json.getString("date")));
            }
}
