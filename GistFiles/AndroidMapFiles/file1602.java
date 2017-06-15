package tools.annotation;

import java.lang.annotation.Inherited;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
public @interface RemoteService {
}
