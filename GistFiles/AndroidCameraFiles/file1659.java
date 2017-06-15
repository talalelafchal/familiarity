package com.ctrlsmart.bean;

import com.ctrlsmart.dataWrap.UCBufData;
import com.ctrlsmart.dataWrap.UCDeEncode;

/**
 * Created by Administrator on 2015/3/31.
 */
public class ImeiEncode
{
    UCDeEncode lv_pucDeEncode = new UCDeEncode();

    public static String printHexString(byte[] paramArrayOfByte)
    {
        String str1 = "";
        for (int i = 0;; i++)
        {
            if (i >= paramArrayOfByte.length) {
                return str1;
            }
            String str2 = Integer.toHexString(0xFF & paramArrayOfByte[i]);
            if (str2.length() == 1) {
                str2 = '0' + str2;
            }
            str1 = str1 + str2.toUpperCase();
        }
    }

    public UCBufData GetBufDataByStr(String paramString)
    {
        if (paramString == null) {
            return null;
        }
        int i = paramString.getBytes().length;
        byte[] arrayOfByte = new byte[1024];
        UCBufData localUCBufData = new UCBufData();
        localUCBufData.BuildBufData(arrayOfByte, i, 1024);
        return localUCBufData;
    }

    public byte[] GetDataByBufData(UCBufData paramUCBufData)
    {

        if (paramUCBufData != null)
        {
            byte[] arrayOfByte1 = paramUCBufData.buf;

            int i = paramUCBufData.dataLen;
//      Log.e("ZZZZ","arrayOfByte1 ="+printHexString(paramUCBufData.buf)+"i ="+i);
            byte[] arrayOfByte2 = new byte[i];
            System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
            return arrayOfByte2;
        }
        return null;
    }

    public String GetEncodeData(String paramString)
    {
//	Log.e("ImeiEncode", "paramString ="+paramString);
        byte[] arrayOfByte1 = paramString.getBytes();
//    Log.e("ImeiEncode", "paramString.getBytes() ="+printHexString(arrayOfByte1));
        int i = arrayOfByte1.length;
        byte[] arrayOfByte2 = new byte[1024];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
//    new String(arrayOfByte2);
        UCDeEncode localUCDeEncode = new UCDeEncode();
        UCBufData localUCBufData = new UCBufData();
//    Log.e("ImeiEncode", "localUCBufData ="+printHexString(arrayOfByte2)+"i ="+i);
        localUCBufData.BuildBufData(arrayOfByte2, i, 1024);
        localUCDeEncode.EncodeData(localUCBufData);

        String str = printHexString(GetDataByBufData(localUCBufData));
//    Log.e("IMei", "IMeistr ="+str);
        return str;
    }

    public String GetStrByBufData(UCBufData paramUCBufData)
    {
        if (paramUCBufData != null) {
            return new String(paramUCBufData.buf, 0, paramUCBufData.dataLen);
        }
        return null;
    }

    public String getEncryption(String paramString, int paramInt)
    {
        return null;
    }

    public void setKey()
    {
        System.out.println("++++++++设置key++++++");
        byte[] arrayOfByte = "bjsdax".getBytes();
        this.lv_pucDeEncode.SetKey(arrayOfByte, arrayOfByte.length);
    }
}