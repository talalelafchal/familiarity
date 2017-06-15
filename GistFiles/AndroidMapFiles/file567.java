package com.bgstation0.android.application.zinc;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.ProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    // メンバフィールドの定義
    private MainActivity mainActivity = null;   // MainActivity型mainActivityをnullにセット.
    private View fragmentView = null;   // View型fragmentViewをnullにセット.
    private WebView webView = null; // WebView型webViewをnullにセット.
    private final String BUNDLE_ARGUMENT_KEY_URL = "url";   // 定数BUNDLE_ARGUMENT_KEY_URLを"url"とする.
    private ProgressBar progressBar = null; // ProgressBar型progressBarにnullをセット.
    private CustomWebViewClient customWebViewClient = null; // CustomWebViewClient型customWebViewClientにnullをセット.
    private CustomWebChromeClient customWebChromeClient = null; // CustomWebChromeClient型customWebChromeClientにnullをセット.

    // コンストラクタ
    public WebFragment() {
        // Required empty public constructor
    }

    // ビューの生成時.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // ビューの生成.
        fragmentView = inflater.inflate(R.layout.fragment_web, container, false);   // inflater.inflateでR.layout.fragment_webをベースにfragmentViewを生成.
        mainActivity = (MainActivity)getActivity(); // getActivityでmainActivity取得.
        // progressBarの初期化.
        progressBar = (ProgressBar)fragmentView.findViewById(R.id.progressBar);  // progressBarを取得.
        progressBar.setVisibility(View.INVISIBLE);  // progressBarを非表示.
        // webViewを探してロードさせる.
        webView = (WebView)fragmentView.findViewById(R.id.webView);  // webViewを取得.
        // customWebViewClientのセット.
        customWebViewClient = new CustomWebViewClient(mainActivity, this);  // customWebViewClientを生成.
        webView.setWebViewClient(customWebViewClient);  // webView.setWebViewClientでcustomWebViewClientをセット.
        // customWebChromeClientのセット.
        customWebChromeClient = new CustomWebChromeClient(this);    // customWebChromeClientを生成.
        webView.setWebChromeClient(customWebChromeClient);  // webView.setWebChromeClientでcustomWebChromeClientをセット.
        // 引数の受領.
        Bundle args = getArguments();   // getArgumentsでargs取得.
        if (args != null) {
            String url = args.getString(BUNDLE_ARGUMENT_KEY_URL);   // args.getStringでurlを取得.
            if (url != null){
                loadUrl(url);   // loadUrlでurlをロード.
            }
        }
        return fragmentView;    // fragmentViewを返す.
    }

    // URLのロード.
    public void loadUrl(String url){
        webView.loadUrl(url);   // webView.loadUrlでロード.
    }

    // progressBarの表示/非表示.
    public void setProgressBarVisibility(boolean state){
        if (state){
            progressBar.setVisibility(View.VISIBLE);    // 表示
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);  // 非表示
        }
    }

    // progressBarの進捗をセット.
    public void setProgress(int progress){
        progressBar.setProgress(progress);  // progressBar.setProgressでprogressをセット.
    }

    // webViewが戻れるなら戻る.
    public boolean goBack(){
        if (webView.canGoBack()){   // 戻れる場合.
            WebBackForwardList webBackForwardList = webView.copyBackForwardList();  // webBackForwardListを取得.
            int page = webBackForwardList.getCurrentIndex();    // 現在のページ数
            if (page >= 1) { // 1以上.(戻れる.)
                String prevUrl = webBackForwardList.getItemAtIndex(page - 1).getUrl();  // 前のURLを取得.
                mainActivity.setMenuUrlBar(prevUrl);    // prevUrlをセット.
                webView.goBack();   // 戻る.
                return true;    // true.戻る.
            }
            else{
                return false;   // false.終了
            }
        }
        else{
            return false;   // false.終了
        }
    }

    // 現在表示しているWebページのタイトルを取得.
    public String getTitle(){
        return webView.getTitle();  // webView.getTitleでタイトルを取得.
    }

    // 現在表示しているWebページのURLを取得.
    public String getUrl(){
        return webView.getUrl();    // webView.getUrlでURLを取得.
    }
}