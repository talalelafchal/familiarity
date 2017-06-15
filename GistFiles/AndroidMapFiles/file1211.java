package utils.sqllite;

/**
 * Description:
 * <p/>
 * Date: 14-2-3
 * Author: Administrator
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
