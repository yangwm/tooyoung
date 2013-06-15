/**
 * 
 */
package cc.tooyoung.common.page;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.common.util.ArrayUtil;

/**
 * 
 * @author yangwm Jun 13, 2012 4:16:34 PM
 */
public class PageWrapperUtilTest {
    
    @Test 
    public void testPaginationAndReverseWithVectorCursor() {
        long[] items = new long[210];
        for (int i = 0; i < items.length; i++) {
            items[i] = 101 + i * 3;
        }
        long maxId = 0;
        int count = 20;
        long[] expecteds = Arrays.copyOf(items, items.length);
        ArrayUtil.reverse(expecteds);
        
        for (int i = 1; i <= 11; i++) {
            PaginationParam paginationParam = new PaginationParam.Builder().maxId(maxId).count(count).build();
            PageWrapper<long[]> pageWrapper = PageWrapperUtil.paginationAndReverse(items, paginationParam);
            ApiLogger.debug(paginationParam + ", " + PageWrapperUtil.toJson(pageWrapper, "ids", pageWrapper.result));
            
            int startPos = (i - 1) * count;
            int endPos = (startPos + count) < items.length ? (startPos + count) : items.length;
            Assert.assertArrayEquals(Arrays.copyOfRange(expecteds, startPos, endPos), pageWrapper.result);

            maxId = pageWrapper.nextCursor + 1; // for test cursor not in vector  
        }
    }

    @Test 
    public void testPaginationAndReverseWithVectorPage() {
        long[] items = new long[210];
        for (int i = 0; i < items.length; i++) {
            items[i] = 101 + i;
        }
        int count = 20;
        int page = 1;
        long[] expecteds = Arrays.copyOf(items, items.length);
        ArrayUtil.reverse(expecteds);
        
        for (int i = 1; i <= 11; i++) {
            page = i;
            
            PaginationParam paginationParam = new PaginationParam.Builder().count(count).page(page).build();
            PageWrapper<long[]> pageWrapper = PageWrapperUtil.paginationAndReverse(items, paginationParam);
            ApiLogger.debug(paginationParam + ", " + PageWrapperUtil.toJson(pageWrapper, "ids", pageWrapper.result));
            
            int startPos = (i - 1) * count;
            int endPos = (startPos + count) < items.length ? (startPos + count) : items.length;
            Assert.assertArrayEquals(Arrays.copyOfRange(expecteds, startPos, endPos), pageWrapper.result);
        }
    }
    
    @Test 
    public void testToJson() {
        long[] items = new long[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = (2300000000000000L + 101) + (items.length - 1 - i);
        }
        /*
         * descending order with db pagination, so need reverse to ascending 
         */
        ArrayUtil.reverse(items);
        ApiLogger.debug(Arrays.toString(items));
        
        int count = 20;
        int page = 1;
        long[] expecteds = Arrays.copyOf(items, items.length);
        ArrayUtil.reverse(expecteds);
        
        PaginationParam paginationParam = new PaginationParam.Builder().count(count).page(page).build();
        PageWrapper<long[]> pageWrapper = PageWrapperUtil.paginationAndReverse(items, paginationParam);
        ApiLogger.debug(paginationParam + ", " + PageWrapperUtil.toJson(pageWrapper, "ids", pageWrapper.result));
        
    }
    
    @Test 
    public void testWrap() {
        long[] items = new long[10];
        for (int i = 0; i < items.length; i++) {
            items[i] = (2300000000000000L + 101) + (items.length - 1 - i);
        }
        ApiLogger.debug(Arrays.toString(items)); // descending order with db pagination
        
        int count = 20;
        long[] expecteds = Arrays.copyOf(items, items.length);
        
        PageWrapper<long[]> pageWrapper = PageWrapperUtil.wrap(items, count);
        ApiLogger.debug(PageWrapperUtil.toJson(pageWrapper, "ids", pageWrapper.result));
        Assert.assertArrayEquals(expecteds, pageWrapper.result);
    }

}
