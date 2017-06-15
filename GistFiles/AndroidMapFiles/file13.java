@Singleton
public class BookService
{
    private final JobManager jobManager;

    @Inject
    public BookService (JobManager jobManager)
    {
        this.jobManager = jobManager;
    }

    void submitBook (String name)
    {
        jobManager.schedule( SendBookRequestJob.buildJobRequest( name ) );
    }
  
}