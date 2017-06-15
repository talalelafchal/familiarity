package com.bgstation0.android.application.zinc;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    // メンバフィールドの定義
    private FragmentManager fragmentManager = null; // FragmentManager型fragmentManagerをnullにセット.
    private final String FRAGMENT_TAG_PREFIX_WEB = "web";   // 定数FRAGMENT_TAG_PREFIX_WEBを"web"とする.
    private final String FRAGMENT_TAG_PREFIX_TABS = "tabs"; // 定数FRAGMENT_TAG_PREFIX_TABSを"tabs"とする.
    private final String BUNDLE_ARGUMENT_KEY_URL = "url";   // 定数BUNDLE_ARGUMENT_KEY_URLを"url"とする.
    private final String HTTP_URL_BG1_BLOG = "http://bg1.hatenablog.com";   // 定数HTTP_URL_BG1_BLOGを"http://bg1.hatenablog.com"とする.
    private int webFragmentNo = 0;  // int型webFragmentNoを0にセット.
    private String currentFragmentTag = null;   // String型currentFragmentTagをnullにセット.
    private MenuItem menuItemUrlBar = null; // MenuItem型menuItemUrlBarをnullにセット.
    private EditText menuUrlBar = null; // EditText型menuUrlBarをnullにセット.
    private Map<String, Fragment> fragmentMap = null;   // Map<String, Fragment>型fragmentMapをnullにセット.

    // アクティビティの生成時.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // fragmentMapの作成.
        fragmentMap = new HashMap<String, Fragment>();  // HashMap<String, Fragment>オブジェクトを生成し, fragmentMapに格納.
        // 最初のWebフラグメントの追加.
        fragmentManager = getSupportFragmentManager();  // getSupportFragmentManagerでfragmentManagerを取得.
        // webFragmentを作成して, タグ名を決めて追加.
        WebFragment webFragment = new WebFragment();    // webFragmentの生成.
        String fragmentTag = FRAGMENT_TAG_PREFIX_WEB + webFragmentNo;   // fragmentTagはFRAGMENT_TAG_PREFIX_WEBにwebFragmentNoを付けたものとする.
        addFragment(webFragment, fragmentTag);  // フラグメントの追加.
        webFragmentNo++;    // webFragmentNoをインクリメント.
    }

    // メニュー作成時
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // getMenuInflater().inflate()でmenu_mainからメニュー作成.
        menuItemUrlBar = menu.findItem(R.id.menu_item_urlbar);  // menuItemUrlBarを取得.
        menuItemUrlBar.setVisible(true);    // menuItemUrlBar.setVisibleでURLバーのメニューアイテムを表示.
        View view = menuItemUrlBar.getActionView(); // menuItemUrlBar.getActionViewでviewを取得.
        menuUrlBar = (EditText)view.findViewById(R.id.urlBar);   // urlBarを取得.
        menuUrlBar.setOnEditorActionListener(this); // menuUrlBar.setOnEditorActionListenerにthisを指定.
        return true;
    }

    // メニュー選択時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 選択されたメニューアイテムごとに振り分ける.
        int id = item.getItemId();  // item.getItemIdでidを取得.
        if (id == R.id.menu_item_show_tabs) {    // タブ一覧の表示.
            // 現在表示しているタブのキャプチャを撮影.
            if (currentFragmentTag.contains(FRAGMENT_TAG_PREFIX_WEB)) {  // "web"が含まれているなら.
                String path = getCacheDir() + "/" + currentFragmentTag + ".jpg";    // pathを生成.
                WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(currentFragmentTag);   // fragmentManager.findFragmentByTagでcurrentFragmentTagなwebFragmentを取得.
                View view = webFragment.getView();
                captureView(path, view);    // captureViewでviewのキャプチャをpathに保存.
            }
            // tabsFragmentを作成して, タグ名を決めて追加.
            TabsFragment tabsFragment = new TabsFragment(); // tabsFragmentの生成.
            String fragmentTag = FRAGMENT_TAG_PREFIX_TABS;  // fragmentTagはFRAGMENT_TAG_PREFIX_TABSとする.
            addFragment(tabsFragment, fragmentTag); // フラグメントの追加.
            setMenuUrlBar("");  // setMenuUrlBarでURLバーを空に.
            menuItemUrlBar.setVisible(false);   // URLバーの非表示.
        }
        else if (id == R.id.menu_item_add_tab) {  // 新しいタブの追加.
            // 現在表示しているタブのキャプチャを撮影.
            if (currentFragmentTag.contains(FRAGMENT_TAG_PREFIX_WEB)) {  // "web"が含まれているなら.
                String path = getCacheDir() + "/" + currentFragmentTag + ".jpg";    // pathを生成.
                WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(currentFragmentTag);   // fragmentManager.findFragmentByTagでcurrentFragmentTagなwebFragmentを取得.
                View view = webFragment.getView();
                captureView(path, view);    // captureViewでviewのキャプチャをpathに保存.
            }
            // webFragmentを作成して, タグ名を決めて追加.
            WebFragment webFragment = new WebFragment();    // webFragmentの生成.
            String fragmentTag = FRAGMENT_TAG_PREFIX_WEB + webFragmentNo;   // fragmentTagはFRAGMENT_TAG_PREFIX_WEBにwebFragmentNoを付けたものとする.
            addFragment(webFragment, fragmentTag);  // フラグメントの追加.
            webFragmentNo++;    // webFragmentNoをインクリメント.
            setMenuUrlBar("");  // setMenuUrlBarでURLバーを空に.
        }
        else if (id == R.id.menu_item_add_bookmark) {    // ブックマークの追加.
            // ブックマーク追加ダイアログの表示.
            showBookmarkAddDialog();    // showBookmarkAddDialogでブックマーク追加ダイアログを表示.
        }
        return super.onOptionsItemSelected(item);
    }

    // フラグメントの追加
    public void addFragment(Fragment addFragment, String fragmentTag){
        // 他のフラグメントを非表示にしてから, フラグメントを追加し, 表示.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();   // fragmentManager.beginTransactionでfragmentTransaction取得.
        for (Map.Entry<String, Fragment> entry: fragmentMap.entrySet()){    // 追加前のフラグメントマップを列挙.
            Fragment fragment = entry.getValue();   // fragmentを取得.
            fragmentTransaction.hide(fragment); // fragmentTransaction.hideでfragmentを非表示.
        }
        fragmentTransaction.add(R.id.content, addFragment, fragmentTag);    // fragmentTransaction.addでaddFragmentをfragmentTagを付けてR.id.contentに追加.
        fragmentTransaction.show(addFragment);  // fragmentTransaction.showでaddFragmentを表示.
        fragmentTransaction.commit();   // fragmentTransaction.commitで確定.
        fragmentMap.put(fragmentTag, addFragment);  // fragmentMap.putでfragmentTagとaddFragmentの組を追加.
        currentFragmentTag = fragmentTag;   // currentFragmentTagにfragmentTagを格納.
    }

    // フラグメントの切り替え
    public void changeFragment(String tabName){
        // 指定されたtabNameのフラグメントは表示, それ以外は非表示.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();   // fragmentManager.beginTransactionでfragmentTransaction取得.
        for (Map.Entry<String, Fragment> entry : fragmentMap.entrySet()) {    // 追加前のフラグメントマップを列挙.
            String tab = entry.getKey();    // tabを取得.
            Fragment fragment = entry.getValue();   // fragmentを取得.
            if (tab.equals(tabName)){
                fragmentTransaction.show(fragment); // ragmentTransaction.showで表示.
            }
            else {
                fragmentTransaction.hide(fragment); // ragmentTransaction.hideで非表示.
            }
        }
        fragmentTransaction.commit();   // fragmentTransaction.commitで確定.
        currentFragmentTag = tabName;
    }

    // URLの切り替え(指定したタブのwebFragmentのWebViewのURLに切り替える.)
    public void changeUrl(String tabName){
        // menuUrlBarを指定したタブのWebViewのURLにする.
        if (tabName.contains(FRAGMENT_TAG_PREFIX_WEB)){ // "web"が含まれているなら.
            WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(tabName);  // webFragmentを取得.
            menuItemUrlBar.setVisible(true);    // menuItemUrlBar.setVisibleでURLバーのメニューアイテムを表示.
            setMenuUrlBar(webFragment.getUrl());
        }
    }

    // フラグメントの削除
    public void removeFragment(String tabName){
        // tabNameでfragmentを探して, 削除.
        Fragment fragment = fragmentManager.findFragmentByTag(tabName); // fragmentManager.findFragmentByTagでtabNameなfragmentを取得.
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();   // fragmentManager.beginTransactionでfragmentTransaction取得.
            fragmentTransaction.remove(fragment);   // fragmentTransaction.removeでfragmentを削除.
            fragmentTransaction.commit();   // fragmentTransaction.commitで確定.
            fragmentMap.remove(tabName);    // fragmentMapからも削除.
        }
    }

    // エディタアクションハンドラ
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            // キーボードの非表示.
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);    // inputMethodManagerを取得.
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);  // inputMethodManager.hideSoftInputFromWindowでソフトウェアキーボードの非表示.
            // URLを取得してロード.
            String url = menuUrlBar.getText().toString();   // menuUrlBar.getText().toString()でURLバーのurlを取得.
            loadUrl(url);   // loadUrlでurlをロード.
            return true;
        }
        return false;
    }

    // URLのロード.
    public void loadUrl(String url){
        // 現在のフラグメントを取得して, そのフラグメントでロードする.
        WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(currentFragmentTag);   // currentFragmentTagでwebFragmentを引く.
        webFragment.loadUrl(url);   // webFragment.loadUrlでurlをロード.
    }

    // menuUrlBarにURLをセット.
    public void setMenuUrlBar(String url){
        menuUrlBar.setText(url);
    }

    // バックキーが押された時.
    @Override
    public void onBackPressed() {
        WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(currentFragmentTag);   // currentFragmentTagでwebFragmentを引く.
        if (!webFragment.goBack()){
            super.onBackPressed();
        }
    }

    // fragmentMapの取得.
    public Map<String, Fragment> getFragmentMap(){
        // fragmentMapを返す.
        return fragmentMap;
    }

    // フラグメントのビューのキャプチャを撮る.
    public void captureView(String path, View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);    // bitmapを作成.
        Canvas canvas = new Canvas(bitmap); // bitmapからCanvasオブジェクトcanvasを生成.
        view.draw(canvas);  // view.drawでcanvasにviewを描画.
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.close();
                fos = null;
            }
        }
        catch (Exception e) {
            Log.d("Zinc:e:", e.toString());
        }
        finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            }
            catch (Exception e) {
                Log.d("Zinc:e:", e.toString());
            }
        }
    }

    // ブックマークの追加ダイアログの表示.
    public void showBookmarkAddDialog(){
        // URL編集ダイアログの生成と表示.
        final Context context = this;   // thisをcontextに格納.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);    // getSystemServiceでinflaterを取得.
        View dialogView = inflater.inflate(R.layout.dialog_edit_url, null); // inflater.inflateでdialog_edit_urlをもとにdialogViewを作成.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    // AlertDialog.Builderオブジェクトbuilderを生成.
        WebFragment webFragment = (WebFragment)fragmentManager.findFragmentByTag(currentFragmentTag);   // currentFragmentTagでwebFragmentを引く.
        String title = webFragment.getTitle();  // webFragment.getTitleでタイトルを取得.
        String url = webFragment.getUrl();  // webFragment.getUrlでURLを取得.
        EditText editTextTitle = (EditText)dialogView.findViewById(R.id.edit_text_title);   // editTextTitleを取得.
        EditText editTextUrl = (EditText)dialogView.findViewById(R.id.edit_text_url);   // editTextUrlを取得.
        editTextTitle.setText(title);   // editTextTitle.setTextでtitleをセット.
        editTextUrl.setText(url);   // editTextUrl.setTextでurlをセット.
        builder.setTitle("ブックマークの追加");  // builder.setTitleでタイトル"ブックマークの追加"をセット.
        builder.setView(dialogView);    // builder.setViewでビューレイアウトはdialogView.
        builder.setPositiveButton("追加", new DialogInterface.OnClickListener() { // "追加ボタン"の処理.
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // とりあえずトースト表示.
                Toast.makeText(context, "追加ボタンが押されました!", Toast.LENGTH_LONG).show(); // "追加ボタンが押されました!"とトースト表示.
            }
        });
        builder.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 何もしない.
            }
        });
        builder.create().show();    // ダイアログの作成と表示.
    }
}