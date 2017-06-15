diff -ru 0.5/android/support/test/filters/SdkSuppress.java 0.6-alpha/android/support/test/filters/SdkSuppress.java
--- 0.5/android/support/test/filters/SdkSuppress.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/filters/SdkSuppress.java	2016-08-02 22:18:50.000000000 +0900
@@ -21,12 +21,20 @@
 import java.lang.annotation.Target;
 
 /**
- * Indicates that a specific test or class requires a minimum API Level to execute.
+ * Indicates that a specific test or class requires a minimum or maximum API Level to execute.
  * <p/>
- * Test(s) will be skipped when executed on android platforms less than specified level.
+ * Test(s) will be skipped when executed on android platforms less/more than specified level
+ * (inclusive).
  */
 @Retention(RetentionPolicy.RUNTIME)
 @Target({ElementType.TYPE, ElementType.METHOD})
 public @interface SdkSuppress {
-    int minSdkVersion();
+    /**
+     * The minimum API level to execute (inclusive)
+     */
+    int minSdkVersion() default 1;
+    /**
+     * The maximum API level to execute (inclusive)
+     */
+    int maxSdkVersion() default Integer.MAX_VALUE;
 }
Only in 0.6-alpha/android/support/test/internal/runner: AndroidLogOnlyBuilder.java
diff -ru 0.5/android/support/test/internal/runner/AndroidRunnerBuilder.java 0.6-alpha/android/support/test/internal/runner/AndroidRunnerBuilder.java
--- 0.5/android/support/test/internal/runner/AndroidRunnerBuilder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/AndroidRunnerBuilder.java	2016-08-02 22:18:50.000000000 +0900
@@ -26,6 +26,7 @@
 import org.junit.internal.builders.IgnoredBuilder;
 import org.junit.internal.builders.JUnit3Builder;
 import org.junit.internal.builders.JUnit4Builder;
+import org.junit.runner.Runner;
 import org.junit.runners.model.RunnerBuilder;
 
 /**
@@ -37,6 +38,8 @@
     private final AndroidJUnit4Builder mAndroidJUnit4Builder;
     private final AndroidSuiteBuilder mAndroidSuiteBuilder;
     private final AndroidAnnotatedBuilder mAndroidAnnotatedBuilder;
+    private final AndroidLogOnlyBuilder mAndroidLogOnlyBuilder;
+
     // TODO: customize for Android ?
     private final IgnoredBuilder mIgnoredBuilder;
 
@@ -50,6 +53,7 @@
         mAndroidSuiteBuilder = new AndroidSuiteBuilder(runnerParams);
         mAndroidAnnotatedBuilder = new AndroidAnnotatedBuilder(this, runnerParams);
         mIgnoredBuilder = new IgnoredBuilder();
+        mAndroidLogOnlyBuilder = new AndroidLogOnlyBuilder(runnerParams);
     }
 
     @Override
@@ -76,4 +80,15 @@
     protected RunnerBuilder suiteMethodBuilder() {
         return mAndroidSuiteBuilder;
     }
+
+    @Override
+    public Runner runnerForClass(Class<?> testClass) throws Throwable {
+        // Check if this is a dry-run with -e log true argument passed to the runner.
+        Runner runner = mAndroidLogOnlyBuilder.runnerForClass(testClass);
+        if (runner != null) {
+            return runner;
+        }
+        // Otherwise use the default behaviour
+        return super.runnerForClass(testClass);
+    }
 }
diff -ru 0.5/android/support/test/internal/runner/RunnerArgs.java 0.6-alpha/android/support/test/internal/runner/RunnerArgs.java
--- 0.5/android/support/test/internal/runner/RunnerArgs.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/RunnerArgs.java	2016-08-02 22:18:50.000000000 +0900
@@ -14,6 +14,7 @@
 import java.io.FileNotFoundException;
 import java.io.FileReader;
 import java.io.IOException;
+import java.lang.ClassLoader;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
@@ -44,9 +45,11 @@
     static final String ARGUMENT_NOT_TEST_PACKAGE = "notPackage";
     static final String ARGUMENT_TIMEOUT = "timeout_msec";
     static final String ARGUMENT_TEST_FILE = "testFile";
+    static final String ARGUMENT_NOT_TEST_FILE = "notTestFile";
     static final String ARGUMENT_DISABLE_ANALYTICS = "disableAnalytics";
     static final String ARGUMENT_APP_LISTENER = "appListener";
     static final String ARGUMENT_IDLE = "idle";
+    static final String ARGUMENT_CLASS_LOADER = "classLoader";
 
     // used to separate multiple fully-qualified test case class names
     private static final char CLASS_SEPARATOR = ',';
@@ -73,9 +76,10 @@
     public final boolean disableAnalytics;
     public final List<ApplicationLifecycleCallback> appListeners;
     public final boolean idle;
+    public final ClassLoader classLoader;
 
     /**
-     * Encapsulates a test class and optional method
+     * Encapsulates a test class and optional method.
      */
     public static class TestArg {
         public final String testClassName;
@@ -112,6 +116,7 @@
         this.disableAnalytics = builder.disableAnalytics;
         this.appListeners = Collections.unmodifiableList(builder.appListeners);
         this.idle = builder.idle;
+        this.classLoader = builder.classLoader;
     }
 
     public static class Builder {
@@ -136,9 +141,10 @@
         private List<ApplicationLifecycleCallback> appListeners =
                 new ArrayList<ApplicationLifecycleCallback>();
         private boolean idle = false;
+        private ClassLoader classLoader = null;
 
         /**
-         * Populate the arg data from given Bundle
+         * Populate the arg data from the given Bundle.
          */
         public Builder fromBundle(Bundle bundle) {
             this.debug = parseBoolean(bundle.getString(ARGUMENT_DEBUG));
@@ -146,6 +152,8 @@
                     parseUnsignedInt(bundle.get(ARGUMENT_DELAY_IN_MILLIS), ARGUMENT_DELAY_IN_MILLIS);
             this.tests.addAll(parseTestClasses(bundle.getString(ARGUMENT_TEST_CLASS)));
             this.tests.addAll(parseTestClassesFromFile(bundle.getString(ARGUMENT_TEST_FILE)));
+            this.notTests.addAll(
+                    parseTestClassesFromFile(bundle.getString(ARGUMENT_NOT_TEST_FILE)));
             this.notTests.addAll(parseTestClasses(bundle.getString(ARGUMENT_NOT_TEST_CLASS)));
             this.listeners.addAll(parseAndLoadClasses(bundle.getString(ARGUMENT_LISTENER),
                     RunListener.class));
@@ -166,11 +174,13 @@
             this.codeCoveragePath = bundle.getString(ARGUMENT_COVERAGE_PATH);
             this.suiteAssignment = parseBoolean(bundle.getString(ARGUMENT_SUITE_ASSIGNMENT));
             this.idle = parseBoolean(bundle.getString(ARGUMENT_IDLE));
+            this.classLoader = parseAndLoadClass(bundle.getString(ARGUMENT_CLASS_LOADER),
+                    ClassLoader.class);
             return this;
         }
 
         /**
-         * Populate the arg data from the instrumentation:metadata attribute in Manifest
+         * Populate the arg data from the instrumentation:metadata attribute in Manifest.
          */
         public Builder fromManifest(Instrumentation instr) {
             PackageManager pm = instr.getContext().getPackageManager();
@@ -194,7 +204,7 @@
 
 
         /**
-         * Utility method to split String element data in CSV format into a List
+         * Utility method to split String element data in CSV format into a List.
          *
          * @return empty list if null input, otherwise list of strings
          */
@@ -206,7 +216,7 @@
         }
 
         /**
-         * Parse boolean value from a String
+         * Parse boolean value from a String.
          *
          * @return the boolean value, false on null input
          */
@@ -215,7 +225,7 @@
         }
 
         /**
-         * Parse int from given value - except either int or string
+         * Parse int from given value - except either int or string.
          *
          * @return the value, -1 if not found
          * @throws NumberFormatException if value is negative or not a number
@@ -233,7 +243,7 @@
         }
 
         /**
-         * Parse long from given value - except either Long or String
+         * Parse long from given value - except either Long or String.
          *
          * @return the value, -1 if not found
          * @throws NumberFormatException if value is negative or not a number
@@ -250,7 +260,7 @@
         }
 
         /**
-         * Parse test package data from given CSV data in following format
+         * Parse test package data from given CSV data in the following format:
          * com.android.foo,com.android.bar,...
          *
          * @return list of package names, empty list if input is null
@@ -266,7 +276,7 @@
         }
 
         /**
-         * Parse test class and method data from given CSV data in following format
+         * Parse test class and method data from given CSV data in following format:
          * com.TestClass1#method1,com.TestClass2,...
          *
          * @return list of TestArg data, empty list if input is null
@@ -282,7 +292,7 @@
         }
 
         /**
-         * Parse an individual test class and optionally method from given string
+         * Parse an individual test class and optionally method from given string.
          * <p/>
          * Expected format: com.TestClass1[#method1]
          */
