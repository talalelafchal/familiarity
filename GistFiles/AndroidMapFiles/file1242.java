package com.example.androidlab;
 
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
 
public class CustomAdapter extends ArrayAdapter {
  private Context context;
  private List<Map> list;
 
	public CustomAdapter(Context context, int textViewResourceId, List<Map> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = objects;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.list_item, null);
		}
		Map map = list.get(position);
		if (map != null) {
			TextView txtId = (TextView) v.findViewById(R.id.txtId);
			if (txtId != null) {
				txtId.setText(map.get("Id").toString());
			}
			TextView txtValue = (TextView) v.findViewById(R.id.txtValue);
			if (txtValue != null) {
				txtValue.setText(map.get("Value").toString());
			}
		}
		return v;
	}
 
}