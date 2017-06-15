package com.stonete.qrtoken.utils;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.stonete.qrtoken.decoding.RGBLuminanceSource;

/**
 * Created by kangwei on 2015/3/4.
 */
public class QrCodeUtil {

    public static String getQrCodeFromPhoto(String photoPath){
        if (TextUtils.isEmpty(photoPath)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        BitmapFactory.Options options = new BitmapFactory.Options();
        int sampleSize = (int) (options.outHeight / (float) 300);
        if (sampleSize <= 0) sampleSize = 1;
        options.inSampleSize = sampleSize;
        Bitmap scanBitmap = BitmapFactory.decodeFile(photoPath, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();

        String qrCode = null;
        try {
            qrCode = reader.decode(bitmap1, hints).getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qrCode;
    }
}
