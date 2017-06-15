///
///Your adapter code
///

    public View getView(int position, View convertView, ViewGroup parent) {
        
        //example of usage
        //let holder.image be some ImageView, mContentUri some video file Uri
        new VideoThumbnailLoader(getContext(), holder.image, MediaStore.Images.Thumbnails.MICRO_KIND).execute(mContentUri);
        
    }
///