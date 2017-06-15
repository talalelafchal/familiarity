package com.ozateck.oz_tripple.drawer;

/**
 * Created by ShimejiOzaki on 6/1/15.
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozateck.oz_tripple.R;

public class DrawerListAdapter extends ArrayAdapter<DrawerListData>{

	public static final int DIVIDING_INDEX = -1;
	private Context context;

	public DrawerListAdapter(Context context, ArrayList<DrawerListData> listData){
		super(context, 0, listData);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){

		DrawerListData data = getItem(position);

		// リストビュー用レイアウト
		if(convertView == null){
			if(data.index == DIVIDING_INDEX){
				// 仕切り線用レイアウト
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_drawer_list_divider, parent, false);
			}else{
				// データ用レイアウト
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.item_drawer_list_data, parent, false);
				ImageView ivIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
				ivIcon.setImageDrawable(context.getDrawable(data.resId));
				TextView tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
				tvTitle.setText(data.title);
			}
		}
		return convertView;
	}

	@Override
	public boolean isEnabled(int position){
		boolean enabledFlg = true;
		if(getItem(position).index == DIVIDING_INDEX){
			enabledFlg = false;
		}
		return enabledFlg;
	}
}