import android.app.Application;
import android.os.Build;
import annotations.*;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.util.Modules;
import com.squareup.otto.Bus;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.runners.model.InitializationError;
import roboguice.RoboGuice;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;

/**
 * A test runner that gives you dependency injection via RoboGuice,
 * as well as handles custom annotations applied to test methods.
 */
public class RobolectricTestRunnerWithInjection extends RobolectricTestRunner {
    private static final int SDK_INT = Build.VERSION.SDK_INT;
    private ModuleWrapper moduleWrapper = new TestRunnerModule();

    public RobolectricTestRunnerWithInjection(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    final public void beforeTest(Method method) {

        /**
         * Method annotations
         */
        Annotation[] methodAnnotations = method.getAnnotations();
        for (Annotation annotation : methodAnnotations) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            if (AndroidVersion.class == annotationClass) {
                setAndroidVersion((AndroidVersion) annotation);
            }
            if (Bind.class == annotationClass) {
                Bind bind = (Bind) annotation;
                moduleWrapper.bind(bind.from(), bind.to());
            }
            if (BindMultiple.class == annotationClass) {
                BindMultiple bindMultiple = (BindMultiple) annotation;
                Class<?>[] from = bindMultiple.from();
                Class<?>[] to = bindMultiple.to();
                for (int i = 0; i < from.length; i++) {
                    moduleWrapper.bind(from[i], to[i]);
                }
            }
            if (UsesScreenSize.class == annotationClass) {
                UsesScreenSize usesScreenSize = (UsesScreenSize)annotation;
                try {
                    ScreenSize screenSize = usesScreenSize.value();
                    Robolectric.getShadowApplication().getResourceLoader().setLayoutQualifierSearchPath(screenSize.getPath());
                    Robolectric.getShadowApplication().getResources().getConfiguration().screenLayout = screenSize.getConfigurationSize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    final public void prepareTest(Object test) {
        Annotation[] classAnnotations = test.getClass().getAnnotations();
        for (Annotation annotation : classAnnotations) {
            Class<? extends Annotation> annotationClass = annotation.annotationType();
            if (LoadsLibraryResources.class == annotationClass) {
                LoadsLibraryResources loadsLibraryResources = (LoadsLibraryResources) annotation;
                try {
                    Robolectric.getShadowApplication().getResourceLoader().loadLibraryProjectResources(new File(loadsLibraryResources.filePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (BindModule.class == annotationClass) {
                BindModule bindModule = (BindModule) annotation;
                try {
                    Class<? extends ModuleWrapper> theClass = bindModule.value();
                    ModuleWrapper theOldBinder = moduleWrapper;
                    ModuleWrapper theNewBinder;
                    boolean isInnerClass = theClass.isMemberClass();
                    boolean isStaticClass = Modifier.isStatic(theClass.getModifiers());
                    if (isInnerClass && !isStaticClass) {
                        Constructor<? extends ModuleWrapper> theClassConstructor =
                                theClass.getConstructor(theClass.getEnclosingClass());
                        theNewBinder = theClassConstructor.newInstance(test);
                    } else {
                        Constructor<? extends ModuleWrapper> theClassConstructor = theClass.getConstructor();
                        theNewBinder = theClassConstructor.newInstance();
                    }

                    theNewBinder.configure();
                    theNewBinder.overrideBindings(theOldBinder);
                    moduleWrapper = theNewBinder;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Application injectedApplication = Robolectric.application;
        Injector injector = RoboGuice.getInjector(injectedApplication);
        RoboGuice.setBaseApplicationInjector(injectedApplication, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(injectedApplication)).with(moduleWrapper.getModule()));
        injector.injectMembers(test);
    }

    @Override
    final public void afterTest(Method method) {
        resetStaticState();
        moduleWrapper.clearBindings();
    }

    @Override
    protected void resetStaticState() {
        setStaticValue(Build.VERSION.class, "SDK_INT", SDK_INT);
    }

    private void setAndroidVersion(AndroidVersion androidVersion) {
        final int targetSdkVersion = androidVersion.value();
        setStaticValue(Build.VERSION.class, "SDK_INT", targetSdkVersion);
    }

    private class TestRunnerModule extends ModuleWrapper {

        @Override
        public void configure() {
            // No modules bound in default implementation
        }
    }

    public static abstract class ModuleWrapper {
        private Map<Class, Class> classToClassBindings = new HashMap<Class, Class>();
        private Map<Class, Object> classToInstanceBindings = new HashMap<Class, Object>();

        private AbstractModule module = new WrappedModule();

        final public AbstractModule getModule() {
            return module;
        }

        final public void clearBindings() {
            classToClassBindings.clear();
            classToInstanceBindings.clear();
        }

        // TODO: make a binding builder so this reads more natural
        final public void bind(Class from, Class to) {
            classToClassBindings.put(from, to);
        }

        // TODO: make a binding builder so this reads more natural
        final public void bind(Class from, Object toInstance) {
            classToInstanceBindings.put(from, toInstance);
        }

        final public void overrideBindings(ModuleWrapper that) {
            /**
             * Remove previously bound keys from both lists, so
             * we can properly override the bindings, or we might
             * end up with duplicates
             */
            Set<Class> classKeys = new HashSet<Class>();
            classKeys.addAll(that.classToClassBindings.keySet());
            classKeys.addAll(that.classToInstanceBindings.keySet());
            classToClassBindings.keySet().removeAll(classKeys);
            classToInstanceBindings.keySet().removeAll(classKeys);

            // Now add everything we're overriding
            classToClassBindings.putAll(that.classToClassBindings);
            classToInstanceBindings.putAll(that.classToInstanceBindings);
        }

        public abstract void configure();

        private class WrappedModule extends AbstractModule {
            @Override
            final protected void configure() {

                for (Map.Entry<Class, Class> entry : classToClassBindings.entrySet()) {
                    bind(entry.getKey()).to(entry.getValue());
                }

                for (Map.Entry<Class, Object> entry : classToInstanceBindings.entrySet()) {
                    bind(entry.getKey()).toInstance(entry.getValue());
                }
            }
        }
    }
}