@@ -298,7 +308,7 @@
         }
 
         /**
-         * Parse and load the content of a test file
+         * Parse and load the content of a test file.
          *
          * @param filePath path to test file containing full package names of test classes and
          *                 optionally methods to add.
@@ -330,9 +340,9 @@
         }
 
         /**
-         * Create a set of objects given a CSV string of full class names and type
+         * Create a set of objects given a CSV string of full class names and type.
          *
-         * @return the List of RunListeners or empty list on null input
+         * @return the List of objects or empty list on null input
          */
         private <T> List<T> parseAndLoadClasses(String classString, Class<T> type) {
             List<T> objects = new ArrayList<T>();
@@ -345,13 +355,30 @@
         }
 
         /**
+         * Create an object of the given full class name.
+         *
+         * @return the object instance or null on null input
+         */
+        private <T> T parseAndLoadClass(String classString, Class<T> type) {
+            List<T> classLoaders = parseAndLoadClasses(classString, type);
+            if (!classLoaders.isEmpty()) {
+                if (classLoaders.size() > 1) {
+                    throw new IllegalArgumentException(String.format(
+                        "Expected 1 class loader, %d given", classLoaders.size()));
+                }
+                return classLoaders.get(0);
+            }
+            return null;
+        }
+
+        /**
          * Load and add object given class string.
          * <p/>
          * No effect if input is null or empty.
          * <p/>
          *
          * @param objects the List to add to
-         * @param className the fully qualified class name\
+         * @param className the fully qualified class name
          *
          * @throws IllegalArgumentException if listener cannot be loaded
          */
diff -ru 0.5/android/support/test/internal/runner/TestRequestBuilder.java 0.6-alpha/android/support/test/internal/runner/TestRequestBuilder.java
--- 0.5/android/support/test/internal/runner/TestRequestBuilder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/TestRequestBuilder.java	2016-08-02 22:18:50.000000000 +0900
@@ -41,6 +41,7 @@
 import java.io.IOException;
 import java.lang.annotation.Annotation;
 import java.util.ArrayList;
