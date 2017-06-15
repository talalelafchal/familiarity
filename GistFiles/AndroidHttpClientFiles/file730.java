

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by TalErez on 17/08/2016.
 */
public class SearchAdapt extends ArrayAdapter<String> {

    public SearchAdapt(Context context, int resource,List<String> objects) {
        super(context, resource,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater=LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.searchitem, null);
        }


        TextView tx=(TextView)convertView.findViewById(R.id.textView6);
        String object=getItem(position);
        tx.setText(object.toString());

        return convertView;
    }
}
