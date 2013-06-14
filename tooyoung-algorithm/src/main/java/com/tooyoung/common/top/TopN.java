/**
 * 
 */
package com.tooyoung.common.top;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import com.tooyoung.common.collection.VectorInterface;
import com.tooyoung.common.stat.StatLog;
import com.tooyoung.common.util.ApiLogger;


/**
 * 求inputs中的top n (inputs.length是friendCount为M，inputs[i].length是vectorSize为210) 
 * 算法描述： 先构建一个M*pos的heap（TreeSet）, 后续比较每个vector剩余数据，大于heap中最小值则需替换heap中数据，否则直接跳过该vector   
 * 算法描述： 
 *  取每个vector倒数第1个有效值来构建一个大小接近M的heap(TreeSet)并做为比较参照，分配一个大小为N数组并做为最终结果，构建循环进行一下动作直到得到top n  
 *      取出heap中最大值放入最终结果数组  
 *      后续比较每个vector剩余数据，大于heap中最小值则需替换heap中数据，否则直接跳过该vector并从heap移除对该vector比较资格     
 * 
 * 时间复杂度：O(N+lg(M)*lg(M)*lg(210)) 
 * 空间复杂度：O(M + N) 
 * 性能衰变度：随N增大性能有下降微略
 * 
 * bench: 
 * testTopN TopN, cosume time 7693.05223 ms, top'n 45 from vectorSize 210, friendCount 2000, times 10000 per time 0.769305 ms.
 * testTopN TopN, cosume time 8239.508364 ms, top'n 500 from vectorSize 210, friendCount 2000, times 10000 per time 0.82395 ms.
 * testTopN TopN, cosume time 9738.92101 ms, top'n 2000 from vectorSize 210, friendCount 2000, times 10000 per time 0.973892 ms.
 * testTopN TopN, cosume time 14669.831664 ms, top'n 5000 from vectorSize 210, friendCount 2000, times 10000 per time 1.466983 ms.
 * testTopN TopN, cosume time 32921.186183 ms, top'n 20000 from vectorSize 210, friendCount 2000, times 10000 per time 3.292118 ms.
 * 
 * @author yangwm Jun 9, 2012 12:33:08 AM
 */
public class TopN {

    /**
     * top n 
     * 
     * @param inputs's vector must order desc 
     * @param n
     * @return result's length <= n 
     */
    public static long[] top(Collection<? extends VectorInterface> vectorItems, int n) {
        VectorInterface[] vectors = getVectors(vectorItems);
        return top(vectors, n);
    }

    /**
     * top n 
     * 
     * @param inputs's vector must order desc 
     * @param n
     * @return result's length <= n 
     */
    private static long[] top(VectorInterface[] vectors, int n) {
        StatLog.inc("TopN.top.all");
        
        TreeSet<RowColumn> heap = initHeap(vectors);

        long[] result = new long[n];
        int pos = 0;
        while (pos < n) {
            if (heap.isEmpty()) {
                break;
            }
            
            /* 
             * 找到最大值以及所在数据行列 并移除,  
             */
            RowColumn rowColumn = heap.pollLast(); // O(1) 

            /*
             * save result 
             */
            result[pos] = rowColumn.value;
            pos++;
            
            /*
             * 当前heap中最大值，对应行号的列号指针后移并保存新的最大值到heap中 
             */
            if (rowColumn.columnIdx > 0) {
                VectorInterface vitem = vectors[rowColumn.rowIdx];
                
                int column = rowColumn.columnIdx - 1; // 指针前移 
                long[] items = (long[])vitem.getItems();
                long val = items[column]; // 将指针后移之后的数据加入到堆中，待比较
                RowColumn newRowColumn = new RowColumn(val, rowColumn.rowIdx, column);
                if (newRowColumn.value > 0) {
                    heap.add(newRowColumn); // O(lg*m) 
                } else {
                    ApiLogger.warn("TopN.top add vitem.getLen():" + vitem.getLen() 
                            + ", newRowColumn:" + newRowColumn
                            + ", rowColumn:" + rowColumn);
                }
            } 
        }
        
        if (pos < n) {
            StatLog.inc("TopN.top.lessN");
            return Arrays.copyOf(result, pos);
        }
        return result;
    }
    
