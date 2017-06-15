package com.stonete.qrtoken.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;

import com.stonete.qrtoken.sharedperfences.GetEncryptLaterPrivate_Key;

public class SHA256Encrypt {

    private static byte[] getHash(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        digest.reset();
        return digest.digest(password.getBytes());
    }

    public static String bin2hex(String strForEncrypt) {
        byte[] data = getHash(strForEncrypt);
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static String getsharedperMD5(Context ctx) {
        String mprikey = GetEncryptLaterPrivate_Key.getprivatekey(ctx);
        // 对私钥进行sha256加密
        try {
            String msha256 = SHA256Encrypt.bin2hex(mprikey);
            String nsha256 = msha256.toLowerCase();
            return nsha256;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}