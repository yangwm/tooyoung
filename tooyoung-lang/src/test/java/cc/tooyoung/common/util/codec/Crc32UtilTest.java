/**
 * 
 */
package cc.tooyoung.common.util.codec;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jul 30, 2013 12:49:06 AM
 */
public class Crc32UtilTest {
    
    @Test
    public void testCrc32() {
        long h = Crc32Util.getCrc32(String.valueOf("256"));
        ApiLogger.debug("Crc32UtilTest testCrc32 h:" + h);
    }

}
