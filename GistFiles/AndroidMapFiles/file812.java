package stub;

import android.util.Log;
import meta.RemoteResponse;
import org.apache.commons.lang.StringUtils;
import utils.Base64;
import utils.SerialUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class BaseStub {
    public static String IP = System.getProperty("server.ip");
    public static String PORT = System.getProperty("server.port");

    protected Object remoteInvoke(String serviceName, String methodName, Object[] params) {
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(getUrl(serviceName, methodName, params));
            conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            int nRead;
            byte[] bytes = new byte[1024];
            while ((nRead = is.read(bytes, 0, 1024)) > 0) {
                baos.write(bytes, 0, nRead);
            }
            String rtnStr = new String(baos.toByteArray(), "utf-8");
            RemoteResponse response = (RemoteResponse) SerialUtils.readObject(Base64.decode(rtnStr));
            if (StringUtils.isEmpty(response.getError())) {
                return response.getRes();
            } else {
                throw new Exception(response.getError());
            }
        } catch (Exception e) {
            Log.e("error", "error", e);
        } finally {
            try {
                is.close();
                conn.disconnect();
            } catch (IOException e) {
                Log.e("error", "error", e);
            }
        }
        return null;
    }

    protected String getUrl(String serviceName, String methodName, Object[] params) throws IOException {
        String url = "http://" + IP + ":" + PORT + "/android/remoteInvoke?" + "serviceName=" + serviceName + "&methodName=" + methodName;
        for (Object param : params) {
            url += "&params[]=" + URLEncoder.encode(Base64.encodeObject((Serializable) param), "utf-8");
        }
        return url;
    }
}
