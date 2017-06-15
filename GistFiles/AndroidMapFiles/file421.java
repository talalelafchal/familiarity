@Module(subcomponents = {
        TestMainActivitySubComponent.class,
        TestSecondaryActivitySubComponent.class
})
public abstract class TestBindingModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(TestMainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(SecondaryActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainSecondaryInjectorFactory(TestSecondaryActivitySubComponent.Builder builder);
}