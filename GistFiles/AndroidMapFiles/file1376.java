public class LostMobile extends Activity {

    // [... snip ...]

    mMainRecycler = (RecyclerView) findViewById(R.id.mainList);
    mMainRecycler.setHasFixedSize(true);
    mMainRecycler.setLayoutManager(new LinearLayoutManager(this));
    mMainListAdapter = new MyAdapter(this, remoteMap);
    // [... snip ...]
    mMainRecycler.setAdapter(mMainListAdapter);

    mBeforeRecycler = (RecyclerView) findViewById(R.id.beforeItemsList);
    mBeforeRecycler.setLayoutManager(new LinearLayoutManager(this));
    mBeforeItemsAdapter = new MiniStatesAdapter(this, remoteMap);
    mBeforeRecycler.setAdapter(mBeforeItemsAdapter);

    mAfterRecycler = (RecyclerView) findViewById(R.id.afterItemsList);
    mAfterRecycler.setLayoutManager(new LinearLayoutManager(this));
    mAfterItemsAdapter = new MiniStatesAdapter(this, remoteMap);
    mAfterRecycler.setAdapter(mAfterItemsAdapter);

    // [... snip ...]
}
