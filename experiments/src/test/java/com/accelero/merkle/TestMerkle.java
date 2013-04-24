package com.accelero.merkle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMerkle extends Assert {
    private static final Logger  LOGGER        = Logger.getLogger(TestMerkle.class);

    public static final String[] BLOCKS        = { "String 1", "String 2", "A log string in a short world", "Some more data", "Lastly, the last data",
            "Lastly, the last data", "One more string to keep count odd" };

    public static final String[] REMOTE_BLOCKS = { "String 1", "String 2", "A log string in a short world", "Some more data", "Lastly, the last dataXXX",
            "Lastly, the last data", "One more string to keep count odd" };

    @Test
    public void helloWorld() {
        HashTree h = new HashTree(BLOCKS.length);
        HashTree hr = new HashTree(REMOTE_BLOCKS.length);
        for (int i = 0; i < BLOCKS.length; i++) {
            h.updateElement(i, BLOCKS[i].getBytes());
        }
        for (int i = 0; i < REMOTE_BLOCKS.length; i++) {
            hr.updateElement(i, REMOTE_BLOCKS[i].getBytes());
        }

        LOGGER.debug(h);
        LOGGER.debug(hr);

        List<Integer> diff = h.getDifferences(hr);
        LOGGER.debug(String.format("Differences[%s]", diff));
    }

    private static final int    MORE_SIZE = 22000;
    private static final int    ROW_SIZE  = 4096;
    private static final double ERR_RATE  = 0.001;

    @Test
    public void testMore() {
        Random r = new Random();
        List<Integer> exceptions = new ArrayList<Integer>();

        HashTree in = new HashTree(MORE_SIZE);
        HashTree out = new HashTree(MORE_SIZE);

        byte[][] data = new byte[MORE_SIZE][];
        for (int i = 0; i < MORE_SIZE; i++) {
            data[i] = new byte[ROW_SIZE];
            r.nextBytes(data[i]);
            in.updateElement(i, data[i]);

            if (r.nextDouble() < ERR_RATE) {
                exceptions.add(i);
                data[i][0] ^= 0xff;
            }
            out.updateElement(i, data[i]);
        }

        List<Integer> diff = in.getDifferences(out);
        LOGGER.debug(String.format("Expect[%s]", exceptions));
        LOGGER.debug(String.format("Found [%s]", diff));

    }

    @Test(threadPoolSize = 2, invocationCount = 1000)
    public void testMoreRandom() {
        Random r = new Random();
        List<Integer> exceptions = new ArrayList<Integer>();

        // init the random data array
        int colSize = 500 + r.nextInt(20000);
        int rowSize = 4096;
        double errRate = r.nextDouble() * 0.01;

        HashTree in = new HashTree(colSize);
        HashTree out = new HashTree(colSize);
        ArrayList<Integer> inList = new ArrayList<Integer>(colSize);
        ArrayList<Integer> outList = new ArrayList<Integer>(colSize);

        byte[][] data = new byte[colSize][];
        for (int i = 0; i < colSize; i++) {
            data[i] = new byte[rowSize];
            r.nextBytes(data[i]);
            inList.add(i);
            outList.add(i);
        }

        // now do random inserts into in tree
        while (inList.size() > 0) {
            int ptr = r.nextInt(inList.size());
            int index = inList.get(ptr);
            in.updateElement(index, data[index]);
            inList.remove(ptr);
        }

        while (outList.size() > 0) {
            int ptr = r.nextInt(outList.size());
            int index = outList.get(ptr);
            if (r.nextDouble() < errRate) {
                exceptions.add(index);
                data[index][0] ^= 0xff;
            }
            out.updateElement(index, data[index]);
            outList.remove(ptr);
        }

        Collections.sort(exceptions);

        //LOGGER.debug(in);
        //LOGGER.debug(out);

        List<Integer> diff = in.getDifferences(out);
        //LOGGER.debug(String.format("Expect[%s]", exceptions));
        //LOGGER.debug(String.format("Found [%s]", diff));

        assertEquals(exceptions, diff);
        LOGGER.info("Compare is ok");
    }


}
