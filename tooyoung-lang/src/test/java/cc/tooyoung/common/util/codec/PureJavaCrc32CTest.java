/**
 * 
 */
package cc.tooyoung.common.util.codec;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jul 30, 2013 1:01:28 AM
 */
public class PureJavaCrc32CTest {
    
    @Test
    public void testCrc32() {
        long h = computeCrc(String.valueOf("256"));
        ApiLogger.debug("Crc32UtilTest testCrc32 h:" + h);
    }


    private static int computeCrc(String str) {
        PureJavaCrc32C crc = new PureJavaCrc32C();
        try {
            byte[] data = str.getBytes("utf-8");
            crc.update(data, 0, data.length);
            return crc.getIntValue();  
        } catch (UnsupportedEncodingException e) {
            ApiLogger.warn(new StringBuilder(64).append("Error: getCrc32, str=").append(str), e);
            return -1;
        }
    }

    
}
