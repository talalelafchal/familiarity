package com.devahoy.sample.ahoygson;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Post> mPosts;
    private ViewHolder mViewHolder;

    private Bitmap mBitmap;
    private Post mPost;
    private Activity mActivity;

    public CustomAdapter(Activity activity, List<Post> posts) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mPosts = posts;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.post, parent, false);
            mViewHolder = new ViewHolder();
            mViewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.post_thumbnail);
            mViewHolder.author = (TextView) convertView.findViewById(R.id.post_author);
            mViewHolder.title = (TextView) convertView.findViewById(R.id.post_title);
            mViewHolder.date = (TextView) convertView.findViewById(R.id.post_date);

            convertView.setTag(mViewHolder);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }


        mPost = mPosts.get(position);

        if (mPost.getThumbnail() != null) {



            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        URL url = new URL(mPost.getThumbnail());
                        mBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } catch (MalformedURLException e) {

                    } catch (IOException e) {

                    }
                    return null;
                }
            }.execute();


            mViewHolder.thumbnail.setImageBitmap(mBitmap);
        }

        // ถ้าใช้ Picasso ก็ uncomment ข้างล้างนี้ แล้วลบ AsyncTask ออก
        // Picasso.with(mActivity)
        //    .load(mPost.getThumbnail())
        //    .into(mViewHolder.thumbnail);

        mViewHolder.author.setText(mPost.getAuthor());
        mViewHolder.title.setText(mPost.title);
        mViewHolder.date.setText(mPost.date);

        return convertView;
    }

    private static class ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView author;
        TextView date;
    }
}
