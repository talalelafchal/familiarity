package com.java.yamanoboriold.httpclient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.support.annotation.NonNull;

public class HttpGet {

    public int get(@NonNull String path, String dst) throws IOException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (InputStream in = connection.getInputStream();
                     OutputStream out = new FileOutputStream(dst)) {
                    writeFile(out, in, connection.getContentLength());
                }
                return HttpURLConnection.HTTP_OK;
            } else {
                return connection.getResponseCode();
            }
        } finally {
            connection.disconnect();
        }
    }

    private void writeFile(OutputStream out, InputStream in, int contentLength) throws IOException {
        byte buf[] = new byte[8192];
        int len;
        int remain = contentLength;
        while ((len = in.read(buf, 0, Math.min(buf.length, remain))) != -1) {
            out.write(buf, 0, len);
            remain -= len;
        }
    }
}
