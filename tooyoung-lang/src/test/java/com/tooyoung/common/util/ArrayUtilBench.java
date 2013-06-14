/**
 * 
 */
package com.tooyoung.common.util;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

/**
 * 
 * @author yangwm Mar 4, 2012 2:27:15 PM
 */
public class ArrayUtilBench {
    
    private static Random rand = new Random();
    private static final long[] getRandom200() {
        long[] _values = new long[200];
        for (int i = 0; i < 200; i++) {
            _values[i] = rand.nextInt(Integer.MAX_VALUE);
        }
        Arrays.sort(_values);
        return _values;
    }

    /*
     * testSort cosume time 124
     */
    @Test
    public void testSort() {
        long[] inputArray1 = getRandom200();
        long[] inputArray2 = getRandom200();
        
        long beginTime = System.nanoTime();
        for (int i = 0; i < 2000; i++) {
            long[] result = ArrayUtil.sort(inputArray1, inputArray2);
            int resultLength = result.length;
        }
        long cosumeTime = System.nanoTime() - beginTime;
        System.out.println("testSort cosume time " + (cosumeTime/1000000));
    }

    protected static ThreadLocal<long[]> localBuf = new ThreadLocal<long[]>() {
        protected long[] initialValue() {
            return new long[(210) * 2000];
        }
    };
    
    /*
     * get totalIds cosume time 120
     * testGetSortIds cosume time 219
     */
    @Test
    public void testGetSortIds() {
        long beginTime = System.nanoTime();
        long[] dest = localBuf.get();
        int pos = 0;
        for (int i = 0; i < 2000; i++) {
            long[] values = getRandom200();
            System.arraycopy(values, 0, dest, pos, values.length);               
            pos += values.length;
        }
        long[] totalIds = new long[pos];
        System.arraycopy(dest, 0, totalIds, 0, pos);
        long cosumeTime = System.nanoTime() - beginTime;
        System.out.println("get totalIds cosume time " + (cosumeTime/1000000));
        Arrays.sort(totalIds);
        
        ArrayUtil.reverse(totalIds);
        cosumeTime = System.nanoTime() - beginTime;
        System.out.println("testGetSortIds cosume time " + (cosumeTime/1000000) + ", totalIds length:" + totalIds.length 
                + ", result's max 200:" + Arrays.toString(Arrays.copyOf(totalIds, 200)));
    }
    
}
