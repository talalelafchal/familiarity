public class PhotoTouchHelper  extends ItemTouchHelper.SimpleCallback {
    private PhotoAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public GuideTouchHelper(PhotoAdapter adapter, RecyclerView recyclerView){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = adapter;
        mRecyclerView = recyclerView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemRemove(viewHolder, mRecyclerView);
    }
}