package com.bgstation0.android.application.zinc;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabsFragment extends Fragment implements AdapterView.OnItemClickListener{

    // メンバフィールドの定義
    private MainActivity mainActivity = null;   // MainActivity型mainActivityをnullにセット.
    private View fragmentView = null;   // View型fragmentViewにnullをセット.
    private GridView tabsGridView = null;   // GridView型tabsGridViewをnullにセット.
    private List<TabsGridItem> tabsGridItemList = null; // List<TabsGridItem>型tabsGridItemListをnullにセット.
    private TabsGridAdapter adapter = null; // TabsGridAdapter型adapterをnullにセット.
    private Map<String, Fragment> fragmentMap = null;   // Map<String, Fragment>型fragmentMapをnullにセット.
    private final String FRAGMENT_TAG_PREFIX_WEB = "web";   // 定数FRAGMENT_TAG_PREFIX_WEBを"web"とする.
    private final String FRAGMENT_TAG_PREFIX_TABS = "tabs"; // 定数FRAGMENT_TAG_PREFIX_TABSを"tabs"とする.

    public TabsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // タブ一覧を取得して, GridViewで表示.
        mainActivity = (MainActivity)getActivity(); // mainActivityを取得.
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_tabs, container, false);  // inflater.inflateでfragment_tabsを元にfragmentViewを作成.
        tabsGridView = (GridView)fragmentView.findViewById(R.id.tabsGridView);  // tabsGridViewを取得.
        tabsGridItemList = new ArrayList<TabsGridItem>();   // newでArrayList<TabsGridItem>オブジェクトtabsGridItemListを生成.
        adapter = new TabsGridAdapter(mainActivity, R.layout.grid_item_tabs, tabsGridItemList); // adapterを生成.
        tabsGridView.setAdapter(adapter);   // tabsGridView.setAdapterでtabsGridViewにadapterをセット.
        fragmentMap = mainActivity.getFragmentMap();    // mainActivity.getFragmentMapでfragmentMapを取得.
        addTabsGridItem();  // addTabsGridItemでアイテム追加.
        adapter.notifyDataSetChanged(); // 更新反映.
        tabsGridView.setOnItemClickListener(this);  // tabsGridView.setOnItemClickListenerでthisをセット.
        return fragmentView;    // fragmentViewを返す.
    }

    // TabsGridItemの追加.
    public void addTabsGridItem(){
        // webFragmentの列挙.
        for (Map.Entry<String, Fragment> entry : fragmentMap.entrySet()){   // fragmentMapからFragmentの列挙.
            String tabName = entry.getKey();    // entry.getKeyでtabNameを取得.
            if (tabName.contains(FRAGMENT_TAG_PREFIX_WEB)) {  // タブ名に"web"が含まれている場合.
                WebFragment webFragment = (WebFragment) entry.getValue();    // webFragmentを取得.
                TabsGridItem item = new TabsGridItem(); // TabsGridItemオブジェクトitemを生成.
                item.tabName = tabName; // item.tabNameにtabNameを代入.
                item.dispName = webFragment.getTitle(); // webFragment.getTitleで取得したタイトルをitem.dispNameに代入.
                adapter.add(item);  // adapter.addでitemを追加.
            }
        }
    }

    // グリッドアイテムが選択された時.
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        // 選択されたアイテムの取得.
        GridView gridView = (GridView)parent;   // parentをGridView型gridViewにキャスト.
        TabsGridItem gridItem = (TabsGridItem) gridView.getItemAtPosition(position);   // gridItemを取得.
        mainActivity.changeFragment(gridItem.tabName);  // mainActivity.changeFragmentでgridItem.tabNameなFragmentに切り替える.
        mainActivity.changeUrl(gridItem.tabName);   // mainActivity.changeUrlでgridItem.tabNameなFragmentのWebViewのURLに切り替える.
        mainActivity.removeFragment(FRAGMENT_TAG_PREFIX_TABS);  // mainActivity.removeFragmentでFRAGMENT_TAG_PREFIX_TABSなFragmentを削除.
    }
}