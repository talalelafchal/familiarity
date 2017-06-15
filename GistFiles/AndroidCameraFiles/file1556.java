package com.ctrlsmart.dataWrap;

/**
 * Created by Administrator on 2015/3/31.
 */
public class UCBufData
{
    public static byte[] buf = null;
    public int dataLen = 0;
    public int maxBufLen = 0;

    public void BuildBufData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
        this.buf = paramArrayOfByte;
        this.dataLen = paramInt1;
        this.maxBufLen = paramInt2;
    }
}