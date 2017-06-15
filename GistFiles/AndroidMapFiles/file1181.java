private class RecyclerViewMapViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MapViewListItemView mapViewListItemView = new MapViewListItemView(getActivity());
	    mapViewListItemView.mapViewOnCreate(null);
        mMapViewListItemViews.add(mapViewListItemView);
        return new MapViewHolder(mapViewListItemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MapViewHolder mapViewHolder = (MapViewHolder) holder;
        mapViewHolder.mapViewListItemViewOnResume();
    }
}