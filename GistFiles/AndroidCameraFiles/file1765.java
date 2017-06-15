package com.ctrlsmart.dataWrap;

/**
 * Created by Administrator on 2015/3/31.
 */
public class UCDeEncode
{
    private long v_uiKey = 1L;

//  private void ConvertData(UCBufData paramUCBufData)
//  {
//    byte[] arrayOfByte = new byte[paramUCBufData.dataLen];
//    int i = 0;
//    int j = (int)(this.v_uiKey % paramUCBufData.dataLen);
//    int k = 0;
//    if (k >= j)
//    {
//      System.arraycopy(arrayOfByte, 0, paramUCBufData.buf, 0, paramUCBufData.dataLen);
//      return;
//    }
//    int m = k;
//    for (;;)
//    {
//      if (m >= paramUCBufData.dataLen)
//      {
//        k++;
//        break;
//      }
//      int n = i + 1;
//      arrayOfByte[i] = paramUCBufData.buf[((int)((m + this.v_uiKey) % paramUCBufData.dataLen))];
//      m += j;
//      i = n;
//    }
//  }


    private void ConvertData(UCBufData paramUCBufData)
    {
        byte[] arrayOfByte = new byte[paramUCBufData.dataLen];

        int j = (int)(this.v_uiKey % paramUCBufData.dataLen);
        int i = 0;
        int k = 0;
        int m = 0;
        int n = 0 ;
        for(k = 0;k<j;k++)
        {
            for(i=k;i<paramUCBufData.dataLen;i +=j){
                m = n+1;
                arrayOfByte[n] = paramUCBufData.buf[(int)((this.v_uiKey+i)%paramUCBufData.dataLen)];

                n = m;
            }
        }
        System.arraycopy(arrayOfByte, 0, paramUCBufData.buf, 0, paramUCBufData.dataLen);

    }

    private void DecodeData(UCBufData paramUCBufData, UCRand paramUCRand, int paramInt)
    {
        int i = 0;
        int j = paramUCBufData.dataLen;
        long l = paramUCRand.Rand();
        for (;;)
        {
            if (i >= j) {
                return;
            }
            int k = 0xFF & paramUCBufData.buf[i];
            paramUCBufData.buf[i] = ((byte)(int)(0xFF & (k ^ l & 0xFF ^ paramInt)));
            l += ((0xFF & paramUCBufData.buf[i]) + paramUCRand.Rand()) % 16777216L;
            i++;
        }
    }

    private void DeconvertData(UCBufData paramUCBufData)
    {
        byte[] arrayOfByte1 = new byte[paramUCBufData.dataLen];
        int i = 0;
        int j = (int)(this.v_uiKey % paramUCBufData.dataLen);
        int k = 0;
        if (k >= j)
        {
            System.arraycopy(arrayOfByte1, 0, paramUCBufData.buf, 0, paramUCBufData.dataLen);
            return;
        }
        int m = k;
        for (;;)
        {
            if (m >= paramUCBufData.dataLen)
            {
                k++;
                break;
            }
            int n = (int)((m + this.v_uiKey) % paramUCBufData.dataLen);
            byte[] arrayOfByte2 = paramUCBufData.buf;
            int i1 = i + 1;
            arrayOfByte1[n] = arrayOfByte2[i];
            m += j;
            i = i1;
        }
    }

//  private void EncodeData(UCBufData paramUCBufData, UCRand paramUCRand, int paramInt)
//  {
//    int i = 0;
//    int j = paramUCBufData.dataLen;
//    long l = paramUCRand.Rand();
//    for (;;)
//    {
//      if (i >= j) {
//        return;
//      }
//      long k = (long)0xFF & paramUCBufData.buf[i];
//      paramUCBufData.buf[i] = (byte)((int)((long)0xFF & (k ^ (l & 0xFF) ^ (long)paramInt)));
//      l += (k + paramUCRand.Rand()) % 16777216L;
//      i++;
//    }
//
//  }

