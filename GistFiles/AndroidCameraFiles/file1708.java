package com.example.androidrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.androidrecyclerview.R;
import com.example.androidrecyclerview.dao.PhotoItemCollectionDao;
import com.example.androidrecyclerview.dao.PhotoItemDao;
import com.example.androidrecyclerview.manager.Contextor;
import com.example.androidrecyclerview.view.PhotoListItem;
import com.example.androidrecyclerview.view.PhotoListViewHolder;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListViewHolder> {
    PhotoItemCollectionDao dao;
    int lastPosition = -1;
    int counter = 1;

    public void setDao(PhotoItemCollectionDao dao) {
        this.dao = dao;
    }

    @Override
    public PhotoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("ANDRCY", "Create ViewHolder " + counter);
        counter++;
        return new PhotoListViewHolder(new PhotoListItem(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(PhotoListViewHolder holder, int position) {
        PhotoItemDao dao = (PhotoItemDao) getItem(position);
        holder.item.setNameText(dao.getCaption());
        holder.item.setNameDescription(dao.getUsername() + "\n" + dao.getCamera());
        holder.item.setImageUrl(dao.getImageUrl());

        // List view item animation
        if(position > lastPosition) {
            Animation anim = AnimationUtils.loadAnimation(Contextor.getInstance().getContext(), R.anim.up_from_bottom);
            holder.item.setAnimation(anim);
            lastPosition = position;
        }
    }

    private Object getItem(int position) {
        return dao.getData().get(position);
    }

    @Override
    public int getItemCount() {
        if(dao == null)
            return 0;
        if(dao.getData() == null)
            return 0;
        return dao.getData().size();
    }

    public void increaseLastPosition(int amount) {
        lastPosition += amount;
    }
}