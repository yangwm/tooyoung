/**
 * 
 */
package cc.tooyoung.common.util.codec;

import junit.framework.Assert;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jul 2, 2013 11:24:08 PM
 */
public class MD5UtilTest {
    
    @Test
    public void testMd5() {
        String strMd5 = MD5Util.md5("123456");
        ApiLogger.debug("MD5UtilTest testMd5 strMd5:" + strMd5);
        Assert.assertEquals("e10adc3949ba59abbe56e057f20f883e", strMd5);
    }

}
