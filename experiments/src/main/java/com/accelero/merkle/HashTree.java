package com.accelero.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import org.apache.log4j.Logger;

public class HashTree {
    private static final Logger LOGGER = Logger.getLogger(HashTree.class);

    private Node root;
    private int nodes;

    // preallocate the hashtree
    public HashTree(int nodes) {
        this.nodes = nodes;
        root = new Node(nodes);
    }

    public void updateElement(Integer index, byte[] data) {
        // get a leaf, figure out its hash and merge it in
        Leaf newLeaf = new Leaf(index, data);

        // ok, we got a leaf, figure out where to put it in the tree
        resetLeafNode(root, nodes, index, newLeaf);
    }

    private void resetLeafNode(Node node, int size, int relativeIndex, Leaf newLeaf) {
        // are we the node?
        if (size <= 1) {
            // yes.  and this'll set the hash at our node level too
            node.setLeaf(newLeaf);
            return;
        }

        // binary search for our leaf
        int half = size / 2;
        if (relativeIndex < half) {
            resetLeafNode(node.getLeft(), half, relativeIndex, newLeaf);
        } else {
            resetLeafNode(node.getRight(), size - half, relativeIndex - half, newLeaf);
        }

        // since we're not a leaf node, recompute our hash from our kids
        node.updateHash();
    }

    public List<Integer> getDifferences(HashTree other) {
        List<Integer> diffs = new ArrayList<Integer>();

        // well, we walk each tree in order, if hash is different, we recurse, otherwise we are done
        compareTrees(diffs, this.root, other.root);

        return diffs;
    }

    private static void compareTrees(List<Integer> diffs, Node us, Node them) {
        if (us.getLeaf() != null) {
            if (!Arrays.equals(us.getHash(), them.getHash())) {
                diffs.add(us.getLeaf().getIndex());
            }
        } else {
            if (!Arrays.equals(us.getHash(), them.getHash())) {
                compareTrees(diffs, us.getLeft(), them.getLeft());
                compareTrees(diffs, us.getRight(), them.getRight());
            }
        }
    }

    @Override
    public String toString() {
        Formatter f = new Formatter();
        f.format("HashTree: Nodes[%d]\n", nodes);
        dump(f, this.root, 1);
        return f.toString();
    }

    private void dump(Formatter f, Node node, int depth) {
        String s = "%" + depth + "s Depth[%d] ";
        f.format(s, " ", depth);
        if (node == null) {
            f.format("node=null?\n");
        } else if (node.getLeaf() != null) {
            f.format("leaf[%s]\n", node);
        } else {
            f.format("left[%b] right[%b] hash[%s]\n", node.getLeft() != null, node.getRight() != null, Util.byteArray2Hex(node.getHash()));
            dump(f, node.getLeft(), depth + 1);
            dump(f, node.getRight(), depth + 1);
        }
    }
}
