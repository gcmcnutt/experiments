package com.accelero.merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static Hash generateHash(byte[] data) throws RuntimeException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't proceed", e);
        }
        return new Hash(md.digest(data));
    }

    public static Hash generateHash(Hash left, Hash right) throws RuntimeException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can't proceed", e);
        }
        md.update(left.getBytes());
        md.update(right.getBytes());
        return new Hash(md.digest());
    }
}
