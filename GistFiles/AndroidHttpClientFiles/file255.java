package com.demo.php.listview; 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView; 
public class DataAdapter extends BaseAdapter {
  Context mContext; 
	private LayoutInflater mInflater;
	String targetmonth;
	String targetyear;
	String targetamount; 
	String[] month;
	String[] year;
	String[] amount;
	public DataAdapter(Context c, String[] month, String[] year, String[] amount) {
		this.month = month;
		this.year = year;
		this.amount = amount; 
		mContext = c;
		mInflater = LayoutInflater.from(c); 
	} 
	public int getCount() {
		return month.length;
	} 
	public Object getItem(int position) {
		return position;
	} 
	public long getItemId(int position) {
		return position;
	} 
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.customgrid, parent, false);
			holder = new ViewHolder();
			holder.month = (TextView) convertView
					.findViewById(R.id.targetmonth); 
			holder.year = (TextView) convertView.findViewById(R.id.targetyear); 
			holder.amount = (TextView) convertView
					.findViewById(R.id.targetamount); 
			if (position == 0) {
				convertView.setTag(holder);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {

			holder.month.setText(month[position]);
			holder.year.setText(year[position]);
			holder.amount.setText(amount[position]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return convertView;
	} 
	static class ViewHolder {
		TextView month;
		TextView year, amount; 
	}

}
