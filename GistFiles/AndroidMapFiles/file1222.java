package gturedi.gist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class FileUtil {

    public static final String UTF_8 = "utf-8";

    public static void deleteRecursively(File dir) {
        if (dir.isDirectory()) {
            for (String item : dir.list()) {
                deleteRecursively(new File(dir, item));
            }
        }
        dir.delete();
    }

    public static String convertStreamToString(InputStream in) {
        try {
            return new String(convertStreamToByteArray(in), UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] convertStreamToByteArray(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyStream(in, out);
        return out.toByteArray();
    }

    public static void copyStream(InputStream in, OutputStream out) {
        try {
            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}