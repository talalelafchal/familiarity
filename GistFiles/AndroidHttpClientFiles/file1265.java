public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<MovieListItem> mMovieList = new ArrayList<>();
	private Context mContext;
	private boolean mShowBomberCount;

	public MovieListAdapter(Context context, List<MovieListItem> mMovieList, boolean showBomberCount) {
		this.mContext = context;
		this.mMovieList = mMovieList;
		this.mShowBomberCount = showBomberCount;
	}

	public List<MovieListItem> getMovieList() {
		return mMovieList;
	}

	public void setMovieList(List<MovieListItem> mMovieList) {
		this.mMovieList = mMovieList;
	}

	@Override
	public int getItemViewType(int position) {
		return R.layout.card_movie_list;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new MovieListItemHolder(LayoutInflater.from(parent.getContext())
			.inflate(viewType, parent, false), this.mShowBomberCount);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
		MovieListItemHolder holder = (MovieListItemHolder)viewHolder;
			
		final MovieListItem movieItem = this.mMovieList.get(p);
		holder.mTextMovieName.setText(movieItem.getTitleChinese());
		String thumbnailPath = movieItem.getThumbnailPath();

		holder.mImageMovieCover.setImageResource(R.drawable.img_empty);
		if (!thumbnailPath.isEmpty())
			Picasso.with(holder.mImagePoster.getContext())
					.load(MainActivity.getResizePhoto(this.mContext, thumbnailPath))
					.error(R.drawable.img_empty)
					.into(holder.mImagePoster);
		if (movieItem.getPhotoLists().size() > 0) {
			String coverUrl = movieItem.getPhotoLists().get(
					(int) (Math.random() * movieItem.getPhotoLists().size())).getPath();
			Picasso.with(holder.mImageMovieCover.getContext())
					.load(MainActivity.getResizePhoto(this.mContext, coverUrl))
					.error(R.drawable.img_empty)
					.into(holder.mImageMovieCover);
		}
		holder.mTextDuration.setText(this.mContext.getResources().getString(R.string.text_duration)
				+ ": " + movieItem.getDuration());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
		holder.mTextReleaseDate.setText(this.mContext.getResources().getString(R.string.text_release_date)
				+ ": " + dateFormat.format(movieItem.getReleaseDate()));

		holder.mRipple.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMovieDetail(movieItem.getId(), movieItem.getTitleChinese());
			}
		});
		holder.mButtonOrder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showMovieDetail(movieItem.getId(), movieItem.getTitleChinese());
			}
		});

		holder.mTextGoodBomber.setText(String.valueOf(movieItem.getGoodBomber()));
		holder.mTextNormalBomber.setText(String.valueOf(movieItem.getNormalBomber()));
		holder.mTextBadBomber.setText(String.valueOf(movieItem.getBadBomber()));
		holder.mProgressGoodBomber.setProgress((int) (movieItem.getGoodRate() * 100));
		holder.mProgressNormalBomber.setProgress((int) (movieItem.getNormalRate() * 100));
		holder.mProgressBadBomber.setProgress((int) (movieItem.getBadRate() * 100));
	}

	private void showMovieDetail(int id, String name) {
		GAApplication.getTracker(mContext).send(new HitBuilders.AppViewBuilder()
				.build());
		Intent movieDetail = new Intent(mContext, MovieDetailActivity.class);
		movieDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, id);
		movieDetail.putExtra(MovieDetailActivity.EXTRA_MOVIE_NAME, name);
		Activity activity = (Activity) mContext;
		activity.startActivity(movieDetail);
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public int getItemCount() {
		return this.mMovieList.size();
	}

	class MovieListItemHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.card_movie_list_item)
		CardView mCardMovie;
		@InjectView(R.id.ripple)
		MaterialRippleLayout mRipple;
		@InjectView(R.id.layout_bomber_count)
		View mLayoutBomberCount;

		@InjectView(R.id.image_movie_cover)
		ImageView mImageMovieCover;

		@InjectView(R.id.image_movie_poster)
		ImageView mImagePoster;

		@InjectView(R.id.text_movie_name)
		TextView mTextMovieName;

		@InjectView(R.id.text_release_date)
		TextView mTextReleaseDate;
		@InjectView(R.id.text_duration)
		TextView mTextDuration;
		@InjectView(R.id.text_good_bomber)
		TextView mTextGoodBomber;
		@InjectView(R.id.progress_good_bomber)
		ArcProgress mProgressGoodBomber;
		@InjectView(R.id.text_normal_bomber)
		TextView mTextNormalBomber;
		@InjectView(R.id.progress_normal_bomber)
		ArcProgress mProgressNormalBomber;
		@InjectView(R.id.text_bad_bomber)
		TextView mTextBadBomber;
		@InjectView(R.id.progress_bad_bomber)
		ArcProgress mProgressBadBomber;
		@InjectView(R.id.button_order)
		Button mButtonOrder;

		MovieListItemHolder(View itemView, boolean showBomberCount) {
			super(itemView);
			ButterKnife.inject(this, itemView);
			if (showBomberCount)
				this.mLayoutBomberCount.setVisibility(View.VISIBLE);
			else
				this.mLayoutBomberCount.setVisibility(View.GONE);
		}
	}
}
