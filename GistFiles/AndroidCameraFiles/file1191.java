package com.ctrlsmart.bcDecode;

import android.graphics.Point;

import java.lang.reflect.Array;

/**
 * Created by Administrator on 2015/4/12.
 */
public class UCIdentifySD4Barcode
{
    private static final int USER_DEF_MAX_RESULT = 10;
    private static boolean useBinary = false;
    private int[][] v_QRbbox;
    private int[][] v_SDbbox;
    private int v_iLevel;
    private String[] v_strQRResult = new String[10];
    private String[] v_strSD4Result = new String[10];
    private ZBarImageScanner v_ucImgScanner = new ZBarImageScanner();

    public UCIdentifySD4Barcode()
    {
        int[] arrayOfInt1 = { 10, 8 };
        this.v_SDbbox = ((int[][]) Array.newInstance(Integer.TYPE, arrayOfInt1));
        int[] arrayOfInt2 = { 10, 8 };
        this.v_QRbbox = ((int[][])Array.newInstance(Integer.TYPE, arrayOfInt2));
        this.v_iLevel = 0;
    }

    public int GetErrorLevel()
    {
        return this.v_iLevel;
    }

    public Point GetLeftBottomPos()
    {
        return new Point(this.v_SDbbox[0][6], this.v_SDbbox[0][7]);
    }

    public Point GetLeftTopPos()
    {
        return new Point(this.v_SDbbox[0][0], this.v_SDbbox[0][1]);
    }

    public Point GetRightBottomPos()
    {
        return new Point(this.v_SDbbox[0][4], this.v_SDbbox[0][5]);
    }

    public Point GetRightTopPos()
    {
        return new Point(this.v_SDbbox[0][2], this.v_SDbbox[0][3]);
    }

    public String IdentifyJSBarcode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
//        if (useBinary)
//        {
//            byte[][] arrayOfByte = UCConvertImg.Convert1DImgTo2DImg(paramArrayOfByte, paramInt1, paramInt2);
//            UCBinary.BinaryImg(arrayOfByte);
//            UCSmothBinaryImg.Process(arrayOfByte);
//            System.arraycopy(UCConvertImg.ConvertGrayImgFrom2D(arrayOfByte), 0, paramArrayOfByte, 0, paramArrayOfByte.length);
//        }
//        if (useBinary) {}
//        for (boolean bool = false;; bool = true)
//        {
//            useBinary = bool;
//            this.v_iLevel = this.v_ucImgScanner.zbar_scans_image(paramArrayOfByte, paramInt1 * paramInt2, paramInt1, paramInt2, this.v_strSD4Result, this.v_strQRResult, this.v_SDbbox, this.v_QRbbox);
//            if (this.v_iLevel == -1) {
//                break;
//            }
//            return this.v_strSD4Result[0];
//        }
        return null;
    }
}

