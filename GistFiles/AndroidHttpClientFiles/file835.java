import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * http工具类 http可以使用HttpURLConnection/HttpClient
 *
 * @author
 * @date
 * @version 
 */
public class HttpUtils {
    /**
     * 获取网络图片
     * @param urlString
     * @return
     * @date
     */
    public static Bitmap getNetWorkBitmap(String urlString) {
        URL mImgUrl;
        Bitmap mBitmap = null;
        try {
            mImgUrl = new URL(urlString);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = (HttpURLConnection) mImgUrl.openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            // 将得到的数据转化成InputStream
            InputStream is = urlConn.getInputStream(); //得到网络返回的IO流
            // 将InputStream转换成Bitmap
            mBitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return mBitmap;
    }
}