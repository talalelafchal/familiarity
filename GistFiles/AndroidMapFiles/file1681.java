package putugunation.com.mapsroute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import putugunation.com.mapsroute.R;
import putugunation.com.mapsroute.models.Data;


/**
 * Created by gunaputu on 20/10/2015.
 */
public class AdapterListVIew extends ArrayAdapter<Data> {

    private Context context;
    int layoutResourceId;
    private List<Data> listData;
    private List<String> list;

    LayoutInflater inflater;
    private ArrayList<Data> arraylist;

    public AdapterListVIew(Context context, int layoutResourceId, List<Data> listData, List<String> list) {
        super(context, layoutResourceId, listData);
        this.list= list;
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listData  = listData;

        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(listData);

    }

    public AdapterListVIew(Context context, int layoutResourceId, List<Data> listData) {
        super(context, layoutResourceId, listData);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.listData  = listData;

        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(listData);

    }

    public View getView(int position,View view,ViewGroup parent){

        Data datas = listData.get(position);

        Holder holder ;
        if(view == null){
            holder =new Holder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_item_data,parent, false);

            holder.name = (TextView) view.findViewById(R.id.name_of_data);
            holder.distance = (TextView) view.findViewById(R.id.distance);
            holder.duration = (TextView) view.findViewById(R.id.duration);
            holder.latitude = (TextView) view.findViewById(R.id.latitude);
            holder.longitude = (TextView) view.findViewById(R.id.longitude);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.name.setText(datas.getModified());
        holder.duration.setText("Duration from current position, it's about " + datas.getLegs().getDuration().getText());
        holder.distance.setText("from current position, it's about " + datas.getLegs().getDistance().getText());
        holder.latitude.setText("Latitude : " + datas.getLatitude());
        holder.longitude.setText("Longitude : " + datas.getLongitude());

        return view;
    }


    static class Holder{
        private TextView name;
        private TextView distance;
        private TextView duration;
        private TextView latitude;
        private TextView longitude;
    }



    @Override
    public Data getItem(int position) {

        if(this.listData != null) {
            this.list.get(position);
        } else {
            return null;
        }


        return  null;
    }


    //Filter  for mSearch function
  /*  public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        listData.clear();
        if (charText.length() == 0) {
            listData.addAll(arraylist);
        } else {
            for (int i=0; i<listData.size(); i++) {
                if (listData.get(i).getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    listData.add(listData.get(i));
                }
            }
        }
        notifyDataSetChanged();
    }*/
}