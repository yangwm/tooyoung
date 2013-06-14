/**
 * 
 */
package com.tooyoung.common.top;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import com.tooyoung.common.collection.SortedVector;
import com.tooyoung.common.collection.VectorInterface;

/**
 * TopN Bench
 * 
 * @author yangwm Mar 27, 2012 9:18:46 PM
 */
public class TopNBench {
    
    private static final int vectorSize = 210;
    private static final int friendCount = 2000;
    
    private static Random rand = new Random();
    private static long[] getRandomVector() {
        long[] _values = new long[vectorSize];
        long randValue = rand.nextInt(Integer.MAX_VALUE);
        for (int i = 0; i < vectorSize; i++) {
            _values[i] = randValue + i;
        }
        Arrays.sort(_values);
        return _values;
    }
    private static Map<String, ? extends VectorInterface> getAllVectorMap() {
        long[][] allIds = new long[friendCount][];
        for (int i = 0; i < friendCount; i++) {
            long[] values = getRandomVector();
            allIds[i] = values; 
        }
        //System.out.println(allIds.length);
        Map<String, SortedVector> vectorMap = new HashMap<String, SortedVector>();
        for (long[] vector : allIds) {
            vectorMap.put(vector.toString() + ".v", new SortedVector(vector));
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
            long[] result = TopN.top(vectorMap.values(), n);
        }
        
        long beginTime = System.nanoTime();
        for (int i = 0; i < times;i ++) {
            long[] result = TopN.top(vectorMap.values(), n);
        }
        long cosumeTime = System.nanoTime() - beginTime;
        System.out.println("testTopN " + TopN.class.getSimpleName() + ", cosume time " + (cosumeTime/1000000.0)
                + " ms, top'n " + n + " from vectorSize " + vectorSize + ", friendCount " + friendCount 
                + ", times " + times + " per time " + (cosumeTime/times/1000000.0)+ " ms." );
        //System.out.println("result length:" + result.length + ", result" + Arrays.toString(result));
    }

}

/*

testTopN TopN, cosume time 7693.05223 ms, top'n 45 from vectorSize 210, friendCount 2000, times 10000 per time 0.769305 ms.
testTopN TopN, cosume time 8239.508364 ms, top'n 500 from vectorSize 210, friendCount 2000, times 10000 per time 0.82395 ms.
testTopN TopN, cosume time 6953.388118 ms, top'n 1000 from vectorSize 210, friendCount 2000, times 10000 per time 0.695338 ms.
testTopN TopN, cosume time 10327.519846 ms, top'n 2000 from vectorSize 210, friendCount 2000, times 10000 per time 1.032751 ms.
testTopN TopN, cosume time 14669.831664 ms, top'n 5000 from vectorSize 210, friendCount 2000, times 10000 per time 1.466983 ms.
testTopN TopN, cosume time 12561.911601 ms, top'n 5000 from vectorSize 210, friendCount 2000, times 10000 per time 1.256191 ms.



*/

