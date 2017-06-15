package com.example.abhishek.assignmentwiredelta.Adaprter;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.abhishek.assignmentwiredelta.Model.CompanyDetails;
import com.example.abhishek.assignmentwiredelta.R;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<CompanyDetails> worldpopulationlist=null;
    private ArrayList<CompanyDetails> arraylist;
    public ListViewAdapter(Context context,
                           List<CompanyDetails> worldpopulationlist) {
        Log.i("working4", "working");

        mContext = context;
        this.worldpopulationlist = worldpopulationlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<CompanyDetails>();
        this.arraylist.addAll(worldpopulationlist);
    }



    public class ViewHolder {
        TextView CompanyId;
        TextView CompanyName;
        TextView CompanyOwner;
        TextView CompanystartDate;
        TextView CompanyDescription;
        TextView CompanyDepartments;

    }

    @Override
    public int getCount() {
        return worldpopulationlist.size();
    }

    @Override
    public CompanyDetails getItem(int position) {
        return worldpopulationlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        CompanyDetails compant = worldpopulationlist.get(position);
        Log.i("working3", "working");
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listtwo_searchresults, null);
            // Locate the TextViews in listview_item.xml
            holder.CompanyId = (TextView) view.findViewById(R.id.company_id);
            holder.CompanyName = (TextView) view.findViewById(R.id.company_name);
            holder.CompanyOwner = (TextView) view.findViewById(R.id.company_owner);
            holder.CompanystartDate = (TextView) view.findViewById(R.id.company_startdate);
            holder.CompanyDescription = (TextView) view.findViewById(R.id.company_Description);
            holder.CompanyDepartments = (TextView) view.findViewById(R.id.company_Departments);
            // Locate the ImageView in listview_item.xml
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.CompanyId.setText("CompanyId:"+ compant.getCompanyid());
        holder.CompanyName.setText("Company Name:" + compant.getCompanyname());
        holder.CompanyOwner.setText("Company Owner:" + compant.getCompanyowner());
        holder.CompanystartDate.setText("Company startdate:"+ compant.getCompanystartdate());
        holder.CompanyDescription.setText("Company Description:" + compant.getCompanydescription());
        holder.CompanyDepartments.setText("Company Departments:" + compant.getCompanydepartments());



        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        worldpopulationlist.clear();
        if (charText.length() == 0) {
            worldpopulationlist.addAll(arraylist);
        } else {
            for (CompanyDetails wp : arraylist) {
                if (wp.getCompanyname().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    worldpopulationlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filter1(String one) {

        one = one.toLowerCase(Locale.getDefault());
        worldpopulationlist.clear();
        if (one.length() == 0) {
            worldpopulationlist.addAll(arraylist);
        } else {
            for (CompanyDetails wp : arraylist) {
                if (wp.getCompanydepartments().toLowerCase(Locale.getDefault())
                        .contains(one)) {
                    worldpopulationlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}


