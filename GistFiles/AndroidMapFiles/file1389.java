package com.example.android.searchjsonhttp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 2017/2/24.
 * Create Custom ArrayAdapter to tell system how to allocate the data into list view
 */

public class BookListAdapter extends ArrayAdapter<BookList> {

    //initiate ArrayList with null
    private ArrayList<BookList> bookListList = null;
    //initiate ArrayList for filter
    private ArrayList<BookList> fList;
    //initiate Filter
    private Filter filter;

    /**
     * This is custom constructor. The context is used to inflate the layout file, and the list is
     * the data we want to populate into the lists
     * @param context The current context. Used to inflate the layout file.
     * @param bookLists A list of Book object to dispaly in a list
     */
    public BookListAdapter(Context context, List<BookList> bookLists) {
        //refer to parent's constructor
        super(context,0,bookLists);
        this.bookListList = new ArrayList<>(bookLists);
        this.fList=new ArrayList<>(bookLists);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_specific, parent, false);
        }

        //Get the BookList object located at this position in the list
        BookList customList = getItem(position);
        //Locate TextView of Books title
        TextView title = (TextView) convertView.findViewById(R.id.title);
        //Locate TextView of Books publisher
        TextView author = (TextView) convertView.findViewById(R.id.publisher);
        //Set this text in the Title TextView
        title.setText(customList.getmTitle());
        //Set this text in the Publisher TextView
        author.setText(customList.getmPublisher());

        //Return the whole list item layout, so that it can be shown in the ListView
        return convertView;
    }

    /**
     * Returns a filter that can be used to constrain Books Title with a filtering pattern
     */
    @NonNull
    @Override
    public Filter getFilter() {
        if (filter==null) {
            filter = new BookTitleFilter();
        }
        return filter;
    }


    /**
     * Create a custom Filter class to filter Books title
     */
    private class BookTitleFilter extends Filter {

        /**
         *Invoked in a worker thread to filter the data according to the constraint
         * @param constraint
         * @return the results that will then be published in the UI thread through
         * publishResults()
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //Holds the results of a filtering operation. The results are the values computed by the
            //filtering operating and the number of these values.
            FilterResults results = new FilterResults();
            //Convert the constraint to String with lowercase
            String prefix = constraint.toString().toLowerCase();

            if (prefix==null||prefix.length()==0) {
                ArrayList<BookList> list = new ArrayList<BookList>(bookListList);
                //Contains all the values computed by the filtering operation
                results.values=list;
                //Contains the number of values computed by the filtering operation
                results.count=list.size();
            }else {
                final ArrayList<BookList> list = new ArrayList<BookList>(bookListList);
                final ArrayList<BookList> nlist = new ArrayList<BookList>();
                int count = list.size();

                for (int i=0; i<count;i++) {
                    final BookList booklist = list.get(i);
                    final String value = booklist.getmTitle().toLowerCase();

                    if (value.startsWith(prefix)){
                        nlist.add(booklist);
                    }
                }
                results.values=nlist;
                results.count=nlist.size();
            }
            return results;
        }

        /**
         * Invoked in the UI thread to publish the filtering results in the user interface.
         * @param constraint the constraint used to filter the data
         * @param results the results of the filtering operation
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            fList = (ArrayList<BookList>)results.values;

            clear();
            int count = fList.size();
            for (int i = 0; i<count;i++){
                BookList booklist=(BookList) fList.get(i);
                add(booklist);
            }
        }
    }
}
