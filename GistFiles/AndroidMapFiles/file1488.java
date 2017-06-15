package io.github.tslamic.adntest;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DeviceNamesArray {

    private static String[] sMap;

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
        for (int i = 0, size = sMap.length / 2; i < size; i++) {
            final String map = sMap[i * 2];
            if (map.equals(model)) {
                return sMap[i * 2 + 1];
            }
        }
        return fallback;
    }

    public static void createHolder(Context context) {
        final List<String> holder = new ArrayList<>();
        BufferedReader reader = null;
        try {
            final InputStream in = context.getResources().openRawResource(R.raw.devices);
            reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while (null != (line = reader.readLine())) {
                final String[] pair = line.split("=");
                final String model = pair[0].trim();
                final String name = pair[1].trim();
                holder.add(model);
                holder.add(name);
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
        sMap = holder.toArray(new String[holder.size()]);
    }

}
