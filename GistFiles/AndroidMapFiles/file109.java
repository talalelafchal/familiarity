package me.vaughandroid.testutils.rules;

import org.joda.time.DateTimeZone;
import org.joda.time.tz.Provider;
import org.joda.time.tz.UTCProvider;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A {@link TestRule} which prevents errors when using the
 * <a href="https://github.com/dlew/joda-time-android">joda-time-android</a> lib in JVM unit tests.
 * Without this rule, tests will log a caught exception:
 * {@code java.io.IOException: Resource not found: "org/joda/time/tz/data/ZoneInfoMap"}
 *
 * @author vaughandroid@gmail.com
 */
public class JodaAndroidFixRule implements TestRule {

  private final Provider provider;

  public JodaAndroidFixRule() {
    this(new UTCProvider());
  }

  public JodaAndroidFixRule(Provider provider) {
    this.provider = provider;
  }

  @Override public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        DateTimeZone.setProvider(provider);
        base.evaluate();
      }
    };
  }
}
