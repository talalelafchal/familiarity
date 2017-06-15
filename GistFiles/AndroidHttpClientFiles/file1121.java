package com.mmyuksel.proje;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Common_Grid_Adapter extends ArrayAdapter<ListObject> {

	List<Origin> objects;

	public Common_Grid_Adapter(Context context, int textViewResourceId,
			List<ListObject> istanimex) {
		super(context, textViewResourceId, istanimex);
		this.context = context;
	}

	private Context context;

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.user_list_layout, null);
		}

		ListObject item = getItem(position);
		if (item != null) {
		
			 TextView itemView = (TextView)
			 view.findViewById(R.id.txID);
			 TextView itemView2 = (TextView)
					 view.findViewById(R.id.txLabel1);
			 
			 if (itemView != null) {
		
		 itemView.setText(Integer.toString(item.ID));
		 itemView2.setText(item.Label);
			 
			 }
		}
		return view;
	}
}
