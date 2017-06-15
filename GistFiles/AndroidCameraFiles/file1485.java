package com.ctrlsmart.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ctrlsmart.bean.EditListInfo;
import com.ctrlsmart.fpcx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/3/28.
 */
public class EditlListAdapter extends BaseAdapter {
    LayoutInflater linflate;
    List<EditListInfo> list;
    DelAfterCallBack delAfterCallBack;
    ShowToastCallBack showToastCallBack;
    Context context;
    public EditlListAdapter(Context context,int count) {
        this.context = context;
        this.linflate = LayoutInflater.from(context);
        list = new ArrayList<EditListInfo>();
        for(int i=0;i<count;i++){
            EditListInfo elInfro = new EditListInfo();
            list.add(elInfro);
        }

    }

    public void setCallBack(DelAfterCallBack cb,ShowToastCallBack st){
        this.delAfterCallBack = cb;
        this.showToastCallBack = st;
    }

    public List<EditListInfo> getList(){
        return list;
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.e("CU","position ="+position);
//        for(int i=0;i<list.size();i++){
//            Log.e("list+++++++++++++","list item fpCode"+list.get(i).getFpCode());
//        }
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = linflate.inflate(R.layout.listitem_layout, null);

            viewHolder = new ViewHolder();
            viewHolder.idTV = (TextView)convertView.findViewById(R.id.id_tv);
            viewHolder.fpCodeET = (EditText)convertView.findViewById(R.id.fpCode);
            viewHolder.fpCodeET.setTag(position);
            viewHolder.fpCodeET.addTextChangedListener(new MyTextWatcher(viewHolder) {
                @Override
                public void afterETChanged(Editable s, ViewHolder holder) {
                    int p = (Integer)holder.fpCodeET.getTag();
                    modifyListCode(s.toString(),p);
                    if(s.length()!=12){
                        holder.fpCodeET.setTextColor(Resources.getSystem().getColor(android.R.color.holo_red_dark));
                    }else{
                        holder.fpCodeET.setTextColor(Resources.getSystem().getColor(android.R.color.holo_blue_light));
                    }
                }
            });
            viewHolder.fpNumET = (EditText)convertView.findViewById(R.id.fpNum);
            viewHolder.fpNumET.addTextChangedListener(new MyTextWatcher(viewHolder) {


                @Override
                public void afterETChanged(Editable s, ViewHolder holder) {
                    int p = (Integer)holder.fpNumET.getTag();
                    modifyListNumber(s.toString(), p);
                    if(s.length()!=8){
                        holder.fpNumET.setTextColor(Resources.getSystem().getColor(android.R.color.holo_red_dark));
                    }else{
                        holder.fpNumET.setTextColor(Resources.getSystem().getColor(android.R.color.holo_blue_light));
                    }
                }
            });
            viewHolder.fpNumET.setTag(position);
            viewHolder.delBtn = (Button)convertView.findViewById(R.id.del_btn);

            viewHolder.delBtn.setOnClickListener(new MyClickListenner(viewHolder) {

                @Override
                public void click(ViewHolder holder) {
                    if(list.size() > 1) {
                        int p = (Integer) holder.fpNumET.getTag(); //不能写成 list.remove((Integer) holder.fpNumET.getTag());
//                        Log.e("CU","position pppppp="+p);
//                        for(int i=0;i<list.size();i++){
//                            Log.e("list","list!!!!!!! item fpCode"+list.get(i).getFpCode());
//                        }
                        list.remove(p);
                        Log.e("","list.size ="+list.size());
                        if(delAfterCallBack != null){
                            delAfterCallBack.delItemAfter();
                        }
                    }
                }

            });
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.fpNumET.setTag(position);
        }
        //必须更新editText内容，否则与显示内容与list中不一致
//        Log.e("list","position=== "+position+"list.get(i)========"+list.get(position).getFpCode());
        EditListInfo eLInfo = getCurrentInfo(position);
        viewHolder.fpCodeET.setText(eLInfo.getIssue());
        viewHolder.fpNumET.setText(eLInfo.getBillNumber());
//        Log.e("list","position=== "+position+"list.get(i)========"+list.get(position).getFpCode());
        viewHolder.idTV.setText(1+position+" ");
        return convertView;
    }

    public abstract class MyTextWatcher implements TextWatcher {
        ViewHolder holder;
        public MyTextWatcher(ViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            afterETChanged(s,holder);

        }

        public abstract void afterETChanged(Editable s,ViewHolder holder);



    }
    /**
     *
     * 方法名：modifyListNumber
     * 功能：修改号码
     * 参数：
     * @param number
     * @param p
     * 创建人：huanghsh
     * 创建时间：2012-6-8
     */
    private void modifyListNumber(String number ,int p){
        if(p>=list.size()) return;
        EditListInfo  t = list.get(p);
        t.setBillNumber(number);
        Log.e("","number ="+number);
        list.set(p, t);
    }

    /**
     *
     * 方法名：modifyListNumber
     * 功能：修改号码
     * 参数：
     * @param number
     * @param p
     * 创建人：huanghsh
     * 创建时间：2012-6-8
     */
    private void modifyListCode(String number ,int p){
        if(p>=list.size()) return;
        EditListInfo  t = list.get(p);
        t.setIssue(number);
        Log.e("","code number ="+number);
        list.set(p, t);
    }


    public abstract class MyClickListenner implements View.OnClickListener{
        ViewHolder holder;
        public MyClickListenner(ViewHolder holder){
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            click(holder);
        }

        public abstract void click(ViewHolder holder);
    }

    private final class ViewHolder{
        private TextView idTV;
        private EditText fpCodeET;
        private EditText fpNumET;
        private Button delBtn;

    }
/*
* addItem
* 增加一行：
*
* */
    public void addItem(){
//        for(int i=0;i<list.size();i++){
//            Log.e("list","list=============="+list.get(i).getFpCode());
//        }
        EditListInfo info = new EditListInfo();

        list.add(info);
    }

    private EditListInfo getCurrentInfo(int position){
        return list.get(position);
    }



    public interface DelAfterCallBack{
        public void delItemAfter();
    }

    public interface ShowToastCallBack{
        public void showToast(String s);
    }
}
