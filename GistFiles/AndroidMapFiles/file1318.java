public class MyFavoriteActivity extends AppCompatActivity {

    private ProgressBar listProgressBar;
    TextView mNotificationTextView;
    RecyclerView mListMovieRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= M)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, null));
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listProgressBar = (ProgressBar) findViewById(R.id.list_movie_progressbar);
        mNotificationTextView = (TextView) findViewById(R.id.notification_textview);

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_my_favorite));

        mListMovieRecyclerView = (RecyclerView) findViewById(R.id.list_movie_recyclerview);


    }

}