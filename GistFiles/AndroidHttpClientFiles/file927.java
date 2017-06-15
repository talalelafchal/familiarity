package edu.ualr.cpsc7398.updatechecker.controller.recyclerviewadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Collections;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataConclusion;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class ConclusionAdapter extends RecyclerView.Adapter<ConclusionAdapter.ViewHolder> {
    private static final String TAG = "ConclusionAdapter";

    List<UrlDataConclusion> listUrlDataConclusion = Collections.emptyList();
    //if we want to delete/add, we need to update listUrlDataConclusion list


    Context context;

    public ConclusionAdapter(List<UrlDataConclusion> listUrlDataConclusion, Context applicationContext) {
        this.listUrlDataConclusion = listUrlDataConclusion;
        this.context = applicationContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) { //I created the viewholder for my custom row

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_for_conclusion_adapter, viewGroup, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");

        switch (position) {
            case 0:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;

            default:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }

        viewHolder.getEditText1().setText(listUrlDataConclusion.get(position).getValue());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, UrlDataConclusion data) {
        listUrlDataConclusion.add(position, data);
        notifyItemInserted(listUrlDataConclusion.size() - 1);
//        notifyItemInserted(position);

    }

    public void update(int position, UrlDataConclusion data) {
        listUrlDataConclusion.remove(position);
        notifyItemRemoved(position);

        insert(position, data);
    }

    public void updateOnlyValue(int position, UrlDataConclusion data) {
        listUrlDataConclusion.add(position, data);
        notifyItemChanged(position, data);

        listUrlDataConclusion.remove(listUrlDataConclusion.size() - 1);
        notifyItemRemoved(listUrlDataConclusion.size() - 1);

    }

    //get item from predefined position
    public UrlDataConclusion getItemFromPredefinedPosition(int position) {
        return listUrlDataConclusion.get(position);
    }

    //remove all

    public void removeAll() {
        for (int i = 0; i < listUrlDataConclusion.size() - 1; i++) {
            listUrlDataConclusion.remove(i);
            notifyItemRemoved(i);
        }
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(UrlDataConclusion data) {
        int position = listUrlDataConclusion.indexOf(data);
        listUrlDataConclusion.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return listUrlDataConclusion.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder { //can be as separate class
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
            editText1 = (EditText) v.findViewById(R.id.rowRecyclerViewConclusionLayoutEditText);

        }

        public EditText getEditText1() {
            return editText1;
        }

    }


}
