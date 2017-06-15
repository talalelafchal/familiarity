package gturedi.gist;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

    public static final String UA_ANDROID_LG = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    public static String makePost(String url, Map<String, String> headers, byte[] params) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);

            con.setRequestProperty("User-Agent", UA_ANDROID_LG);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");

            if (headers != null)
                for (Map.Entry<String, String> item : headers.entrySet())
                    con.setRequestProperty(item.getKey(), item.getValue());

            OutputStream os = con.getOutputStream();
            os.write(params);
            os.close();

            return FileUtil.convertStreamToString(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String makeGet(String url, Map<String, String> headers, Map<String, String> params) {
        try {
            url = url + createUrlParams(params);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.addRequestProperty("User-Agent", UA_ANDROID_LG);
            addHeaders(conn, headers);
            return FileUtil.convertStreamToString(conn.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void downloadFile(String url, String path) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.addRequestProperty("User-Agent", UA_ANDROID_LG);
            FileOutputStream out = new FileOutputStream(path);
            FileUtil.copyStream(conn.getInputStream(), out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // helpers //

    private static String createUrlParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder("?");
        for (Map.Entry<String, String> item : params.entrySet()) {
            String charset = "utf-8";
            try {
                result.append(URLEncoder.encode(item.getKey(), charset))
                        .append("=")
                        .append(URLEncoder.encode(item.getValue(), charset))
                        .append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    private static void addHeaders(HttpURLConnection conn, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> item : headers.entrySet()) {
                conn.addRequestProperty(item.getKey(), item.getValue());
            }
        }
    }

}