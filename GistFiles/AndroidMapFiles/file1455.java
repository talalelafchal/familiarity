public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> implements View.OnClickListener {

    private ArrayList<Photo> photos;
    public ArrayList<Photo> photosToRemove;
    private OnItemClickListener onItemClickListener;

    private Context context;
   

    public PhotoAdapter(Context context, ArrayList<Photo> photos, Map<Integer, String> authors, int colHeigth) {
        this.photos = photos;
        this.context = context;
        this.photosToRemove = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myphotos, parent, false);
        v.setOnClickListener(this);
        BusProvider.getInstance().register(this);
        PhotoViewHolder photoViewHolder = new PhotoViewHolder(v);
        return photoViewHolder;
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        String tempUrl = context.getFilesDir() + UrlBuilder.localCoverPhoto(photo.remoteId);

        Glide.with(context)
                .load(tempUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.photoCover);

        holder.itemView.setTag(photo);
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.clear(holder.photoCover);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onClick(final View v) {
        // Give some time to the ripple to finish the effect
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    onItemClickListener.onItemClick(v, (Photo) v.getTag());
                }
            }, 50);
        }

    }

    public void onItemRemove(final RecyclerView.ViewHolder viewHolder, final RecyclerView mRecyclerView) {

        final int adapterPosition = viewHolder.getAdapterPosition();
        final Photo mPhoto = photos.get(adapterPosition);

        Snackbar snackbar = Snackbar
                .make(mRecyclerView, context.getString(R.string.removed_from_device), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int mAdapterPosition = viewHolder.getAdapterPosition();
                        photos.add(mAdapterPosition, mPhoto);
                        notifyItemInserted(mAdapterPosition);
                        mRecyclerView.scrollToPosition(mAdapterPosition);
                        photosToRemove.remove(mPhoto);
                    }
                });
        snackbar.show();
        photos.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        photosToRemove.add(mPhoto);
    }

    public static class PhotoViewHolder extends  RecyclerView.ViewHolder {
        ImageView photoCover;
    
        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoCover = (ImageView) itemView.findViewById(R.id.catalog_photo_cover);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(View view, Photo photo);

    }
}