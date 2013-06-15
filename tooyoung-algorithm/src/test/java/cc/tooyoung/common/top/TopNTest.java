/**
 * 
 */
package cc.tooyoung.common.top;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cc.tooyoung.common.CommonConst;
import cc.tooyoung.common.collection.SortedVector;
import cc.tooyoung.common.collection.VectorInterface;
import cc.tooyoung.common.util.ApiLogger;

/**
 * TopN Test
 * 
 * @author yangwm Mar 28, 2012 10:16:36 PM
 */
public class TopNTest {
    
    @Test
    public void testTopN() {
        Map<String, ? extends VectorInterface> vectorMap = getAllVectorMap();
        
        for (int column = -1; column >= 0; column--) {
            ApiLogger.debug(column);
        }
        
        long[] result = TopN.top(vectorMap.values(), 20);
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
        
        result = TopN.top(vectorMap.values(), 30);
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
        
        result = TopN.top(vectorMap.values(), CommonConst.TIMELINE_SIZE);
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
    }
    
    private static Map<String, ? extends VectorInterface> getAllVectorMap() {
        long[] input0 = new long[] { 2, 3, 4, 5, 7, 8, 9, 95, 98, 120 };
        long[] input1 = new long[] { 20, 21, 22, 23, 24, 25, 33, 34, 35, 45, 166 };
        long[] input2 = new long[] { 11, 12, 13, 14, 15, 17, 134, 135, 145, 150, 0, 0 };
        long[] input3 = new long[] { 0, 0, 0, 0, 0, 0 };
        long[] input4 = new long[] { 0, 3000000000000271L, 3000000000000272L, 3000000000000273L, 1, 6, 0, 0 };
        long[] input5 = new long[] { 3000000000000041L, 3000000000000040L };
        long[][] inputs = new long[6][];
        inputs[0] = input0;
        inputs[1] = input1;
        inputs[2] = input2;
        inputs[3] = input3;
        inputs[4] = input4;
        inputs[5] = input5;
        
        Map<String, SortedVector> vectorMap = new HashMap<String, SortedVector>();
        for (long[] vector : inputs) {
            vectorMap.put(vector.toString() + ".v", new SortedVector(vector));
        }
        return vectorMap;
    }

}
