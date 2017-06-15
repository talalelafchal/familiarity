package com.mobiconn.tracker.support.db.repository;

import android.database.Cursor;

import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Joao Pedro Evangelista
 */
public class FieldAnnotationBasedReader<T> extends FieldAnnorationOperations<T> {


    public Persistable<?, ?> read(Cursor cursor, Class<T> persistableClass, Persistable<?, ?> instance) throws IllegalAccessException {
        List<java.lang.reflect.Field> annotatedFields = getAnnotatedFields(persistableClass);
        for (Field annotatedField : annotatedFields) {
            com.mobiconn.tracker.support.db.repository.Field annotation = annotatedField.getAnnotation(com.mobiconn.tracker.support.db.repository.Field.class);
            annotatedField.set(instance, getValueTypeBased(annotation.value(), cursor, annotation.type()));
        }
        return instance;
    }

    private <B> Object getValueTypeBased(String annotationValue, Cursor cursor, Class<B> classType) {
        Type type = ((ParameterizedType) classType.getGenericSuperclass()).getActualTypeArguments()[0];
        int columnIndex = cursor.getColumnIndex(annotationValue);
        if (Long.class.equals(type)) {
            return cursor.getLong(columnIndex);
        } else if (String.class.equals(type)) {
            return cursor.getString(columnIndex);
        } else if (Integer.class.equals(type)) {
            return cursor.getInt(columnIndex);
        } else if (Double.class.equals(type)) {
            return cursor.getDouble(columnIndex);
        } else {
            return null;
        }

    }
}
