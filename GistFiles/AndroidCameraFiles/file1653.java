package sagarpreet97.reminder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sagarpreet chadha on 19-07-2016.
 */
public class ListView_Adapter extends ArrayAdapter<Reminder_listview_data> {

    ArrayList<Reminder_listview_data> mData;
    Context context;


    public ListView_Adapter(Context context, ArrayList<Reminder_listview_data> objects) {
        super(context, 0, objects);
        mData = objects;
        this.context = context;
    }

//    @Override
//    public int getViewTypeCount() {
//        return 1;
//    }

//    @Override
//    public int getItemViewType(int position) {
//        return
//    }

    @Override
    public int getCount() {

//        HashMap<String, ArrayList<Task>> map = new HashMap<>();
//        for (String key : map.keySet()) {
//
//        }

        return (mData.size()) ;
    }

//    private static class BatchHolder {
//        TextView batchNameTextView;
//        TextView instructorNameTextView;
//        TextView numStudentsTextView;
//        ImageView imageView;
//
//        public BatchHolder(TextView batchNameTextView, TextView instructorNameTextView, TextView numStudentsTextView, ImageView imageView) {
//            this.batchNameTextView = batchNameTextView;
//            this.instructorNameTextView = instructorNameTextView;
//            this.numStudentsTextView = numStudentsTextView;
//            this.imageView = imageView;
//        }
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View v = LayoutInflater.from(context).inflate(R.layout.listview_layout ,
                        parent, false);

        ImageView imageView=(ImageView)v.findViewById(R.id.imageView) ;
        TextView title=(TextView)v.findViewById(R.id.mtitle) ;
        TextView desc=(TextView)v.findViewById(R.id.mdescription) ;
        Reminder_listview_data temp=mData.get(position) ;
        String mtitle , mdesc ;

        Bitmap bm=temp.getBm() ;
        mtitle=temp.getTitle() ;
        mdesc=temp.getDesc() ;
        if(bm!=null) {
            imageView.setImageBitmap(bm);
        }

        title.setText(mtitle) ;
        desc.setText(mdesc) ;

        return v;
    }
}
