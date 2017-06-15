/*
 * Copyright (C) 2015 tslamic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tslamic.adntest;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public final class DeviceNamesMap {

    private static Map<String, String> sMap;

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
        final Map<String, String> holder = new HashMap<>();
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
