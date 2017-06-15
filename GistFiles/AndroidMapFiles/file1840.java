package io.github.tslamic.adntest;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeviceNamesTwoArrays {

    private static String[] sKey;
    private static String[] sVal;

    public static String getCurrentDeviceName(String fallback) {
        return getDeviceName(android.os.Build.MODEL, fallback);
    }

    public static String getDeviceName(String model, String fallback) {
        if (null == sKey || null == sVal) {
            throw new IllegalStateException("devices not initialized");
        }
        if (android.text.TextUtils.isEmpty(model)) {
            return fallback;
        }
        for (int i = 0, size = sKey.length; i < size; i++) {
            if (sKey[i].equals(model)) {
                return sVal[i];
            }
        }
        return fallback;
    }

    public static void createHolder(Context context) {
        final List<String> keys = new ArrayList<>();
        final List<String> vals = new ArrayList<>();
        BufferedReader reader = null;
        try {
            final InputStream in = context.getResources().openRawResource(R.raw.devices);
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while (null != (line = reader.readLine())) {
                final String[] pair = line.split("=");
                final String model = pair[0].trim();
                final String name = pair[1].trim();
                keys.add(model);
                vals.add(name);
            }
        } catch (IOException ignore) {
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                }
            }
        }
        sKey = keys.toArray(new String[keys.size()]);
        sVal = vals.toArray(new String[vals.size()]);
    }

}
