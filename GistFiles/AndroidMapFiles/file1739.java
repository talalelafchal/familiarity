package com.bgstation0.android.application.zinc;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bg1 on 2016/10/15.
 */

public class TabsGridAdapter extends ArrayAdapter<TabsGridItem> {

    // メンバフィールドの定義
    LayoutInflater inflater = null;    // LayoutInflater型inflaterをnullにセット.

    // コンストラクタ
    public TabsGridAdapter(Context context, int resource, List<TabsGridItem> objects){
        super(context, resource, objects);  // 親コンストラクタに渡す.
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);   // inflaterの取得.
    }

    // アイテム表示
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // convertViewがnullの時.
        if (convertView == null){   // convertViewがnullなら.
            convertView = inflater.inflate(R.layout.grid_item_tabs, null);    // inflater.inflateでconvertViewを取得.
        }
        // ImageViewにタブ名のファイルをセット.
        ImageView ivThumbnail = (ImageView)convertView.findViewById(R.id.tabThumbnail); // ivThumbnailを取得.
        String path = getContext().getCacheDir() + "/" + getItem(position).tabName + ".jpg"; // サムネイルのpathを生成.
        ivThumbnail.setImageURI(Uri.parse(path));   // ivThumbnail.setImageURIでpathの画像をセット.
        ivThumbnail.setAdjustViewBounds(true);  // ivThumbnail.setAdjustViewBoundsでサムネイルの縦横比を維持.
        // TextViewにWebページの名前をセット.
        TextView tvDispName = (TextView)convertView.findViewById(R.id.tabDispName); // tvDispNameを取得.
        tvDispName.setText(getItem(position).dispName); // tvDispName.setTextでdispNameをセット.
        return convertView; // convertViewを返す.
    }
}