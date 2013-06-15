/**
 * 
 */
package cc.tooyoung.common.mcq;

import junit.framework.Assert;

import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jun 18, 2012 9:40:15 PM
 */
public class McqBaseManagerTest {
    
    @Test
    public void testStatus() {
        String result = McqBaseManager.status();
        Assert.assertEquals("\r\nreading_mcq(yangwm,true):\ttrue", result);
        ApiLogger.debug("testStatus result:" + result);
    }
    
}
