package io.github.tslamic.adntest;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DeviceNamesProperties {

    private static Properties sProperties;

    public static String getCurrentDeviceName(String fallback) {
        return getDeviceName(android.os.Build.MODEL, fallback);
    }

    public static String getDeviceName(String model, String fallback) {
        if (null == sProperties) {
            throw new IllegalStateException("devices not initialized");
        }
        if (android.text.TextUtils.isEmpty(model)) {
            return fallback;
        }
        final String map = sProperties.getProperty(model);
        if (android.text.TextUtils.isEmpty(map)) {
            return fallback;
        } else {
            return map;
        }
    }

    public static void loadProperties(Context context) {
        InputStream input = null;
        Properties props = null;
        try {
            input = context.getResources().openRawResource(R.raw.devices);
            props = new Properties();
            props.load(input);
        } catch (IOException ignore) {
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }
        sProperties = props;
    }

}
