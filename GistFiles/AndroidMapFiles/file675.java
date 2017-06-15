public class PhotoFragment extends Fragment implements PhotoAdapter.OnItemClickListener {

    public static final String TAG = "PhotoFragment";
    private int colsNr;
    private ArrayList<Photo> photos;
    PhotoAdapter mAdapter;

    @Bind(R.id.catalog_rv)
    RecyclerView recyclerView;

    @Bind(R.id.catalog_empty_view)
    LinearLayout emptyView;

  
    public static PhotoFragment newInstance() {
        PhotoFragment fragment = new PhotoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colsNr = getResources().getInteger(R.integer.main_grid_columns);
   
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_store_main, container, false);
        ButterKnife.bind(this, view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), colsNr);
        recyclerView.setLayoutManager(gridLayoutManager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        photos = (ArrayList<Photo>) Photo.getAllDownloaded();
        refreshAdapter();
        if (photos.isEmpty()) {
            showEmptyView();
        }
        else {
            hideEmptyView();
            mAdapter = new PhotoAdapter(getContext(), photos, authorIndex, colHeigth);
            mAdapter.setOnItemClickListener(this);
            recyclerView.swapAdapter(mAdapter, false);
            ItemTouchHelper.Callback callback = new PhotoTouchHelper(mAdapter, recyclerView);
            ItemTouchHelper helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onPause() {
        ArrayList<Integer> photoIds = new ArrayList<>();
        if (mAdapter != null) {
            if (mAdapter.photosToRemove != null) {
                for (Photo photo: mAdapter.photosToRemove){

                    photoIds.add(photo.remoteId);
                }
                Intent mServiceIntent = new Intent(getActivity(), PhotoRemoveService.class);
                mServiceIntent.putExtra(Constants.ARG_GUIDES_TO_DELETE, photoIds);
                getActivity().startService(mServiceIntent);
            }
        }

        super.onPause();
    }

   
    private void showEmptyView() {
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Subscribe
    public void onEmptyPhotos(MyPhotosEmptyEvent event) {
        Log.d(TAG, "list is empty");
        showEmptyView();
    }


    @Override
    public void onItemClick(View view, Photo photo) {
        //@TODO do some action
    }

   
}