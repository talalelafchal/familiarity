package com.bitgrind.android.guice;

import com.google.common.base.Throwables;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A static utility class which servces as the Guice entry point for application
 * initialization on Android.
 * <p>
 * Modules are loaded based on class names specified in the string resource
 * "modules". The resource is located based on the application package name. If
 * the resource is from a different, the resource id may be statically
 * initialized using {@link #setModulesResourceId(int)} to bypass name
 * resolution.
 */
public class Bootstrap {
  private static final List<Module> overrides = new ArrayList<Module>(1);
  private static Injector injector;
  private static int modulesResourceId;

  protected Bootstrap() {}

  /**
   * Provides the resource id of a string-array containing the class names of
   * modules to install. This can be used to work around resource location
   * problems when the resource is not in the same package as the application.
   * This happens when the aplication is compiled with the
   * "--rename-manifest-package" flag.
   *
   * @param id a resource id corresponding to a string-array
   */
  public static void setModulesResourceId(int id) {
    modulesResourceId = id;
  }

  /**
   * Provides a module which will be installed as an override to any normally
   * configured modules. THe bindings in this module are permitted to replace
   * any existing bindings. This is intended primary for use in testing to
   * provide for injection of mocked dependencies.
   *
   * @param module a module which will be used for overriding bindings
   */
  public static void addOverrideModule(Module module) {
    overrides.add(module);
  }

  /**
   * Inject members (fields and methods) in the provided Context (Activity,
   * Service, etc) after creating the Injector, if necessary.
   *
   * @param context the injection target
   */
  public static synchronized void inject(Context context) {
    if (injector == null) {
      injector = createInjector(context);
    }
    injector.injectMembers(context);
  }

  /**
   * Returns the Injector after creating it, if necessary.
   *
   * @param context any context of the application
   * @return the Injector
   */
  public static synchronized Injector getInjector(Context context) {
    if (injector == null) {
      injector = createInjector(context);
    }
    return injector;
  }

  /**
   * Destroy any previously created Injector so that the next call to
   * {@link #getInjector(Context)} or {@link #inject(Context)} will recreate the
   * Injector. This is normally only of interest in testing scenarios.
   */
  public static synchronized void reset() {
    injector = null;
    overrides.clear();
    modulesResourceId = 0;
  }

  private static Injector createInjector(Context context) {
    List<Module> modules = initializeModules(context);
    modules.add(new BaseModule(context));
    if (!overrides.isEmpty()) {
      return Guice.createInjector(Modules.override(modules).with(overrides));
    }
    return Guice.createInjector(modules);
  }

  private static List<Module> initializeModules(Context context) {
    final List<Module> modules = new ArrayList<Module>();
    Resources resources = context.getResources();
    if (modulesResourceId == 0) {
      modulesResourceId = resources.getIdentifier("modules", "array", context.getPackageName());
    }
    if (modulesResourceId != 0) {
      String[] moduleClasses = resources.getStringArray(modulesResourceId);
      Map<String, Exception> failedModules = new HashMap<String, Exception>(moduleClasses.length);
      if (moduleClasses != null) {
        for (String className : moduleClasses) {
          try {
            modules.add(Class.forName(className).asSubclass(Module.class).newInstance());
          } catch (InstantiationException e) {
            failedModules.put(className, e);
          } catch (IllegalAccessException e) {
            failedModules.put(className, e);
          } catch (ClassNotFoundException e) {
            failedModules.put(className, e);
          }
        }
        if (!failedModules.isEmpty()) {
          StringBuilder description =
              new StringBuilder("Failed to instantiate the following modules:\n");
          for (Entry<String, Exception> entry : failedModules.entrySet()) {
            description.append("Module['").append(entry.getKey()).append("']:\n");
            description.append(Throwables.getStackTraceAsString(entry.getValue())).append("\n");
          }
          throw new IllegalArgumentException(description.toString());
        }
      }
    }
    return modules;
  }

  private static class BaseModule implements Module {
    private Context context;

    public BaseModule(Context context) {
      this.context = context;
    }

    @Override
    public void configure(Binder binder) {
      binder.bind(Context.class).toInstance(context.getApplicationContext());
      binder.bind(Application.class).toInstance((Application) context.getApplicationContext());
    }
  }
}
