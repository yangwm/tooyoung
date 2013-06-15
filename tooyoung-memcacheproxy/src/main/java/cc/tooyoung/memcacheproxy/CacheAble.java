/**
 * 
 */
package cc.tooyoung.memcacheproxy;

import java.util.Date;
import java.util.Map;

import cc.tooyoung.memcache.vika.CasValue;

/**
 * 
 * @author yangwm Oct 21, 2012 7:30:06 PM
 */
public interface CacheAble<T> {

    /**
     * get 
     * 
     * @param key
     * @return
     */
    T get(String key);
    /**
     * multi get 
     * 
     * @param keys
     * @return
     */
    Map<String, T> getMulti(String[] keys);

    /**
     * set with policy (setAll or setAndDeleteL1 or setAndIfExistL1) 
     * 
     * setAll -- set master/slave and masterL1/slaveL1 
     * setAndDeleteL1 -- set master/slave, and delete masterL1/slaveL1 (keep L1 cache hot) 
     * setAndIfExistL1 -- set master/slave, and but if value exist could be set with masterL1/slaveL1 (keep L1 cache hot) 
     * 
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, T value);
    boolean set(String key, T value, Date expdate);

    /**
     * get casValue from master(getCas not support slave/L1 cache, because master casUnique only can compare with self and can't cas null) 
     * 
     * @param key
     * @return
     */
    CasValue<T> getCas(String key);
    
    /**
     * cas master/slave and masterL1/slaveL1 
     * 
     * @param key
     * @param value
     * @return
     */
    boolean cas(String key, CasValue<T> value);
    boolean cas(String key, CasValue<T> value, Date expdate);
    
    /**
     * delete master/slave and masterL1/slaveL1 
     * 
     * @param key
     * @return
     */
    boolean delete(String key);
    
}
