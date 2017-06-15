@Module
public class JobsModule
{
    @Provides
    @Singleton
    JobManager provideJobManager (Application application, AppJobCreator jobCreator)
    {
        JobManager.create( application ).addJobCreator( jobCreator );
        return JobManager.instance();
    }

    @Provides @IntoMap
    @StringKey( SendBookRequestJob.JOB_TAG )
    Job provideSendBookRequestJob (BookApi api, Bus bus)
    {
        return new SendBookRequestJob( api, bus );
    }
  
}