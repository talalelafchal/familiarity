package com.mobiconn.tracker.support.db.repository;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Joao Pedro Evangelista
 */
public interface Persistable<T extends Serializable, PK> extends Serializable {

    ContentValues toValues();

    List<T> getValues(Cursor cursor);

    T get(Cursor cursor);

    PK getId();

    T from(Cursor cursor);
}
