public class MovieListFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_TAB_POSITION = "TAB_POSITION";

	@InjectView(R.id.list_movie)
	SuperRecyclerView mListMovie;

	private MovieListAdapter mAdapter;
	private int mCurrentTab = 0;
	private int mCurrentPage = 1;
	private boolean mLoadingMore = false;
	private boolean mShouldLoadMore = false;

	public static MovieListFragment newInstance(int currentTabPosition) {
		MovieListFragment fragment = new MovieListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TAB_POSITION, currentTabPosition);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			this.mCurrentTab = getArguments().getInt(ARG_TAB_POSITION);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
		this.initView(rootView);
		this.loadMovieList();
		return rootView;
	}

	private void initView(View rootView) {
		ButterKnife.inject(this, rootView);
		this.mListMovie.getSwipeToRefresh().setColorSchemeResources(
				R.color.primary,
				R.color.accent,
				android.R.color.holo_orange_dark);
		final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		this.mListMovie.setLayoutManager(layoutManager);
		this.mListMovie.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (mAdapter != null)
					mAdapter.getMovieList().clear();
				mCurrentPage = 1;
				loadMovieList();
			}
		});
		this.mListMovie.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (mShouldLoadMore && newState == RecyclerView.SCROLL_STATE_IDLE && !mLoadingMore) {
					mListMovie.showMoreProgress();
					loadMovieList();
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
				int visibleItemCount = layoutManager.getChildCount();
				int totalItemCount = layoutManager.getItemCount();
				mShouldLoadMore = (firstVisibleItem + visibleItemCount == totalItemCount);
			}
		});
	}

	private void loadMovieList() {
		AsyncHttpClient httpClient = new AsyncHttpClient();
		String url = formatMovieListRequest();
		Logger.d(ApiTask.API_LOG_TAG, url);
		httpClient.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				Logger.json(ApiTask.API_LOG_TAG, response.toString());
				List<MovieListItem> movieList = new ArrayList<>();
				Gson gson = new Gson();
				if (mAdapter == null) {
					mAdapter = new MovieListAdapter(new ArrayList<MovieListItem>());
					mListMovie.setAdapter(mAdapter);
				}
				mListMovie.getSwipeToRefresh().setRefreshing(false);
				mListMovie.hideMoreProgress();
				try {
					JSONArray objects = response.getJSONArray(ApiTask.RESPONSE_OBJECTS);
					if (objects.length() > 0) {
						for (int i = 0; i < objects.length(); i++)
							movieList.add(gson.fromJson(objects.getJSONObject(i).toString(), MovieListItem.class));
						mAdapter.getMovieList().addAll(movieList);
					} else {
						if (mAdapter.getMovieList().size() <= 0)
							Toast.makeText(getActivity(), getResources().getString(R.string.warn_no_movies),
									Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getActivity(), getResources().getString(R.string.warn_no_more_movies),
									Toast.LENGTH_SHORT).show();
						return;
					}
				}
				catch (JSONException e) {
					this.onFailure(statusCode, headers, e, response);
				}

				mAdapter.notifyDataSetChanged();
				mCurrentPage++;
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Logger.e((Exception)throwable);

				mListMovie.getSwipeToRefresh().setRefreshing(false);
				mListMovie.hideMoreProgress();
			}
		});
	}

	private String formatMovieListRequest() {
		JSONObject q = new JSONObject();
		try {
			JSONArray filters = new JSONArray();
			JSONObject filter = new JSONObject();
			filter.put(Query.PARAM_NAME, Query.FIELD_RELEASE_STATUS);
			filter.put(Query.PARAM_OP, Query.OPERATOR_EQUAL);
			filter.put(Query.PARAM_VAL, MovieListTab.values()[this.mCurrentTab]);
			filters.put(filter);
			JSONArray orderBy = new JSONArray();
			JSONObject dateSort = new JSONObject();
			dateSort.put(Query.PARAM_FIELD, Query.FIELD_RELEASE_DATE);
			dateSort.put(Query.PARAM_DIRECTION, Query.OPERATOR_DESC);
			orderBy.put(dateSort);
			q.put(Query.PARAM_FILTERS, filters);
			q.put(Query.PARAM_ORDER_BY, orderBy);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		Resources res = this.getActivity().getResources();
		try {
			return String.format("%s%s%s?q=%s&%s=%d", res.getString(R.string.host),
					res.getString(R.string.api_root), res.getString(R.string.api_movie_list),
					URLEncoder.encode(q.toString(), "UTF8"),
					Query.PARAM_PAGE, this.mCurrentPage);
		}
		catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}