+import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
@@ -55,10 +56,7 @@
  * restrictions.
  */
 public class TestRequestBuilder {
-
-    private static final String LOG_TAG = "TestRequestBuilder";
-
-    static final String EMULATOR_HARDWARE = "goldfish";
+    private static final String TAG = "TestRequestBuilder";
 
     // Excluded test packages
     private static final String[] DEFAULT_EXCLUDED_PACKAGES = {
@@ -273,11 +271,16 @@
 
         @Override
         protected boolean evaluateTest(Description description) {
-            final SdkSuppress s = getAnnotationForTest(description);
-            if (s != null && getDeviceSdkInt() < s.minSdkVersion()) {
-                return false;
+            final SdkSuppress sdkSuppress = getAnnotationForTest(description);
+            if (sdkSuppress != null) {
+                if (getDeviceSdkInt() >= sdkSuppress.minSdkVersion()
+                    && getDeviceSdkInt() <= sdkSuppress.maxSdkVersion()
+                    ) {
+                    return true; // run the test
+                }
+                return false; // don't run the test
             }
-            return true;
+            return true; // no SdkSuppress, run the test
         }
 
         private SdkSuppress getAnnotationForTest(Description description) {
@@ -304,7 +307,14 @@
     /**
      * Class that filters out tests annotated with {@link RequiresDevice} when running on emulator
      */
-    private class RequiresDeviceFilter extends AnnotationExclusionFilter {
+    @VisibleForTesting
+    class RequiresDeviceFilter extends AnnotationExclusionFilter {
+
+        static final String EMULATOR_HARDWARE_GOLDFISH = "goldfish";
+        static final String EMULATOR_HARDWARE_RANCHU = "ranchu";
+
+        private final Set<String> emulatorHardwareNames =
+                new HashSet<>(Arrays.asList(EMULATOR_HARDWARE_GOLDFISH, EMULATOR_HARDWARE_RANCHU));
 
         RequiresDeviceFilter() {
             super(RequiresDevice.class);
@@ -314,7 +324,7 @@
         protected boolean evaluateTest(Description description) {
             if (!super.evaluateTest(description)) {
                 // annotation is present - check if device is an emulator
-                return !EMULATOR_HARDWARE.equals(getDeviceHardware());
+                return !emulatorHardwareNames.contains(getDeviceHardware());
             }
             return true;
         }
@@ -405,30 +415,23 @@
     /**
      * A {@link Filter} to support the ability to filter out multiple class#method combinations.
      */
-    private static class ClassAndMethodFilter extends Filter {
+    private static class ClassAndMethodFilter extends ParentFilter {
 
         private Map<String, MethodFilter> mMethodFilters = new HashMap<>();
 
         @Override
-        public boolean shouldRun(Description description) {
+        public boolean evaluateTest(Description description) {
             if (mMethodFilters.isEmpty()) {
                 return true;
             }
-            if (description.isTest()) {
-                String className = description.getClassName();
-                MethodFilter methodFilter = mMethodFilters.get(className);
-                if (methodFilter != null) {
-                    return methodFilter.shouldRun(description);
-                }
-            } else {
-                // Check all children, if any
-                for (Description child : description.getChildren()) {
-                    if (shouldRun(child)) {
-                        return true;
-                    }
-                }
+            String className = description.getClassName();
+            MethodFilter methodFilter = mMethodFilters.get(className);
+            if (methodFilter != null) {
+                return methodFilter.shouldRun(description);
             }
-            return false;
+            // This test class was not explicitly excluded and none of it's test methods were
+            // explicitly included or excluded. Should be run, return true:
+            return true;
         }
 
         @Override
@@ -437,28 +440,28 @@
         }
 
         public void addMethod(String className, String methodName) {
-            MethodFilter mf = mMethodFilters.get(className);
-            if (mf == null) {
-                mf = new MethodFilter(className);
-                mMethodFilters.put(className, mf);
+            MethodFilter methodFilter = mMethodFilters.get(className);
+            if (methodFilter == null) {
+                methodFilter = new MethodFilter(className);
+                mMethodFilters.put(className, methodFilter);
             }
-            mf.add(methodName);
+            methodFilter.addInclusionMethod(methodName);
         }
 
         public void removeMethod(String className, String methodName) {
-            MethodFilter mf = mMethodFilters.get(className);
-            if (mf == null) {
-                mf = new MethodFilter(className);
-                mMethodFilters.put(className, mf);
+            MethodFilter methodFilter = mMethodFilters.get(className);
+            if (methodFilter == null) {
+                methodFilter = new MethodFilter(className);
+                mMethodFilters.put(className, methodFilter);
             }
-            mf.remove(methodName);
+            methodFilter.addExclusionMethod(methodName);
         }
     }
 
     /**
      * A {@link Filter} used to filter out desired test methods from a given class
      */
-    private static class MethodFilter extends Filter {
+    private static class MethodFilter extends ParentFilter {
 
         private final String mClassName;
         private Set<String> mIncludedMethods = new HashSet<>();
@@ -478,23 +481,19 @@
         }
 
         @Override
-        public boolean shouldRun(Description description) {
-            if (description.isTest()) {
-                String methodName = description.getMethodName();
-                // Parameterized tests append "[#]" at the end of the method names.
-                // For instance, "getFoo" would become "getFoo[0]".
-                methodName = stripParameterizedSuffix(methodName);
-                if (mExcludedMethods.contains(methodName)) {
-                    return false;
-                }
-                // don't filter out descriptions with method name "initializationError", since
-                // Junit will generate such descriptions in error cases, See ErrorReportingRunner
-                return mIncludedMethods.isEmpty() || mIncludedMethods.contains(methodName)
-                    || methodName.equals("initializationError");
+        public boolean evaluateTest(Description description) {
+            String methodName = description.getMethodName();
+            // Parameterized tests append "[#]" at the end of the method names.
+            // For instance, "getFoo" would become "getFoo[0]".
+            methodName = stripParameterizedSuffix(methodName);
+            if (mExcludedMethods.contains(methodName)) {
+                return false;
             }
-            // At this point, this could only be a description of this filter
-            return true;
-
+            // don't filter out descriptions with method name "initializationError", since
+            // Junit will generate such descriptions in error cases, See ErrorReportingRunner
+            return mIncludedMethods.isEmpty()
+                    || mIncludedMethods.contains(methodName)
+                    || methodName.equals("initializationError");
         }
 
         // Strips out the parameterized suffix if it exists
@@ -506,11 +505,11 @@
             return name;
         }
 
-        public void add(String methodName) {
+        public void addInclusionMethod(String methodName) {
             mIncludedMethods.add(methodName);
         }
 
-        public void remove(String methodName) {
+        public void addExclusionMethod(String methodName) {
             mExcludedMethods.add(methodName);
         }
     }
@@ -557,6 +556,16 @@
     }
 
     /**
+     * Instructs the test builder if JUnit3 suite() methods should be executed.
+     *
+     * @param ignoreSuiteMethods true to ignore all suite methods.
+     */
+    public TestRequestBuilder ignoreSuiteMethods(boolean ignoreSuiteMethods) {
+        mIgnoreSuiteMethods = ignoreSuiteMethods;
+        return this;
+    }
+
+    /**
      * Add a test class to be executed. All test methods in this class will be executed, unless a
      * test method was explicitly included or excluded.
      *
@@ -583,7 +592,6 @@
     public TestRequestBuilder addTestMethod(String testClassName, String testMethodName) {
         mIncludedClasses.add(testClassName);
         mClassMethodFilter.addMethod(testClassName, testMethodName);
-        mIgnoreSuiteMethods = true;
         return this;
     }
 
@@ -592,7 +600,6 @@
      */
     public TestRequestBuilder removeTestMethod(String testClassName, String testMethodName) {
         mClassMethodFilter.removeMethod(testClassName, testMethodName);
-        mIgnoreSuiteMethods = true;
         return this;
     }
 
@@ -631,7 +638,7 @@
             mFilter = mFilter.intersect(
                     new SizeFilter(forTestSize));
         } else {
-            Log.e(LOG_TAG, String.format("Unrecognized test size '%s'",
+            Log.e(TAG, String.format("Unrecognized test size '%s'",
                     forTestSize.getSizeQualifierName()));
         }
         return this;
@@ -728,6 +735,9 @@
         if (runnerArgs.logOnly) {
             setSkipExecution(true);
         }
+        if (runnerArgs.classLoader != null) {
+            setClassLoader(runnerArgs.classLoader);
+        }
         return this;
     }
 
@@ -813,7 +823,7 @@
         if (mApkPaths.isEmpty()) {
             throw new IllegalStateException("neither test class to execute or apk paths were provided");
         }
-        Log.i(LOG_TAG, String.format("Scanning classpath to find tests in apks %s",
+        Log.i(TAG, String.format("Scanning classpath to find tests in apks %s",
                 mApkPaths));
         ClassPathScanner scanner = createClassPathScanner(mApkPaths);
 
@@ -835,7 +845,7 @@
         try {
             return scanner.getClassPathEntries(filter);
         } catch (IOException e) {
-            Log.e(LOG_TAG, "Failed to scan classes", e);
+            Log.e(TAG, "Failed to scan classes", e);
         }
         return Collections.emptyList();
     }
@@ -855,9 +865,9 @@
             Class<?> clazz = Class.forName(className);
             return (Class<? extends Annotation>)clazz;
         } catch (ClassNotFoundException e) {
-            Log.e(LOG_TAG, String.format("Could not find annotation class: %s", className));
+            Log.e(TAG, String.format("Could not find annotation class: %s", className));
         } catch (ClassCastException e) {
-            Log.e(LOG_TAG, String.format("Class %s is not an annotation", className));
+            Log.e(TAG, String.format("Class %s is not an annotation", className));
         }
         return null;
     }
diff -ru 0.5/android/support/test/internal/runner/TestSize.java 0.6-alpha/android/support/test/internal/runner/TestSize.java
--- 0.5/android/support/test/internal/runner/TestSize.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/TestSize.java	2016-08-02 22:18:50.000000000 +0900
@@ -201,15 +201,22 @@
 
     @Override
     public boolean equals(Object o) {
-        if (this == o) return true;
-        if (o == null || getClass() != o.getClass()) return false;
+        if (this == o) {
+            return true;
+        }
+        if (o == null || getClass() != o.getClass()) {
+            return false;
+        }
+
         TestSize testSize = (TestSize) o;
-        return Objects.equals(mSizeQualifierName, testSize.mSizeQualifierName);
+
+        return mSizeQualifierName.equals(testSize.mSizeQualifierName);
+
     }
 
     @Override
     public int hashCode() {
-        return Objects.hash(mSizeQualifierName);
+        return mSizeQualifierName.hashCode();
     }
 
     private static boolean runTimeSmallerThanThreshold(float testRuntime, float runtimeThreshold) {
Only in 0.6-alpha/android/support/test/internal/runner: intercepting
diff -ru 0.5/android/support/test/internal/runner/junit3/AndroidJUnit3Builder.java 0.6-alpha/android/support/test/internal/runner/junit3/AndroidJUnit3Builder.java
--- 0.5/android/support/test/internal/runner/junit3/AndroidJUnit3Builder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit3/AndroidJUnit3Builder.java	2016-08-02 22:18:50.000000000 +0900
@@ -13,10 +13,11 @@
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
+
 package android.support.test.internal.runner.junit3;
 
-import android.util.Log;
 import android.support.test.internal.util.AndroidRunnerParams;
+import android.util.Log;
 
 import junit.framework.TestCase;
 
@@ -32,7 +33,8 @@
  */
 public class AndroidJUnit3Builder extends JUnit3Builder {
 
-    private static final String LOG_TAG = "AndroidJUnit3Builder";
+    private static final String TAG = "AndroidJUnit3Builder";
+
     private final AndroidRunnerParams mAndroidRunnerParams;
 
     /**
@@ -46,16 +48,12 @@
     public Runner runnerForClass(Class<?> testClass) throws Throwable {
         try {
             if (isJUnit3Test(testClass)) {
-                if (mAndroidRunnerParams.isSkipExecution()) {
-                    return new JUnit38ClassRunner(new NonExecutingTestSuite(testClass));
-                } else {
-                    return new JUnit38ClassRunner(
-                            new AndroidTestSuite(testClass, mAndroidRunnerParams));
-                }
+                return new JUnit38ClassRunner(
+                        new AndroidTestSuite(testClass, mAndroidRunnerParams));
             }
         } catch (Throwable e) {
             // log error message including stack trace before throwing to help with debugging.
-            Log.e(LOG_TAG, "Error constructing runner", e);
+            Log.e(TAG, "Error constructing runner", e);
             throw e;
         }
         return null;
diff -ru 0.5/android/support/test/internal/runner/junit3/AndroidSuiteBuilder.java 0.6-alpha/android/support/test/internal/runner/junit3/AndroidSuiteBuilder.java
--- 0.5/android/support/test/internal/runner/junit3/AndroidSuiteBuilder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit3/AndroidSuiteBuilder.java	2016-08-02 22:18:50.000000000 +0900
@@ -13,6 +13,7 @@
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
+
 package android.support.test.internal.runner.junit3;
 
 import android.util.Log;
@@ -33,6 +34,7 @@
 public class AndroidSuiteBuilder extends SuiteMethodBuilder {
 
     private static final String LOG_TAG = "AndroidSuiteBuilder";
+
     private final AndroidRunnerParams mAndroidRunnerParams;
 
     /**
@@ -55,12 +57,8 @@
                     throw new IllegalArgumentException(testClass.getName() +
                             "#suite() did not return a TestSuite");
                 }
-                if (mAndroidRunnerParams.isSkipExecution()) {
-                    return new JUnit38ClassRunner(new NonExecutingTestSuite((TestSuite) t));
-                } else {
-                    return new JUnit38ClassRunner(new AndroidTestSuite((TestSuite) t,
-                            mAndroidRunnerParams));
-                }
+                return new JUnit38ClassRunner(new AndroidTestSuite((TestSuite) t,
+                        mAndroidRunnerParams));
             }
         } catch (Throwable e) {
             // log error message including stack trace before throwing to help with debugging.
diff -ru 0.5/android/support/test/internal/runner/junit3/AndroidTestSuite.java 0.6-alpha/android/support/test/internal/runner/junit3/AndroidTestSuite.java
--- 0.5/android/support/test/internal/runner/junit3/AndroidTestSuite.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit3/AndroidTestSuite.java	2016-08-02 22:18:50.000000000 +0900
@@ -25,7 +25,6 @@
 
 import org.junit.Ignore;
 
-import java.util.Map;
 import java.util.concurrent.ExecutionException;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
diff -ru 0.5/android/support/test/internal/runner/junit4/AndroidAnnotatedBuilder.java 0.6-alpha/android/support/test/internal/runner/junit4/AndroidAnnotatedBuilder.java
--- 0.5/android/support/test/internal/runner/junit4/AndroidAnnotatedBuilder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit4/AndroidAnnotatedBuilder.java	2016-08-02 22:18:50.000000000 +0900
@@ -13,11 +13,10 @@
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
+
 package android.support.test.internal.runner.junit4;
 
 import android.support.annotation.VisibleForTesting;
-import android.support.test.internal.runner.junit3.JUnit38ClassRunner;
-import android.support.test.internal.runner.junit3.NonExecutingTestSuite;
 import android.support.test.runner.AndroidJUnit4;
 import android.support.test.internal.util.AndroidRunnerParams;
 import android.util.Log;
@@ -27,8 +26,6 @@
 import org.junit.runner.Runner;
 import org.junit.runners.model.RunnerBuilder;
 
-import static android.support.test.internal.util.AndroidRunnerBuilderUtil.isJUnit3Test;
-
 /**
  * A specialized {@link AnnotatedBuilder} that can Android runner specific features
  */
@@ -45,17 +42,9 @@
     @Override
     public Runner runnerForClass(Class<?> testClass) throws Exception {
         try {
-            // check if we need to skip execution and return an appropriate non executing runner
-            if (mAndroidRunnerParams.isSkipExecution()) {
-                if (isJUnit3Test(testClass)) {
-                    return new JUnit38ClassRunner(new NonExecutingTestSuite(testClass));
-                }
-                return new NonExecutingJUnit4ClassRunner(testClass);
-            }
-
             RunWith annotation = testClass.getAnnotation(RunWith.class);
             // check if its an Android specific runner otherwise default to AnnotatedBuilder
-            if (annotation != null && annotation.value().equals(AndroidJUnit4.class)) {
+            if (annotation != null && AndroidJUnit4.class.equals(annotation.value())) {
                 Class<? extends Runner> runnerClass = annotation.value();
                 try {
                     // try to build an AndroidJUnit4 runner
diff -ru 0.5/android/support/test/internal/runner/junit4/AndroidJUnit4Builder.java 0.6-alpha/android/support/test/internal/runner/junit4/AndroidJUnit4Builder.java
--- 0.5/android/support/test/internal/runner/junit4/AndroidJUnit4Builder.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit4/AndroidJUnit4Builder.java	2016-08-02 22:18:50.000000000 +0900
@@ -13,6 +13,7 @@
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
+
 package android.support.test.internal.runner.junit4;
 
 import android.util.Log;
@@ -28,7 +29,8 @@
  */
 public class AndroidJUnit4Builder extends JUnit4Builder {
 
-    private static final String LOG_TAG = "AndroidJUnit4Builder";
+    private static final String TAG = "AndroidJUnit4Builder";
+
     private final AndroidRunnerParams mAndroidRunnerParams;
 
     /**
@@ -41,16 +43,10 @@
     @Override
     public Runner runnerForClass(Class<?> testClass) throws Throwable {
         try {
-            // check if we need to skip execution and return an appropriate runner.
-            if (mAndroidRunnerParams.isSkipExecution()) {
-                // we don't check for junit3 here because the AndroidJUnit3Builder would already
-                // have picked up the test class. See AllDefaultPossibilitiesBuilder for details.
-                return new NonExecutingJUnit4ClassRunner(testClass);
-            }
             return new AndroidJUnit4ClassRunner(testClass, mAndroidRunnerParams);
         } catch (Throwable e) {
             // log error message including stack trace before throwing to help with debugging.
-            Log.e(LOG_TAG, "Error constructing runner", e);
+            Log.e(TAG, "Error constructing runner", e);
             throw e;
         }
     }
diff -ru 0.5/android/support/test/internal/runner/junit4/AndroidJUnit4ClassRunner.java 0.6-alpha/android/support/test/internal/runner/junit4/AndroidJUnit4ClassRunner.java
--- 0.5/android/support/test/internal/runner/junit4/AndroidJUnit4ClassRunner.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit4/AndroidJUnit4ClassRunner.java	2016-08-02 22:18:50.000000000 +0900
@@ -15,8 +15,13 @@
  */
 package android.support.test.internal.runner.junit4;
 
+import android.support.test.internal.runner.junit4.statement.RunAfters;
+import android.support.test.internal.runner.junit4.statement.RunBefores;
+import android.support.test.internal.runner.junit4.statement.UiThreadStatement;
 import android.support.test.internal.util.AndroidRunnerParams;
 
+import org.junit.After;
+import org.junit.Before;
 import org.junit.Test;
 import org.junit.internal.runners.statements.FailOnTimeout;
 import org.junit.runners.BlockJUnit4ClassRunner;
@@ -24,6 +29,9 @@
 import org.junit.runners.model.InitializationError;
 import org.junit.runners.model.Statement;
 
+import java.util.List;
+import java.util.concurrent.TimeUnit;
+
 
 /**
  * A specialized {@link BlockJUnit4ClassRunner} that can handle timeouts
@@ -39,19 +47,55 @@
     }
 
     /**
-     * Default to <a href="http://junit.org/javadoc/latest/org/junit/Test.html">
-     * <code>Test</code></a> level timeout if set. Otherwise, set the timeout that was passed to the
-     * instrumentation via argument
+     * Returns a {@link Statement} that invokes {@code method} on {@code test}
+     */
+    @Override
+    protected Statement methodInvoker(FrameworkMethod method, Object test) {
+        if (UiThreadStatement.shouldRunOnUiThread(method)) {
+            return new UiThreadStatement(super.methodInvoker(method, test), true);
+        }
+        return super.methodInvoker(method, test);
+    }
+
+    @Override
+    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
+        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
+                Before.class);
+        return befores.isEmpty() ? statement : new RunBefores(method, statement,
+                befores, target);
+    }
+
+    @Override
+    protected Statement withAfters(FrameworkMethod method, Object target, Statement statement) {
+        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
+                After.class);
+        return afters.isEmpty() ? statement : new RunAfters(method, statement, afters,
+                target);
+    }
+
+    /**
+     * Default to <a href="http://junit.org/javadoc/latest/org/junit/Test.html#timeout()">
+     * <code>org.junit.Test#timeout()</code></a> level timeout if set. Otherwise, set the timeout
+     * that was passed to the instrumentation via argument.
      */
     @Override
     protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
+        // test level timeout i.e @Test(timeout = 123)
         long timeout = getTimeout(method.getAnnotation(Test.class));
-        if (timeout > 0) {
-            return new FailOnTimeout(next, timeout);
-        } else if (mAndroidRunnerParams.getPerTestTimeout() > 0) {
-            return new FailOnTimeout(next, mAndroidRunnerParams.getPerTestTimeout());
+
+        // use runner arg timeout if test level timeout is not present
+        if (timeout <= 0 && mAndroidRunnerParams.getPerTestTimeout() > 0) {
+            timeout = mAndroidRunnerParams.getPerTestTimeout();
+        }
+
+        if (timeout <= 0) {
+            // no timeout was set
+            return next;
         }
-        return next;
+
+        return FailOnTimeout.builder()
+                            .withTimeout(timeout, TimeUnit.MILLISECONDS)
+                            .build(next);
     }
 
     private long getTimeout(Test annotation) {
Only in 0.6-alpha/android/support/test/internal/runner/junit4: NonExecutingBlockJUnit4ClassRunnerWithParametersFactory.java
diff -ru 0.5/android/support/test/internal/runner/junit4/NonExecutingJUnit4ClassRunner.java 0.6-alpha/android/support/test/internal/runner/junit4/NonExecutingJUnit4ClassRunner.java
--- 0.5/android/support/test/internal/runner/junit4/NonExecutingJUnit4ClassRunner.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/junit4/NonExecutingJUnit4ClassRunner.java	2016-08-02 22:18:50.000000000 +0900
@@ -24,7 +24,7 @@
  * A specialized {@link BlockJUnit4ClassRunner} that will generate test results, by skipping test
  * execution and loading.
  */
-class NonExecutingJUnit4ClassRunner extends BlockJUnit4ClassRunner {
+public class NonExecutingJUnit4ClassRunner extends BlockJUnit4ClassRunner {
 
     private static final Statement NON_EXECUTING_STATEMENT = new Statement() {
         @Override
Only in 0.6-alpha/android/support/test/internal/runner/junit4: NonExecutingJUnit4ClassRunnerWithParameters.java
Only in 0.6-alpha/android/support/test/internal/runner/junit4: statement
diff -ru 0.5/android/support/test/internal/runner/tracker/AnalyticsBasedUsageTracker.java 0.6-alpha/android/support/test/internal/runner/tracker/AnalyticsBasedUsageTracker.java
--- 0.5/android/support/test/internal/runner/tracker/AnalyticsBasedUsageTracker.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/runner/tracker/AnalyticsBasedUsageTracker.java	2016-08-02 22:18:50.000000000 +0900
@@ -5,7 +5,6 @@
 import android.net.Uri;
 import android.os.Build;
 import android.os.SystemClock;
-import android.provider.Settings;
 import android.util.Log;
 import android.view.Display;
 import android.view.WindowManager;
@@ -171,11 +170,16 @@
             if (null == screenResolution) {
                 Display display = ((WindowManager) targetContext.getSystemService(Context.WINDOW_SERVICE))
                         .getDefaultDisplay();
-                screenResolution = new StringBuilder()
-                        .append(display.getWidth())
-                        .append("x")
-                        .append(display.getHeight())
-                        .toString();
+                // Headless devices don't have a Display.
+                if (null == display) {
+                    screenResolution = "0x0";
+                } else {
+                    screenResolution = new StringBuilder()
+                            .append(display.getWidth())
+                            .append("x")
+                            .append(display.getHeight())
+                            .toString();
+                }
             }
 
             if (null == userId) {
@@ -268,4 +272,4 @@
         }
     }
 
-}
\ No newline at end of file
+}
diff -ru 0.5/android/support/test/internal/util/AndroidRunnerBuilderUtil.java 0.6-alpha/android/support/test/internal/util/AndroidRunnerBuilderUtil.java
--- 0.5/android/support/test/internal/util/AndroidRunnerBuilderUtil.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/internal/util/AndroidRunnerBuilderUtil.java	2016-08-02 22:18:50.000000000 +0900
@@ -15,6 +15,9 @@
  */
 package android.support.test.internal.util;
 
+import junit.framework.TestCase;
+import junit.framework.TestSuite;
+
 /**
  * Util methods for {@link android.support.test.internal.runner.AndroidRunnerBuilder}
  */
@@ -27,6 +30,31 @@
      * @return true if the test class is a JUnit3 test
      */
     public static boolean isJUnit3Test(Class<?> testClass) {
-        return junit.framework.TestCase.class.isAssignableFrom(testClass);
+        return TestCase.class.isAssignableFrom(testClass);
+    }
+
+    /**
+     * Checks if a particular test class is a JUnit3 test suite
+     *
+     * @param testClass test class to check
+     * @return true if the test class is a JUnit3 test suite
+     */
+    public static boolean isJUnit3TestSuite(Class<?> testClass) {
+        return TestSuite.class.isAssignableFrom(testClass);
+    }
+
+    /**
+     * Checks if a JUnit3 test class has a suite method
+     *
+     * @param testClass test class to check
+     * @return true if the test class has a suite method
+     */
+    public static boolean hasSuiteMethod(Class<?> testClass) {
+        try {
+            testClass.getMethod("suite");
+        } catch (NoSuchMethodException e) {
+            return false;
+        }
+        return true;
     }
 }
diff -ru 0.5/android/support/test/runner/AndroidJUnitRunner.java 0.6-alpha/android/support/test/runner/AndroidJUnitRunner.java
--- 0.5/android/support/test/runner/AndroidJUnitRunner.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/runner/AndroidJUnitRunner.java	2016-08-02 22:18:50.000000000 +0900
@@ -92,6 +92,12 @@
  * The file should contain a list of line separated test classes and optionally methods (expected
  * format: com.android.foo.FooClassName#testMethodName).
  * <p/>
+ * <b>Running all tests not listed in a file:</b> adb shell am instrument -w
+ * -e notTestFile /sdcard/tmp/notTestFile.txt
+ * com.android.foo/com.android.test.runner.AndroidJUnitRunner
+ * The file should contain a list of line separated test classes and optionally methods (expected
+ * format: com.android.foo.FooClassName#testMethodName).
+ * <p/>
  * <b>Running all tests in a java package:</b> adb shell am instrument -w
  * -e package com.android.foo.bar
  * com.android.foo/android.support.test.runner.AndroidJUnitRunner
@@ -155,6 +161,9 @@
  * <code>RunListener</code></a>s to observe the test run:</b>
  * -e listener com.foo.Listener,com.foo.Listener2
  * <p/>
+ * <b> To specify a custom {@link java.lang.ClassLoader} to load the test class:</b>
+ * -e classLoader com.foo.CustomClassLoader
+ * <p/>
  * <b>Set timeout (in milliseconds) that will be applied to each test:</b>
  * -e timeout_msec 5000
  * <p/>
@@ -165,9 +174,8 @@
  * <a href="http://junit.org/javadoc/latest/org/junit/Test.html#timeout()">
  * <code>org.junit.Test#timeout()</code></a>
  * annotation take precedence over both, this flag and
- * <a href="http://junit.org/javadoc/latest/org/junit/Test.html#timeout()">
- * <code>org.junit.Test#timeout()</code></a>
- * annotation.
+ * <a href="http://junit.org/javadoc/latest/org/junit/rules/Timeout.html">
+ * <code>org.junit.rules.Timeout</code></a> rule.
  * <p/>
  * <b>To disable Google Analytics:</b>
  * -e disableAnalytics true
@@ -243,6 +251,7 @@
 
     @Override
     public void onStart() {
+        setJsBridgeClassName("android.support.test.espresso.web.bridge.JavaScriptBridge");
         super.onStart();
 
         if (mRunnerArgs.idle) {
diff -ru 0.5/android/support/test/runner/MonitoringInstrumentation.java 0.6-alpha/android/support/test/runner/MonitoringInstrumentation.java
--- 0.5/android/support/test/runner/MonitoringInstrumentation.java	2016-02-22 20:52:48.000000000 +0900
+++ 0.6-alpha/android/support/test/runner/MonitoringInstrumentation.java	2016-08-02 22:18:50.000000000 +0900
@@ -16,6 +16,8 @@
 
 package android.support.test.runner;
 
+import static android.support.test.internal.util.Checks.checkNotMainThread;
+
 import android.app.Activity;
 import android.app.Application;
 import android.app.Fragment;
@@ -29,13 +31,17 @@
 import android.os.IBinder;
 import android.os.Looper;
 import android.os.MessageQueue.IdleHandler;
+import android.os.UserHandle;
 import android.support.test.InstrumentationRegistry;
 import android.support.test.internal.runner.hidden.ExposedInstrumentationApi;
 import android.support.test.internal.runner.intent.IntentMonitorImpl;
-import android.support.test.runner.intent.IntentStubberRegistry;
 import android.support.test.internal.runner.lifecycle.ActivityLifecycleMonitorImpl;
 import android.support.test.internal.runner.lifecycle.ApplicationLifecycleMonitorImpl;
+import android.support.test.internal.util.Checks;
+import android.support.test.internal.runner.intercepting.DefaultInterceptingActivityFactory;
+import android.support.test.runner.intercepting.InterceptingActivityFactory;
 import android.support.test.runner.intent.IntentMonitorRegistry;
+import android.support.test.runner.intent.IntentStubberRegistry;
 import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
 import android.support.test.runner.lifecycle.ApplicationLifecycleMonitorRegistry;
 import android.support.test.runner.lifecycle.ApplicationStage;
@@ -65,9 +71,6 @@
 import java.util.concurrent.atomic.AtomicInteger;
 import java.util.concurrent.atomic.AtomicLong;
 
-import static android.support.test.internal.util.Checks.checkMainThread;
-import static android.support.test.internal.util.Checks.checkNotMainThread;
-
 /**
  * An instrumentation that enables several advanced features and makes some hard guarantees about
  * the state of the application under instrumentation.
@@ -93,7 +96,7 @@
     private static final long MILLIS_TO_POLL_FOR_ACTIVITY_STOP =
             MILLIS_TO_WAIT_FOR_ACTIVITY_TO_STOP / 40;
 
-    private static final String TAG = "MonitoringInstrumentation";
+    private static final String TAG = "MonitoringInstr";
 
     private static final int START_ACTIVITY_TIMEOUT_SECONDS = 45;
     private ActivityLifecycleMonitorImpl mLifecycleMonitor = new ActivityLifecycleMonitorImpl();
@@ -105,6 +108,8 @@
     private AtomicBoolean mAnActivityHasBeenLaunched = new AtomicBoolean(false);
     private AtomicLong mLastIdleTime = new AtomicLong(0);
     private AtomicInteger mStartedActivityCounter = new AtomicInteger(0);
+    private String mJsBridgeClassName;
+    private AtomicBoolean mIsJsBridgeLoaded = new AtomicBoolean(false);
 
     private IdleHandler mIdleHandler = new IdleHandler() {
         @Override
@@ -115,6 +120,7 @@
     };
 
     private volatile boolean mFinished = false;
+    private volatile InterceptingActivityFactory mInterceptingActivityFactory;
 
     /**
      * Sets up lifecycle monitoring, and argument registry.
@@ -151,6 +157,7 @@
         super.onCreate(arguments);
         specifyDexMakerCacheProperty();
         setupDexmakerClassloader();
+        useDefaultInterceptingActivityFactory();
     }
 
     private final void installMultidex() {
@@ -188,6 +195,17 @@
         System.getProperties().put("dexmaker.dexcache", dexCache.getAbsolutePath());
     }
 
+    protected final void setJsBridgeClassName(final String className){
+      if (null == className) {
+        throw new NullPointerException("JsBridge class name cannot be null!");
+      }
+
+      if (mIsJsBridgeLoaded.get()) {
+        throw new IllegalStateException("JsBridge is already loaded!");
+      }
+      mJsBridgeClassName = className;
+    }
+
     private void setupDexmakerClassloader() {
         ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
         // must set the context classloader for apps that use a shared uid, see
@@ -224,11 +242,7 @@
     public void onStart() {
         super.onStart();
 
-        runOnMainSync(new Runnable() {
-            @Override
-            public void run() {
-                tryLoadingJsBridge();
-            }});
+        tryLoadingJsBridge(mJsBridgeClassName);
 
         // Due to the way Android initializes instrumentation - all instrumentations have the
         // possibility of seeing the Application and its classes in an inconsistent state.
@@ -410,6 +424,36 @@
     }
 
     /**
+     * This API was added in Android API 23 (M)
+     */
+    @Override
+    public ActivityResult execStartActivity(
+        Context who, IBinder contextThread, IBinder token, String target,
+        Intent intent, int requestCode, Bundle options) {
+        Log.d(TAG, "execStartActivities(context, ibinder, ibinder, fragmentTarget, intent," +
+            "requestCode, bundle)");
+        mIntentMonitor.signalIntent(intent);
+        ActivityResult ar = stubResultFor(intent);
+        if (ar != null) {
+            Log.i(TAG, String.format("Stubbing intent %s", intent));
+            return ar;
+        }
+        return super.execStartActivity(who, contextThread, token, target, intent, requestCode,
+            options);
+    }
+
+    /**
+     * This API was added in Android API 17 (JELLY_BEAN_MR1)
+     */
+    @Override
+    public ActivityResult execStartActivity(
+            Context who, IBinder contextThread, IBinder token, Activity target,
+            Intent intent, int requestCode, Bundle options, UserHandle user) {
+        return super.execStartActivity(who, contextThread, token, target, intent, requestCode,
+                options, user);
+    }
+
+    /**
      * {@inheritDoc}
      */
     @Override
@@ -610,27 +654,61 @@
                 lastNonConfigurationInstance);
     }
 
+    @Override
+    public Activity newActivity(ClassLoader cl, String className, Intent intent)
+        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
+        return mInterceptingActivityFactory.shouldIntercept(cl, className, intent)
+                ? mInterceptingActivityFactory.create(cl, className, intent)
+                : super.newActivity(cl, className, intent);
+    }
+
     /**
-     * Loads the JS Bridge for Espresso Web. Only call this method from the main thread!
+     * Use the given InterceptingActivityFactory to create Activity instance in
+     * {@link #newActivity(ClassLoader, String, Intent)}. This can be used to override default
+     * behavior of activity in tests e.g. mocking startService() method in Activity under test,
+     * to avoid starting the real service and instead verifying that a particular service was
+     * started.
+     *
+     * @param interceptingActivityFactory InterceptingActivityFactory to be used for creating
+     *                                    activity instance in {@link #newActivity(ClassLoader,
+     *                                    String, Intent)}
      */
-    private void tryLoadingJsBridge() {
-        checkMainThread();
-        try {
-            Class<?> jsBridge = Class.forName(
-                    "android.support.test.espresso.web.bridge.JavaScriptBridge");
-            Method install = jsBridge.getDeclaredMethod("installBridge");
-            install.invoke(null);
-        } catch (ClassNotFoundException ignored) {
-            Log.i(TAG, "No JSBridge.");
-        } catch (NoSuchMethodException nsme) {
-            Log.i(TAG, "No JSBridge.");
-        } catch (InvocationTargetException ite) {
-            throw new RuntimeException(
-                    "JSbridge is available at runtime, but calling it failed.", ite);
-        } catch (IllegalAccessException iae) {
-            throw new RuntimeException(
-                    "JSbridge is available at runtime, but calling it failed.", iae);
-        }
+    public void interceptActivityUsing(InterceptingActivityFactory interceptingActivityFactory) {
+        Checks.checkNotNull(interceptingActivityFactory);
+        mInterceptingActivityFactory = interceptingActivityFactory;
+    }
+
+    /**
+     * Use default mechanism of creating activity instance in
+     * {@link #newActivity(ClassLoader, String, Intent)}
+     */
+
+    public void useDefaultInterceptingActivityFactory() {
+        mInterceptingActivityFactory = new DefaultInterceptingActivityFactory();
+    }
+
+    /**
+     * Loads the JS Bridge for Espresso Web. This method will be ran on the main thread!
+     *
+     * @param className the name of the JsBridge class
+     */
+    private void tryLoadingJsBridge(final String className) {
+        runOnMainSync(new Runnable() {
+            @Override
+            public void run() {
+                try {
+                    Class<?> jsBridge = Class.forName(className);
+                    Method install = jsBridge.getDeclaredMethod("installBridge");
+                    install.invoke(null);
+                    mIsJsBridgeLoaded.set(true);
+                } catch (ClassNotFoundException | NoSuchMethodException ignored) {
+                    Log.i(TAG, "No JSBridge.");
+                } catch (InvocationTargetException | IllegalAccessException ite) {
+                    throw new RuntimeException(
+                            "JSbridge is available at runtime, but calling it failed.", ite);
+                }
+            }
+        });
     }
 
     /**
Only in 0.6-alpha/android/support/test/runner: intercepting
