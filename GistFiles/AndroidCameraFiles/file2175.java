package com.ctrlsmart.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ctrlsmart.bean.EditListInfo;
import com.ctrlsmart.fpcx.R;

import java.util.List;

/**
 * Created by Administrator on 2015/4/1.
 */
public class ResultListAdapter extends BaseAdapter {
    Context context;
    List<EditListInfo> listShow;
    LayoutInflater rinflate;
    public ResultListAdapter(Context context,List<EditListInfo>listShow){
        this.context = context;
        this.rinflate = LayoutInflater.from(context);
        this.listShow = listShow;
    }


    @Override
    public int getCount() {
        return this.listShow.size();
    }

    @Override
    public Object getItem(int position) {
        return this.listShow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("ResultListAdapter","getView");
        ResultViewHolder rViewHolder;
        if(convertView == null) {
            convertView = rinflate.inflate(R.layout.resultitem_layout, null);

            rViewHolder = new ResultViewHolder();
            rViewHolder.rTV = (TextView)convertView.findViewById(R.id.r_id_tv);
            rViewHolder.rFPNum = (TextView)convertView.findViewById(R.id.r_fpNum);

            rViewHolder.rFPTime = (TextView)convertView.findViewById(R.id.r_fpTime);

            rViewHolder.rFPCompany = (TextView)convertView.findViewById(R.id.r_fpCompany);
            convertView.setTag(rViewHolder);
        }else{
            rViewHolder = (ResultViewHolder)convertView.getTag();
        }
        rViewHolder.rTV .setText(1 + position + " ");
        rViewHolder.rFPNum.setText(this.context.getString(R.string.fpnum) +listShow.get(position).getBillNumber());
        rViewHolder.rFPTime.setText(this.context.getString(R.string.fptime)+listShow.get(position).getTime());
        rViewHolder.rFPCompany.setText(this.context.getString(R.string.company)+listShow.get(position).getCompany());
        return convertView;

    }

    public class ResultViewHolder{
        TextView rTV;
        TextView rFPNum;
        TextView rFPTime;
        TextView rFPCompany;

    }
}