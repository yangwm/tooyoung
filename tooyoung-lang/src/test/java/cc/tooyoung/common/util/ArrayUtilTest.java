/**
 * 
 */
package cc.tooyoung.common.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * array util test
 * 
 * @author yangwm Mar 2, 2012 9:20:46 PM
 */
public class ArrayUtilTest {
    
    @Test
    public void testToRawIntArr(){
        String[] inputArray1 = { "2", "2", "4", "6", "8", "15" };
        
        int[] result = ArrayUtil.toRawIntArr(inputArray1);
        Assert.assertArrayEquals(new int[] { 2, 2, 4, 6, 8, 15 }, result);
    }
    
    @Test
    public void testReverseCopy(){
        long[] inputArray1 = { 2, 2, 4, 6, 8, 15 };
        
        long[] result = ArrayUtil.reverseCopy(inputArray1, inputArray1.length);
        Assert.assertArrayEquals(new long[] { 15, 8, 6, 4, 2, 2 }, result);
    }
    
    @Test
    public void testSort() {
        long[] inputArray1 = { 2, 2, 4, 6, 8, 15 };
        long[] inputArray2 = { 0, 0, 1, 3, 7, 9, 10, 11 };
        long[] result = ArrayUtil.sort(inputArray1, inputArray2);
        Assert.assertArrayEquals(new long[] {0, 0, 1, 2, 2, 3, 4, 6, 7, 8, 9, 10, 11, 15}, result);
        
        result = ArrayUtil.sort(inputArray1, new long[]{});
        Assert.assertArrayEquals(inputArray1, result);
        
        result = ArrayUtil.sort(new long[]{}, inputArray2);
        Assert.assertArrayEquals(inputArray2, result);
    }

    @Test
    public void testSortDesc() {
        long[] inputArray1 = { 2, 2, 4, 6, 8, 15 };
        ArrayUtil.sortDesc(inputArray1);
        System.out.println(Arrays.toString(inputArray1));
        Assert.assertArrayEquals(new long[] { 15, 8, 6, 4, 2, 2 }, inputArray1);
    }
    
    @Test
    public void testAddToArrays(){
        long[] array1 = { 2, 2, 4, 6, 8, 15 };
        long id = 345;
        long[] result = ArrayUtil.addTo(array1, id);
        Assert.assertArrayEquals(new long[] { 2, 2, 4, 6, 8, 15, 345 }, result);
    }
    
    @Test
    public void testAddToLimit() {
            long[] array1 = { 2, 2, 4, 6, 8, 15, 17 };
            long id = 345;
            int limit = 10;
            
            long[] result = ArrayUtil.addTo(array1, id, limit);
            Assert.assertArrayEquals(new long[] { 2, 2, 4, 6, 8, 15, 17, 345 }, result);
            
            result = ArrayUtil.addTo(result, 4, limit);
            Assert.assertArrayEquals(new long[] { 2, 2, 4, 6, 8, 15, 17, 345}, result);
            
            result = ArrayUtil.addTo(result, 124, limit);
            result = ArrayUtil.addTo(result, 125, limit);
            Assert.assertArrayEquals(new long[] { 2, 2, 4, 6, 8, 15, 17, 345, 124, 125}, result);
            
            result = ArrayUtil.addTo(result, 126, limit);
            Assert.assertArrayEquals(new long[] { 2, 4, 6, 8, 15, 17, 345, 124, 125, 126}, result);
            result = ArrayUtil.addTo(result, 127, limit);
            Assert.assertArrayEquals(new long[] { 4, 6, 8, 15, 17, 345, 124, 125, 126, 127}, result);       
    }

    @Test
    public void testGetLimited(){
            long[] array1 = { 1, 2, 4, 6, 8, 15, 17 };
            long[] array2 = { 1, 2, 4, 6, 8, 15, 17, 18, 19, 20, 21};
            long[] array3 = null;
            long[] array4 = {};
            int limit = 10;
            
            long[] result = ArrayUtil.getLimited(array1, limit);
            Assert.assertArrayEquals(new long[] { 1, 2, 4, 6, 8, 15, 17}, result);
            result = ArrayUtil.getLimited(array2, limit);
            Assert.assertArrayEquals(new long[] { 2, 4, 6, 8, 15, 17, 18, 19, 20, 21}, result);
            result = ArrayUtil.getLimited(array3, limit);
            Assert.assertArrayEquals(null, result);
            result = ArrayUtil.getLimited(array4, limit);
            Assert.assertArrayEquals(new long[]{}, result);
    }
    
