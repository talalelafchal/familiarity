    public boolean sendPostRequest() {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("param1", "value1");
        postParams.put("param2", "value2");
        String postData = encode(postParams);
        if (postData == null || postData.isEmpty()) {
            return false;
        }

        HttpsURLConnection conn = null;
        String responseData = "";
        int httpStatusCode = -1;
        try {
            URL loginUrl = new URL("http://some-server.com");

            /* prepare the POST request */
            conn = (HttpsURLConnection) loginUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setFixedLengthStreamingMode(postData.getBytes().length);

            /* add the POST data */
            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            BufferedWriter writer = new BufferedWriter(os);
            writer.write(postData);
            writer.flush();
            os.close();
            writer.close();

            /* send the request */
            conn.connect();

            /* get the server response */
            /* (this assumes that the server still sends a response even if it isn't 200(OK)) */
            httpStatusCode = conn.getResponseCode();
            InputStreamReader is = null;
            switch (httpStatusCode) {
                case HttpsURLConnection.HTTP_OK:
                    is = new InputStreamReader(conn.getInputStream());
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    is = new InputStreamReader(conn.getErrorStream());
                    break;
                default:
                    break;
            }
            if (is != null) {
                BufferedReader reader = new BufferedReader(is);
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    responseData = responseData.concat(inputLine);
                }
                is.close();
                reader.close();
            }
            Log.d(TAG, responseData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "invalid URL", e);

            return false;
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "connection timed-out", e);

            return false;
        } catch (IOException e) {
            Log.e(TAG, "connection error", e);

            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        /* do something with the responseData.. */

        return true;
    }
    
    /**
     * URL-encodes a given set of param=value pairs.<br/>
     * The output will be of the form "param1=value1&param2=value2&.."<br/>
     * <br/>
     * We could have used NameValuePair from org.apache.http but that's
     * not anymore available starting API 23. A HashMap is used instead.
     *
     * @param params map containing the param=value pairs
     * @return the URL-encoded string or null if encoding is not possible
     */
    private String encode(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        String encodedParams = null;

        try {
            StringBuilder sb = new StringBuilder();
            boolean isFirstParam = true;

            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey() == null
                    || entry.getKey().isEmpty()
                    || entry.getValue() == null) {
                    return null;
                }

                if (isFirstParam) {
                    isFirstParam = false;
                } else {
                    sb.append('&');
                }

                /* URLEncoder encodes spaces into "+" insted of "%20" */
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8").replace("+", "%20"));

                encodedParams = sb.toString();
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encoding failed", e);
        }

        return encodedParams;
    }
