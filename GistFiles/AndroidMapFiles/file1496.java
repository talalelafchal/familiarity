
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SuppressWarnings("unused")
public class DLUtil {

    /** Value - {@value}, Tag for Log output.*/
    public static final String TAG = "DLUtil";

    /** Value - {@value}, output buffer size.*/
    public static final int BUFLEN = 1024;

    /** Value - {@value}, key for message digest algorithm.*/
    public static final String SHA_256 = "SHA-256";

    /** Value - {@value}, key for UTF charset name.*/
    public static final String UTF_8 = "UTF-8";

    /** Value - {@value}, key for ISO charset name.*/
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * Returns hex string of provided byte array.
     *
     * @param bytes     Provided byte array.
     * @return          Resulted hex String after conversion.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) {
                stringBuilder.append("0");
            }
            stringBuilder.append(Integer.toHexString(intVal).toUpperCase());
        }
        return stringBuilder.toString();
    }

    /**
     * Returns utf8 byte array of provided String.
     *
     * @param string    Base String to be converted to bytes.
     * @return          Array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String string) {
        try {
            return string.getBytes(UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns content of a file in String format.
     *
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename              Name of the file.
     * @return                      Content of file as String.
     * @throws java.io.IOException  IOException that can be caused by either reading File or
     *                              or converting File's content to String.
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read;
            int count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB
                        && bytes[2] == (byte) 0xBF) {

                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), UTF_8) : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Returns serialized string of provided object.
     *
     * @param obj Serializable object.
     * @return    Serialized string of provided object.
     * @see       com.tigerspike.android.commons.DLUtil#deserializeObjectFromString(String)
     */
    public static String serializeObjectToString(Serializable obj) {
        Log.d(TAG, "trying to serialize Object[" + obj.getClass().getName() + "]");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        String strSerialized = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            strSerialized = byteArrayOutputStream.toString(ISO_8859_1);
            strSerialized = URLEncoder.encode(strSerialized, UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strSerialized;
    }

    /**
     * Returns serializable object from provided string.
     *
     * @param str Serialized String value of serializable object.
     * @return    Serializable object
     *
     * @see com.tigerspike.android.commons.DLUtil#serializeObjectToString(java.io.Serializable)
     */
    public static Object deserializeObjectFromString(String str) {
        Object obj = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            String redStr = java.net.URLDecoder.decode(str, UTF_8);
            byteArrayInputStream = new ByteArrayInputStream(
                    redStr.getBytes(ISO_8859_1));
            objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    /**
     * Returns SHA-256 hash of given string.
     *
     * Gets message digest algorithm SHA-256 from MessageDigest class, computes hash of the given
     * String and return the computed value.
     *
     * @param base                      Input string for which hash is required.
     * @return SHA256                   Returns SHA-256 hash of base String
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException is thrown in case MessageDigest
     *                                  class cannot find SHA-256 algorithm.
     */
    public static String computeSHA256Hash(String base) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_256);

        byte[] byteData = new byte[0];
        try {
            byteData = digest.digest(base.getBytes(UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuffer = new StringBuilder();

        for (byte aByteData : byteData) {
            stringBuffer.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    /**
     * Capitalize first letter of the String.
     *
     * @param s input String
     * @return String with first letter capitalized
     */
    public static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Returns rounded to two decimal points double value as string.
     *
     * @param value Double value.
     * @return rounded double value as string.
     */
    public static String getRoundedDoubleString(double value) {
        return getRoundedDoubleString(value, 2);
    }

    /**
     * Returns rounded to provided decimal points double value as string.
     *
     * @param value Double value.
     * @param decimals No. of decimals.
     * @return rounded double value as string.
     */
    public static String getRoundedDoubleString(double value, int decimals) {
        String roundVal = "0.0";
        try {
            roundVal = String.format("%." + decimals + "f", value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roundVal;
    }

    /**
     * Returns real path from URI.
     *
     * @param context Current context.
     * @param contentUri Content Uri from which real path is required.
     * @return Real path from provided URI.
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(columnIndex);
        }
        cursor.close();
        return path;
    }

    /**
     * Shares a text only intent.  This will only prompt the user to share if there is an activity
     * that can consume the intent
     *
     * @param context Current context.
     * @param title Title of the share intent.
     * @param text The text that is used as the content of the intent.
     * @return true if a chooser was successfully created.
     */
    public static boolean shareIntent(Context context, String title, String text) {
        if (context == null){
            return false;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");

        if (context.getPackageManager() != null
                && shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(shareIntent, title));
            return true;
        }
        return false;
    }

    /**
     * Triggers share action with text, title and image.
     *
     * @param context   Current context.
     * @param title     Title of the share intent.
     * @param text      The text that is used as the content of the intent.
     * @param bitmap    Image Bitmap that will be shared.
     * @return true if a chooser was successfully created.
     */
    public static boolean shareIntent(Context context, String title, String text, Bitmap bitmap) {
        // Protect against null pointers
        if (context == null || bitmap == null) {
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Share Image");
        values.put(MediaStore.Images.Media.BUCKET_ID, "share");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Share Image");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        Uri uri = context.getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        OutputStream outstream = null;
        try {
            outstream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, outstream);
            outstream.close();
            outstream = null;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            if (text != null) {
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                shareIntent.putExtra("sms_body", text);
            }

            if (context.getPackageManager() != null
                    && shareIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(shareIntent, title));
                return true;
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (outstream != null) {
                try {
                    outstream.flush();
                } catch (IOException ex) {
                    // no need to do anything here
                }
                try {
                    outstream.close();
                } catch (IOException ex) {
                    // no need to do anything here
                }
            }
        }
        return false;
    }

    /**
     * get Date string from timestamp .
     * @param timeStamp the timestamp by millionseconds
     * @param format the date format like : "yyyy-MM-dd HH:mm:ss"
     * @return Date string
     */
    public static String getDateStrFromTimestamp(long timeStamp, String format){
        if(timeStamp>0 ){
            try {
                if(format == null){
                    format = "yyyy-MM-dd HH:mm:ss";
                }
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Date date = new Date();
                date.setTime(timeStamp);
                return sdf.format(date);
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }
}
