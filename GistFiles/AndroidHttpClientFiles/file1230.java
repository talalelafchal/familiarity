package com.example.wisatajogja;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{

	static class Holder
	{
		TextView	title, detail;
		ImageView	thumb;
	}

	private List<E_Lokasi>	listLokasi;
	private LayoutInflater	inflater;
	private ImageLoader		imageLoader;

	public MyAdapter(Context context, List<E_Lokasi> listLokasi,
			ImageLoader imageLoader)
	{
		this.listLokasi = listLokasi;
		this.imageLoader = imageLoader;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return listLokasi.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return listLokasi.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Holder holder;
		if (convertView == null)
		{

			convertView = inflater.inflate(R.layout.single_row, null);
			holder = new Holder();
			holder.title = (TextView) convertView.findViewById(R.id.titleRow);
			holder.detail = (TextView) convertView.findViewById(R.id.detailRow);
			holder.thumb = (ImageView) convertView.findViewById(R.id.thumbRow);

			convertView.setTag(holder);

		} else
		{
			holder = (Holder) convertView.getTag();
		}
		holder.title.setText(listLokasi.get(position).getNama());
		holder.detail.setText(listLokasi.get(position).getAlamat());
		imageLoader.displayImage(listLokasi.get(position).getGambar(),
				holder.thumb, ImageUtils.getImgOpt());

		return convertView;
	}

}
