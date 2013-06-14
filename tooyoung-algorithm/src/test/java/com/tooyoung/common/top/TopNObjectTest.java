/**
 * 
 */
package com.tooyoung.common.top;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tooyoung.common.CommonConst;
import com.tooyoung.common.collection.SortedObjectVector;
import com.tooyoung.common.collection.VectorInterface;
import com.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Sep 15, 2012 4:29:03 PM
 */
public class TopNObjectTest {
    
    @Test
    public void testTopN() {
        Map<String, ? extends VectorInterface> vectorMap = getAllVectorMap();
        
        for (int column = -1; column >= 0; column--) {
            ApiLogger.debug(column);
        }
        
        Object[] result = (Object[]) TopNObject.top(vectorMap.values(), 20);
//        for (Object r : result) {
//            ApiLogger.debug(r.getClass());
//        }
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
        
        result = TopNObject.top(vectorMap.values(), 30, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
        
        result = TopNObject.top(vectorMap.values(), CommonConst.TIMELINE_SIZE, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        ApiLogger.debug("result length:" + result.length + ", result" + Arrays.toString(result));
    }
    
    private static Map<String, ? extends VectorInterface> getAllVectorMap() {
        Long[] input0 = new Long[] { (long)2, (long)3, (long)4, (long)5, (long)7, (long)8, (long)9, (long)95, (long)98, (long)120 };
        Long[] input1 = new Long[] { (long)20, (long)21, (long)22, (long)23, (long)24, (long)25, (long)33, (long)34, (long)35, (long)45, (long)166 };
        Long[] input2 = new Long[] { (long)11, (long)12, (long)13, (long)14, (long)15, (long)17, (long)134, (long)135, (long)145, (long)150, (long)0, (long)0 };
        Long[] input3 = new Long[] { (long)0, (long)0, (long)0, (long)0, (long)0, (long)0 };
        Long[] input4 = new Long[] { (long)0, (long)3000000000000271L, (long)3000000000000272L, (long)3000000000000273L, (long)1, (long)6, (long)0, (long)0 };
        Long[] input5 = new Long[] { (long)3000000000000041L, (long)3000000000000040L };
        Long[][] inputs = new Long[6][];
        inputs[0] = input0;
        inputs[1] = input1;
        inputs[2] = input2;
        inputs[3] = input3;
        inputs[4] = input4;
        inputs[5] = input5;
        
        Map<String, SortedObjectVector<Long>> vectorMap = new HashMap<String, SortedObjectVector<Long>>();
        for (Long[] vector : inputs) {
            vectorMap.put(vector.toString() + ".iv", new SortedObjectVector<Long>(vector));
        }
        return vectorMap;
    }

}
