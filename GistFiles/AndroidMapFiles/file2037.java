package com.bgstation0.android.application.zinc;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, LoaderManager.LoaderCallbacks<String>{    // SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener, LoaderManager.LoaderCallbacks<String>を実装.

    // メンバフィールドの定義.
    public DBHelper hlpr = null;    // DBHelper型hlprにnullをセット.
    public SQLiteDatabase sqlite = null;    // SQLiteDatabase型sqliteにnullをセット.
    public DownloadManager downloadManager = null;  // DownloadManager型downloadManagerにnullをセット.
    public Uri downloadUri = null;
    public String downloadFilename = null;
    public int tabNo = 1;   // int型tabNoに1をセット.
    public FragmentTabHost tabHost = null;  // FragmentTabHost型tabHostにnullをセット.
    public List<TabHost.TabSpec> tabSpecs = null;  // List<TabHost.TabSpec>型tabSpecsにnullをセット.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DBHelperの作成はここに移動.
        hlpr = new DBHelper(getApplicationContext());   // DBHelperを生成.

        // DownloadManagerの確保.
        if (downloadManager == null) {   // nullなら.
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);  // getSystemServiceでDOWNLOAD_SERVICEを取得.
        }

        // tabSpecsの生成.
        tabSpecs = new ArrayList<TabHost.TabSpec>();    // new ArrayList<TabHost.TabSpec>でtabSpecsを生成.
        // FragmentManagerの取得.
        FragmentManager fragmentManager = getSupportFragmentManager();  // getSupportFragmentManagerでFragmentManagerを取得.(support.v4の場合.)
        // tabHostの取得.
        tabHost = (FragmentTabHost)findViewById(R.id.tabhost);  // tabHostを取得.
        // tabHostの準備.
        tabHost.setup(this, fragmentManager, R.id.content); // tabHost.setupで準備.
        // タブタグネームのセット.
        String tabTagName = "tab" + tabNo; // tabTagNameは"tab" + tabNoとする.
        // タブスペックの作成.
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabTagName);    // tabHost.newTabSpecでtabSpecを作成.
        // タブの表示名のセット.
        tabSpec.setIndicator("新しいタブ" + tabNo);  // tabSpec.setIndicatorでタブに表示する名前をセット.
        // タブの追加.
        Bundle args = new Bundle(); // argsを生成.
        args.putString("tag", tabTagName);  // tabTagNameをセット.
        // ショートカットから起動された時.
        Intent intent = getIntent();    // getIntentでintentを取得.
        if (intent != null) {
            String title = intent.getStringExtra(Intent.EXTRA_TEXT);  // intent.getStringExtraでIntent.EXTRA_TEXTを指定してtitleを取得.
            if (title != null) { // titleがnullでない場合.
                args.putString("title", title); // args.putStringでtitleをセット.
                String url = intent.getDataString(); // intent.getDataStringでurlを取得.
                args.putString("url", url); // args.putStringでurlをセット.
            }
        }
        tabHost.addTab(tabSpec, WebViewTabFragment.class, args);    // tabHost.addTabでタブを追加.
        tabSpecs.add(tabSpec);  // tabSpecs.addでtabSpecを追加.
        // tabNoを増やす.
        tabNo++;    // tabNoを1つ増やす.

    }

    // 新しいインテントが発行された時.(アプリは既に起動しているがショートカットが押された場合.)
    @Override
    protected void onNewIntent (Intent intent){
        super.onNewIntent(intent);

        // タブタグネームのセット.
        String tabTagName = "tab" + tabNo; // tabTagNameは"tab" + tabNoとする.
        // タブスペックの作成.
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabTagName);    // tabHost.newTabSpecでtabSpecを作成.
        // タブの表示名のセット.
        tabSpec.setIndicator("新しいタブ" + tabNo);  // tabSpec.setIndicatorでタブに表示する名前をセット.
        // タブの追加.
        Bundle args = new Bundle(); // argsを生成.
        args.putString("tag", tabTagName);  // tabTagNameをセット.
        // ショートカットから起動された時.
        if (intent != null) {   // intentがnullでなければ.
            String title = intent.getStringExtra(Intent.EXTRA_TEXT);  // intent.getStringExtraでIntent.EXTRA_TEXTを指定してtitleを取得.
            if (title != null) { // titleがnullでない場合.
                args.putString("title", title); // args.putStringでtitleをセット.
                String url = intent.getDataString(); // intent.getDataStringでurlを取得.
                args.putString("url", url); // args.putStringでurlをセット.
            }
        }
        tabHost.addTab(tabSpec, WebViewTabFragment.class, args);    // tabHost.addTabでタブを追加.
        // tabNoを増やす.
        tabNo++;    // tabNoを1つ増やす.

        // 追加したタブを表示.
        TabWidget tw = tabHost.getTabWidget();  // tabHost.getTabWidgetでtwを取得.
        int count = tw.getTabCount();   // tw.getTabCount()でタブの数を取得.
        tabHost.setCurrentTab(count - 1);   // tabHost.setCurrentTabで最後のタブを選択.
    }

    // メニュー作成時
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // リソースからメニュー作成.
        getMenuInflater().inflate(R.menu.menu_main, menu); // getMenuInflater().inflate()でmenu_mainからメニュー作成.
        // searchViewの取得.
        MenuItem searchItem = menu.findItem(R.id.action_search);    // menu.findItemでsearchItemを取得.
        //searchItem.setOnActionExpandListener(this); // searchItem.setOnActionExpandListenerでthis(自分自身)を渡す.
        MenuItemCompat.setOnActionExpandListener(searchItem, this); // 上のだと落ちてしまうので, MenuItemCompatのものを使う.
        SearchView searchView = (SearchView)searchItem.getActionView(); // searchItem.getActionViewでsearchViewを取得.
        searchView.setOnQueryTextListener(this);    // searchView.setOnQueryTextListenerでthis(自分自身)を渡す.
        return true;    // trueを返す.
    }

    // メニュー準備時
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // 現在のフラグメントを取得.
        FragmentManager fragmentManager = getSupportFragmentManager();  // getSupportFragmentManagerでfragmentManager取得.
        int index = tabHost.getCurrentTab();    // tabHost.getCurrentTabで現在のタブのインデックスを取得.
        String tag = tabSpecs.get(index).getTag();  // tabSpecs.get(index).getTagでtabSpecsからtagを取得.
        WebViewTabFragment wf = (WebViewTabFragment)fragmentManager.findFragmentByTag(tag); // fragmentManager.findFragmentByTagでtagからwfを取得.
        if (!wf.isPC) { // PCでない.
            MenuItem mitem = (MenuItem)menu.findItem(R.id.action_show_pcsite);  // R.id.action_show_pcsiteなるMenuItemのmitem取得.
            mitem.setTitle("PCサイトの表示"); //  mitem.setTitleで"PCサイトの表示".
        }
        else{   // PC
            MenuItem mitem = (MenuItem)menu.findItem(R.id.action_show_pcsite);  // R.id.action_show_pcsiteなるMenuItemのmitem取得.
            mitem.setTitle("モバイルサイトの表示");   //  mitem.setTitleで"モバイルサイトの表示".
        }
        return true;
    }

    // メニュー選択時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 選択されたアイテムごとに振り分ける.
        int id = item.getItemId();  // item.getItemIdで選択されたidを取得.
        if (id == R.id.action_bookmark_add) {   // "ブックマークの登録"

            // URL編集ダイアログを表示.
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);    // getSystemServiceでinflaterサービスを取得.
            final View layout = inflater.inflate(R.layout.url_edit_dialog, null);   // inflateでリソースR.layout.dialogからダイアログのViewを作成.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);    // AlertDialog.Builderの作成.
            WebView webView = (WebView) findViewById(R.id.webview);  // webViewを取得.
            final String name = webView.getTitle();   // webView.getTitleでページタイトル取得.
            final String url = webView.getUrl();      // webView.getUrlでページURL取得.
            builder.setTitle("ブックマークの登録");    // setTitleでタイトルに"ブックマークの登録"をセット.
            EditText editTextName = (EditText) layout.findViewById(R.id.edit_text_name); // editTextName取得.
            EditText editTextUrl = (EditText) layout.findViewById(R.id.edit_text_url);  // editTextUrl取得.
            editTextName.setText(name);
            editTextUrl.setText(url);
            builder.setView(layout);    // setViewでビューにlayoutをセット.
            builder.setPositiveButton("登録", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    // DB登録処理
                    EditText registName = (EditText) layout.findViewById(R.id.edit_text_name);   // OK押した後のedit_text_name取得.
                    EditText registUrl = (EditText) layout.findViewById(R.id.edit_text_url);     // OK押した後のedit_text_url取得.
                    String strName = registName.getText().toString(); // registName.getTextの文字列取得.
                    String strUrl = registUrl.getText().toString();   // registUrl.getTextの文字列取得.
                    long id = -1;   // insertの戻り値を格納するidに-1をセット.
                    try {
                        if (sqlite == null) {    // sqliteがnullなら.
                            sqlite = hlpr.getWritableDatabase();    // hlpr.getWritableDatabaseでDBの書き込みが可能に.
                        }
                        ContentValues values = new ContentValues(); // テーブルに挿入する値の箱ContentValuesを用意.
                        values.put("name", strName);    // nameとstrNameのキーをput.
                        values.put("url", strUrl);      // urlとstrUrlのキーをput.
                        id = sqlite.insertOrThrow("bookmark", null, values);    // sqlite.insertOrThrowでinsert.
                    } catch (Exception ex) {
                        Log.d("Zinc", ex.toString());
                    } finally {
                        sqlite.close();
                        sqlite = null;
                    }

                }
            });
            builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // キャンセル処理(実質何もしない.).
                }
            });
            builder.create().show();    // ダイアログの作成と表示.
        } else if (id == R.id.action_bookmark_manager) {    // "ブックマークの管理"

            // IntentでBookmarkActivityを起動.
            String pkgName = getPackageName();  // getPackageNameでパッケージ名取得.
            Intent intent = new Intent();   // intent作成.
            intent.setClassName(pkgName, pkgName + ".BookmarkActivity");    // intent.setClassNameでBookmarkActivityのパッケージ名を指定.
            startActivityForResult(intent, 1001);   // startActivityForResultでrequestCodeを1001として起動.

        } else if (id == R.id.action_history) {    // "履歴"

            // IntentでHistoryActivityを起動.
            String pkgName = getPackageName();  // getPackageNameでパッケージ名取得.
            Intent intent = new Intent();   // intent作成.
            intent.setClassName(pkgName, pkgName + ".HistoryActivity");    // intent.setClassNameでHistoryActivityのパッケージ名を指定.
            startActivityForResult(intent, 1002);   // startActivityForResultでrequestCodeを1002として起動.

        } else if (id == R.id.action_download) { // "ダウンロード"

            // URLやファイル名の取得.
            // URLバーからとるように変更.
            EditText urlBar = (EditText) findViewById(R.id.urlbar);  // urlBarから直接取得.
            String strUrl = urlBar.getText().toString();   // urlBar.getText().toString()でstrUrlに格納.
            if (!strUrl.startsWith("http://")){ // "http://"が無い場合.
                strUrl = "http://" + strUrl;    // 補完.
            }
            downloadUri = Uri.parse(strUrl);  // strUrlをUri.parseでパースし, downloadUriに格納.
            downloadFilename = downloadUri.getLastPathSegment(); // downloadUri.getLastPathSegmentで最後のパス区切り(ファイル名の部分)を取り出し, downloadFilenameに格納.
            // ダウンロードリクエストの作成.
            DownloadManager.Request request = new DownloadManager.Request(downloadUri); //DownloadManager.Requestオブジェクトrequestを作成.
            request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, "/" + downloadFilename); // request.setDestinationInExternalFilesDirでDownloadsディレクトリ直下にファイルdownloadFilenameをダウンロードするように指定する.
            request.setTitle("Zinc-Download");  // request.setTitleでタイトルを"Zinc-Download"にする.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // request.setAllowedNetworkTypesでモバイルネットワーク, Wifiともに許可.
            request.setMimeType("application/octet-stream");    // request.setMimeTypeでMIMEタイプは"application/octet-stream"にしておく.
            // ダウンロードレシーバーの作成.
            DownloadReceiver downloadReceiver = new DownloadReceiver();
            downloadReceiver.activity = this;   // downloadReceiver.activityにthis(MainActivity自身)をセット.
            registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)); // registerReceiverでレシーバーにダウンロード完了時のインテントフィルタを登録.
            long downloadId = downloadManager.enqueue(request); // downloadManager.enqueueでrequestを登録.
        } else if (id == R.id.action_newtab_add) {   // "新しいタブ"

            // タブタグネームのセット.
            String tabTagName = "tab" + tabNo; // tabTagNameは"tab" + tabNoとする.
            // タブスペックの作成.
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(tabTagName);    // tabHost.newTabSpecでtabSpecを作成.
            // タブの表示名のセット.
            tabSpec.setIndicator("新しいタブ" + tabNo);  // tabSpec.setIndicatorでタブに表示する名前をセット.
            // タブの追加.
            Bundle args = new Bundle(); // argsを生成.
            args.putString("tag", tabTagName);  // tabTagNameをセット.
            tabHost.addTab(tabSpec, WebViewTabFragment.class, args);    // tabHost.addTabでタブを追加.
            tabSpecs.add(tabSpec);  // tabSpecs.addでtabSpecを追加.
            // tabNoを増やす.
            tabNo++;    // tabNoを1つ増やす.

        } else if (id == R.id.action_currenttab_delete) {   // "このタブを削除"

            // タブが1個の場合.
            if (tabHost.getTabWidget().getChildCount() <= 1){  // tabHost.getTabWidget().getChildCount()が1以下.
                finish();   // 終了.
            }
            else {   // 2個以上.
                // 現在のタブのみ削除.
                int index = tabHost.getCurrentTab();    // tabHost.getCurrentTabで現在のタブのindexを取得.
                tabSpecs.remove(index);  // tabSpecs.removeでindexを削除.
                // いったん全タブの削除.
                tabHost.clearAllTabs();    // tabHost.clearAllTabsでいったん全てのタブを削除.(いろいろ調べたが今のところこういう方法しかできない模様.)
                // これはいらなかった.
                /*
                FragmentManager fragmentManager = getSupportFragmentManager();  // getSupportFragmentManagerでfragmentManager取得.
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();   // fragmentManager.beginTransactionでfragmentTransactionを取得.
                List<Fragment> fl = fragmentManager.getFragments(); // fragmentManager.getFragmentsでフラグメントリストflを取得.
                for (Fragment f : fl) {  // 全てのフラグメントを削除.
                    fragmentTransaction.remove(f);  // fragmentTransaction.removeでfを削除.
                }
                fragmentTransaction.commit();
                */
                // tabDataListから残りのタブを復元.
                for (TabHost.TabSpec tabSpec : tabSpecs) {
                    // タブの追加.
                    tabHost.addTab(tabSpec, WebViewTabFragment.class, null);    // tabHost.addTabでタブを追加.
                }
            }
        } else if (id == R.id.action_show_pcsite){  // "PCサイトの表示"
            // 現在のフラグメントを取得.
            FragmentManager fragmentManager = getSupportFragmentManager();  // getSupportFragmentManagerでfragmentManager取得.
            int index = tabHost.getCurrentTab();    // tabHost.getCurrentTabで現在のタブのインデックスを取得.
            String tag = tabSpecs.get(index).getTag();  // tabSpecs.get(index).getTagでtabSpecsからtagを取得.
            WebViewTabFragment wf = (WebViewTabFragment)fragmentManager.findFragmentByTag(tag); // fragmentManager.findFragmentByTagでtagからwfを取得.
            if (!wf.isPC) { // PCでない.
                wf.reloadPCSite();    // PCサイトでリロード.
            }
            else{
                wf.reloadPhone();   // モバイルサイトでリロード.
            }
        } else if (id == R.id.action_make_shortcut){    // "ショートカットの作成"
            // タイトルとURL取得.
            WebView wv = (WebView)findViewById(R.id.webview);   // wv取得.
            String title = wv.getTitle();   // タイトル取得.
            String url = wv.getUrl();   // URL取得.
            if (url != null) {   // urlがnullでなければ.
                // URLを表示するインテントを作成.
                Intent launcherIntent = new Intent(Intent.ACTION_VIEW); // Intent.ACTION_VIEWなlauncherIntentを作成.
                launcherIntent.addCategory(Intent.CATEGORY_DEFAULT);   // launcherIntent.addCategoryでIntent.CATEGORY_DEFAULTを追加.
                launcherIntent.setClassName(getPackageName(), getClass().getName());    // launcherIntent.setClassNameでパッケージ名とクラス名をセット.
                launcherIntent.putExtra(Intent.EXTRA_TEXT, title);  // launcherIntent.putExtraでIntent.EXTRA_TEXTにtitleをセット.
                launcherIntent.setData(Uri.parse(url)); // launcherIntent.setDataでurlをセット.
                // ショートカットを作成するインテントを作成.
                Intent shortcutIntent = new Intent();   // shortcutIntentを作成.
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);  // shortcutIntent.putExtraでIntent.EXTRA_SHORTCUT_INTENTにlauncherIntentを指定.
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title); // shortcutIntent.putExtraでEXTRA_SHORTCUT_NAMEにtitleをセット.
                Parcelable iconResource = Intent.ShortcutIconResource.fromContext(this, R.mipmap.ic_launcher);  // ic_launcherからIntent.ShortcutIconResource.fromContextでiconResourceを取得.
                shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource); // shortcutIntent.putExtraでIntent.EXTRA_SHORTCUT_ICON_RESOURCEにiconResourceを指定.
                shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");   // shortcutIntent.setActionで"com.android.launcher.action.INSTALL_SHORTCUT"をアクションとすることでショートカットが作成される.
                sendBroadcast(shortcutIntent);  // sendBroadcastでshortcutIntentを送信.
            }
        } else if (id == R.id.action_show_source){  // ソースの表示.
            // タイトルとURL取得.
            WebView wv = (WebView)findViewById(R.id.webview);   // wv取得.
            String title = wv.getTitle();   // タイトル取得.
            String url = wv.getUrl();   // URL取得.
            if (url != null) {   // urlがnullでなければ.
                // argsの準備.
                Bundle args = new Bundle(); // argsを生成.
                args.putString("url", url);  // urlをセット.
                // HTTPリクエスト(非同期処理)の開始.
                getSupportLoaderManager().initLoader(0, args, this);    // getSupportLoaderManager().initLoaderでローダー初期化.
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // ローダーが作られた時.
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args){
        // Loaderの生成.
        if (args != null) {  // argsがnullでなければ.
            String url = args.getString("url"); // args.getStringでurlを取得.
            return new HttpRequestAsyncTaskLoader(this, url);   // HttpRequestAsyncTaskLoaderを生成して, インスタンスを返す.
        }
        else{
            return null;    // nullを返す.
        }
    }

    // ロードが終了した時.
    @Override
    public void onLoadFinished(Loader<String> loader, String data){
        // とりあえずトーストで表示.
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();  // Toastでdataを表示.
    }

    // ローダーがリセットされた時.
    @Override
    public void onLoaderReset(Loader<String> loader){

    }

    // アクティビティの結果が返ってきたとき.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 結果に対する処理.
        super.onActivityResult(requestCode, resultCode, data);  // まず親クラスのonActivityResult.
        if (resultCode == RESULT_CANCELED) { // RESULT_CANCELEDの場合.
            return; // 何もせず終了.
        }
        Bundle bundle = data.getExtras();   // 引数のdataからdata.getExtrasでbundle取得.
        switch (requestCode) {
            case 1001:  // ブックマークの管理から戻ってきた場合.
            case 1002:  // 履歴から戻ってきた場合.(いまのところ共通している.)
                if (resultCode == RESULT_OK) {   // RESULT_OKなら.
                    String name = bundle.getString("selectedName"); // bundle.getStringで"selectedName"をキーとしてnameを取得.
                    String url = bundle.getString("selectedUrl");   // bundle.getStringで"selectedUrl"をキーとしてurlを取得.
                    EditText urlBar = (EditText) findViewById(R.id.urlbar);  // urlbsr取得.
                    String load = null; // ロードするURLのloadをnullにセット.
                    String show = null; // 表示するURlのshowをurlにセット.
                    if (url.startsWith("https://")) {    // "https://"の場合.
                        load = url; // loadにurlをそのまま代入.
                        show = url; // showにurlをそのまま代入.
                    } else if (url.startsWith("http://")) {    // "http://"の場合.
                        load = url; // loadにurlをそのまま代入.
                        show = url.substring(7);    // showにurlの7文字目から始まる文字列を代入.
                    } else {   // それ以外.
                        load = "http://" + url; // urlの頭に"http"を付けてloadに代入.
                        show = url; // showにurlをそのまま代入.
                    }
                    urlBar.setText(show);   // urlBarはshowをセット.
                    WebView webView = (WebView) findViewById(R.id.webview);  // webViewを取得.
                    webView.loadUrl(load);   // webView.loadUrlでloadをロード.
                }
                break;  // breakで抜ける.
            default:
                break;  // breakで抜ける.
        }
    }

    // ハードバックキーが押された時.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // WebViewを取得.
        WebView wv = (WebView) findViewById(R.id.webview);   // webViewを取得.
        EditText urlBar = (EditText) findViewById(R.id.urlbar);  // urlBar取得.

        // ページが進んでいたらページを戻す.
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack() == true) {   // BACKキーが押されて, バック可能な(つまり進んでる)場合.
            // バックフォワードリストの取得.
            WebBackForwardList wvBackFwdList = wv.copyBackForwardList();    // wv.copyBackForwardListでリストを取得.
            int page = wvBackFwdList.getCurrentIndex(); // wvBackFwdList.getCurrentIndexで何ページ目かを取得.
            if (page >= 1) { // 1ページ目以上なら.
                String prevUrl = wvBackFwdList.getItemAtIndex(page - 1).getUrl();   // wvBackFwdList.getItemAtIndex(page - 1).getUrlで1つ前のURLを取得.
                String show = null; // 表示するURLのshowをnullにセット.
                if (prevUrl.startsWith("http://")) {    // "http://"の場合.
                    show = prevUrl.substring(7);    // showにprevUrlの7文字目から始まる文字列を代入.
                } else {   // "https://"の場合と考えられる.
                    show = prevUrl; // showにそのままprevUrlを代入.
                }
                urlBar.setText(show);    // urlBar.setTextで1つ前のURLをセット.
                wv.goBack();    // wv.goBackで1つ戻る.
            }
        } else {
            // 親クラスに任せる.
            super.onKeyDown(keyCode, event);    // super.onKeyDownに任せる.
        }

        // trueを返す.
        return true;

    }

    // クエリ文字列が変更された時.
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    // クエリ文字列が確定した時.
    @Override
    public boolean onQueryTextSubmit(String query){
        // webViewの中からすべて検索.
        WebView webView = (WebView)findViewById(R.id.webview);  // webViewを取得.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {  // API level 16以上なら.
            webView.findAllAsync(query);    // webView.findAllAsyncで検索.(API level 16で登場.)
        }
        else {  // API level 15なら.
            webView.findAll(query); // webView.findAllで検索.(API level 16ではduplicate.)
        }
        return false;
    }

    // SearchViewの入力画面を開いた時.
    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    // SearchViewの入力画面を閉じた時.
    @Override
    public boolean onMenuItemActionCollapse(MenuItem item){
        // 検索結果をクリア.
        WebView webView = (WebView)findViewById(R.id.webview);  // webViewを取得.
        webView.clearMatches(); // webView.clearMatchesで検索結果をクリア.
        return true;
    }

    // アクティビティが破棄された時.
    @Override
    public void onDestroy() {
        // hlprが残っていたら閉じる.
        if (hlpr != null) {
            hlpr.close();
            hlpr = null;
        }
        super.onDestroy();
    }
}