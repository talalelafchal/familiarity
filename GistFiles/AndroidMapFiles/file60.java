package com.example.ContactForm.ImageUtils;

/**
 * Created with IntelliJ IDEA.
 * User: I
 * Date: 11.02.13
 * Time: 15:09
 * To change this template use File | Settings | File Templates.
 */
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){
            System.out.println("CopyStream e = "+ex.toString());
        }
    }
}