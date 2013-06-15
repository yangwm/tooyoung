/**
 * 
 */
package cc.tooyoung.common.top;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import cc.tooyoung.common.CommonConst;
import cc.tooyoung.common.collection.VectorInterface;
import cc.tooyoung.common.stat.StatLog;
import cc.tooyoung.common.util.ApiLogger;


/**
 * see TopN.java
 * 
 * @author yangwm Jun 9, 2012 12:33:08 AM
 */
public class TopNObject {

    /**
     * top n 
     * 
     * @param inputs's vector must order desc 
     * @param n
     * @return result's length <= n 
     */
    public static Object[] top(Collection<? extends VectorInterface> vectorItems, int n) {
        return top(vectorItems, n, CommonConst.EMPTY_OBJECT_ARRAY);
    }
    public static Object[] top(Collection<? extends VectorInterface> vectorItems, int n, Object[] newObject) {
        VectorInterface[] vectors = getVectors(vectorItems);
        return top(vectors, n, newObject.getClass());
    }

    /**
     * top n 
     * 
     * @param inputs's vector must order desc 
     * @param n
     * @return result's length <= n 
     */
    private static Object[] top(VectorInterface[] vectors, int n, Class<? extends Object[]> newType) {
        StatLog.inc("TopN.top.all");
        
        TreeSet<RowColumn> heap = initHeap(vectors);

        Object[] result = (Object[]) ((newType == Object[].class) ? new Object[n] : Array.newInstance(newType.getComponentType(), n));
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
                Object[] items = (Object[])vitem.getItems();
                Object val = items[column]; // 将指针后移之后的数据加入到堆中，待比较
                RowColumn newRowColumn = new RowColumn(val, rowColumn.rowIdx, column);
                if (newRowColumn.value != null) { // 值需大于0 TODO  
                    //TODO System.out.println(newRowColumn);
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
                    Object[] items = (Object[])vitem.getItems();
                    Object val = items[column];
                    newRowColumn = new RowColumn(val, i, column);
                    if (newRowColumn.value != null) { // 值需大于0 TODO  
                        //TODO System.out.println(newRowColumn);
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
                    Object val = vitem.getItems()[column];
                    Object lastVal = vitem.getItems()[column + 1];
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
    
    private static Object[] getLogArray(VectorInterface vitem, int endPos) {
        Object[] items = (Object[])vitem.getItems();
        if (endPos > items.length) {
            endPos = items.length;
        }
        int startPos = endPos - 10;
        if (startPos < 0) {
            startPos = 0;
        }
        Object[] tmp = new Object[endPos - startPos];
        System.arraycopy(vitem.getItems(), startPos, tmp, 0, tmp.length);
        return tmp;
    }
    
    static class RowColumn implements Comparable<RowColumn> {
        Object value;
        int rowIdx;
        int columnIdx;
        
        public RowColumn(Object value, int rowIdx, int columnIdx) {
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
        
        @SuppressWarnings("unchecked")
        @Override
        public int compareTo(RowColumn o) {
            return ((Comparable)value).compareTo((Comparable)o.value);
        }
    }

}