    private void EncodeData(UCBufData paramUCBufData, UCRand paramUCRand, int paramInt)
    {
        int i = 0;
        int j = paramUCBufData.dataLen;
        long l = paramUCRand.Rand();
        for (i=0;i<j;i++)
        {
            long k = (long)(0xFF & paramUCBufData.buf[i]);
            paramUCBufData.buf[i] = (byte)((int)(0xFF & (k ^ (l & 0xFF) ^ (long)paramInt)));
            l += (k + paramUCRand.Rand()) % 0x1000000;

        }

    }
    private boolean GetCheckVal(UCBufData paramUCBufData)
    {
        long l1 = 0;
        int j = 0;
        long l2 = 0;
        int k = 0;
        if (paramUCBufData.dataLen > 3)
        {
            l1 = 0L;
            int i = -3 + paramUCBufData.dataLen;
            j = 0;
            if (j > i) {


                l2 = l1 & 0xFFFFFF;
                byte[] arrayOfByte1 = paramUCBufData.buf;
                k = i + 1;
                if (arrayOfByte1[i] == (0xFF & l2 >> 16)) {

                }
            }
        }

        int m;
        byte[] arrayOfByte3;
        do
        {
            byte[] arrayOfByte2;
            do
            {

                l1 += l1 * (0xFF & paramUCBufData.buf[j]);
                j++;

                arrayOfByte2 = paramUCBufData.buf;
                m = k + 1;
            } while (arrayOfByte2[k] != (0xFF & l2 >> 8));
            arrayOfByte3 = paramUCBufData.buf;

        } while (arrayOfByte3[m] != (l2 & 0xFF));
        paramUCBufData.dataLen = (-3 + paramUCBufData.dataLen);
        return false;
    }

