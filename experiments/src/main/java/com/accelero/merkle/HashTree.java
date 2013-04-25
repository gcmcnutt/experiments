package com.accelero.merkle;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

public class HashTree {
    private static final Logger LOGGER = Logger.getLogger(HashTree.class);

    private Node                root;
    private int                 nodes;

    // preallocate the hashtree
    public HashTree(int nodes) {
        this.nodes = nodes;
        root = new Node(nodes);
    }

    public void updateElement(Integer index, byte[] data) {
        // get a leaf, figure out its hash and merge it in
        Leaf newLeaf = new Leaf(index, data);

        // binary search for our node - keep track of the path
        Stack<Node> stack = new Stack<Node>();
        int size = nodes;
        Node walker = root;
        while (size > 1) {
            stack.push(walker);
            int half = size / 2;
            if (index < half) {
                walker = walker.getLeft();
                size = half;
            } else {
                walker = walker.getRight();
                size -= half;
                index -= half;
            }
        }

        // found the node, update it and walk our way back and update the hash
        walker.setLeaf(newLeaf);

        while (!stack.isEmpty()) {
            stack.pop().updateHash();
        }
    }

    public List<Integer> getDifferences(HashTree other) {
        List<Integer> diffs = new ArrayList<Integer>();

        // well, we walk each tree in order, if hash is different, we recurse, otherwise we are done
        compareTrees(diffs, this.root, other.root);

        return diffs;
    }

    private static void compareTrees(List<Integer> diffs, Node us, Node them) {
        if (us.getLeaf() != null) {
            if (!us.getHash().equals(them.getHash())) {
                diffs.add(us.getLeaf().getIndex());
            }
        } else {
            if (!us.getHash().equals(them.getHash())) {
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
            f.format("left[%b] right[%b] hash[%s]\n", node.getLeft() != null, node.getRight() != null, node.getHash());
            dump(f, node.getLeft(), depth + 1);
            dump(f, node.getRight(), depth + 1);
        }
    }
}
