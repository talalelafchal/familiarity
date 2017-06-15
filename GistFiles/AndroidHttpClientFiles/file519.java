public class MovieDetailActivity extends ActionBarActivity
		implements View.OnClickListener, ObservableScrollViewCallbacks {

	public static final String EXTRA_MOVIE_ID = "MOVIE_ID";
	public static final String EXTRA_MOVIE_NAME = "MOVIE_NAME";
	public static final String EXTRA_MOVIE_DETAIL = "MOVIE_DETAIL";
	public static final String EXTRA_MOVIE_COVER = "MOVIE_COVER";
	public static final String EXTRA_MOVIE_POSTER = "MOVIE_POSTER";

	@InjectView(R.id.toolbar)
	Toolbar mToolbar;
	@InjectView(R.id.scroll)
	ObservableScrollView mScrollView;
	@InjectView(R.id.image_photo)
	ImageView mImage;
	@InjectView(R.id.text_title_chinese)
	TextView mTextTitleChinese;
	@InjectView(R.id.text_release_date)
	TextView mTextReleaseDate;
	@InjectView(R.id.text_description)
	TextView mTextDescription;
	@InjectView(R.id.text_duration)
	TextView mTextDuration;
	@InjectView(R.id.text_director)
	TextView mTextDirector;
	@InjectView(R.id.text_actors)
	TextView mTextActors;
	@InjectView(R.id.view_genre1)
	ViewStub mViewGenre1;
	@InjectView(R.id.view_genre2)
	ViewStub mViewGenre2;
	@InjectView(R.id.view_genre3)
	ViewStub mViewGenre3;
	@InjectView(R.id.button_read_more)
	Button mButtonReadMore;

	private MaterialDialog mProgressDialog;

	@InjectView(R.id.fab_order)
	FloatingActionButton mFabOrder;
	boolean mIsFabOrderShow = true;
	@InjectView(R.id.button_photo)
	Button mButtonPhoto;
	@InjectView(R.id.button_trailer)
	Button mButtonTrailer;
	@InjectView(R.id.button_comment)
	Button mButtonComment;

	private int mMovieId;
	private MovieInfo mMovieInfo;
	private int mParallaxImageHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window w = getWindow(); // in Activity's onCreate() for instance
			w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		setContentView(R.layout.activity_movie_detail_v1);
		ButterKnife.inject(this);
		this.setSupportActionBar(this.mToolbar);
		if (this.getSupportActionBar() != null) {
			this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			this.getSupportActionBar().setTitle("");
		}

		this.mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0,
				this.getResources().getColor(R.color.primary)));
		this.mScrollView.setScrollViewCallbacks(this);
		this.mParallaxImageHeight = this.getResources().getDimensionPixelOffset(R.dimen.parallax_image_height);
		this.setupButtonIcon();
		if (getIntent() != null) {
			this.mMovieId = this.getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
			this.mButtonPhoto.setOnClickListener(this);
			this.mButtonTrailer.setOnClickListener(this);
			this.mButtonComment.setOnClickListener(this);
			this.mFabOrder.setOnClickListener(this);
			this.queryMovieDetail();
		}
	}

	private void queryMovieDetail() {
		mProgressDialog = new MaterialDialog.Builder(this)
				.title(getResources().getString(R.string.app_name_chinese))
				.content(R.string.loading)
				.progress(true, 0)
				.cancelable(false)
				.show();
		AsyncHttpClient httpClient = new AsyncHttpClient();
		httpClient.get(String.format("%s%s/%s/%d", this.getResources().getString(R.string.api_host),
				this.getResources().getString(R.string.api_root),
				MovieInfo.TABLE_NAME, this.mMovieId), new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Gson gson = new Gson();
				mMovieInfo = gson.fromJson(response.toString(), MovieInfo.class);
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				displayMovieDetail(mMovieInfo);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				Logger.e(ApiTask.API_LOG_TAG, (Exception) throwable);
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				Toast.makeText(MovieDetailActivity.this,
						getResources().getString(R.string.error_fail_to_load_detail),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	private void displayMovieDetail(final MovieInfo movieInfo) {
		if (movieInfo.getPhotoList().size() > 0) {
			int index = (int) (Math.random() * movieInfo.getPhotoList().size());
			String url = movieInfo.getPhotoList().get(index).getUrl();
			Picasso.with(this.mImage.getContext())
					.load(url)
					.into(this.mImage);
		}
		this.mTextTitleChinese.setText(movieInfo.getTitleChinese());
		this.mTextReleaseDate.setText(this.getResources().getString(R.string.text_release_date) +
				": " + movieInfo.getReleaseDate());
		this.mTextDescription.setText(movieInfo.getDescription().length() > 100 ?
				movieInfo.getDescription().substring(0, 100) + "..." : movieInfo.getDescription());
		this.mButtonReadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Logger.wtf(String.valueOf(mTextDescription.getText().toString().length()));
				if (mTextDescription.getText().toString().replace("...", "").length() > 100) {
					mTextDescription.setText(movieInfo.getDescription().substring(0, 100) + "...");
					mButtonReadMore.setText(getResources().getString(R.string.text_more));
				} else {
					mTextDescription.setText(movieInfo.getDescription());
					mButtonReadMore.setText(getResources().getString(R.string.text_less));
				}
			}
		});
		this.mTextDuration.setText(this.getResources().getString(R.string.text_duration) +
				": " + movieInfo.getDuration());

		if (movieInfo.getGenreList().size() > 0) {
			View genreView = this.mViewGenre1.inflate();
			TextView textGenre = (TextView) genreView.findViewById(R.id.text_genre);
			textGenre.setText(movieInfo.getGenreList().get(0).getGenre());
		}
		if (movieInfo.getGenreList().size() > 1) {
			View genreView = this.mViewGenre2.inflate();
			TextView textGenre = (TextView) genreView.findViewById(R.id.text_genre);
			textGenre.setText(movieInfo.getGenreList().get(1).getGenre());
		}
		if (movieInfo.getGenreList().size() > 2) {
			View genreView = this.mViewGenre3.inflate();
			TextView textGenre = (TextView) genreView.findViewById(R.id.text_genre);
			textGenre.setText(movieInfo.getGenreList().get(2).getGenre());
		}
		this.mTextDirector.setText(movieInfo.getDirector());
		if (movieInfo.getActorList().size() > 0) {
			StringBuilder actors = new StringBuilder();
			for (int i = 0; i < movieInfo.getActorList().size(); i++) {
				actors.append(String.format("%d. %s", i + 1, movieInfo.getActorList().get(i).getActorName()));
				if (i < movieInfo.getActorList().size() - 1)
					actors.append("\n");
			}
			this.mTextActors.setText(actors.toString());
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.button_trailer:
				if (!mMovieInfo.getTrailerList().isEmpty()) {
					Intent trailerIntent = new Intent(this, TrailerActivity.class);
					trailerIntent.putParcelableArrayListExtra(TrailerActivity.EXTRA_TRAILER_LIST,
							mMovieInfo.getTrailerList());
					int coverIndex = (int) (Math.random() * mMovieInfo.getPhotoList().size());
					trailerIntent.putExtra(EXTRA_MOVIE_COVER, mMovieInfo.getPhotoList().get(coverIndex).getUrl());
					trailerIntent.putParcelableArrayListExtra(PhotoListActivity.EXTRA_PHOTO_LIST,
							mMovieInfo.getPhotoList());
					trailerIntent.putExtra(EXTRA_MOVIE_DETAIL, mMovieInfo);
					startActivity(trailerIntent);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
				break;
			case R.id.button_photo:
				if (!mMovieInfo.getPhotoList().isEmpty()) {
					Intent photoList = new Intent(MovieDetailActivity.this, PhotoListActivity.class);
					photoList.putExtra(PhotoListActivity.EXTRA_POSTER, mMovieInfo.getThumbnailPath());
					photoList.putParcelableArrayListExtra(PhotoListActivity.EXTRA_PHOTO_LIST,
							mMovieInfo.getPhotoList());
					startActivity(photoList);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
				break;

			case R.id.button_comment:
				Intent intent = new Intent(MovieDetailActivity.this, CommentActivity.class);
				intent.putParcelableArrayListExtra(PttCommentFragment.EXTRA_PTT_COMMENTS,
						mMovieInfo.getArticleList());
				intent.putExtra(EXTRA_MOVIE_NAME, mMovieInfo.getTitleChinese());
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;

			case R.id.fab_order:
				if (mMovieInfo.getTrailerList().size() >= 1) {
					Intent trailerIntent = YouTubeIntents.createPlayVideoIntentWithOptions(this,
							TrailerActivity.getVideoId(mMovieInfo.getTrailerList().get(0).getUrl()),
							true, true);
					startActivity(trailerIntent);
				}
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
		MenuItem shareItem = menu.findItem(R.id.menu_item_share);
		ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
		if (mShareActionProvider != null)
			mShareActionProvider.setShareIntent(getShareIntent());
		else {
			mShareActionProvider = new ShareActionProvider(this);
			mShareActionProvider.setShareIntent(getShareIntent());
			MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	private Intent getShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_message) +
						"[" + this.getIntent().getStringExtra(EXTRA_MOVIE_NAME) + "]" +
						" - from " + this.getResources().getString(R.string.app_name_chinese) + "app" +
						"\n" + getResources().getString(R.string.app_link)
				);
		return intent;
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		int primaryColor = this.getResources().getColor(R.color.primary);
		float alpha = 1 - (float) Math.max(0, mParallaxImageHeight - 1.5 * scrollY) / mParallaxImageHeight;
		this.mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, primaryColor));
		if (mMovieInfo != null) {
			if (alpha > 0.95) {
				this.mToolbar.setTitle(mMovieInfo.getTitleChinese());
				this.mTextTitleChinese.setVisibility(View.INVISIBLE);
//				ViewPropertyAnimator.animate(this.mTextTitleChinese).alpha(0.0f).setDuration(10).start();
			} else {
				this.mToolbar.setTitle("");
				this.mTextTitleChinese.setVisibility(View.VISIBLE);
//				ViewPropertyAnimator.animate(this.mTextTitleChinese).alpha(1.0f).setDuration(10).start();
			}
		}

		// handle image parallex scroll
		ViewHelper.setTranslationY(this.mImage, scrollY / 4);

		// translate FAB

//		System.out.printf("%d, %d\n", scrollY, this.mToolbar.getHeight());
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		if (scrollY < mToolbar.getHeight() + metrics.density * 70) {
			if (!this.mIsFabOrderShow) {
				ViewPropertyAnimator.animate(this.mFabOrder).cancel();
				ViewPropertyAnimator.animate(this.mFabOrder).scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
				this.mIsFabOrderShow = true;
			}
		} else {
			if (this.mIsFabOrderShow) {
				ViewPropertyAnimator.animate(this.mFabOrder).cancel();
				ViewPropertyAnimator.animate(this.mFabOrder).scaleX(0f).scaleY(0f).setDuration(200).start();
				this.mIsFabOrderShow = false;
			}
		}
	}

	@Override
	public void onDownMotionEvent() {

	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {

	}


	private void setupButtonIcon() {
		int iconSize = 72;
		IconicFontDrawable goodBomberIcon = new IconicFontDrawable(this);
		goodBomberIcon.setIcon("gmd-dashboard");
		goodBomberIcon.setIconColor(this.getResources().getColor(android.R.color.black));
		goodBomberIcon.setBounds(0, 0, iconSize, iconSize);
		mButtonPhoto.setCompoundDrawables(null, goodBomberIcon, null, null);

		IconicFontDrawable normalBomberIcon = new IconicFontDrawable(this);
		normalBomberIcon.setIcon("gmd-local-movies");
		normalBomberIcon.setIconColor(this.getResources().getColor(android.R.color.black));
		normalBomberIcon.setBounds(0, 0, iconSize, iconSize);
		mButtonTrailer.setCompoundDrawables(null, normalBomberIcon, null, null);

		IconicFontDrawable badBomberIcon = new IconicFontDrawable(this);
		badBomberIcon.setIcon("gmd-thumbs-up-down");
		badBomberIcon.setIconColor(this.getResources().getColor(android.R.color.black));
		badBomberIcon.setBounds(0, 0, iconSize, iconSize);
		mButtonComment.setCompoundDrawables(null, badBomberIcon, null, null);
	}
}
