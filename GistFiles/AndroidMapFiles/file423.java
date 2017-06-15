package com.mobiconn.tracker.support.db.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joao Pedro Evangelista
 */
public abstract class FieldAnnorationOperations<T> {

    public List<java.lang.reflect.Field> getAnnotatedFields(Class<T> persistableClass) {
        List<java.lang.reflect.Field> collector = new ArrayList<>();
        for (java.lang.reflect.Field field : persistableClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(com.mobiconn.tracker.support.db.repository.Field.class)) {
                collector.add(field);
            }
        }
        return collector;
    }
}
