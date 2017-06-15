package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds multiple classes.
 *
 * @author Christopher J. Perry {github.com/christopherperry}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindMultiple {
    Class<?>[] from();
    Class<?>[] to();
}