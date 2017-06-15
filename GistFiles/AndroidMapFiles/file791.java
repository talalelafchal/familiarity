@Module(subcomponents = {
        MainActivitySubComponent.class,
        SecondaryActivitySubComponent.class
})
public abstract class BindingModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(MainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(SecondaryActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainSecondaryInjectorFactory(SecondaryActivitySubComponent.Builder builder);
}