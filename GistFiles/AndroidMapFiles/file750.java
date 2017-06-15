package cn.saymagic.fircode;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by saymagic on 15/5/22.
 */
public class FirCode {

    private static String API_KEY = null;

    private static String API_SECRECT = null;

    private static final String CHECK_INCODE_URL = "http://incode.fir.im/api/verify";

    private static final String USE_INCODE_URL = "http://incode.fir.im/api/occupy";

    /**
     * 禁止实例化
     */
    private FirCode() {
    }

    /**
     * 初始化apiKey与apiSecrect，在调用check()与use()方法前必须先死奥用此方法。仅需调用一次即可。
     *
     * @param apiKey
     * @param apiSecrect
     */
    public static void init(String apiKey, String apiSecrect) {
        API_KEY = apiKey;
        API_SECRECT = apiSecrect;
    }

    /**
     * 检查API_KEY与API_SECRET是否初始化，
     *
     * @return
     */
    public static boolean checkIfInit() {
        if (API_KEY == null || API_SECRECT == null)
            throw new ExceptionInInitializerError("未初始化API_KEY与API_SECRET,请调用FirCode.init(key,secrt)方法进行初始化");
        return true;
    }

    /**
     * 同步检测code是否被使用
     * @param inCode
     * @return 返回格式详见Fir Code API
     */
    public static String check(String inCode) {
        checkIfInit();
        System.out.println("doCheck线程：" + Thread.currentThread().getId());
        String url = CHECK_INCODE_URL + "?sign=" + MD5(API_SECRECT + inCode) + "&api_key=" + API_KEY + "&code=" + inCode;
        try {
            return get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步检查code是否使用，完成后在主线程回调CallBack接口
     * @param inCode
     * @param callBack
     */
    public static void  checkAsyn(final String inCode,final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = check(inCode);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResponse(result);
                    }
                });
            }
        }).start();

    }

    /**
     * 同步使用code
     * @param inCode
     * @return 返回格式详见Fir Code API
     */
    public static String use(String inCode) {
        checkIfInit();
        try {
            return put(USE_INCODE_URL, "sign=" + MD5(API_SECRECT + inCode) + "&api_key=" + API_KEY + "&code=" + inCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 异步使用code，完成后在主线程回调CallBack接口
     * @param inCode
     * @param callBack
     */
    public static void  useAsyn(final String inCode,final CallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result = use(inCode);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResponse(result);
                    }
                });
            }
        }).start();

    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }


    /**
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public static String get(String url,
                             Map<String, String> headers) throws IOException {
        return fetch("GET", url, null, headers);
    }


    /**
     * @param url
     * @param body
     * @param headers
     * @return
     * @throws IOException
     */
    public static String put(String url, String body,
                             Map<String, String> headers) throws IOException {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return fetch("PUT", url, body, headers);
    }

    /**
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public static String put(String url, String body) throws IOException {
        return put(url, body, null);
    }


    /**
     * @param method
     * @param url
     * @param body
     * @param headers
     * @return
     */
    public static String fetch(String method, String url, String body,
                               Map<String, String> headers) {
        // connection
        URL u;
        HttpURLConnection conn = null;
        OutputStream os;
        InputStream is;
        String response = null;
        try {
            u = new URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            // method
            if (method != null) {
                conn.setRequestMethod(method);
            }

            // headers
            if (headers != null) {
                for (String key : headers.keySet()) {
                    conn.addRequestProperty(key, headers.get(key));
                }
            }

            // body
            if (body != null) {
                conn.setDoOutput(true);
                os = conn.getOutputStream();
                os.write(body.getBytes());
                os.flush();
                os.close();
            }

            is = conn.getInputStream();
            response = streamToString(is);
            is.close();

            if (conn.getResponseCode() == 301) {
                String location = conn.getHeaderField("Location");
                return fetch(method, location, body, headers);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            is = conn.getErrorStream();
            try {
                response = streamToString(is);
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return response;
        }
        return response;
    }

    /**
     * 将输入流转化为String
     *
     * @param in
     * @return
     * @throws IOException
     */
    static public String streamToString(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * 生成s的MD5值
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface CallBack {
        void onResponse(String result);
    }
}

