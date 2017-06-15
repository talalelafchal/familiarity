package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Description:
 * <p/>
 * Date: 14-2-2
 * Author: Administrator
 */
public class SerialUtils {
    /**
     * Return byte array of serializable object
     *
     * @param object serializable object
     * @return <code>byte[]</code>
     * @throws java.io.IOException <code>java.io.IOException</code>
     */
    public static byte[] getObjectByteArray(Serializable object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        oos.close();
        return bos.toByteArray();
    }

    /**
     * Deserialize byte array to object
     *
     * @param objByteArray <code>bytep[</code>
     * @return <code>java.lang.Object</code>
     * @throws IOException            <code>java.io.IOException</code>
     * @throws ClassNotFoundException <code>java.lang.ClassNotFoundException</code>
     */
    public static Object readObject(byte[] objByteArray) throws IOException, ClassNotFoundException {
        Object result;
        ByteArrayInputStream bis = new ByteArrayInputStream(objByteArray);
        ObjectInputStream is = new ObjectInputStream(bis);
        result = is.readObject();
        is.close();
        return result;
    }
}
