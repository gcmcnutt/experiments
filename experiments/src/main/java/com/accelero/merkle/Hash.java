package com.accelero.merkle;

import java.util.Arrays;
import java.util.Formatter;

public class Hash {
    private byte[] hash;

    public Hash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getBytes() {
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        return Arrays.equals(hash, ((Hash)other).hash);
    }

    @Override
    public String toString() {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
