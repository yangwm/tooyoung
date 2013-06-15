/**
 * 
 */
package cc.tooyoung.common.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;

import cc.tooyoung.common.CommonConst;


/**
 * Sorted Vector for Object array
 * 
 * @author yangwm Jun 9, 2012 12:33:08 AM
 */
public class SortedObjectVector<E> implements VectorInterface, Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5874381915587424628L;
    
    private int limit;
    private E[] items;
    private int len;

    public SortedObjectVector(E[] items) {
        this(items.length, CommonConst.VECTOR_LIMIT, items);
    }
    @SuppressWarnings("unchecked")
    public SortedObjectVector(int initial, int limit, E[] items) {
        initial = initial > items.length ? initial : items.length;
        if (initial > limit) {
            initial = limit;
        }
        
        this.limit = limit;
        this.items = (E[]) Array.newInstance(items.getClass().getComponentType(), initial + CommonConst.VECTOR_THRESHOLD);
        this.len = Math.min(initial, items.length);
        
        Arrays.sort(items);
        int startPos = items.length > this.len ? items.length - this.len : 0;
        System.arraycopy(items, startPos, this.items, 0, this.len);
    }

    // -------------- base api ----------------------
    
    /**
     * add by asc
     * 
     * @param item 
     * @return
     */
    public boolean add(E item){
        if (len <= 0) {
            items[0] = item;
            len = 1;
            return true;
        }
        
        ensureCapacity();
        
        /*
         * index >= 0, find the value in items, so do nothing
         * index < 0 is finded, so add position:(-index) - 1, 
         */
        int index = find(item);
        if (index < 0) {
            int addPos = (-index) - 1;
            if (addPos < len) { // if add position not the last(end), will move array start add position 
                System.arraycopy(items, addPos, items, (-index), len - addPos);
            }
            //System.out.println("addPos:" + addPos + ", len:" + len);
            items[addPos] = item;
            len++;
        }
        return true;
    }
    private void ensureCapacity() {
        int minCapacity = (len + 1);
        int oldCapacity = items.length;
        int limitCapacity = limit + CommonConst.VECTOR_THRESHOLD;
        if (minCapacity > oldCapacity && len < limitCapacity) { // len < limitCapacity 
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity > limitCapacity) {
                newCapacity = limitCapacity;
            } else if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            
            // minCapacity is usually close to size, so this is a win:
            items = Arrays.copyOf(items, newCapacity);
        }
        
        /*
         * (len + 1) more than (limit + threshold), will trim oldest the number(threshold) of items 
         */
        if (minCapacity > limitCapacity) {
            System.arraycopy(items, CommonConst.VECTOR_THRESHOLD, items, 0, limit);
            len = limit;
        }
    }

    public boolean delete(E item){
        if (len <= 0) {
            return true;
        }

        int index = find(item);
        if (index >= len - 1) {
            len--;
        } else if (index >= 0) {
            int delPos = index + 1;
            System.arraycopy(items, delPos, items, index, len - delPos);
            len--;
        }
        return true;
    }
    
    public boolean contains(E item){
        if (items.length > 0 && len > 0) {
            int index = find(item);
            return index >= 0;
        }
        return false;
    }
    
    public int find(Object key) {
        if (len <= 0) {
            return -1;
        }
        if (key.equals(items[len - 1])) { // may be the last, because delete the latest 
            return len - 1;
        }
        
        return Arrays.binarySearch(items, 0, len, key);
    }
    
    
    // -------------- extend api ----------------------
    
    public int getLimit() {
        return limit;
    }
    
    public E[] getItems() {
        return items;
    }
    public int getLen() {
        return len;
    }

    public E[] getActualItems(){
        if (len >= items.length) {
            return items;
        }
        
        return Arrays.copyOf(items, len);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder(100 + 20 * 4 * len);
        sb.append("{SortedObjectVector:limit:" + limit + ",items.length:" + items.length + ",len=" + len + ",items:[");
        for (int i = 0; i < len; i++) {
            sb.append(items[i]).append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }

}
