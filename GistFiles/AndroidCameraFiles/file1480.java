package sagarpreet97.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sagarpreet chadha on 19-07-2016.
 */
public class FAQAdapter extends BaseExpandableListAdapter {

    ArrayList<faq> data ;
    LayoutInflater inflater;

    public FAQAdapter(Context context, ArrayList<faq> dta){
        data=dta ;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return data.size() ;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public faq getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition).getDesc() ;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View output = convertView;
        if(output==null){
            output = inflater.inflate(R.layout.row_layout_faq , parent,false);
        }

        String title=data.get(groupPosition).getTitle() ;
        TextView textView = (TextView)output.findViewById(R.id.textView);
        textView.setText(title);
        return output;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View output = convertView;
        if(output==null){
            output = inflater.inflate(R.layout.row_layout_faq_child1 ,parent,false);
        }
        TextView textView = (TextView)output.findViewById(R.id.textView2);
        String desc=data.get(groupPosition).getDesc() ;
        textView.setText(desc);
        return output;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return  false ;
    }
}