    private boolean GetData(UCBufData paramUCBufData, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    {
        int i = paramUCBufData.dataLen;
        boolean bool = false;
        if (paramInt1 <= i)
        {
            int j = paramInt1 + paramInt2;
            int k = paramUCBufData.dataLen;
            bool = false;
            if (j <= k)
            {
                System.arraycopy(paramUCBufData.buf, paramInt1, paramArrayOfByte, 0, paramInt2);
                System.arraycopy(paramUCBufData.buf, paramInt1 + paramInt2, paramUCBufData.buf, paramInt1, paramUCBufData.dataLen - paramInt1 - paramInt2);
                paramUCBufData.dataLen -= paramInt2;
                bool = true;
            }
        }
        return bool;
    }

//  private boolean InsertData(UCBufData paramUCBufData, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
//  {
//    int i = paramUCBufData.dataLen;
//    boolean bool1 = false;
//    long l = 0;
//    int j = 0;
//    if (paramInt1 <= i)
//    {
//      l = paramInt2 + paramUCBufData.dataLen;
//      boolean bool2 = l < paramUCBufData.maxBufLen;
//      bool1 = false;
//      if (!bool2) {
//        j = -1 + paramUCBufData.dataLen;
//      }
//    }
//    for (int k = j + paramInt2;; k--)
//    {
//      if (j < paramInt1)
//      {
//        System.arraycopy(paramArrayOfByte, 0, paramUCBufData.buf, paramInt1, paramInt2);
//        paramUCBufData.dataLen = ((int)(0xFFFF & l));
//        bool1 = true;
//        return bool1;
//      }
//      paramUCBufData.buf[k] = paramUCBufData.buf[j];
//      j--;
//    }
//  }

    private boolean InsertData(UCBufData paramUCBufData, int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    {
        int i = paramUCBufData.dataLen;
//    boolean bool1 = false;
        long l = 0;
        int j = 0;
        if (paramInt1 <= i)
        {
            l = paramInt2 + paramUCBufData.dataLen;
            boolean bool2 = l < paramUCBufData.maxBufLen;

            if (bool2) {
                j = -1 + paramUCBufData.dataLen;

                for (int k = j + paramInt2;; k--)
                {
//    	  Log.e("interor", "k===="+k+"j===="+j);
                    if (j < paramInt1)
                    {
//        	Log.e("interor", "j < paramInt1");
                        System.arraycopy(paramArrayOfByte, 0, paramUCBufData.buf, paramInt1, paramInt2);
                        paramUCBufData.dataLen = ((int)(0xFFFF & l));

                        return true;
                    }
//        Log.e("interor", "k===="+k+"j====j");
                    paramUCBufData.buf[k] = paramUCBufData.buf[j];
                    j--;

                }
            }
        }
        return false;
    }


    private boolean SetCheckVal(UCBufData paramUCBufData)
    {
        if (3 + paramUCBufData.dataLen < paramUCBufData.maxBufLen)
        {
            long l1 = 0L;
            for (int i = 0;; i++)
            {
                if (i >= paramUCBufData.dataLen)
                {
                    long l2 = l1 & 0xFFFFFF;
                    byte[] arrayOfByte1 = paramUCBufData.buf;
                    int j = paramUCBufData.dataLen;
                    paramUCBufData.dataLen = (j + 1);
                    arrayOfByte1[j] = ((byte)(int)(0xFF & l2 >> 16));
                    byte[] arrayOfByte2 = paramUCBufData.buf;
                    int k = paramUCBufData.dataLen;
                    paramUCBufData.dataLen = (k + 1);
                    arrayOfByte2[k] = ((byte)(int)(0xFF & l2 >> 8));
                    byte[] arrayOfByte3 = paramUCBufData.buf;
                    int m = paramUCBufData.dataLen;
                    paramUCBufData.dataLen = (m + 1);
                    arrayOfByte3[m] = ((byte)(int)(l2 & 0xFF));
                    return true;
                }
                l1 += l1 * (0xFF & paramUCBufData.buf[i]);
            }
        }
        return false;
    }



    public boolean DecodeData(UCBufData paramUCBufData)
    {
        UCRand localUCRand;
        byte[] arrayOfByte;
        if (paramUCBufData != null)
        {
            localUCRand = new UCRand();
            localUCRand.SetSRand(this.v_uiKey);
            arrayOfByte = new byte[1];
            if (GetData(paramUCBufData, (int)(0xFFFF & localUCRand.Rand() % paramUCBufData.dataLen), arrayOfByte, 1))
            {
                DecodeData(paramUCBufData, localUCRand, 0xFF & arrayOfByte[0]);
                DeconvertData(paramUCBufData);
                if(GetCheckVal(paramUCBufData)){
                    return true;
                }

            }
        }
        return false;
    }

    public boolean EncodeData(UCBufData paramUCBufData)
    {
        UCRand localUCRand;
        int i;
        long l1;
        long l2;
        byte[] arrayOfByte;
        if (paramUCBufData != null)
        {
            localUCRand = new UCRand();
            localUCRand.SetSRand(this.v_uiKey);
            i = (int)((256.0D * Math.random()) % 256);//(int)(256.0D * Math.random())
            l1 = localUCRand.Rand();
//      Log.e("UCDeEncode","l1=="+l1);

            if (SetCheckVal(paramUCBufData)) {
//    	  Log.e("QQQQQQQQ!!!!111111","paramUCBufData.bufyyy === "+ImeiEncode.printHexString(paramUCBufData.buf)+"i========"+i);
                ConvertData(paramUCBufData);
//    	  Log.e("QQQQQQQQ!!!!222222","paramUCBufData.bufyyy === "+ImeiEncode.printHexString(paramUCBufData.buf)+"i========"+i);
//    	  Log.e("y22222222222222","paramUCBufData.dataLen === "+paramUCBufData.dataLen+"paramUCBufData.maxBufLen===="+paramUCBufData.maxBufLen);

            }
        }else{
            return false;
        }
//    label49:
//    long l2;
//    byte[] arrayOfByte;
//    do
//    {
//      return false;
//      ConvertData(paramUCBufData);
//      Log.e("yyyyxxxx","paramUCBufData.bufyyy === "+ImeiEncode.printHexString(paramUCBufData.buf)+"i========"+i);
//      Log.e("1111111111111","paramUCBufData.dataLen === "+paramUCBufData.dataLen+"paramUCBufData.maxBufLen===="+paramUCBufData.maxBufLen);
        EncodeData(paramUCBufData, localUCRand, i);
//      Log.e("22222222222222","paramUCBufData.dataLen === "+paramUCBufData.dataLen+"paramUCBufData.maxBufLen===="+paramUCBufData.maxBufLen);
//      Log.e("yyyyyyyyZZZZZZZZZ","paramUCBufData.bufyyy === "+ImeiEncode.printHexString(paramUCBufData.buf));
        l2 = l1 % (1 + paramUCBufData.dataLen);
//      Log.e("UCDeEncodel2","l2===="+l2);
        arrayOfByte = new byte[1];
        arrayOfByte[0] = ((byte)(i & 0xFF));
        if(InsertData(paramUCBufData, (int)(0xFFFF & l2), arrayOfByte, 1)){
//    	Log.e("xxxxxxxxxxxxxxxxx","paramUCBufData.bufyyy ????????????"+ImeiEncode.printHexString(paramUCBufData.buf));

            return true;
        }
//    Log.e("yyyyyyyyQQQQQQQQQQQQQQQQQQ","paramUCBufData.bufyyy === "+ImeiEncode.printHexString(paramUCBufData.buf));
        return false;
    }


    public void SetKey(byte[] paramArrayOfByte, int paramInt)
    {
        long l = 0;
        if (paramArrayOfByte != null) {
            l = 1L;

            for (int i = 0;i<paramInt; i++)
            {

                l = (l * (0xFF & paramArrayOfByte[i])) % 16777216L;
            }
            this.v_uiKey = l;
        }
    }
}