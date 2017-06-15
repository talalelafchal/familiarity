package software.is.com.icommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import software.is.com.icommunity.R;
import software.is.com.icommunity.model.Post;
import software.is.com.icommunity.model.PostGroup;


public class GroupBasesAdapter extends android.widget.BaseAdapter implements AdapterView.OnClickListener {

    private Context context;
    public ArrayList<PostGroup> list = new ArrayList<PostGroup>();

    public GroupBasesAdapter(Context context, ArrayList<PostGroup> list) {
        this.context = context;
        this.list = list;
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


        ViewHolder mViewHolder = null;

        if (convertView == null) {

            LayoutInflater mInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_my_group, parent, false);

            mViewHolder = new ViewHolder(convertView);

            PostGroup item = list.get(position);


            mViewHolder.title.setText(item.getPost().get(position).getGroup_name());

//            Picasso.with(context)
//                    .load(item.getpAvatar())
//                    .transform(new RoundedTransformation(50, 4))
//                    .resize(100, 100)
//                    .into(mViewHolder.avatar);


        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    @Override
    public void onClick(View view) {

    }

    public class ViewHolder {


        TextView title;

        public ViewHolder(View row) {
            title = (TextView) row.findViewById(R.id.title_my_group);

        }
    }



}

