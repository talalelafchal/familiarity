    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {

        Log.v(LOG_TAG, movies.get(i).getMovie_img_url());

        Glide.with(context).load(movies.get(i).getMovie_img_url()).error(R.drawable.ic_camera_black_24dp).into(viewHolder.img_android);
    }