@Singleton
public class AppJobCreator implements JobCreator
{
    @Inject
    Map<String, Provider<Job>> jobs;

    @Inject
    AppJobCreator ()
    {
    }

    @Override
    public Job create (String tag)
    {
        Provider<Job> jobProvider = jobs.get( tag );
        return jobProvider != null ? jobProvider.get() : null;
    }
}