    /**
     * 取每个vector倒数第1个有效值来构建一个大小接近M的heap(TreeSet)并做为比较参照
     * 
     * @param vectors
     * @return
     */
    private static TreeSet<RowColumn> initHeap(VectorInterface[] vectors) {
        int vectorsLen = vectors.length;
        
        TreeSet<RowColumn> heap = new TreeSet<RowColumn>();
        for (int i = 0; i < vectorsLen; i++) {
            VectorInterface vitem = vectors[i];
            try {
                if (vitem == null || vitem.getLen() < 1){
                    continue;
                }
                
                RowColumn newRowColumn = null;
                boolean hasError = false;
                // 引入for-each，为解决vector实际有效ids后面有0现象 
                for (int column = vitem.getLen() - 1; column >= 0; column--) {
                    long[] items = (long[])vitem.getItems();
                    long val = items[column];
                    newRowColumn = new RowColumn(val, i, column);
                    if (newRowColumn.value > 0) { // 值需大于0 
                        heap.add(newRowColumn);
                        break;
                    } else {
                        hasError = true;
                    }
                }
                
                if (hasError) {
                    ApiLogger.warn("TopN.top initHeap vitem.getLen():" + vitem.getLen() 
                            + ", newRowColumn:" + newRowColumn
                            + ", vectorArr:" + Arrays.toString(getLogArray(vitem, vitem.getLen())));
                }
                
                /* TODO yangwm 校验程序，算法稳定后去除 
                int checkIdx = 0;
                for (int column = newRowColumn.columnIdx - 1; column >= 0 && checkIdx < 3; column--) {
                    checkIdx++;
                    long val = vitem.getItems()[column];
                    long lastVal = vitem.getItems()[column + 1];
                    if (lastVal < val && cn.sina.api.commons.util.UuidHelper.isUuidAfterUpdate(val)) {
                        ApiLogger.error("TopN.top check vitem.getLen():" + vitem.getLen() 
                                + ", newRowColumn:" + newRowColumn
                                + ", vectorArr:" + Arrays.toString(getLogArray(vitem, newRowColumn.columnIdx + 1)));
                        break;
                    }
                }*/
            } catch (Exception ex) {
                ApiLogger.warn(ex);
                continue;
            }
        }
        
        if (heap.size() > vectorsLen) {
            ApiLogger.warn("TopN.top heap heap.size:" + heap.size()); // heap:" + heap
        }
        return heap;
    }

    /**
     * vectorItemMap's all vectorItem of actual value 
     * 
     * @param vectorItemMap
     * @return
     */
    private static VectorInterface[] getVectors(Collection<? extends VectorInterface> vectorItems){
        VectorInterface[] vectors = new VectorInterface[vectorItems.size()];
        int k = 0;
        for (VectorInterface vector : vectorItems) {
            vectors[k] = vector;
            k++;
        }
        return vectors;
    }
    
    private static long[] getLogArray(VectorInterface vitem, int endPos) {
        long[] items = (long[])vitem.getItems();
        if (endPos > items.length) {
            endPos = items.length;
        }
        int startPos = endPos - 10;
        if (startPos < 0) {
            startPos = 0;
        }
        long[] tmp = new long[endPos - startPos];
        System.arraycopy(vitem.getItems(), startPos, tmp, 0, tmp.length);
        return tmp;
    }
    
    static class RowColumn implements Comparable<RowColumn> {
        long value;
        int rowIdx;
        int columnIdx;
        
        public RowColumn(long value, int rowIdx, int columnIdx) {
            this.value = value;
            this.rowIdx = rowIdx;
            this.columnIdx = columnIdx;
        }
        
        public RowColumn(int rowIdx, int columnIdx) {
            this.rowIdx = rowIdx;
            this.columnIdx = columnIdx;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("RowColumn{")
            .append(value)
            .append(",")
            .append(rowIdx)
            .append(",")
            .append(columnIdx)
            .append("}");
            return sb.toString();
        }
        
        @Override
        public int compareTo(RowColumn o) {
            if (value > o.value) {
                return 1;
            } else if (value < o.value) {
                return -1;
            }
            return 0;
        }
    }

}
