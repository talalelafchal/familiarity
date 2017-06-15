package com.example.homework1003;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<ArticleDTO> {
    private int resource;

  public CustomAdapter(Context context, int resource, List<ArticleDTO> objects) {
		super(context, resource, objects);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		convertView = View.inflate(getContext(), this.resource, null);

		TextView titleView = (TextView) convertView.findViewById(R.id.titleView);
		TextView contentView = (TextView) convertView.findViewById(R.id.contentView);
		TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
//		ImageView thumbnailsImageView = (ImageView) convertView.findViewById(R.id.thumbnailsImageView);

		ArticleDTO item = getItem(position);

		titleView.setText(item.getTitle());
		contentView.setText(item.getContent());
		dateView.setText(item.getDate());
//		Uri uri = Uri.parse(item.getImageUrl());
//		thumbnailsImageView.setImageURI(uri);

		return convertView;
	}
}