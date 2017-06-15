package com.mobiconn.tracker.support.db.repository;

import java.lang.annotation.*;

/**
 * @author Joao Pedro Evangelista
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    /**
     * String representation of the name of column  on database to be mapped
     */
    String value();

    Class<?> type();

}
