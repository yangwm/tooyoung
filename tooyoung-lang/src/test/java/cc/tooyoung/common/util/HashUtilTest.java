/**
 * 
 */
package cc.tooyoung.common.util;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.util.HashUtil.HashAlg;

/**
 * 
 * @author yangwm Jan 24, 2013 11:23:24 AM
 */
public class HashUtilTest {

    @Test
    public void testGetHashCrc32() {
        int hash = HashUtil.getHash(1821155363, 32, HashAlg.CRC32);
        Assert.assertEquals(12, hash);
        hash = HashUtil.getHash(1821155363, 128, HashAlg.CRC32);
        Assert.assertEquals(51, hash);
    }

    @Test
    public void testGetHashNone() {
        int hash = HashUtil.getHash(1821155363, 32, HashAlg.NONE);
        Assert.assertEquals(1, hash);
        hash = HashUtil.getHash(1821155363, 128, HashAlg.NONE);
        Assert.assertEquals(64, hash);
    }
    
}