    @Test
    public void testCopy(){
        long[] array1 = { 0, 0, 1, 2, 2, 3, 4, 6, 7, 8, 9, 10, 11, 15 };
        
        long[] result = Arrays.copyOf(array1, array1.length);
        Assert.assertArrayEquals(new long[] { 0, 0, 1, 2, 2, 3, 4, 6, 7, 8, 9, 10, 11, 15 }, result);
        
        result = Arrays.copyOfRange(array1, array1.length - 10, array1.length);
        Assert.assertArrayEquals(new long[] { 2, 3, 4, 6, 7, 8, 9, 10, 11, 15 }, result);
        
        int vectorFrom = array1.length - 15;
        if (vectorFrom < 0) {
            vectorFrom = 0;
        }
        result = Arrays.copyOfRange(array1, vectorFrom, array1.length);
        Assert.assertArrayEquals(new long[] { 0, 0, 1, 2, 2, 3, 4, 6, 7, 8, 9, 10, 11, 15 }, result);
    }
    
    @Test
    public void testRemoveAll() {
        long[] sources = { 5, 9, 2, 8, 11, 7, 18, 10, 12, 3 };
        long[] removes = { 5, 3 };
        long[] results = ArrayUtil.removeAll(sources, removes);
        //System.out.println(Arrays.toString(results));
        Assert.assertEquals(results.length, sources.length - removes.length);
        
        removes = new long[]{ 5, 3, 11, 10 };
        results = ArrayUtil.removeAll(sources, removes);
        //System.out.println(Arrays.toString(results));
        Assert.assertEquals(results.length, sources.length - removes.length);
    }
    @Test
    public void testRemoveAllFor() {
        long init = 3000000000000000L;
        
        long[] sources = new long[10];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = init + i;
        }
        //System.out.println("++++++++++++++sources:" + Arrays.toString(sources));
        
        for (int i = 0; i < 10; i++) {
            HashSet<Long> removeSet = new HashSet<Long>();
            for (int j = 0; j < i; j++) {
                removeSet.add(init + RandomUtils.nextInt(i));
            }
            long[] removes = new long[removeSet.size()];
            int j = 0;
            for (Long remove : removeSet) {
                removes[j++] = remove;
            }
            
            //System.out.println("--------------removes:" + Arrays.toString(removes));
            long[] results = ArrayUtil.removeAll(sources, removes);
            //System.out.println("--------------results:" + Arrays.toString(results));
            Assert.assertEquals(results.length, sources.length - removes.length);
        }
    }

    @Test
    public void testRemoveAllSet() {
        long[] sources = { 5, 9, 2, 8, 11, 7, 18, 10, 12, 3 };
        long[] removes = { 5, 3 };
        Set<Long> removeSet = new HashSet<Long>(removes.length);
        for (long remove : removes) {
            removeSet.add(remove);
        }
        long[] results = ArrayUtil.removeAll(sources, removes);
        Assert.assertEquals(results.length, sources.length - removes.length);
        Assert.assertArrayEquals(results, new long[]{ 9, 2, 8, 11, 7, 18, 10, 12 });
        
        removes = new long[]{ 5, 3, 11, 10 };
        removeSet = new HashSet<Long>(removes.length);
        for (long remove : removes) {
            removeSet.add(remove);
        }
        results = ArrayUtil.removeAll(sources, removes);
        Assert.assertEquals(results.length, sources.length - removes.length);
        Assert.assertArrayEquals(results, new long[]{ 9, 2, 8, 7, 18, 12 });
    }
    @Test
    public void testRemoveAllSetFor() {
        long init = 3000000000000000L;
        
        long[] sources = new long[10];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = init + i;
        }
        
        for (int i = 0; i < 10; i++) {
            HashSet<Long> removeSet = new HashSet<Long>();
            for (int j = 0; j < i; j++) {
                removeSet.add(init + RandomUtils.nextInt(i));
            }
            
            long[] results = ArrayUtil.removeAll(sources, removeSet);
            Assert.assertEquals(results.length, sources.length - removeSet.size());
        }
    }
}
