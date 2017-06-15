package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds the specified ModuleWrapper for the test.
 * Use this to override the default module in the test runner.
 *
 * @author Christopher J. Perry {github.com/christopherperry}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindModule {
    Class<? extends ModuleWrapper> value();
}