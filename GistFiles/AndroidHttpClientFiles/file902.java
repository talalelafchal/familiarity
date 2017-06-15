package edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralStatistics;
import edu.ualr.cpsc7398.updatechecker.view.fragment.StatisticsFragment;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {
    private static final String TAG = "StatisticsAdapter";

    List<UrlDataGeneralStatistics> listUrlDataGeneralStatistics = Collections.emptyList();
    //if we want to delete/add, we need to update listUrlDataGeneralStatistics list


    Context context;

    public StatisticsAdapter() {
        listUrlDataGeneralStatistics = new ArrayList<>();
        context = StatisticsFragment.mStatisticsContext;
    }

    public StatisticsAdapter(List<UrlDataGeneralStatistics> listUrlDataGeneralStatistics, Context applicationContext) {
        this.listUrlDataGeneralStatistics = listUrlDataGeneralStatistics;
        this.context = applicationContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) { //I created the viewholder for my custom row

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_for_statistics_adapter, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");

        switch (position) {
            case 0:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
                break;
            case 1:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
                break;
            default:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
        }

        viewHolder.getTextView1().setText(listUrlDataGeneralStatistics.get(position).getName());
        viewHolder.getEditText1().setText(listUrlDataGeneralStatistics.get(position).getValue());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, UrlDataGeneralStatistics data) {
        listUrlDataGeneralStatistics.add(position, data);
//        notifyItemInserted(listUrlDataGeneralStatistics.size()-1);
        notifyItemInserted(position);
    }

    public void update(int position, UrlDataGeneralStatistics data) { //general concept, blinks the row
        listUrlDataGeneralStatistics.remove(position);
        notifyItemRemoved(position);

        insert(position, data);
    }


    public void updateOnlyValue(int position, UrlDataGeneralStatistics data) {
        listUrlDataGeneralStatistics.add(position, data);
        notifyItemChanged(position, data);

        listUrlDataGeneralStatistics.remove(listUrlDataGeneralStatistics.size() - 1);
        notifyItemRemoved(listUrlDataGeneralStatistics.size() - 1);

    }

    //get item from predefined position
    public UrlDataGeneralStatistics getItemFromPredefinedPosition(int position) {
        return listUrlDataGeneralStatistics.get(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(UrlDataGeneralStatistics data) {
        int position = listUrlDataGeneralStatistics.indexOf(data);
        listUrlDataGeneralStatistics.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return listUrlDataGeneralStatistics.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder { //can be as separate class
        private TextView textView1;
        private EditText editText1;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            textView1 = (TextView) v.findViewById(R.id.rowRecyclerViewStatisticsLayoutTextView);
            editText1 = (EditText) v.findViewById(R.id.rowRecyclerViewStatisticsLayoutEditText);

        }

        public EditText getEditText1() {
            return editText1;
        }

        public TextView getTextView1() {
            return textView1;
        }

    }
}
