/**
 * 
 */
package com.tooyoung.common.util;




/**
 * hash util 
 * 
 * @author yangwm Jan 6, 2013 9:20:03 PM
 */
public class HashUtil {
    
    public static class HashAlg {
        public static String NONE = "none";
        public static String CRC32 = "crc32";
    }
    
    public static class NoneHash {
        public static String OLD = "old";
        public static String NEW = "new";
    }

    public static int getHash(long id, int splitCount, String hashAlg) {
        return getHash(id, splitCount, hashAlg, NoneHash.NEW);
    }
    public static int getHash(long id, int splitCount, String hashAlg, String noneHash) {
        long h = id;
        if (hashAlg == null || HashAlg.NONE.equals(hashAlg)) {
            ;
        } else if (HashAlg.CRC32.equals(hashAlg)) {
            h = CommonUtil.getCrc32(String.valueOf(id));
        } else {
            ApiLogger.warn("HashUtil getHash HashAlg warn, id:" + id + ", hashAlg:" + hashAlg);
        }
        
        if (NoneHash.OLD.equals(noneHash)) {
            return (int)(h % splitCount);
        } else if (NoneHash.NEW.equals(noneHash)) {
            return (int)(h / splitCount % splitCount);
        } else {
            ApiLogger.warn("HashUtil getHash NoneHash warn, id:" + id + ", splitCount:" + splitCount + ", noneHash:" + noneHash);
            return (int)(h / splitCount % splitCount);
        }
    }

}
