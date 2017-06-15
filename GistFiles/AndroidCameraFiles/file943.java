package com.ctrlsmart.bcDecode;

/**
 * Created by Administrator on 2015/4/13.
 */
public class UCConvertImg
{
//    public static byte[][] Convert1DImgTo2DImg(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//    {
//        int i = 4 * ((31 + paramInt1 * 8) / 32);
//        int[] arrayOfInt = { paramInt2, i };
//        byte[][] arrayOfByte = (byte[][]) Array.newInstance(Byte.TYPE, arrayOfInt);
//        int j = 0;
//        int k = 0;
//        if (k >= paramInt2) {
//            return arrayOfByte;
//        }
//        int m = 0;
//        for (;;)
//        {
//            if (m >= i)
//            {
//                k++;
//                break;
//            }
//            arrayOfByte[k][m] = paramArrayOfByte[j];
//            m++;
//            j++;
//        }
//    }
//
//    public static byte[][] Convert24BitsImgTo8BitsImg(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//    {
//        if (paramArrayOfByte == null)
//        {
//            arrayOfByte = null;
//            return arrayOfByte;
//        }
//        int i = 4 * ((31 + paramInt1 * 24) / 32);
//        int[] arrayOfInt = { paramInt2, 4 * ((31 + paramInt1 * 8) / 32) };
//        byte[][] arrayOfByte = (byte[][])Array.newInstance(Byte.TYPE, arrayOfInt);
//        int j = 0;
//        int k = 0;
//        label66:
//        int m;
//        if (k < paramInt2) {
//            m = j;
//        }
//        for (int n = 0;; n++)
//        {
//            if (n >= paramInt1)
//            {
//                j += i;
//                k++;
//                break label66;
//                break;
//            }
//            int i1 = paramArrayOfByte[m];
//            int i2 = paramArrayOfByte[(m + 1)];
//            int i3 = paramArrayOfByte[(m + 2)];
//            arrayOfByte[k][n] = ((byte)(0xFF & i3 * 77 + i2 * 151 + i1 * 28 >> 8));
//            m += 3;
//        }
//    }
//
//    public static byte[] ConvertBinaryImgToGray(int[][] paramArrayOfInt)
//    {
//        int i = paramArrayOfInt.length;
//        if (i > 0)
//        {
//            int j = paramArrayOfInt[0].length;
//            if (j > 0)
//            {
//                byte[] arrayOfByte = new byte[j * i];
//                int k = 0;
//                int m = i - 1;
//                if (m < 0) {
//                    return arrayOfByte;
//                }
//                int n = 0;
//                for (;;)
//                {
//                    if (n >= j)
//                    {
//                        m--;
//                        break;
//                    }
//                    arrayOfByte[k] = ((byte)(0xFF & paramArrayOfInt[m][n]));
//                    n++;
//                    k++;
//                }
//            }
//        }
//        return null;
//    }
//
//    public static byte[] ConvertBinaryImgToGray(boolean[][] paramArrayOfBoolean)
//    {
//        int i = paramArrayOfBoolean.length;
//        if (i > 0)
//        {
//            int j = paramArrayOfBoolean[0].length;
//            if (j > 0)
//            {
//                byte[] arrayOfByte = new byte[j * i];
//                int k = 0;
//                int n;
//                for (int m = i - 1;; m--)
//                {
//                    if (m < 0) {
//                        return arrayOfByte;
//                    }
//                    n = 0;
//                    if (n < j) {
//                        break;
//                    }
//                }
//                if (paramArrayOfBoolean[m][n] != 0) {
//                    arrayOfByte[k] = 0;
//                }
//                for (;;)
//                {
//                    n++;
//                    k++;
//                    break;
//                    arrayOfByte[k] = -1;
//                }
//            }
//        }
//        return null;
//    }
//
//    public static byte[] ConvertGrayImgFrom2D(byte[][] paramArrayOfByte)
//    {
//        int i = paramArrayOfByte.length;
//        if (i > 0)
//        {
//            int j = paramArrayOfByte[0].length;
//            if (j > 0)
//            {
//                byte[] arrayOfByte = new byte[j * i];
//                int k = 0;
//                int m = 0;
//                if (m >= i) {
//                    return arrayOfByte;
//                }
//                int n = 0;
//                for (;;)
//                {
//                    if (n >= j)
//                    {
//                        m++;
//                        break;
//                    }
//                    arrayOfByte[k] = paramArrayOfByte[m][n];
//                    n++;
//                    k++;
//                }
//            }
//        }
//        return null;
//    }
//
//    public static byte[] ConvertGrayImgFrom2D_crop(byte[][] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
//    {
//        int i = Math.max(paramInt1, 0);
//        int j = Math.max(paramInt2, 0);
//        int k = Math.min(i + paramInt3, paramArrayOfByte[0].length) - i;
//        int m = Math.min(j + paramInt4, paramArrayOfByte.length) - j;
//        if ((m > 0) && (k > 0))
//        {
//            byte[] arrayOfByte = new byte[k * m];
//            int n = 0;
//            int i1 = j + (m - 1);
//            if (i1 < j) {
//                return arrayOfByte;
//            }
//            int i2 = i;
//            for (;;)
//            {
//                if (i2 >= k + i)
//                {
//                    i1--;
//                    break;
//                }
//                arrayOfByte[n] = paramArrayOfByte[i1][i2];
//                i2++;
//                n++;
//            }
//        }
//        return null;
//    }
//
//    public static int[][] Flip180Roate(int[][] paramArrayOfInt)
//    {
//        int i = paramArrayOfInt.length;
//        int[][] arrayOfInt = new int[i][];
//        for (int j = 0;; j++)
//        {
//            if (j >= i) {
//                return arrayOfInt;
//            }
//            arrayOfInt[j] = ((int[])paramArrayOfInt[(i - 1 - j)].clone());
//        }
//    }
//
//    public static byte[][] Flip180RoateGray(byte[][] paramArrayOfByte)
//    {
//        int i = paramArrayOfByte.length;
//        byte[][] arrayOfByte = new byte[i][];
//        for (int j = 0;; j++)
//        {
//            if (j >= i) {
//                return arrayOfByte;
//            }
//            arrayOfByte[j] = ((byte[])paramArrayOfByte[(i - 1 - j)].clone());
//        }
//    }
}

