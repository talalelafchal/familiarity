// Attach the listener to the AdapterView onCreate
yourListView.setOnScrollListener(new InfiniteScrollListener(5) {
    @Override
    public void loadMore(int page, int totalItemsCount) {
        List<HashMap<String, String>> newData = loader.loadData();
        dataList.addAll(newData);
        adapter.notifyDataSetChanged();
    }
});