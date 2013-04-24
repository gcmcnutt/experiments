package com.accelero.merkle;

import java.util.Formatter;

import org.apache.log4j.Logger;

public class Node {
    private static final Logger LOGGER = Logger.getLogger(Leaf.class);

    private Node left;
    private Node right;
    private byte[] hash;
    private Leaf leaf;

    // pre-allocates nodes based on the count
    public Node(int count) {
        if (count <= 1) {
            // initial hash isn't too interesting
            this.hash = Util.generateHash(new byte[0]);
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Set end hash: hash[%s]", Util.byteArray2Hex(hash)));
            }
            return;
        }

        int half = count / 2;
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug(String.format("Generate children: count[%d] half[%d]", count, half));
        }
        left = new Node(half);
        right = new Node(count - half);
        updateHash();
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public byte[] getHash() {
        return hash;
    }

    public Leaf getLeaf() {
        return leaf;
    }

    public void setLeaf(Leaf leaf) {
        this.leaf = leaf;
        this.hash = Util.generateHash(leaf.getData());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("Set Leaf Node: " + this);
        }
    }

    public void updateHash() {
        hash = Util.generateHash(left.hash, right.hash);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("Set Node Hash: " + this);
        }
    }

    @Override
    public String toString() {
        Formatter f = new Formatter();
        f.format("Hash[%s] Leaf[%s]", Util.byteArray2Hex(hash), leaf);
        return f.toString();
    }
}