package com.example.ContactForm;

/**
 * Created with IntelliJ IDEA.
 * User: I
 * Date: 11.02.13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ContactForm.ImageUtils.ImageLoader;

public class AvatarListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public AvatarListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        if(position%2==0)vi.setBackgroundResource(R.drawable.list_background);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        HashMap<String, String> avatar = new HashMap<String, String>();
        avatar = data.get(position);

        // Setting all values in listview
        title.setText(avatar.get(AvatarListActivity.KEY_TITLE));
        imageLoader.DisplayImage(avatar.get(AvatarListActivity.KEY_THUMB_URL), thumb_image);
        return vi;
    }
}