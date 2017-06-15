package annotations;

import android.os.Build;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Changes the Android version Robolectric reports to your code.
 * Allows you to test logic flows based on Android version.
 *
 * @author Christopher J. Perry {github.com/christopherperry}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AndroidVersion {
    int value() default Build.VERSION_CODES.HONEYCOMB;
}