/**
 * 
 */
package com.tooyoung.memcacheproxy;

import java.util.concurrent.atomic.AtomicBoolean;

import com.tooyoung.common.util.ApiLogger;


/**
 * 
 * 
 * @author yangwm Nov 5, 2012 1:07:21 AM
 */
public class MemCacheUtil {

    //try cas times
    public static int CAS_TIME = 2;
    public static int MAX_CAS_TIME = 3;
    
    public static final String KEY_SEPERATOR = ".";
    

    public static String toKey(String rawKey, String keySuffix) {
        return rawKey + KEY_SEPERATOR + keySuffix;
    }

    public static String[] toKeys(String[] rawKeys, String keySuffix) {
        int len = rawKeys.length;
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = toKey(rawKeys[i], keySuffix);
        }
        return keys;
    }

    public static String parseRawKey(String key) {
        int pos = key.indexOf(KEY_SEPERATOR);
        return key.substring(0, pos);
    }
    
    
    /**
     * cache debug 
     */
    public static AtomicBoolean cacheDebug = new AtomicBoolean(false);
    public static boolean isDebugEnabled() {
        return ApiLogger.isDebugEnabled() && cacheDebug.get();
    }
    
}
