public class SendBookRequestJob extends Job
{
    static final String JOB_TAG = "send_book_request_tag";

    private static final String PARAM_BOOK_NAME = "book_name";

    private final BookApi api;
    private final Bus bus;

    @Inject
    SendBookRequestJob (BookApi api, Bus bus)
    {
        this.api = api;
        this.bus = bus;
    }

    @NonNull
    @Override
    protected Result onRunJob (final Params params)
    {
        PersistableBundleCompat extras = params.getExtras();
        String name = extras.getString( PARAM_BOOK_NAME, "" );

        if ( submitRequest( name ) )
        {
            return Result.SUCCESS;
        }

        return Result.FAILURE;
    }

    boolean submitRequest (String name)
    {
        api.submitBook( name ).enqueue( new Callback<Book>()
        {
            @Override
            public void onResponse (Call<Book> call, Response<Book> response)
            {
                if ( response.isSuccessful() )
                {
                    bus.post( new SubmissionSuccessEvent( response.body() ) );
                }
            }

            @Override
            public void onFailure (Call<Place> call, Throwable t)
            {
                bus.post( new SubmissionErrorEvent() );
            }
        } );

        return true;
    }

    public static JobRequest buildJobRequest (String name)
    {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString( PARAM_BOOK_NAME, name );

        return new JobRequest.Builder( SendBookRequestJob.JOB_TAG )
                             .setExecutionWindow( 5_000L, 10_000L )
                             .setBackoffCriteria( 20_000L, JobRequest.BackoffPolicy.EXPONENTIAL )
                             .setRequiredNetworkType( JobRequest.NetworkType.CONNECTED )
                             .setExtras( extras )
                             .setRequirementsEnforced( true )
                             .setPersisted( true )
                             .build();
    }
}