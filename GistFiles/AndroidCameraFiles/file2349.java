package com.ctrlsmart.bcDecode;

/**
 * Created by Administrator on 2015/4/12.
 */
public class UCIdentifySDBarCode
{
//    private static UCIdentifySD4Barcode v_ucIdentifyJSBarcodeMiainCtrl = new UCIdentifySD4Barcode();
//
//    public static int GetErrorLevel()
//    {
//        return v_ucIdentifyJSBarcodeMiainCtrl.GetErrorLevel();
//    }
//
//    public static ResultPoint GetLeftBottom()
//    {
//        return GetReslutPoint(v_ucIdentifyJSBarcodeMiainCtrl.GetLeftBottomPos());
//    }
//
//    public static ResultPoint GetLeftTop()
//    {
//        return GetReslutPoint(v_ucIdentifyJSBarcodeMiainCtrl.GetLeftTopPos());
//    }
//
//    public static ResultPoint GetReslutPoint(Point paramPoint)
//    {
//        if (paramPoint == null) {
//            return null;
//        }
//        return new ResultPoint(paramPoint.x, paramPoint.y);
//    }
//
//    public static ResultPoint GetRightBottom()
//    {
//        return GetReslutPoint(v_ucIdentifyJSBarcodeMiainCtrl.GetRightBottomPos());
//    }
//
//    public static ResultPoint GetRightTop()
//    {
//        return GetReslutPoint(v_ucIdentifyJSBarcodeMiainCtrl.GetRightTopPos());
//    }
//
//    public static String IdentifySDBarCode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//    {
//        if (paramArrayOfByte == null) {
//            return null;
//        }
//        return v_ucIdentifyJSBarcodeMiainCtrl.IdentifyJSBarcode(paramArrayOfByte, paramInt2, paramInt1);
//    }
//
//    public static Result SDDecode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
//    {
//        String str = IdentifySDBarCode(paramArrayOfByte, paramInt1, paramInt2);
//        if ((str != null) && (str != ""))
//        {
//            ResultPoint[] arrayOfResultPoint = new ResultPoint[4];
//            arrayOfResultPoint[0] = GetLeftTop();
//            arrayOfResultPoint[1] = GetRightTop();
//            arrayOfResultPoint[2] = GetRightBottom();
//            arrayOfResultPoint[3] = GetLeftBottom();
//            return new Result(str, null, arrayOfResultPoint, BarcodeFormat.QR_CODE);
//        }
//        return null;
//    }
}
