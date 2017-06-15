package com.bgstation0.android.application.zinc;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bg1.
 */
public class CustomWebViewClient extends WebViewClient{

    // メンバフィールドの定義.
    public WebViewTabFragment fragment = null;    // Activityインスタンスactivityをnullにセット.

    // リンクURLクリックやリダイレクトなどでロードURLが上書きされた時.
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url){
        // リンクをクリックした時に, リンク先のURLをEditTextに反映.
        if (fragment != null) {  // activityがあれば.
            EditText urlBar = (EditText) fragment.fragmentView.findViewById(R.id.urlbar);   // urlBarを取得.
            String show = null; // 表示するURLのshowをnullにセット.
            if (url.startsWith("http://")) {    // "http://"の場合.
                show = url.substring(7);    // showにurlの7文字目から始まる文字列を代入.
            }
            else{   // "https://"の場合と考えられる.
                show = url; // showにそのままurlを代入.
            }
            urlBar.setText(show);    // urlBar.setTextでshowを指定すると, urlBarのURLがリンク先のURLに変わる.
        }
        return super.shouldOverrideUrlLoading(view, url);
        //return false; // こっちでも大丈夫.
    }

    // リンクURLクリックやリダイレクトなどでロードURLが上書きされた時.(API Level 24以降の時.)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
        return super.shouldOverrideUrlLoading(view, request);
        //return false; // こっちでも大丈夫.
    }

    // ページのロードを開始する時.
   @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon){
        super.onPageStarted(view, url, favicon);

        // progressBarがあれば表示.
       if (fragment.progressBar != null){   // fragment.progressBarがnull.
           fragment.progressBar.setVisibility(View.VISIBLE);  // setVisibilityでVISIBLEにする.
       }
    }

    // ページのロードが終了した時.
    @Override
    public void onPageFinished (WebView view, String url){
        super.onPageFinished(view, url);

        // MainActivityを使えるようにしておく.
        MainActivity activity = (MainActivity) fragment.getActivity();  // getActivityでMainActivityを取得.

        // progressBarがあれば非表示.
        if (fragment.progressBar != null){   // fragment.progressBarがnull.
            fragment.progressBar.setVisibility(View.INVISIBLE);  // setVisibilityでINVISIBLEにする.
        }

        // タイトルの取得..
        String name = view.getTitle();  // view.getTitleでタイトルを取得.(urlは引数を使う.)
        // historyテーブルに登録.
        long id = -1;   // insertの戻り値を格納するidに-1をセット.
        try{
            if (activity.sqlite == null) {   // activity.sqliteがnullなら.
                activity.sqlite = activity.hlpr.getWritableDatabase();    // activity.hlpr.getWritableDatabase()でDBの書き込みが可能に.
            }
            ContentValues values = new ContentValues(); // テーブルに挿入する値の箱ContentValuesを用意.
            values.put("name", name);    // "name"をキーにnameをput.
            values.put("url", url);      // "url"をキーにurlをput..
            id = activity.sqlite.insertOrThrow("history", null, values);    // activity.sqlite.insertOrThrowでhistoryテーブルにinsert.
        }
        catch (Exception ex) {
            Log.d("Zinc", ex.toString());
        }
        finally {
            activity.sqlite.close();
            activity.sqlite = null;
        }

        Toast.makeText(activity, name, Toast.LENGTH_LONG).show();    // 読み込んだページのタイトルを表示.
    }

    // リソースがロードされた時.
    @Override
    public void onLoadResource(WebView view, String url) {

        // MainActivityを使えるようにしておく.
        MainActivity activity = (MainActivity) fragment.getActivity();  // getActivityでMainActivityを取得.

        // 簡易的なWebフィルタリング.
        // 例えば画像ファイルが含まれていればロードを停止.
        /* とりあえず使わないのでコメントアウト.
        if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png") || url.contains(".gif")){   // url.containsで".jpg"または".jpeg"または".png"または".gif"が含まれていれば.
            view.stopLoading(); // view.stopLoadingでロードを停止.
        }
        */

        super.onLoadResource(view, url);
    }
}
