package com.mobiconn.tracker.support.db.repository;

import android.content.ContentValues;
import android.os.Parcel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joao Pedro Evangelista
 */
public class FieldAnnotationBasedWriter<T extends Persistable<T, ?>> extends FieldAnnorationOperations<T> {

    public ContentValues write(Class<T> persistableClass, Persistable<T, ?> instance) throws IllegalAccessException {
        List<Field> annotatedFields = getAnnotatedFields(persistableClass);
        Map<String, Object> map = new HashMap<>(annotatedFields.size());
        for (Field annotatedField : annotatedFields) {
            Object value = annotatedField.get(instance);
            com.mobiconn.tracker.support.db.repository.Field annotation = annotatedField
                    .getAnnotation(com.mobiconn.tracker.support.db.repository.Field.class);
            map.put(annotation.value(), value);
        }
        Parcel parcel = Parcel.obtain();
        parcel.writeMap(map);
        parcel.setDataPosition(0);
        ContentValues fromParcel = ContentValues.CREATOR.createFromParcel(parcel);
        if (fromParcel != null) {
            return fromParcel;
        } else {
            return new ContentValues(0);
        }
    }


}