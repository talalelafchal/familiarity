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

import java.util.Collections;
import java.util.List;

import edu.ualr.cpsc7398.updatechecker.R;
import edu.ualr.cpsc7398.updatechecker.model.UrlDataGeneralWhatsGoingOn;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class WhatsGoingOnAdapter extends RecyclerView.Adapter<WhatsGoingOnAdapter.ViewHolder> {
    private static final String TAG = "WhatsGoingOnAdapter";

    List<UrlDataGeneralWhatsGoingOn> listUrlDataGeneralWhatsGoingOn = Collections.emptyList();
    //if we want to delete/add, we need to update listUrlDataGeneralWhatsGoingOn list

    Context context;

    public WhatsGoingOnAdapter(List<UrlDataGeneralWhatsGoingOn> listUrlDataGeneralWhatsGoingOn, Context applicationContext) {
        this.listUrlDataGeneralWhatsGoingOn = listUrlDataGeneralWhatsGoingOn;
        this.context = applicationContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) { //I created the viewholder for my custom row

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_for_whatsgoingon_adapter, viewGroup, false);
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
            case 2:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
                break;
            //another fragment activity calling again, Again position becomes 0. Here is a problem now
            case 3:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
                break;
            case 4:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
                break;

            default:
                viewHolder.getEditText1().setRawInputType(InputType.TYPE_NULL);
        }

        viewHolder.getTextView1().setText(listUrlDataGeneralWhatsGoingOn.get(position).getName());
        viewHolder.getEditText1().setText(listUrlDataGeneralWhatsGoingOn.get(position).getValue());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, UrlDataGeneralWhatsGoingOn data) {
        listUrlDataGeneralWhatsGoingOn.add(position, data);
//        notifyItemInserted(listUrlDataGeneralWhatsGoingOn.size()-1);
        notifyItemInserted(position);

    }

    public void update(int position, UrlDataGeneralWhatsGoingOn data) {
        listUrlDataGeneralWhatsGoingOn.remove(position);
        notifyItemRemoved(position);

        insert(position, data);
    }

    public void updateOnlyValue(int position, UrlDataGeneralWhatsGoingOn data) {
        listUrlDataGeneralWhatsGoingOn.add(position, data);
        notifyItemChanged(position, data);

        listUrlDataGeneralWhatsGoingOn.remove(listUrlDataGeneralWhatsGoingOn.size() - 1);
        notifyItemRemoved(listUrlDataGeneralWhatsGoingOn.size() - 1);
    }

    //get item from predefined position
    public UrlDataGeneralWhatsGoingOn getItemFromPredefinedPosition(int position) {
        return listUrlDataGeneralWhatsGoingOn.get(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(UrlDataGeneralWhatsGoingOn data) {
        int position = listUrlDataGeneralWhatsGoingOn.indexOf(data);
        listUrlDataGeneralWhatsGoingOn.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return listUrlDataGeneralWhatsGoingOn.size();
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
            textView1 = (TextView) v.findViewById(R.id.rowRecyclerViewWhatsGoingOnLayoutTextView);
            editText1 = (EditText) v.findViewById(R.id.rowRecyclerViewWhatsGoingOnLayoutEditText);

        }

        public EditText getEditText1() {
            return editText1;
        }

        public TextView getTextView1() {
            return textView1;
        }
    }


}
