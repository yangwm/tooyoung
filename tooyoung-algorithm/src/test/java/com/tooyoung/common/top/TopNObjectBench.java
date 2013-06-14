/**
 * 
 */
package com.tooyoung.common.top;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.tooyoung.common.CommonConst;
import com.tooyoung.common.collection.SortedObjectVector;
import com.tooyoung.common.collection.VectorInterface;

/**
 * 
 * @author yangwm Sep 15, 2012 6:24:49 PM
 */
public class TopNObjectBench {
    
    private static final int vectorSize = 210;
    private static final int friendCount = 2000;
    
    private static Random rand = new Random();
    private static Long[] getRandomVector() {
        Long[] _values = new Long[vectorSize];
        long randValue = rand.nextInt(Integer.MAX_VALUE);
        for (int i = 0; i < vectorSize; i++) {
            _values[i] = (Long)randValue + i;
        }
        Arrays.sort(_values);
        return _values;
    }
    private static Map<String, ? extends VectorInterface> getAllVectorMap() {
        Long[][] allIds = new Long[friendCount][];
        for (int i = 0; i < friendCount; i++) {
            Long[] values = getRandomVector();
            allIds[i] = values; 
        }
        //System.out.println(allIds.length);
        Map<String, SortedObjectVector<Long>> vectorMap = new HashMap<String, SortedObjectVector<Long>>();
        for (Long[] vector : allIds) {
            vectorMap.put(vector.toString() + ".v", new SortedObjectVector<Long>(vector));
        }
        return vectorMap;
    }
    private static final Map<String, ? extends VectorInterface> allVectorMap = getAllVectorMap();

    @Test
    public void testTopN() {
        Map<String, ? extends VectorInterface> vectorMap = allVectorMap;
        int times = 10 * 1000;
        int n = 1000;
        
        // swarm up 
        for (int i = 0; i < times;i ++) {
            Object[] result = TopNObject.top(vectorMap.values(), n, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        }
        
        long beginTime = System.nanoTime();
        for (int i = 0; i < times;i ++) {
            Object[] result = TopNObject.top(vectorMap.values(), n, CommonConst.EMPTY_LONG_OBJECT_ARRAY);
        }
        long cosumeTime = System.nanoTime() - beginTime;
        System.out.println("testTopN " + TopNObject.class.getSimpleName() + ", cosume time " + (cosumeTime/1000000.0)
                + " ms, top'n " + n + " from vectorSize " + vectorSize + ", friendCount " + friendCount 
                + ", times " + times + " per time " + (cosumeTime/times/1000000.0)+ " ms." );
        //System.out.println("result length:" + result.length + ", result" + Arrays.toString(result));
    }

}

/*

testTopN TopNObject, cosume time 8873.432999 ms, top'n 1000 from vectorSize 210, friendCount 2000, times 10000 per time 0.887343 ms.
testTopN TopNObject, cosume time 13145.519505 ms, top'n 5000 from vectorSize 210, friendCount 2000, times 10000 per time 1.314551 ms.

*/
