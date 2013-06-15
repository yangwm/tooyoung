/**
 * 
 */
package cc.tooyoung.memcacheproxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cc.tooyoung.common.cache.MemcacheClient;
import cc.tooyoung.common.cache.driver.MockMemcacheClient;
import cc.tooyoung.common.util.ApiLogger;
import cc.tooyoung.memcache.vika.CasValue;


/**
 * 
 * 
 * @author yangwm Oct 29, 2012 12:42:06 AM
 */
public class MemCacheTemplateTest {
    
    @Test
    public void testSetWithExpire() {
        String key = MemCacheUtil.toKey("1750715731", "module.swe");
        String val = "abc";
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        memCacheTemplate.setExpire(43200); // 30 * 24 * 60; 1 day = 24 * 60min 
        memCacheTemplate.setExpireL1(43200); // 30 * 24 * 60; 1 day = 24 * 60min 
        
        /*
         * if expire time超过30天： set yangwm.testSet.se 32 0 3
         */
        boolean result = memCacheTemplate.set(key, val);
        Assert.assertTrue(result);
        val = (String) memCacheTemplate.get(key);
        Assert.assertEquals("abc", val);
        
        /*
         * if expire time为30天： set yangwm.testSet.se 32 0 3
         */
        result = memCacheTemplate.set(key, val, new Date((43200 - 1) * 60 * 1000L));
        Assert.assertTrue(result);
        val = (String) memCacheTemplate.get(key);
        Assert.assertEquals("abc", val);
    }
    
    @Test
    public void testSetFixExpire() {
        String key = MemCacheUtil.toKey("1750715731", "module.sfe");
        String val = "abc";
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        memCacheTemplate.setExpire(43200 + 1); // 30 * 24 * 60; 1 day = 24 * 60min 
        memCacheTemplate.setExpireL1(43200 + 1); // 30 * 24 * 60; 1 day = 24 * 60min 
        
        /*
         * if expire time超过30天，那么过期时间将不设置： set yangwm.testSet.se 32 0 3
         */
        boolean result = memCacheTemplate.set(key, val);
        Assert.assertTrue(result);
        val = (String) memCacheTemplate.get(key);
        Assert.assertEquals("abc", val);
        
        /*
         * if expire time超过30天，那么过期时间将不设置： set yangwm.testSet.se 32 0 3
         */
        result = memCacheTemplate.set(key, val, new Date((43200 + 1) * 60 * 1000L));
        Assert.assertTrue(result);
        val = (String) memCacheTemplate.get(key);
        Assert.assertEquals("abc", val);
    }
    
    @Test
    public void testGet() {
        String key = MemCacheUtil.toKey("123", "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        if (memCacheTemplate.set(key, "dsadsadsadsadas")) {
            String value = memCacheTemplate.get(key);
            Assert.assertEquals("dsadsadsadsadas", value);
            memCacheTemplate.delete(key);
            value = memCacheTemplate.get(key);
            Assert.assertEquals(null, value);
        }
    }

    @Test
    public void testGetMulti() {
        String[] keys = MemCacheUtil.toKeys(new String[] { "123", "456", "789" }, "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        if (memCacheTemplate.set(keys[0], "dsadsadsadsadas")) {
            Map<String, String> map = memCacheTemplate.getMulti(keys);
            Assert.assertEquals(true, map.containsKey(keys[0]));
            Assert.assertEquals(false, map.containsKey(keys[1]));
            Assert.assertEquals(false, map.containsKey(keys[2]));
        }
    }

    @Test
    public void testSet() {
        String key = MemCacheUtil.toKey("123", "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        if (memCacheTemplate.set(key, "dsadsadsadsadas")) {
            String value = memCacheTemplate.get(key);
            Assert.assertEquals("dsadsadsadsadas", value);
            memCacheTemplate.delete(key);
            value = memCacheTemplate.get(key);
            Assert.assertEquals(null, value);
        }
    }

    @Test
    public void testDelete() {
        String key = MemCacheUtil.toKey("123", "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        if (memCacheTemplate.set(key, "dsadsadsadsadas")) {
            String value = memCacheTemplate.get(key);
            Assert.assertEquals("dsadsadsadsadas", value);
            memCacheTemplate.delete(key);
            value = memCacheTemplate.get(key);
            Assert.assertEquals(null, value);
        }
    }

    // @Test
    public void testSetExpire() {
        String key = MemCacheUtil.toKey("123", "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        try {
            memCacheTemplate.set(key, "dsadsadsadsadas", new Date(1000));
            Thread.sleep(1000L * 10);
            String value = memCacheTemplate.get(key);
            Assert.assertEquals(null, value);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testCas() {
        String key = MemCacheUtil.toKey("123", "module.c");
        MemCacheTemplate<String> memCacheTemplate = getMemCacheTemplate();
        
        if (memCacheTemplate.set(key, "dsadsadsadsadas")) {
            CasValue<String> valueCas = memCacheTemplate.getCas(key);
            String value = (String) valueCas.getValue() + 0;
            valueCas.setValue(value);
            memCacheTemplate.cas(key, valueCas);

            value = memCacheTemplate.get(key);
            Assert.assertEquals(value, valueCas.getValue());

            memCacheTemplate.delete(key);
            value = memCacheTemplate.get(key);
            Assert.assertEquals(null, value);
        }
    }
    
    private MemCacheTemplate<String> getMemCacheTemplate() {
        MockMemcacheClient memcacheClientMaster = new MockMemcacheClient();
        memcacheClientMaster.setMinSpareConnections(2);
        memcacheClientMaster.setMaxSpareConnections(15);
        memcacheClientMaster.setServerPort("testmc:11211"); // testmcmaster
        memcacheClientMaster.init();
        
        MockMemcacheClient memcacheClientMasterL1 = new MockMemcacheClient();
        memcacheClientMasterL1.setMinSpareConnections(2);
        memcacheClientMasterL1.setMaxSpareConnections(15);
        memcacheClientMasterL1.setServerPort("testmc:11211"); // testmcmasterL1
        memcacheClientMasterL1.init();
        List<MemcacheClient> masterL1List = new ArrayList<MemcacheClient>();
        masterL1List.add(memcacheClientMasterL1);
        
        MockMemcacheClient memcacheClientSlave = new MockMemcacheClient();
        memcacheClientSlave.setMinSpareConnections(2);
        memcacheClientSlave.setMaxSpareConnections(15);
        memcacheClientSlave.setServerPort("testmc:11211"); // testmcslave
        memcacheClientSlave.init();
        
        MemCacheTemplate<String> memCacheTemplate = new MemCacheTemplate<String>();
        memCacheTemplate.setMaster(memcacheClientMaster);
        memCacheTemplate.setMasterL1List(masterL1List);
        memCacheTemplate.setSlave(memcacheClientSlave);
        memCacheTemplate.setExpire(10800); // 7 * 24 * 60; 1 day = 24 * 60min 
        memCacheTemplate.setWirtePolicy("setAndDeleteL1");
        
        return memCacheTemplate;
    }
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        MemCacheTemplate<String> memCacheTemplate = new MemCacheTemplateTest().getMemCacheTemplate(); // new MemCacheTemplate<String>();
        ApiLogger.debug(memCacheTemplate.getExpireTime());
        ApiLogger.debug(memCacheTemplate.getExpireTimeL1());
    }
    
}
