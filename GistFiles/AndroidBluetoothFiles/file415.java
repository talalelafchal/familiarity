package com.morkout.nbsocial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
	public static final int BUFFER_SIZE = 1024;

	
	public static void safeClose(final InputStream is )
	{
		try{
			if(is !=null)
			{
				is.close();
			}
		}
		catch(final IOException e){
			//ignore
		}
	}
	
	public static void safeClose(final OutputStream os )
	{
		try{
			if(os !=null)
			{
				os.close();
			}
		}
		catch(final IOException e){
			//ignore
		}
	}
	/**
    *
    * @param is InputStream
    * @param os OutputStream
    * @throws IOException
    */

   public static void copyBytewise(final InputStream is, final OutputStream os) throws IOException
   {
       int data = -1;
       while ((data = is.read())!= -1)
       {
           os.write(data);
       }
       os.flush();
   }

   public static void copyBuffered(final InputStream is, final OutputStream os) throws IOException {
       final InputStream bufferedIn = decorateWithBuffer(is);
       final OutputStream bufferedOut = decorateWithBuffer(os);
       copyBytewise(bufferedIn, bufferedOut);
   }

   public static InputStream decorateWithBuffer(final InputStream inputStream)
   {
       if (inputStream == null)
       {
           throw new IllegalArgumentException("parameter must not be null");
       }
       if (!(inputStream instanceof BufferedInputStream))
       {
           return new BufferedInputStream(inputStream);
       }
       return inputStream;
   }

   public static OutputStream decorateWithBuffer(final OutputStream outStream)
   {
       if (outStream == null)
       {
           throw new IllegalArgumentException("parameter outStream must not be null");
       }
       if (!(outStream instanceof BufferedOutputStream))
       {
           return new BufferedOutputStream(outStream);
       }
       return outStream;
   }
   
   public static void copyOwnBuffering(final InputStream is, final OutputStream os) throws IOException
   {
	   final byte[] buffer = new byte[BUFFER_SIZE];
	   int length = -1;
	   while((length = is.read(buffer, 0,BUFFER_SIZE)) != -1)
	   {
		   os.write(buffer, 0, length);
	   }
	   os.flush();
   }

}