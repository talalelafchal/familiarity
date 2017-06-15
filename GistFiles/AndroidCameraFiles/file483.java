package com.example.jenny.myapplication.client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.jenny.myapplication.data.Photo;
import com.example.jenny.myapplication.service.ImageServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jennybaotranla@yahoo.com (Jenny La)
 *
 * Photo adapter to display photo recycler view.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoItemViewHolder> {

    /**
     * Provide a reference to the views for each data item
     */
    public class PhotoItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PhotoListItem item;

        public PhotoItemViewHolder(PhotoListItem photoListItem) {
            super(photoListItem);
            this.item = photoListItem;
        }

        @Override
        public void onClick(View v) {
            item.onClick(v);
        }
    }

    private Context context;
    private List<Photo> photos;
    private ImageServiceImpl imageService;

    public PhotoAdapter(Context context, ImageServiceImpl imageService) {
        this.context = context;
        this.imageService = imageService;
        photos = new ArrayList<>();
    }

    public PhotoAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public PhotoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PhotoListItem v = new PhotoListItem(parent.getContext(), imageService);
        PhotoItemViewHolder vh = new PhotoItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PhotoItemViewHolder holder, int position) {
        holder.item.setPhotoData(photos.get(position));
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.item.onClick(view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
