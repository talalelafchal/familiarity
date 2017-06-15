 /**
     * We need dummy cookie storage that wouldn't save any cookies
     * or get any cookies
     */
    public class WebkitCookietHandlerProxy extends CookieHandler {

        @Override
        public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {

            if (uri == null || responseHeaders == null) {
                return;
            }

            String url = uri.toString();

            for (String headerKey : responseHeaders.keySet()) {

                if (headerKey == null || !(headerKey.equalsIgnoreCase("Set-Cookie2") || headerKey.equalsIgnoreCase("Set-Cookie"))) {
                    continue;
                }

                for (String headerValue : responseHeaders.get(headerKey)) {
                    CookieManager.getInstance().setCookie(url, headerValue);
                }

                CookieSyncManager.getInstance().sync();
            }
        }

        @Override
        public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {

            if (uri == null || requestHeaders == null) {
                throw new IllegalArgumentException("Argument is null");
            }

            String url = uri.toString();
            Map<String, List<String>> res = new java.util.HashMap<String, List<String>>();
            String cookie = CookieManager.getInstance().getCookie(url);

            if (cookie != null) {
                res.put("Cookie", Arrays.asList(cookie));
            }
            return res;
        }
    }