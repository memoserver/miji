package com.mijimemo.server.util;
import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA {
    public static final String KEY_SHA = "SHA";

    public static String getSHA(String text) {
        BigInteger sha = null;
        byte[] inputData = text.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(inputData);
            sha = new BigInteger(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha.toString(32);
    }
}
