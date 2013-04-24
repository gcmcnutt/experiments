package com.accelero.merkle;

public class Leaf {
    private Integer index;
    private byte[] data;

    public Leaf(Integer index, byte[] data) {
        this.index = index;
        this.data = data;

    }

    public Integer getIndex() {
        return index;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("index[%d] len[%d]", index, data.length);
    }
}
