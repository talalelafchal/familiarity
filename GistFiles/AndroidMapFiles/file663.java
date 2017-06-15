package com.bgstation0.android.application.zinc;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bg1.
 */
public class HttpRequestAsyncTaskLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    // メンバフィールドの定義.
    private Context context = null; // Context型contextにnullをセット.
    private String url = null; // String型urlにnullをセット.

    // コンストラクタ.
    public HttpRequestAsyncTaskLoader(Context context, String url){
        super(context);

        // 引数のセット.
        this.context = context; // contextをメンバのcontextにセット.
        this.url = url; // urlをメンバのurlにセット.
    }

    // バックグラウンド処理.
    @Override
    public String loadInBackground(){

        // ローカルフィールド.
        String response = "";    // String型responseに""をセット.
        HttpURLConnection connection = null;    // HttpURLConnection型connextionにnullをセット.

        // バックグラウンド処理を実行.
        try {   // tryで囲む.
            // 接続
            connection = (HttpURLConnection) new URL(url).openConnection(); // new URL(url).openConnectionでconnectionを開く.
            connection.setRequestMethod("GET"); // connection.setRequestMethodでHTTPメソッドはGETにする.
            connection.setInstanceFollowRedirects(true);    // connection.setInstanceFollowRedirectsはtrueにして, リダイレクトは自動.
            connection.connect();   // connection.connectで接続.
            // 受信して, ヘッダ取得.
            Map headers = connection.getHeaderFields(); //  connection.getHeaderFieldsでヘッダをマップで取得.
            Iterator headerIt = headers.keySet().iterator();    // headers.keySet().iteratorでヘッダイテレータ取得.
            String headerStr = null;    // String型headerStrにnullをセット.
            while (headerIt.hasNext()) { // headerItに次があれば.
                String headerKey = (String) headerIt.next(); // headerIt.nextでヘッダを取り出して, headerKeyに格納.
                headerStr += headerKey + ":" + headers.get(headerKey) + "\r\n"; // キー(headerKey)と値(headers.get(headerKey))の組を作成し, headerStrに追加.
            }
            // ボディ取得.
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); // connection.getInputStream()からreaderを作成.
            String inputLine;   // 取得できたストリーム文字列.
            while ((inputLine = reader.readLine()) != null) {    // nullでなければ.
                response += inputLine;    // responseにinputLIneを追加.
            }
            reader.close(); // readerを閉じる.
        }
        catch (Exception ex){
            String exStr = ex.toString();
            String s = exStr;
        }
        finally {
            // 終了処理.
            if( connection != null ){
                connection.disconnect();    // 切断.
            }
        }
        // レスポンスを返す.
        return response;    // responseを返す.
    }

    // ローディングの開始.
    @Override
    protected void onStartLoading(){
        // 強制ロード.
        forceLoad();    // forceLoadで実行.
    }
}
