/**
 * 
 */
package cc.tooyoung.common.shard;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.util.ApiLogger;

/**
 * 
 * @author yangwm Jan 24, 2013 9:05:46 PM
 */
public class ShardingUtilTest {

    @Test
    public void testParseClients() {
        Map<String, Integer> clientsConfig = new HashMap<String, Integer>();
        clientsConfig.put("1", new Integer("11"));
        clientsConfig.put("2", new Integer("21"));
        clientsConfig.put("3", new Integer("31"));
        clientsConfig.put("4", new Integer("41"));
        Map<Integer, Integer> clients = ShardingUtil.parseClients(clientsConfig);
        ApiLogger.debug("testParseClients clients:" + clients);
        Assert.assertEquals(new Integer("11"), clients.get(new Integer("1")));
        Assert.assertEquals(new Integer("21"), clients.get(new Integer("2")));
        Assert.assertEquals(new Integer("31"), clients.get(new Integer("3")));
        Assert.assertEquals(new Integer("41"), clients.get(new Integer("4")));
    }
    
}
