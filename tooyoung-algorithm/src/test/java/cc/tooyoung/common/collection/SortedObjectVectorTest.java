/**
 * 
 */
package cc.tooyoung.common.collection;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.CommonConst;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.codec.CodecHandler;

/**
 * 
 * @author yangwm Jun 8, 2012 3:30:03 PM
 */
public class SortedObjectVectorTest {
    
    @Test 
    public void testSerialize() {
        Long[] items = new Long[210];
        for (int i = 0; i < items.length; i++) {
            items[i] = 2300000000000000L + i;
        }
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        byte[] bytes = CodecHandler.encode(vector);
        ApiLogger.debug("[OrderedVectorTest] testSerialize bytes.length:" + bytes.length);
        Assert.assertTrue(bytes.length < 200); // 170 
        
        vector = new SortedObjectVector<Long>(0, 0, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        bytes = CodecHandler.encode(vector);
        ApiLogger.debug("[OrderedVectorTest] testSerialize bytes.length:" + bytes.length);
        Assert.assertTrue(bytes.length < 200); // 170 
        
        vector = new SortedObjectVector<Long>(items);
        bytes = CodecHandler.encode(vector);
        ApiLogger.debug("[OrderedVectorTest] testSerialize bytes.length:" + bytes.length);
        Assert.assertTrue(bytes.length < 3400); // 3034 
    }
    
    @Test
    public void testLimit() {
        int c = 100;
        Long[] items = new Long[210 + c];
        for (int i = 0; i < items.length; i++) {
            items[i] = new Long(i + 1); 
        }
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        for (long item : items) {
            vector.add(item);
        }
        Assert.assertEquals(items.length - c, vector.getLen());
        //Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        vector = new SortedObjectVector<Long>(500, CommonConst.VECTOR_LIMIT, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        for (long item : items) {
            vector.add(item);
        }
        Assert.assertEquals(items.length - c, vector.getLen());
        //Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        vector = new SortedObjectVector<Long>(items);
        Assert.assertEquals(items.length - CommonConst.VECTOR_THRESHOLD - c, vector.getLen());
        //Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        vector = new SortedObjectVector<Long>(500, CommonConst.VECTOR_LIMIT, items);
        Assert.assertEquals(items.length - CommonConst.VECTOR_THRESHOLD - c, vector.getLen());
        //Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
    }
    
    @Test
    public void testDefaultEmptyAdd() {
        Long[] items = new Long[] { 2L, 3L, 4L, 6L, 7L, 8L, 9L, 10L, 11L, 15L, 202L, 203L, 204L, 206L, 207L, 208L, 209L, 210L, 211L, 215L };
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        for (long item : items) {
            vector.add(item);
        }
        Assert.assertEquals(items.length, vector.getLen());
        Assert.assertArrayEquals(items, vector.getActualItems());
        Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        testAddDelete(vector);
        Assert.assertEquals(items.length, vector.getLen());
        Assert.assertArrayEquals(items, vector.getActualItems());
        Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
    }
    
    @Test
    public void testLimitEmptyAdd() {
        Long[] items = new Long[] { 2L, 3L, 4L, 6L, 7L, 8L, 9L, 10L, 11L, 15L, 202L, 203L, 204L, 206L, 207L, 208L, 209L, 210L, 211L, 215L };
        int limit = 10;
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(limit, limit, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        for (long item : items) {
            vector.add(item);
        }
        Assert.assertEquals(items.length, vector.getLen());
        Assert.assertArrayEquals(items, vector.getActualItems());
        Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        testAddDelete(vector);
        Assert.assertEquals(limit < items.length ? limit : items.length, vector.getLen());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), vector.getActualItems());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), Arrays.copyOf(vector.getItems(), vector.getLen()));
    }
    
    @Test
    public void testDefaultNewAdd() {
        Long[] items = new Long[] { 2L, 3L, 4L, 6L, 7L, 8L, 9L, 10L, 11L, 15L, 202L, 203L, 204L, 206L, 207L, 208L, 209L, 210L, 211L, 215L };
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(items);
        Assert.assertEquals(items.length, vector.getLen());
        Assert.assertArrayEquals(items, vector.getActualItems());
        Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        testAddDelete(vector);
        Assert.assertEquals(items.length, vector.getLen());
        Assert.assertArrayEquals(items, vector.getActualItems());
        Assert.assertArrayEquals(items, Arrays.copyOf(vector.getItems(), vector.getLen()));
    }
    
    @Test
    public void testLimitNewAdd() {
        Long[] items = new Long[] { 2L, 3L, 4L, 6L, 7L, 8L, 9L, 10L, 11L, 15L, 202L, 203L, 204L, 206L, 207L, 208L, 209L, 210L, 211L, 215L };
        int limit = 10;
        
        SortedObjectVector<Long> vector = new SortedObjectVector<Long>(limit, limit, items);
        Assert.assertEquals(limit, vector.getLen());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), vector.getActualItems());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), Arrays.copyOf(vector.getItems(), vector.getLen()));
        
        testAddDelete(vector);
        Assert.assertEquals(limit < items.length ? limit : items.length, vector.getLen());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), vector.getActualItems());
        Assert.assertArrayEquals(Arrays.copyOfRange(items, items.length - limit, items.length), Arrays.copyOf(vector.getItems(), vector.getLen()));
    }
    
    private void testAddDelete(SortedObjectVector<Long> vector) {
        int prevLen = vector.getLen();
        Long[] testItems = new Long[] { 1L, 5L, 12L, 17L, 19L };
        for (long item : testItems) {
            vector.add(item);
        }
        int expected = prevLen < vector.getLimit() ? prevLen : vector.getLimit();
        Assert.assertEquals(expected + testItems.length, vector.getLen());
        ApiLogger.debug(vector);
        
        vector.delete(21L);
        vector.delete(1L);
        vector.delete(5L);
        vector.delete(5L);
        vector.delete(12L);
        vector.delete(17L);
        vector.delete(19L);
        ApiLogger.debug(vector);
    }

}
