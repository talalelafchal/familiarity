package com.twansoftware.invoicemakerpro.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by achuinard on 8/11/15.
 */
public class EnumLocalizedSpinner<T> extends ArrayAdapter<T> {
    private final Map<T, String> mStrings;

    public EnumLocalizedSpinner(final Context context, T[] enumItems, Map<T, String> stringResources) {
        super(context, android.R.layout.simple_spinner_item, enumItems);
        mStrings = stringResources;
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        final T item = getItem(position);
        final TextView tv = (TextView) super.getView(position, convertView, parent);
        setupTextView(item, tv);
        return tv;
    }

    @Override
    public final View getDropDownView(int position, View convertView, ViewGroup parent) {
        final TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
        setupTextView(getItem(position), tv);
        return tv;
    }

    protected void setupTextView(T item, TextView tv) {
        tv.setText(mStrings.get(item));
    }
}

