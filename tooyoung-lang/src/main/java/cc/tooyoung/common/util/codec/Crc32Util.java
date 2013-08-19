/**
 * 
 */
package cc.tooyoung.common.util.codec;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

import cc.tooyoung.common.util.ApiLogger;

/**
 * crc32 
 * 
 * @author yangwm Jul 30, 2013 12:46:00 AM
 */
public class Crc32Util {

    private static ThreadLocal<CRC32> crc32Provider = new ThreadLocal<CRC32>(){
        @Override
        protected CRC32 initialValue() {
            return new CRC32();
        }
    };

    public static long getCrc32(byte[] b) {
        CRC32 crc = crc32Provider.get();
        crc.reset();
        crc.update(b);
        return crc.getValue();
    }
    public static long getCrc32(String str) {
        try {
            return getCrc32(str.getBytes("utf-8"));   
        } catch (UnsupportedEncodingException e) {
            ApiLogger.warn(new StringBuilder(64).append("Error: getCrc32, str=").append(str), e);
            return -1;
        }
    }

    private static int getHash4split(long id, int splitCount){
        try {
            long h = getCrc32(String.valueOf(id).getBytes("utf-8"));
            if(h < 0){
                h = -1 * h;
            }
            int hash = (int)(h / splitCount % splitCount);          
            return hash;
        } catch (UnsupportedEncodingException e) {
            ApiLogger.warn(new StringBuilder(64).append("Error: when hash4split, id=").append(id).append(", splitCount=").append(splitCount), e);
            return -1;
        }
    }

    
    public static void main(String[] args){     
        int hash = getHash4split(10506, 256);
        System.out.println(hash);
        
        long h = Crc32Util.getCrc32(String.valueOf("256"));
        System.out.println(h);
    }
    
}
