package com.example.contactlist;


import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserCustomAdapter extends ArrayAdapter<User> implements Filterable {
	Context context;
	int layoutResourceId;
	ArrayList<User> data = new ArrayList<User>();
	private CountryFilter filter;
	 private ArrayList<User> originalList;


	public UserCustomAdapter(Context context, int layoutResourceId,
			ArrayList<User> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		 this.originalList = new ArrayList<User>();
		   this.originalList.addAll(data);
	}
	@Override
	  public Filter getFilter() {
	   if (filter == null){
	    filter  = new CountryFilter();
	   }
	   return filter;
	  }


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		UserHolder holder = null;
		Log.d("main", "pos:" + "" + position);
	
				if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new UserHolder();
			holder.Name = (TextView) row.findViewById(R.id.name);
			
			holder.Number = (TextView) row.findViewById(R.id.number);
			
			holder.img=(ImageView) row.findViewById(R.id.image);
			
			
		//	holder.btnEdit = (ImageButton ) row.findViewById(R.id.btn_e);
		//	holder.btnDelete = (ImageButton ) row.findViewById(R.id.btn_d);
			row.setTag(holder);
		} else {
			holder = (UserHolder) row.getTag();
		}
		User user = data.get(position); 
		
		holder.Name.setText(user.getName());
		holder.img.setImageBitmap(user.getbi());
		holder.Number.setText(user.getNumber());
		
		
		
		
		Log.d("main", "pos:" + "" + position);

		return row;

	}

	static class UserHolder {
		TextView Name,Number;
		ImageView img;
		
	}
	 private class CountryFilter extends Filter
	  {

	   @Override
	   protected FilterResults performFiltering(CharSequence constraint) {

	    constraint = constraint.toString().toLowerCase();
	    FilterResults result = new FilterResults();
	    if(constraint != null && constraint.toString().length() > 0)
	    {
	    ArrayList<User> filteredItems = new ArrayList<User>();

	    for(int i = 0, l = originalList.size(); i < l; i++)
	    {
	     User User = originalList.get(i);
	     if(User.name.toString().toLowerCase().contains(constraint))
	      filteredItems.add(User);
	    }
	    result.count = filteredItems.size();
	    result.values = filteredItems;
	    }
	    else
	    {
	     synchronized(this)
	     {
	      result.values = originalList;
	      result.count = originalList.size();
	     }
	    }
	    return result;
	   }

	   @SuppressWarnings("unchecked")
	   @Override
	   protected void publishResults(CharSequence constraint, 
	     FilterResults results) {

	    data = (ArrayList<User>)results.values;
	    notifyDataSetChanged();
	    clear();
	    for(int i = 0, l = data.size(); i < l; i++)
	     add(data.get(i));
	    notifyDataSetInvalidated();
	   }
	  }


	 }


