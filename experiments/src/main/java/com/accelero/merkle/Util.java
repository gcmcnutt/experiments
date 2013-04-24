package com.accelero.merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Util {

    public static byte[] generateHash(byte[] data) throws RuntimeException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't proceed", e);
        }
        return md.digest(data);
    }

    public static byte[] generateHash(byte[] left, byte[] right) throws RuntimeException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't proceed", e);
        }
        md.update(left);
        md.update(right);
        return md.digest();
    }

    public static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
