import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class ListItemAdapter extends ArrayAdapter<Item> {

	// ViewHolder pattern
	static class ViewHolder {
		public TextView nameElect;
		public TextView potencia;
		public TextView duracion;
		public ImageView icon;
	}

	private Context mContext;
	private ArrayList<Item> mItemList;
	private int mItemLayout;
	private ImageLoader mImageLoader;

	public ListElectAdapter(Context context, int resource,
			ArrayList<Item> mItemList) {
		super(context, resource, mItemList);

		this.mContext = context;
		this.mItemList = mItemList;
		this.mItemLayout = resource;
		this.mImageLoader=ImageLoader.getInstance();
		mImageLoader=ImageLoader.getInstance();
		 options = new DisplayImageOptions.Builder()
	    .showStubImage(R.drawable.ic_launcher)
	    .showImageForEmptyUri(R.drawable.ic_launcher)
	    .cacheOnDisc()
	    .cacheInMemory()
	    .build();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		// If the convertview is null needs to be inflated, if not null it can
		// be reuse it
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_item, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameElect = (TextView) rowView
					.findViewById(R.id.txt1);
			viewHolder.icon = (ImageView) rowView
					.findViewById(R.id.imgLstElect);
			viewHolder.potencia = (TextView) rowView
					.findViewById(R.id.txt2);
			viewHolder.duracion = (TextView) rowView
					.findViewById(R.id.txt3);
			// Store the viewHolder in the tag of the view
			rowView.setTag(viewHolder);
		}
		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Item item = mItemList.get(position);

		holder.nameElect.setText(item.get1());
		holder.potencia.setText(item.get2()+"");
		holder.duracion.setText(item.get3()+"");
		// Load image, decode it to Bitmap and display Bitmap in ImageView (or
		// any other view  which implements ImageAware interface)
		//Using universal-imager-loader lib
		if(item.getFoto()!=null&&! item.getFoto().equals("")){
			holder.icon.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(item.getFoto(), holder.icon,options);
		}else{
			holder.icon.setVisibility(View.GONE);
		}

		return rowView;
	}

	public void changeDataSet(ArrayList<Item> arrayList) {
		mItemList.clear();
		mItemList.addAll(arrayList);
		notifyDataSetChanged();
	}

}
