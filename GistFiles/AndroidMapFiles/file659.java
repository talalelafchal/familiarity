package io.github.tslamic.adntest;

import android.content.Context;
import android.util.ArrayMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DeviceNamesArrayMap {

    private static ArrayMap<String, String> sMap;

    public static String getCurrentDeviceName(String fallback) {
        return getDeviceName(android.os.Build.MODEL, fallback);
    }

    public static String getDeviceName(String model, String fallback) {
        if (null == sMap) {
            throw new IllegalStateException("devices not initialized");
        }
        if (android.text.TextUtils.isEmpty(model)) {
            return fallback;
        }
        final String map = sMap.get(model);
        if (android.text.TextUtils.isEmpty(map)) {
            return fallback;
        } else {
            return map;
        }
    }

    public static void createHolder(Context context) {
        final ArrayMap<String, String> holder = new ArrayMap<>();
        BufferedReader reader = null;
        try {
            final InputStream in = context.getResources().openRawResource(R.raw.devices);
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while (null != (line = reader.readLine())) {
                final String[] pair = line.split("=");
                final String model = pair[0].trim();
                final String name = pair[1].trim();
                holder.put(model, name);
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
        sMap = holder;
    }

}
