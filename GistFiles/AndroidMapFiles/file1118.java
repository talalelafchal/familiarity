@Singleton
@Component(
        modules = {
                AppModule.class, JobsModule.class
        }
)
public interface AppComponent
{
    Application getApplication ();

    JobManager getJobManager ();
}