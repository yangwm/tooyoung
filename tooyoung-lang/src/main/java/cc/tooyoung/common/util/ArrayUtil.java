/**
 * 
 */
package cc.tooyoung.common.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import cc.tooyoung.common.CommonConst;

/**
 * array util 
 * 
 * @author yangwm Mar 2, 2012 8:58:16 PM
 */
public class ArrayUtil {
    
    private ArrayUtil() {
    }

    public static Long[] toLongArr(String[] strArr){
        Long[] longArr = new Long[strArr.length]; 
        for(int i = 0; i < strArr.length; i++){
            longArr[i] = Long.parseLong(strArr[i]);
        }
        return longArr;
    }
    
    public static Long[] toLongArr(long[] longArr){
        Long[] rs = new Long[longArr.length]; 
        for(int i = 0; i < longArr.length; i++){
            rs[i] = Long.valueOf(longArr[i]);
        }
        return rs;
    }
    
    public static long[] toRawLongArr(String[] strArr){
        long[] longArr = new long[strArr.length]; 
        for(int i = 0; i < strArr.length; i++){
            longArr[i] = Long.parseLong(strArr[i]);
        }
        return longArr;
    }
    
    public static int[] toRawIntArr(String strArr[]) {
        int[] intArr = new int[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        return intArr;
    }
    
    public static long[] toLongArr(Collection<Long> ids){
        if(ids == null || ids.size() == 0){
            return new long[0];
        }
        long[] idsArr = new long[ids.size()];
        int idx = 0;
        for(long id : ids){
            idsArr[idx++] = id;
        }
        return idsArr;
    }
    
    public static int[] toIntArr(Collection<Integer> ids){
        if(ids == null || ids.size() == 0){
            return new int[0];
        }
        int[] idsArr = new int[ids.size()];
        int idx = 0;
        for(int id : ids){
            idsArr[idx++] = id;
        }
        return idsArr;
    }
    
    public static String[] splitSimpleString(String str) {
        return StringUtils.split(str, CommonConst.Comma);
    }
    public static String toSimpleString(long a[]) {
        if (a == null) {
            return "";
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            return "";
        }

        StringBuilder b = new StringBuilder();
        for (int i = 0;; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.toString();
            b.append(CommonConst.Comma);
        }
    }

    public static void reverse(long[] b) {
        int left = 0; // index of leftmost element
        int right = b.length - 1; // index of rightmost element

        while (left < right) {
            // exchange the left and right elements
            long temp = b[left];
            b[left] = b[right];
            b[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }
    
    public static void reverse(long[][] b) {
        int left = 0; // index of leftmost element
        int right = b.length - 1; // index of rightmost element

        while (left < right) {
            // exchange the left and right elements
            long[] temp = b[left];
            b[left] = b[right];
            b[right] = temp;

            // move the bounds toward the center
            left++;
            right--;
        }
    }

    /**
     * @param original
     * @param newLength
     * @return
     */
    public static long[] reverseCopy(long[] original, int newLength) {
        //System.out.println("temp length:" + temp.length);
        long[] result = new long[newLength];
        int originalLimit = original.length - newLength;
        for (int i = original.length - 1, resultIdx = 0; i >= originalLimit; i--) {
            result[resultIdx++] = original[i];
        }
        //System.out.println("result length:" + result.length + ", result" + Arrays.toString(result));
        return result;
    }

    /**
     * @param original
     * @param from
     * @param to
     * @return
     */
    public static long[] reverseCopyRange(long[] original, int from, int to) {
        long[] results = Arrays.copyOfRange(original, from, to);
        ArrayUtil.reverse(results);
        return results;
    }

    public static long[] removeAll(long[] sourceArray, long[] removeArray) {
        int sourceLength = sourceArray.length;
        int removeLength = removeArray.length;
        if (sourceLength == 0 || removeLength == 0) {
            return sourceArray;
        }

        /*
         * if removeLength > 2 use removeSet  
         */
        long[] temp = new long[sourceLength];
        int i = 0;
        if (removeLength > 2) {
            Set<Long> removeSet = new HashSet<Long>(removeLength);
            for (long remove : removeArray) {
                removeSet.add((Long)remove);
            }
            for (long source : sourceArray) {
                if (!removeSet.contains((Long)source)) { // not contains so push 
                    temp[i++] = source;
                }
            }
        } else {
            for (long source : sourceArray) { 
                if (!arrayContains(removeArray, source)) { // not contains so push 
                    temp[i++] = source;
                }
            }
        }

        if (i < temp.length) {
            return Arrays.copyOf(temp, i);
        } else {
            return temp;
        }
    }
    private static boolean arrayContains(long[] arrs, long value) {
        for (long arr : arrs) {
            if (arr == value) {
                return true;
            }
        }
        return false;
    }
    
    public static long[] removeAll(long[] sourceArray, Set<Long> removeSet) {
        int sourceLength = sourceArray.length;
        int removeLength = removeSet.size();
        if (sourceLength == 0 || removeLength == 0) {
            return sourceArray;
        }
        
        long[] temp = new long[sourceLength];
        int i = 0;
        for (long source : sourceArray) {
            if (!removeSet.contains((Long)source)) { // not contains so push 
                temp[i++] = source;
            }
        }

        if (i < temp.length) {
            return Arrays.copyOf(temp, i);
        } else {
            return temp;
        }
    }

    /**
     * result will be order by asc 
     * 
     * @param left
     * @param right
     * @return 
     */
    public static long[] sort(long left[], long right[]) {
    	long[] result = addTo(left, right);
        Arrays.sort(result);
        return result;
    }
    
    /**
     * Sorts the specified array of longs into descending numerical order.
     * 
     * @param a the array to be sorted
     * @return 
     */
    public static void sortDesc(long[] a) {
        Arrays.sort(a);
        reverse(a);
    }
    
    /**
     * 求交集而不改变顺序
     * @param arr1 以此为排序依据
     * @param arr2
     * @return
     */
    public static long[] intersectionOrder(long[] arr1, long[] arr2) {
        //获取交集而不打乱arr1的顺序
        HashSet<Long>  tempSet = new HashSet<Long>();
        for (int i = 0; i < arr2.length; i++) {
            tempSet.add(arr2[i]);
        }
        long[] tmpResult = new long[arr1.length];
        int count = 0;
        for (int i = 0; i < arr1.length; i++) {
            if(tempSet.contains(arr1[i])){
                tmpResult[count] = arr1[i];
                count ++;
            }
        }
        //截取掉多余的
        return Arrays.copyOf(tmpResult, count);
    }
    
    /**
     * 往数组里面增加一个值
     * @param arr
     * @param id	
     * @return
     */
    public static long[] addTo(long[] left, long[] right){
        if (left == null) {
            return ArrayUtils.clone(right);
        } else if (right == null) {
            return ArrayUtils.clone(left);
        }
        
        long[] result = new long[left.length + right.length];
        
        int pos = 0;
        System.arraycopy(left, 0, result, pos, left.length);               
        pos += left.length;
        System.arraycopy(right, 0, result, pos, right.length);
        return result;
    }
    
    /**
     * 往数组里面增加一个值
     * @param arr
     * @param id	
     * @return
     */
    public static long[] addTo(long[] arr, long id){
    	return addTo(arr, new long[] {id});
    }

    public static long[] addTo(long[] arr, long id, int limit) {
        if (ArrayUtils.contains(arr, id)) {
            return arr;
        }
        arr = addTo(arr, id);
        return getLimited(arr, limit);
    }

    public static long[] getLimited(long[] arr, int limit) {
        if (arr == null) {
            return arr;
        }

        if (arr.length > limit) {
            long[] result = new long[limit];
            System.arraycopy(arr, arr.length - limit, result, 0, limit);
            return result;
        } else {
            return arr;
        }
    }
        
}
