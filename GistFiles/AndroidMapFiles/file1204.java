/**
 * Contains bindings to ensure the usability of {@code dagger.android} framework classes. This
 * module should be installed in the component that is used to inject the {@link
 * android.app.Application} class.
 */
@Beta
@Module
public abstract class AndroidInjectionModule {
  @Multibinds
  abstract Map<Class<? extends Activity>, AndroidInjector.Factory<? extends Activity>>
      activityInjectorFactories();

  @Multibinds
  abstract Map<Class<? extends Fragment>, AndroidInjector.Factory<? extends Fragment>>
      fragmentInjectorFactories();

  @Multibinds
  abstract Map<Class<? extends Service>, AndroidInjector.Factory<? extends Service>>
      serviceInjectorFactories();

  @Multibinds
  abstract Map<
          Class<? extends BroadcastReceiver>, AndroidInjector.Factory<? extends BroadcastReceiver>>
      broadcastReceiverInjectorFactories();

  private AndroidInjectionModule() {}
}
